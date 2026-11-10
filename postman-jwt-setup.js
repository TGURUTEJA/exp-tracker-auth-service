// Postman Post-response Script for /test-jwt-generator endpoint
// This script extracts the JWT token from the response body and sets it as an environment variable

// Parse the response
let responseData = pm.response.json();

// Check if the request was successful
if (pm.response.code === 200 && !responseData.error) {
    // Extract JWT from response body (new token field)
    if (responseData.token) {
        // Set the JWT as an environment variable
        pm.environment.set("jwt", responseData.token);

        console.log("✓ JWT token successfully set in environment variable: jwt");
        console.log("Token length: " + responseData.token.length + " characters");
    } else {
        console.log("⚠ Token not found in response body");
    }

    // Also set the user ID for future requests
    if (responseData.ID) {
        pm.environment.set("user_id", responseData.ID);
        console.log("✓ User ID set in environment variable: user_id (" + responseData.ID + ")");
    }
} else {
    console.log("✗ Error generating JWT: " + responseData.message);
}

