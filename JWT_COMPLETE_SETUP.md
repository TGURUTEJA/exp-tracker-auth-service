# Postman JWT Auto-Refresh Complete Setup

## 📦 What You've Received

I've created a complete Postman solution for JWT token management with automatic expiry checking and refresh. Here are all the files:

---

## 📁 Files Summary

### 1. **Postman_JWT_AutoRefresh_Collection.json** 🟢 START HERE
**What it is:** Complete Postman collection ready to import  
**Contains:**
- `1. Generate Test JWT` - Endpoint to create initial JWT token
- `2. Create Category (with JWT Auto-Refresh)` - Example endpoint that auto-refreshes JWT

**How to use:**
```
Postman → Import → Select this file → Click Import
```

**Why:** Fastest way to get started in 30 seconds!

---

### 2. **postman-jwt-expiry-check.js**
**What it is:** Standalone pre-request script  
**Contains:** Complete JavaScript code for JWT expiry checking

**How to use:**
- Copy the entire contents
- Paste into Pre-request Script tab of any Postman request

**Why:** If you want to manually add this to your own request

---

### 3. **JWT_EXPIRY_CHECK_GUIDE.md**
**What it is:** Technical deep-dive guide  
**Contains:**
- How the script works (step by step)
- All the code explained
- Console output examples
- Troubleshooting

**How to use:**
- Read this for understanding the mechanics
- Reference for advanced customization

**Why:** Complete technical documentation

---

### 4. **JWT_AUTO_REFRESH_QUICK_START.md** 🟡 QUICK REFERENCE
**What it is:** Quick start guide  
**Contains:**
- 2 ways to setup (import vs manual)
- Step-by-step instructions
- Customization options
- Troubleshooting

**How to use:**
- Read this first for setup instructions
- Reference for issues

**Why:** Fastest reference guide without too much detail

---

## 🚀 Getting Started (5 Steps)

### Step 1: Copy Collection File Location
```
C:\Users\tguru\OneDrive\Desktop\SplitProject\exp-tracker-auth-service\
Postman_JWT_AutoRefresh_Collection.json
```

### Step 2: Open Postman
Click **Import** button (top-left)

### Step 3: Import Collection
Select the `Postman_JWT_AutoRefresh_Collection.json` file

### Step 4: Verify Environment
Check top-right dropdown:
- Select environment (or create: base_url, jwt, user_id)
- `base_url` should be `http://localhost:8082`

### Step 5: Run Requests
1. **First:** Click `1. Generate Test JWT` → **Send**
   - Check Console: should show ✓ JWT token set
2. **Second:** Click `2. Create Category` → **Send**
   - Console shows JWT expiry check
   - Request proceeds with fresh/existing JWT

---

## 🔄 How It Works

```
┌─────────────────────────────────────────────────────────────┐
│                                                             │
│  You Send Request to Category Endpoint                      │
│                ↓                                            │
│  Pre-request Script Runs                                    │
│         ↓              ↓                                     │
│    JWT Exists?     Check Expiration                         │
│      ↓   ↓         ↓    ↓                                    │
│     No  Yes    <1min  >1min                                 │
│      ↓   ↓         ↓    ↓                                    │
│      └─Generate  │  Continue                                │
│        New JWT   └──────┘                                   │
│         ↓                                                   │
│      Update Environment                                     │
│         ↓                                                   │
│      Add JWT to Request                                     │
│         ↓                                                   │
│      Send Request with Fresh JWT ✓                          │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 📊 What Happens in Console

### Scenario 1: JWT is Valid
```
🔐 JWT Expiry Check Pre-request Script
📋 JWT expiration check:
   - Time remaining: 65 minutes
✓ JWT is still valid
✓ Proceeding with request using current JWT
```
**Result:** Request sent immediately ⚡

---

### Scenario 2: JWT Expired
```
🔐 JWT Expiry Check Pre-request Script
📋 JWT expiration check:
   - Time remaining: -10 minutes
⏰ JWT is expired or about to expire!
🔄 Generating new JWT...
✓ New JWT generated and set in environment
✓ Proceeding with request using current JWT
```
**Result:** New JWT generated, request sent with fresh token 🔄

---

### Scenario 3: No JWT Found
```
🔐 JWT Expiry Check Pre-request Script
⚠ No JWT found in environment variable 'jwt'
🔄 Generating new JWT...
✓ New JWT generated and set in environment
```
**Result:** First JWT generated, request sent ✨

---

## 🎯 For Your Endpoint

Your endpoint: `http://localhost:8080/api/App/category/create_category`

**The collection includes:** A template request with everything pre-configured!

Simply:
1. Import the collection ✓
2. Update request URL to your actual endpoint (if different)
3. Add your request body JSON
4. Send! 🎉

---

## 📝 Environment Variables Used

| Variable | Purpose | Default |
|----------|---------|---------|
| `base_url` | API base URL | `http://localhost:8082` |
| `jwt` | JWT token stored here | Empty (auto-populated) |
| `user_id` | User ID for token generation | `1` |

---

## 🔍 Key Features

✅ **Automatic JWT Expiry Detection**
- Decodes JWT payload
- Reads expiration claim
- Compares with current time

✅ **Auto-Refresh on Expiry**
- Calls test-jwt-generator endpoint
- Updates environment with new token
- Seamless handoff to actual request

✅ **Cookie Header Support**
- Automatically includes: `Cookie: access_token={{jwt}}`
- Perfect for session-based auth

✅ **Detailed Console Logging**
- Timestamps
- Time remaining
- Error messages
- Token updates

✅ **Reusable For Any Endpoint**
- Copy script to any request
- Just add: `Cookie: access_token={{jwt}}` header
- Works instantly

---

## 🛠️ Customization

### For Your Endpoint (8080 instead of 8082)
1. Import collection
2. Update `base_url` environment variable to `http://localhost:8080`
3. OR change endpoint URLs in requests

### For Different JWT Generation Payload
In pre-request script, find:
```javascript
const testPayload = {
    "sub": "JWTAUth",
    "role": "USER",
    "iss": "auth-service",
    "password_reset_token": false,
    "ID": pm.environment.get("user_id") || 1
};
```

Change any values as needed for your auth service

---

## 📞 Quick Links

| Document | Purpose | Time |
|----------|---------|------|
| **JWT_AUTO_REFRESH_QUICK_START.md** | Setup & basic usage | 5 min read |
| **JWT_EXPIRY_CHECK_GUIDE.md** | Technical details | 15 min read |
| **postman-jwt-expiry-check.js** | Just the code | Copy-paste |
| **Postman_JWT_AutoRefresh_Collection.json** | Import in Postman | 30 sec setup |

---

## 🎓 Learn More

### What JWT Decoding Does
Takes the token's middle part and decodes the claims (payload):
- `exp` - Expiration timestamp
- `ID` - User ID
- `role` - User role
- `iss` - Issuer (auth-service)

### What Pre-request Script Does
Runs BEFORE your request is sent:
1. Checks environment `jwt` variable
2. Decodes if it exists
3. Compares exp time with now
4. If expired, calls test-jwt-generator
5. Updates jwt in environment
6. Lets request proceed

### Why This Matters
- **No manual token refresh** - Automatic!
- **No 401 errors** - Token always fresh
- **Seamless testing** - Focus on testing, not token management
- **Production-ready** - Same pattern works in real apps

---

## ✅ Checklist

- [ ] Environment variables set (base_url, jwt, user_id)
- [ ] Environment selected in Postman
- [ ] Collection imported
- [ ] Generate JWT request tested
- [ ] Create Category request has Cookie header
- [ ] Pre-request script is in place
- [ ] Console accessible (Ctrl+Alt+C)
- [ ] Ready to test!

---

## 🎉 You're All Set!

Everything is configured and ready to use:
1. **Import** `Postman_JWT_AutoRefresh_Collection.json`
2. **Select** environment
3. **Run** "Generate Test JWT"
4. **Run** "Create Category" (or your endpoint)
5. **Watch** console as JWT is automatically managed

Enjoy hassle-free JWT testing! 🚀

---

**Files Location:**
```
C:\Users\tguru\OneDrive\Desktop\SplitProject\exp-tracker-auth-service\
├── Postman_JWT_AutoRefresh_Collection.json ⭐ IMPORT THIS
├── postman-jwt-expiry-check.js
├── JWT_EXPIRY_CHECK_GUIDE.md
├── JWT_AUTO_REFRESH_QUICK_START.md ⭐ READ THIS
└── postman-jwt-setup.js
```

