# Postman Pre-request Script: JWT Expiry Check & Auto-Refresh

## Overview
This pre-request script automatically checks if your JWT token is expired before sending requests. If it's expired, it automatically generates a new one and updates your environment variable.

---

## Features

✅ Decodes JWT payload to check expiration time  
✅ Checks time remaining until expiration  
✅ Automatically refreshes expired tokens  
✅ Updates environment variable with new JWT  
✅ Detailed console logging for debugging  
✅ Considers tokens expired if < 1 minute remaining  

---

## Installation

### Step 1: In your Postman Request
1. Open the request to your endpoint (e.g., `/api/App/category/create_category`)
2. Click the **Pre-request Script** tab
3. Copy and paste the script below

### Step 2: Set Headers
1. Go to the **Headers** tab
2. Add this header:
   ```
   Cookie: access_token={{jwt}}
   ```

### Step 3: Ensure Environment Variables
Make sure you have these variables in your Postman environment:
- `base_url` - e.g., `http://localhost:8082`
- `jwt` - Your JWT token (will be auto-populated)
- `user_id` - e.g., `1` (optional, for new token generation)

---

## The Script

```javascript
/*
 * Postman Pre-request Script for JWT Expiry Check and Auto-Refresh
 */

// Get JWT from environment
let jwt = pm.environment.get("jwt");

// Function to decode JWT payload
function decodeJWT(token) {
    try {
        const parts = token.split('.');
        if (parts.length !== 3) {
            console.log("⚠ Invalid JWT format");
            return null;
        }
        
        let payload = parts[1];
        
        // Add padding if needed
        const padding = 4 - (payload.length % 4);
        if (padding !== 4) {
            payload += '='.repeat(padding);
        }
        
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
    
    const expirationTime = jwtPayload.exp * 1000;
    const currentTime = Date.now();
    const timeUntilExpiry = expirationTime - currentTime;
    const minutesRemaining = Math.floor(timeUntilExpiry / 60000);
    
    console.log("📋 JWT expiration check:");
    console.log("   - Expires at: " + new Date(expirationTime).toLocaleString());
    console.log("   - Current time: " + new Date(currentTime).toLocaleString());
    console.log("   - Time remaining: " + minutesRemaining + " minutes");
    
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
    
    pm.environment.set("_jwt_check_pending", true);
    generateNewJWT(function(success) {
        pm.environment.set("_jwt_check_pending", false);
    });
} else {
    const decodedJWT = decodeJWT(jwt);
    
    if (decodedJWT) {
        const isExpired = isJWTExpired(decodedJWT);
        
        if (isExpired) {
            console.log("⏸ Requesting JWT refresh...");
            pm.environment.set("_jwt_check_pending", true);
            
            generateNewJWT(function(success) {
                pm.environment.set("_jwt_check_pending", false);
            });
        } else {
            console.log("✓ Proceeding with request using current JWT");
        }
    } else {
        console.log("✗ Could not decode JWT");
    }
}

console.log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
```

---

## How It Works

### 1. **Check JWT Exists**
   - Retrieves JWT from `{{jwt}}` environment variable
   - If missing, triggers new JWT generation

### 2. **Decode JWT Payload**
   - Extracts the payload portion of JWT
   - Decodes base64 to read claims

### 3. **Check Expiration**
   - Reads the `exp` claim (expiration timestamp)
   - Compares with current time
   - Considers expired if < 1 minute remaining

### 4. **Console Output**
   ```
   🔐 JWT Expiry Check Pre-request Script
   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
   📋 JWT expiration check:
      - Expires at: 6/2/2026, 5:30:45 PM
      - Current time: 6/2/2026, 4:25:12 PM
      - Time remaining: 65 minutes
   ✓ JWT is still valid
   ✓ Proceeding with request using current JWT
   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
   ```

### 5. **Auto-Refresh if Expired**
   - Calls `/api/auth/test-jwt-generator` endpoint
   - Sends new test payload with your user ID
   - Updates `{{jwt}}` environment variable with new token
   - Proceeds with request

---

## Setup for Your Endpoint

### Example: Create Category Endpoint

**Step 1:** Create a request in Postman
- Method: `POST`
- URL: `{{base_url}}/api/App/category/create_category`

**Step 2:** Add Pre-request Script (use the script above)
- Paste in the **Pre-request Script** tab

**Step 3:** Add Header
- Go to **Headers** tab
- Add: `Cookie: access_token={{jwt}}`

**Step 4:** Add Body
```json
{
  "categoryName": "Electronics",
  "description": "Electronic products"
}
```

**Step 5:** Run Request
- Click **Send**
- Check Console (Ctrl+Alt+C) for JWT status
- Request proceeds with fresh or existing JWT

---

## Console Output Examples

### ✅ JWT is Valid
```
🔐 JWT Expiry Check Pre-request Script
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📋 JWT expiration check:
   - Expires at: 6/2/2026, 5:30:45 PM
   - Current time: 6/2/2026, 4:25:12 PM
   - Time remaining: 65 minutes
✓ JWT is still valid
✓ Proceeding with request using current JWT
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

### 🔄 JWT Expired and Refreshed
```
🔐 JWT Expiry Check Pre-request Script
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📋 JWT expiration check:
   - Expires at: 6/2/2026, 4:15:00 PM
   - Current time: 6/2/2026, 4:25:12 PM
   - Time remaining: -10 minutes
⏰ JWT is expired or about to expire!
⏸ Requesting JWT refresh...
🔄 Generating new JWT...
✓ New JWT generated and set in environment
✓ Token length: 1247 characters
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

### ⚠️ No JWT Found
```
🔐 JWT Expiry Check Pre-request Script
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
⚠ No JWT found in environment variable 'jwt'
⏸ Requesting JWT generation...
🔄 Generating new JWT...
✓ New JWT generated and set in environment
✓ Token length: 1247 characters
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

## Environment Variables Required

| Variable | Value | Example |
|----------|-------|---------|
| `base_url` | API base URL | `http://localhost:8082` |
| `jwt` | JWT token | `eyJhbGc...` |
| `user_id` | User ID for new tokens | `1` |

---

## Troubleshooting

### JWT not updating
- Check Console (Ctrl+Alt+C) for errors
- Verify `base_url` is correct
- Ensure `/api/auth/test-jwt-generator` endpoint is accessible

### "Invalid JWT format" error
- Check that `jwt` environment variable contains a valid JWT
- JWT should have 3 parts separated by dots: `header.payload.signature`

### Token not persisting
- Make sure environment is selected (not "No Environment")
- Check environment dropdown in top-right of Postman

### Asynchronous timing issues
- First request may not complete JWT generation due to timing
- Solution: Run Generate Test JWT request manually first, then proceed

---

## Files Provided

- `postman-jwt-expiry-check.js` - Standalone script
- `JWT_EXPIRY_CHECK_GUIDE.md` - This guide (you're reading it!)
- `Postman_JWT_Collection.json` - Collection with endpoints pre-configured

---

## Next Steps

1. **Copy the script** from the section above
2. **Paste in Pre-request Script tab** of your Postman request
3. **Add the Cookie header** with `access_token={{jwt}}`
4. **Run your request** - JWT will auto-refresh if needed!

Done! 🎉

