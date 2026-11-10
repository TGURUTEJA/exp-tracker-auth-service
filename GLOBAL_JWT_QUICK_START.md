# 🚀 Global JWT Setup - EASIEST Way (3 Steps)

## The Fastest Solution: Use the Pre-configured Collection

We've already created a collection with the JWT script applied globally to ALL requests. Just import it and you're done!

---

## ⚡ 3-Step Setup (2 Minutes)

### Step 1: Copy Collection File Path
```
C:\Users\tguru\OneDrive\Desktop\SplitProject\exp-tracker-auth-service\
Collection_GlobalJWT_AllRequests.json
```

### Step 2: Import in Postman
1. Open **Postman**
2. Click **Import** (top-left)
3. Select the file
4. Click **Import**

### Step 3: You're Done! ✅
```
Every request in this collection automatically:
✓ Checks JWT expiration
✓ Refreshes if needed
✓ Sends with fresh JWT
```

---

## ✨ What You Get

The collection includes:
- ✓ 1. Generate Test JWT
- ✓ 2. Create Category (with JWT auto-refresh)
- ✓ 3. Get Categories (with JWT auto-refresh)
- ✓ 4. Update Category (with JWT auto-refresh)
- ✓ 5. Delete Category (with JWT auto-refresh)

**Every single request** automatically uses JWT management!

---

## 🎯 How to Add More Requests

1. **Add new request** to the collection
2. Add **Cookie header:** `access_token={{jwt}}`
3. **Done!** JWT auto-refresh automatically applies

No script copying. No manual setup. It's automatic! ✨

---

## 📝 If You Already Have a Collection

You can manually add the global script to your existing collection:

### In Postman:
```
Your Collection Name
    ↓
    (Right-click or click 3-dots menu)
    ↓
    "Edit Collection"
    ↓
    Click "Pre-request Script" tab
    ↓
    Paste the JWT script
    ↓
    Click Save
```

**Done!** Now all requests in YOUR collection use JWT auto-refresh.

---

## 📜 Where to Paste the Script

If setting up manually, here's the script to paste in Collection → Pre-request Script tab:

```javascript
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
    
    pm.sendRequest({
        url: testJwtEndpoint,
        method: 'POST',
        header: {'Content-Type': 'application/json'},
        body: {mode: 'raw', raw: JSON.stringify(testPayload)}
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

---

## ✅ Checklist After Import

- [ ] Imported `Collection_GlobalJWT_AllRequests.json` into Postman
- [ ] Environment selected with base_url, jwt, user_id variables
- [ ] Send "Generate Test JWT" request
- [ ] Check Console: Ctrl+Alt+C
- [ ] See "✓ JWT valid - proceeding with request"
- [ ] Send any other request in collection
- [ ] JWT auto-refresh applies automatically ✓

---

## 🎯 Now You Can:

```
❌ DON'T do this:
Request 1 → Add script manually
Request 2 → Add script manually
Request 3 → Add script manually
...repeat 50 times

✅ DO this instead:
1. Import collection with global script
2. Add new requests
3. Auto-refresh applies to everything!
```

---

## 🌟 Key Points

### The Magic Happens Here:
```
Collection → Pre-request Script (ONE place)
                    ↓
All Requests (get script automatically)
    ├─ Request 1 ✓
    ├─ Request 2 ✓
    ├─ Request 3 ✓
    └─ ... All requests ✓
```

### Each Request Doesn't Need:
```
❌ Individual pre-request scripts
❌ Duplicate code
❌ Manual setup
```

### Requests Just Need:
```
✅ Cookie header: access_token={{jwt}}
✅ That's it!
```

---

## 📊 Console Output (What You'll See)

When you send ANY request in the collection:

```
🔐 [COLLECTION] JWT Expiry Check - Applying to all requests
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📋 JWT expiration check:
   - Expires at: 6/2/2026, 5:30:45 PM
   - Current time: 6/2/2026, 4:25:12 PM
   - Time remaining: 65 minutes
✓ JWT is still valid
✓ JWT valid - proceeding with request
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

## 🎉 Done!

You now have **one collection** with **global JWT auto-refresh**:

1. ✓ Every request has JWT checking
2. ✓ No manual script adding
3. ✓ No duplicated code
4. ✓ New requests inherit the feature automatically
5. ✓ Easy to maintain

**Start using it!** 🚀

---

## 📁 Files Provided

| File | Purpose |
|------|---------|
| `Collection_GlobalJWT_AllRequests.json` | **Import this first!** |
| `COLLECTION_LEVEL_JWT_SETUP.md` | Detailed guide if setting up manually |

---

**Happy Testing!** No more token management headaches! ✨

