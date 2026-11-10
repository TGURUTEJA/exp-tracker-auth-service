/*
 * Postman Pre-request Script for JWT Expiry Check and Auto-Refresh
 *
 * This script:
 * 1. Checks if JWT exists in environment
 * 2. Decodes JWT and checks expiration time
 * 3. If expired, automatically generates a new JWT
 * 4. Updates environment variable with new/existing JWT
 * 5. Proceeds with the request
 */

// Get JWT from environment
let jwt = pm.environment.get("jwt");

// Function to decode JWT payload (without verification)
function decodeJWT(token) {
    try {
        const parts = token.split('.');
        if (parts.length !== 3) {
            console.log("⚠ Invalid JWT format");
            return null;
        }

        // Decode the payload (second part)
        let payload = parts[1];

        // Add padding if needed
        const padding = 4 - (payload.length % 4);
        if (padding !== 4) {
            payload += '='.repeat(padding);
        }

        // Decode base64
        const decoded = atob(payload);
        const parsed = JSON.parse(decoded);

        return parsed;
    } catch (e) {
        console.log("✗ Error decoding JWT: " + e.message);
        return null;
    }
}

// Function to check if JWT is expired
function isJWTExpired(jwtPayload) {
    if (!jwtPayload || !jwtPayload.exp) {
        console.log("⚠ JWT payload missing or no expiration claim");
        return true;
    }

    // exp is in seconds, convert to milliseconds
    const expirationTime = jwtPayload.exp * 1000;
    const currentTime = Date.now();
    const timeUntilExpiry = expirationTime - currentTime;
    const minutesRemaining = Math.floor(timeUntilExpiry / 60000);

    console.log("📋 JWT expiration check:");
    console.log("   - Expires at: " + new Date(expirationTime).toLocaleString());
    console.log("   - Current time: " + new Date(currentTime).toLocaleString());
    console.log("   - Time remaining: " + minutesRemaining + " minutes");

    // Consider expired if less than 1 minute remaining
    if (timeUntilExpiry < 60000) {
        console.log("⏰ JWT is expired or about to expire!");
        return true;
    }

    console.log("✓ JWT is still valid");
    return false;
}

// Function to generate new JWT
function generateNewJWT(callback) {
    console.log("🔄 Generating new JWT...");

    const testJwtEndpoint = pm.environment.get("base_url") + "/api/auth/test-jwt-generator";

    // Default test JWT payload
    const testPayload = {
        "sub": "JWTAUth",
        "role": "USER",
        "iss": "auth-service",
        "password_reset_token": false,
        "ID": pm.environment.get("user_id") || 1
    };

    pm.sendRequest({
        url: testJwtEndpoint,
        method: 'POST',
        header: {
            'Content-Type': 'application/json'
        },
        body: {
            mode: 'raw',
            raw: JSON.stringify(testPayload)
        }
    }, function(err, response) {
        if (err) {
            console.log("✗ Error generating JWT: " + err.message);
            return;
        }

        if (response.code === 200) {
            let responseData = {};
            try {
                responseData = response.json();
            } catch (e) {
                console.log("✗ Error parsing response: " + e.message);
                return;
            }

            if (responseData.token && !responseData.error) {
                pm.environment.set("jwt", responseData.token);
                console.log("✓ New JWT generated and set in environment");
                console.log("✓ Token length: " + responseData.token.length + " characters");
                callback(true);
            } else {
                console.log("✗ Failed to generate JWT: " + responseData.message);
                callback(false);
            }
        } else {
            console.log("✗ JWT generation endpoint returned: " + response.code);
            callback(false);
        }
    });
}

// Main logic
console.log("\n🔐 JWT Expiry Check Pre-request Script");
console.log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

if (!jwt) {
    console.log("⚠ No JWT found in environment variable 'jwt'");
    console.log("⏸ Requesting JWT generation...");

    // Generate JWT synchronously using setTimeout workaround
    pm.environment.set("_jwt_check_pending", true);
    generateNewJWT(function(success) {
        pm.environment.set("_jwt_check_pending", false);
    });

    // Note: Due to Postman limitations, the request may proceed before JWT generation completes
    // Use a small delay or run the test-jwt-generator request manually first
} else {
    // Decode and check expiration
    const decodedJWT = decodeJWT(jwt);

    if (decodedJWT) {
        const isExpired = isJWTExpired(decodedJWT);

        if (isExpired) {
            console.log("⏸ Requesting JWT refresh...");
            pm.environment.set("_jwt_check_pending", true);

            generateNewJWT(function(success) {
                pm.environment.set("_jwt_check_pending", false);
            });

            // Note: Pre-request scripts in Postman don't support async/await properly
            // You may need to add a brief delay in Postman's request settings
        } else {
            console.log("✓ Proceeding with request using current JWT");
        }
    } else {
        console.log("✗ Could not decode JWT");
    }
}

console.log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

