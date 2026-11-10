# Postman JWT Setup Guide

## Overview
This guide explains how to set up Postman to automatically extract JWT tokens from the `/test-jwt-generator` endpoint and store them as environment variables for use in other API requests.

---

## Option 1: Using the Pre-configured Postman Collection (Recommended)

### Step 1: Import the Collection
1. Open Postman
2. Click **Import** (top-left corner)
3. Select **File** tab
4. Choose the file: `Postman_JWT_Collection.json`
5. Click **Import**

### Step 2: Create/Select Environment
1. Click the **Environments** icon (left sidebar)
2. Create a new environment or select existing one
3. Ensure these variables exist (they'll be auto-populated):
   - `base_url` = `http://localhost:8082`
   - `jwt` = (empty, will be filled automatically)
   - `user_id` = (empty, will be filled automatically)

### Step 3: Send the Request
1. Select the **"Generate Test JWT"** request from the collection
2. The body already contains sample payload:
   ```json
   {
     "sub": "JWTAUth",
     "role": "USER",
     "iss": "auth-service",
     "password_reset_token": false,
     "ID": 1
   }
   ```
3. Click **Send**
4. Check the **Console** (bottom or Ctrl+Alt+C) to see:
   - ✓ JWT token successfully set in environment variable: jwt
   - ✓ User ID set in environment variable: user_id

---

## Option 2: Manual Setup in Postman

### Step 1: Create Environment
1. Click **Environments** (left sidebar)
2. Click **+** to create new environment
3. Add these variables:
   - `base_url`: `http://localhost:8082`
   - `jwt_token`: (empty)
   - `user_id`: (empty)
4. Save the environment

### Step 2: Create Request
1. Create new **POST** request
2. URL: `{{base_url}}/api/auth/test-jwt-generator`
3. Headers:
   - `Content-Type`: `application/json`
4. Body (raw, JSON):
   ```json
   {
     "sub": "JWTAUth",
     "role": "USER",
     "iss": "auth-service",
     "password_reset_token": false,
     "ID": 1
   }
   ```

### Step 3: Add Post-response Script
1. Click the **Tests** tab (next to Body)
2. Copy and paste this script:

```javascript
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
```

### Step 4: Send Request
1. Click **Send**
2. Open **Console** to verify JWT was set
3. Check your environment variables - `jwt_token` should now be populated!

---

## Using the JWT in Other Requests

### Option A: In Headers
In any other request, add this header:
```
Authorization: Bearer {{jwt}}
```

### Option B: In Cookie
Add this to the request:
```
Cookie: access_token={{jwt}}
```

### Example Usage
1. Create a new request to any protected endpoint
2. In **Headers** tab, add:
   - Key: `Authorization`
   - Value: `Bearer {{jwt}}`
3. Send the request - it will use the JWT from the environment!

---

## Testing the Setup

### Test Request
```
GET http://localhost:8082/api/auth/status
Headers:
  Authorization: Bearer {{jwt}}
```

---

## Environment Variables Reference

After running the "Generate Test JWT" request:

| Variable | Purpose | Example |
|----------|---------|---------|
| `jwt` | The JWT Bearer token | `eyJhbGciOiJSUzI1NiIs...` |
| `user_id` | User ID from token | `1` |
| `base_url` | API base URL | `http://localhost:8082` |

---

## Troubleshooting

### JWT not being set
- Check **Console** output (Ctrl+Alt+C)
- Verify the response status is 200
- Ensure `response.error` is `false`
- Check that Authorization header exists in response

### Can't see Console output
- In Postman, press **Ctrl+Alt+C** to open Console
- Or click **View** → **Show Postman Console**

### Token not working in other requests
- Verify token is set: Click environment, check `jwt` value
- Make sure other requests use `Bearer {{jwt}}` syntax
- Check token hasn't expired (1 hour validity)

### Environment Variables not updating
- Ensure you have an environment selected (not "No Environment")
- Check the environment name in top-right dropdown
- Verify the script is in the **Tests** tab, not **Pre-request Script**

---

## Advanced: Generate Different User Tokens

Modify the request body to change user properties:

```json
{
  "sub": "JWTAUth",
  "role": "ADMIN",                    // Change role
  "iss": "auth-service",
  "password_reset_token": true,       // For password reset flows
  "ID": 99                            // Different user ID
}
```

Each request will update the `jwt_token` environment variable with the new token!

---

## Files Included

- `Postman_JWT_Collection.json` - Ready-to-import Postman collection
- `postman-jwt-setup.js` - Standalone script file
- `POSTMAN_SETUP.md` - This guide (you're reading it!)

---

## Need Help?

1. **Check the Console**: Ctrl+Alt+C in Postman
2. **Verify Response**: Look at the actual response body
3. **Test Endpoint**: Try calling `/api/auth/status` to verify service is running
4. **Check Configuration**: Ensure `app.jwt.test-endpoint.enabled=true` in `application.properties`

Happy Testing! 🚀

