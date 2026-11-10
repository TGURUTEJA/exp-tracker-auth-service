## Updated Postman Post-script for JWT Test Generator

### Changes Made:
✅ **Extracts JWT from response body** (new `token` field) instead of Authorization header  
✅ **Sets environment variable name to `jwt`** instead of `jwt_token`  
✅ **Extracts from `responseData.token`** directly from JSON response  

---

## Updated Script

```javascript
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
```

---

## What Changed

### Before (Authorization Header):
```javascript
let authHeader = pm.response.headers.get("Authorization");
let jwt = authHeader.replace("Bearer ", "");
pm.environment.set("jwt_token", jwt);
```

### After (Response Body):
```javascript
if (responseData.token) {
    pm.environment.set("jwt", responseData.token);
}
```

---

## Environment Variable Names

| Old Variable | New Variable | Type | Value |
|-------------|-------------|------|-------|
| `jwt_token` | `jwt` | String | JWT token from response |
| `user_id` | `user_id` | String | User ID from response |

---

## Usage in Other Requests

### In Headers:
```
Authorization: Bearer {{jwt}}
```

### Response will contain:
```json
{
  "isError": false,
  "message": "Test JWT generated successfully",
  "ID": 1,
  "token": "eyJhbGciOiJSUzI1NiIsImp0aSI6IjE2YjI3YzJkLWEzNDItNGQyZS04YjEzLTkxN2QzMTU5MTI3MCJ9..."
}
```

---

## Steps to Update Your Postman

1. **Open Your Test Request** in Postman
2. **Go to Tests Tab**
3. **Replace the entire script** with the updated one above
4. **Send the Request**
5. **Check Console** (Ctrl+Alt+C) - you should see:
   ```
   ✓ JWT token successfully set in environment variable: jwt
   ✓ User ID set in environment variable: user_id (1)
   ```

---

## Verify It Works

1. Check your environment variables:
   - `jwt` should now contain the token
   - `user_id` should contain the user ID

2. In any other request, use:
   ```
   Authorization: Bearer {{jwt}}
   ```

Done! 🎉

