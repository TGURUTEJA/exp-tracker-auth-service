# 🌟 Global JWT Auto-Refresh - Best Solution Summary

## Question: How to apply JWT script to ALL API calls without adding it one by one?

## Answer: Use Collection-Level Pre-request Script!

---

## 🎯 The Solution

Add the JWT script **ONCE** at the **Collection level** → Applies to **ALL requests automatically**

```
Traditional Way ❌
├─ Add script to Request 1
├─ Add script to Request 2
├─ Add script to Request 3
└─ ... repeat for every request

Better Way ✅
├─ Add script to Collection
└─ All requests inherit it automatically!
```

---

## 🚀 Two Ways to Get Started

### Option 1: Import Pre-configured Collection (FASTEST - 30 seconds)
```
Download and Import:
Collection_GlobalJWT_AllRequests.json
↓
Everything is already set up and working!
```

**Pros:**
- ✅ Fastest setup (30 seconds)
- ✅ Already has example requests
- ✅ Ready to use immediately

### Option 2: Add to Your Existing Collection (5 minutes)
```
Your Collection
    → Right-click
    → Edit Collection
    → Pre-request Script tab
    → Paste JWT script
    → Save
↓
All your requests now have JWT auto-refresh!
```

**Pros:**
- ✅ Works with your existing collection
- ✅ Keep your current requests
- ✅ Full control

---

## 📋 Files You Have

| File | What to Do |
|------|-----------|
| **Collection_GlobalJWT_AllRequests.json** | 🟢 **Use this first!** Import into Postman |
| **COLLECTION_LEVEL_JWT_SETUP.md** | Detailed instructions if adding manually |
| **GLOBAL_JWT_QUICK_START.md** | Quick reference guide |

---

## 🔑 How It Works

### Collection-Level Pre-request Script Hierarchy

```
┌─────────────────────────────────────┐
│   Collection Pre-request Script     │
│  (JWT check code - runs FIRST)      │
└──────────────┬──────────────────────┘
               │
        ┌──────▼──────┐
        │   Your API   │
        │   Request    │
        │  (now has     │
        │   JWT set!)   │
        └──────────────┘
```

**Result:** Every request in the collection automatically:
1. ✓ Checks JWT expiration
2. ✓ Refreshes if < 1 minute remaining
3. ✓ Proceeds with fresh JWT

---

## ✅ No More Manual Setup for Each Request!

### Before Adding Script:
```
Request 1
    ↓ Copy script
    ↓ Paste in Pre-request tab
Request 2
    ↓ Copy script
    ↓ Paste in Pre-request tab
Request 3
    ↓ Copy script
    ↓ Paste in Pre-request tab
... repeat 50 times 😫
```

### After Using Collection Script:
```
Collection
    ↓ Paste script ONCE
All Requests (automatic!) ✨
    ├─ Request 1 ✓
    ├─ Request 2 ✓
    ├─ Request 3 ✓
    └─ Request 50 ✓
```

---

## 🎁 What You Get

The pre-configured collection includes:

```
Collection_GlobalJWT_AllRequests
    │
    ├─ Pre-request Script (Collection-level)
    │  └─ JWT auto-refresh for ALL requests
    │
    ├─ 1. Generate Test JWT
    ├─ 2. Create Category
    ├─ 3. Get Categories
    ├─ 4. Update Category
    └─ 5. Delete Category
```

**Every single request** automatically gets JWT management!

---

## 📊 Console Output Stays the Same

No matter which request you send, you'll see:

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

## 🛠️ How to Customize

### Add More Requests:
1. Create new request in collection
2. Add header: `Cookie: access_token={{jwt}}`
3. **Done!** JWT auto-refresh automatically applies

### Skip JWT for Specific Request (Optional):
Create in that request's **Pre-request Script**:
```javascript
// Skip JWT for this endpoint
if (pm.request.name === "Status") {
    // Skip
}
```

### Change Expiry Warning Time:
In Collection Pre-request Script, find:
```javascript
if (timeUntilExpiry < 60000) {  // 60000ms = 1 minute
```
Change `60000` to:
- `300000` = 5 minutes
- `1800000` = 30 minutes

---

## ✨ Key Benefits

✅ **Set Once, Works Everywhere**
- Add script to collection
- All requests inherit it

✅ **No Duplicate Code**
- Single script
- Used by all requests
- Easy to maintain

✅ **New Requests Automatic**
- Add new request
- It automatically gets JWT handling
- No extra setup needed

✅ **Centralized Management**
- One place to update
- Changes apply everywhere
- Consistent behavior

---

## 📍 File Locations

All files in:
```
C:\Users\tguru\OneDrive\Desktop\SplitProject\exp-tracker-auth-service\
```

Key files:
- `Collection_GlobalJWT_AllRequests.json` ← Import this!
- `COLLECTION_LEVEL_JWT_SETUP.md` ← Read this for manual setup
- `GLOBAL_JWT_QUICK_START.md` ← Quick reference

---

## 🚀 Getting Started (Choose One)

### Option 1: Fast Track (30 seconds)
1. Import `Collection_GlobalJWT_AllRequests.json`
2. Select environment
3. Get JWT by sending "Generate Test JWT"
4. Use any other request - auto-refresh applies!

### Option 2: Learning Track (if you have existing collection)
1. Read `COLLECTION_LEVEL_JWT_SETUP.md`
2. Add script to your collection's Pre-request Script
3. All your requests now have JWT auto-refresh

---

## ✅ Verification

After setup, send ANY request in collection and check:

**In Console (Ctrl+Alt+C):**
```
🔐 [COLLECTION] JWT Expiry Check - Applying to all requests
✓ JWT valid - proceeding with request
```

**Result:** Request sent with fresh JWT automatically! ✓

---

## 🎓 Technical Details (Optional)

### The Collection-Level Script Does:

1. **Reads JWT from environment**
   ```javascript
   let jwt = pm.environment.get("jwt");
   ```

2. **Decodes JWT payload**
   - Splits on dots: `header.payload.signature`
   - Base64 decodes payload
   - Reads `exp` claim (expiration)

3. **Checks if expired**
   - Gets `exp` timestamp
   - Compares with current time
   - If < 1 minute left → refresh

4. **Generates new JWT if needed**
   - Calls `/api/auth/test-jwt-generator`
   - Updates `jwt` environment variable
   - Logs to console

5. **Runs BEFORE every request**
   - Pre-request script runs first
   - JWT refreshed if needed
   - Request proceeds with fresh token

---

## 🎯 Comparison

| Approach | Setup Time | Maintenance | Best For |
|----------|-----------|-------------|----------|
| Individual Scripts | 2 min × 50 requests | High (change in 50 places) | Single tests |
| **Collection Script** | **2 min × 1 collection** | **Low (change in 1 place)** | **Complete API suite** |
| Pre-request Hook | 5 min | Medium | Team collaboration |

---

## 🚀 Next Steps

1. **Open Postman**
2. **Click Import**
3. **Select:** `Collection_GlobalJWT_AllRequests.json`
4. **Click Import**
5. **Select your environment** (top-right dropdown)
6. **Send "Generate Test JWT"** request
7. **Send any other request** - JWT auto-refresh applies! ✓

---

## 💡 One More Thing

If you want to understand the exact mechanics:
- **Read:** `COLLECTION_LEVEL_JWT_SETUP.md`
- **Copy script from:** `JWT_EXPIRY_CHECK_GUIDE.md`
- **Quick reference:** `GLOBAL_JWT_QUICK_START.md`

---

## 🎉 Success!

You now have:
✅ **Global JWT auto-refresh**
✅ **Applied to ALL requests**
✅ **No per-request script copying**
✅ **Centralized management**
✅ **Easy to maintain**

**Enjoy hassle-free API testing!** 🚀

---

**Questions?**

**"How do I add it to my requests?"**
→ Just add Cookie header: `access_token={{jwt}}`

**"Do I need to update the script for each request?"**
→ No! Collection script applies to all.

**"What if I want to skip JWT for one request?"**
→ Add conditional logic in that request's Pre-request Script

**"Can I use this with folders?"**
→ Yes! Collection script applies to all requests in all folders.

---

## 📝 Summary

**Traditional way:** Add script to every request ❌
**Better way:** Add script to collection once, applies to all ✅

**That's it!** 🎉

