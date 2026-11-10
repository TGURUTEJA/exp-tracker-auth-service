# Quick Start: JWT Auto-Refresh for Category Endpoint

## 🚀 Fastest Way to Get Started

### Option 1: Import Pre-configured Collection (Recommended)

1. **Open Postman**
2. **Click Import** (top-left)
3. **Select:** `Postman_JWT_AutoRefresh_Collection.json`
4. **Click Import**

✅ **Done!** You now have:
- `1. Generate Test JWT` - Gets initial JWT token
- `2. Create Category (with JWT Auto-Refresh)` - Auto-refreshes JWT on expiry

---

## 📋 Manual Setup (If Not Importing)

### Step 1: Create Environment
1. Click **Environments** (left sidebar)
2. Create new or select existing environment
3. Add these variables:
   ```
   base_url = http://localhost:8082
   jwt = (leave empty, will be auto-populated)
   user_id = 1
   ```
4. **Select this environment** (top-right dropdown)

### Step 2: Setup Generate JWT Request
1. **Create new POST request**
2. **URL:** `{{base_url}}/api/auth/test-jwt-generator`
3. **Body (raw JSON):**
   ```json
   {
     "sub": "JWTAUth",
     "role": "USER",
     "iss": "auth-service",
     "password_reset_token": false,
     "ID": 1
   }
   ```
4. **Go to Tests tab** and paste:
   ```javascript
   let responseData = pm.response.json();
   if (pm.response.code === 200 && !responseData.error) {
       if (responseData.token) {
           pm.environment.set("jwt", responseData.token);
           console.log("✓ JWT token set: " + responseData.token.substring(0, 50) + "...");
       }
       if (responseData.ID) {
           pm.environment.set("user_id", responseData.ID);
       }
   }
   ```
5. **Send** this request to generate initial JWT

### Step 3: Setup Create Category Request
1. **Create new POST request**
2. **URL:** `{{base_url}}/api/App/category/create_category`
3. **Headers tab:**
   ```
   Key: Cookie
   Value: access_token={{jwt}}
   ```
4. **Body (raw JSON):**
   ```json
   {
     "categoryName": "Electronics",
     "description": "Electronic products"
   }
   ```

### Step 4: Add Pre-request Script
1. **Go to Pre-request Script tab**
2. **Copy the entire script** from `JWT_EXPIRY_CHECK_GUIDE.md`
3. **Paste it in this tab**
4. **Save** the request

---

## 🎯 Usage

### First Time
1. **Run:** `1. Generate Test JWT` request
   - Check Console (Ctrl+Alt+C)
   - You should see: `✓ JWT token set...`

2. **Run:** `2. Create Category` request
   - Console will show JWT is valid
   - Request proceeds with fresh JWT

### Subsequent Times
- **Just run:** `2. Create Category` request
- **Script automatically:**
  - ✅ Checks if JWT is expired
  - ✅ Refreshes if needed (< 1 minute remaining)
  - ✅ Proceeds with request

---

## 📊 Console Output Examples

### ✅ JWT Valid - No Refresh Needed
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

### 🔄 JWT Expired - Auto-Refreshed
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

---

## 📝 For Other Endpoints

You can apply this same pattern to ANY endpoint that requires JWT:

### Template
1. **Create new request** for your endpoint
2. **Add Header:**
   ```
   Cookie: access_token={{jwt}}
   ```
3. **Go to Pre-request Script tab**
4. **Paste the JWT expiry check script**
5. **Done!**

---

## 🔧 Customization

### Change User ID for New Tokens
In the pre-request script, find this line:
```javascript
"ID": pm.environment.get("user_id") || 1
```

Change `1` to your desired user ID.

### Change Expiry Warning Time
In the script, find:
```javascript
if (timeUntilExpiry < 60000) {  // 60000ms = 1 minute
```

Change `60000` to a different value:
- `300000` = 5 minutes
- `1800000` = 30 minutes
- `3600000` = 1 hour

### Change Base URL
In your Postman environment, update:
```
base_url = http://your-api-url.com
```

---

## ✅ Verification Checklist

- [ ] Environment created with `base_url`, `jwt`, `user_id`
- [ ] Environment selected (top-right dropdown)
- [ ] Generate JWT request works
- [ ] `jwt` variable is populated in environment
- [ ] Create Category request has Cookie header with `access_token={{jwt}}`
- [ ] Pre-request script is in the Pre-request Script tab
- [ ] Console shows JWT status before each request

---

## 🐛 Troubleshooting

### "No JWT found" message
**Solution:** Run `Generate Test JWT` request first

### "Invalid JWT format" error
**Solution:** Check that `jwt` variable contains a valid token starting with `eyJ`

### Cookie not being sent
**Solution:** 
1. Check header is exactly: `Cookie: access_token={{jwt}}`
2. Make sure environment is selected

### Pre-request script not running
**Solution:**
1. Ensure script is in **Pre-request Script** tab (not Tests tab)
2. Request might need a small delay if JWT generation is slow
3. Try running Generate JWT request first

### JWT not refreshing
**Solution:**
1. Check Console for errors
2. Verify `/api/auth/test-jwt-generator` endpoint is working
3. Reduce the expiry warning time (change 60000 to 300000 for 5 min warning)

---

## 📁 Files Provided

| File | Purpose |
|------|---------|
| `Postman_JWT_AutoRefresh_Collection.json` | **Import this!** Pre-configured collection |
| `postman-jwt-expiry-check.js` | Standalone script file |
| `JWT_EXPIRY_CHECK_GUIDE.md` | Detailed technical guide |
| `JWT_AUTO_REFRESH_QUICK_START.md` | This quick start guide |

---

## 🎉 Done!

You now have automatic JWT expiry checking and token refresh on every API request!

**Next Step:** Import the collection and try it out. Let the script handle JWT management for you! 🚀

