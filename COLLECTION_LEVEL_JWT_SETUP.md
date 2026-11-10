# 🌍 Apply JWT Auto-Refresh to ALL API Calls Globally

## The Solution: Collection-Level Pre-request Script

Instead of adding the pre-request script to every individual request, add it ONCE at the **Collection Level**. This applies automatically to ALL requests in that collection.

---

## 🚀 How to Setup (5 Steps)

### Step 1: Open Your Collection
1. In Postman, open your collection
2. Click the **collection name** (e.g., "Auth & Categories")
3. A panel opens on the right

### Step 2: Go to Pre-request Script Tab
1. Look for the tabs: **Info**, **Pre-request Script**, **Tests**, **Variables**
2. Click **Pre-request Script** tab

### Step 3: Add the Script
Copy this entire script:

```javascript
/*
 * COLLECTION-LEVEL Pre-request Script
 * Applies to ALL requests in this collection automatically
 */

let jwt = pm.environment.get("jwt");

function decodeJWT(token) {
    try {
        const parts = token.split('.');
        if (parts.length !== 3) {
            console.log("⚠ Invalid JWT format");
            return null;
        }
        
        let payload = parts[1];
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

function generateNewJWT() {
    console.log("🔄 Generating new JWT...");
    
    const testJwtEndpoint = pm.environment.get("base_url") + "/api/auth/test-jwt-generator";
    const testPayload = {
        "sub": "JWTAUth",
        "role": "USER",
        "iss": "auth-service",
        "password_reset_token": false,
        "ID": pm.environment.get("user_id") || 1
    };
    
    // Using sendRequest callback to handle async
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
            try {
                let responseData = response.json();
                if (responseData.token && !responseData.error) {
                    pm.environment.set("jwt", responseData.token);
                    console.log("✓ New JWT generated and set globally");
                    console.log("✓ Token length: " + responseData.token.length + " characters");
                } else {
                    console.log("✗ Failed to generate JWT: " + responseData.message);
                }
            } catch (e) {
                console.log("✗ Error parsing response: " + e.message);
            }
        } else {
            console.log("✗ JWT generation returned: " + response.code);
        }
    });
}

// Main Logic - Runs for EVERY request in this collection
console.log("\n🔐 [COLLECTION] JWT Expiry Check - Applying to all requests");
console.log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

if (!jwt) {
    console.log("⚠ No JWT found - requesting generation...");
    generateNewJWT();
} else {
    const decodedJWT = decodeJWT(jwt);
    
    if (decodedJWT) {
        const isExpired = isJWTExpired(decodedJWT);
        
        if (isExpired) {
            console.log("⏸ JWT expired - requesting refresh...");
            generateNewJWT();
        } else {
            console.log("✓ JWT valid - proceeding with request");
        }
    } else {
        console.log("✗ Could not decode JWT");
    }
}

console.log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
```

### Step 4: Paste Into Collection
1. Paste the entire script into the **Pre-request Script** tab
2. Click **Save** button (or Ctrl+S)

### Step 5: Done! ✓
Now this script runs for EVERY request in your collection automatically!

---

## ✅ How It Works

```
Every time you send ANY request in the collection:

1. Collection Pre-request Script runs first (automatically)
2. Checks JWT expiration
3. Updates JWT if needed
4. Your actual request proceeds with fresh JWT
```

---

## 🎯 Benefits

✅ **No manual setup per request**
- Script runs for ALL requests automatically
- Add new requests, they inherit the script

✅ **Centralized management**
- Update script once, applies everywhere
- No duplicated code

✅ **Cleaner requests**
- Individual requests don't have redundant scripts
- Easy to read and maintain

✅ **Consistent behavior**
- All requests use same JWT logic
- No missed requests

---

## 🚦 Request-Specific Override

If you want a SPECIFIC request to NOT use JWT checking:

### Option 1: Disable at Request Level
1. Open the specific request
2. Go to **Pre-request Script** tab
3. Add only this line:
```javascript
// Disable collection-level script for this request
```

This won't stop collection script, but you can override values.

### Option 2: Skip JWT for Specific Request
In your request's Pre-request Script, add:
```javascript
// Skip JWT check for this request
if (pm.request.name === "My Specific Request") {
    // This request won't use JWT
} else {
    // Use JWT for all others
}
```

---

## 📋 Environment Variables (Still Needed)

You still need these in your Postman environment:

```
base_url = "http://localhost:8082"
jwt = "" (auto-populated)
user_id = "1"
```

---

## 🔍 Verify It's Working

### Check Console
1. Open Postman Console: **Ctrl+Alt+C**
2. Send any request in collection
3. See output:
   ```
   🔐 [COLLECTION] JWT Expiry Check - Applying to all requests
   📋 JWT expiration check:
      - Time remaining: 65 minutes
   ✓ JWT valid - proceeding with request
   ```

### All requests show same pattern:
- First time: "requesting generation..."
- Subsequent times: "JWT valid" or "requesting refresh..."

---

## 🎓 Hierarchy in Postman

Pre-request scripts run in this order:

```
1. Collection-level script ← Goes here!
   ↓
2. Folder-level script (if using folders)
   ↓
3. Request-level script
```

All run in sequence, so you can have multiple levels if needed.

---

## 📁 For Organized Collections (Using Folders)

If your collection has folders:

```
My Collection
├── Pre-request Script (JWT CHECK) ← Add here
├── Authentication
│   ├── Generate JWT
│   └── Login
├── Category Management
│   ├── Create Category
│   ├── Get Categories
│   └── Update Category
├── Product Management
│   ├── Create Product
│   ├── Get Products
│   └── Delete Product
```

All requests in all folders inherit the collection-level script!

---

## 🔧 Advanced: Conditional JWT Check

If you want to skip JWT for certain endpoints (like login):

Replace the main logic with:

```javascript
// Skip JWT check for these requests
const skipJWTRequests = [
    "Generate Test JWT",
    "Login",
    "Register",
    "Forgot Password"
];

const currentRequest = pm.request.name;

if (skipJWTRequests.includes(currentRequest)) {
    console.log("⏭ Skipping JWT check for: " + currentRequest);
} else {
    // Run JWT check for all other requests
    if (!jwt) {
        console.log("⚠ No JWT found - requesting generation...");
        generateNewJWT();
    } else {
        // ... rest of JWT logic
    }
}
```

---

## 📝 Common Setup Patterns

### Pattern 1: All APIs with JWT (Most Common)
- Add collection-level script
- All requests auto-refresh JWT
- ✓ Simplest approach

### Pattern 2: Some APIs Need JWT, Some Don't
- Add collection-level script
- Skip list: Login, Register, Status
- Other requests auto-refresh

### Pattern 3: Multiple Collections with Different Rules
- Add same script to each collection's pre-request script
- Each collection manages independently

---

## 🚀 Step-by-Step Visual Guide

### In Postman:
```
┌─────────────────────────────────────────┐
│ Your Collection Name                    │
│ (Right-click or click 3 dots)          │
└─────────────────────────────────────────┘
            ↓
┌─────────────────────────────────────────┐
│ Edit Collection                         │
│ ┌─────────────────────────────────────┐│
│ │ Info | Pre-request Script | Tests  ││
│ │      ← CLICK THIS TAB               ││
│ └─────────────────────────────────────┘│
│                                         │
│ [Large text area - paste script here]  │
│                                         │
│              [Save]                    │
└─────────────────────────────────────────┘
```

---

## ✨ The Magic

**Before:**
```
Request 1 → Add script (Ctrl+C → Ctrl+V)
Request 2 → Add script (Ctrl+C → Ctrl+V)
Request 3 → Add script (Ctrl+C → Ctrl+V)
...repeat 50 times
```

**After:**
```
Collection → Add script ONCE
↓
All requests automatically get JWT check ✓
```

---

## 📋 Checklist

- [ ] Opened collection
- [ ] Clicked collection name (not a request)
- [ ] Went to **Pre-request Script** tab
- [ ] Pasted the script
- [ ] Clicked **Save**
- [ ] Set environment variables (base_url, user_id, jwt)
- [ ] Sent a request
- [ ] Checked console (Ctrl+Alt+C)
- [ ] Saw JWT expiry check output ✓

---

## 🎉 Done!

Now every single API call in your collection:
- ✅ Checks JWT expiration
- ✅ Refreshes if needed
- ✅ No manual script adding
- ✅ Automatic for new requests too

**That's it!** No more adding scripts one by one. 🚀

