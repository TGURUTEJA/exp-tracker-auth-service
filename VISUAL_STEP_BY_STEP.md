# 📸 Visual Step-by-Step Guide: Global JWT Setup

## 🎯 Goal: Apply JWT script to ALL requests at once

---

## ✅ Solution: Collection-Level Pre-request Script

This is the **EASIEST** way. Let me show you step-by-step.

---

## 🚀 Method 1: Import Pre-configured Collection (FASTEST)

### Step 1: Locate the File
```
File Path:
C:\Users\tguru\OneDrive\Desktop\SplitProject\exp-tracker-auth-service\
Collection_GlobalJWT_AllRequests.json
```

### Step 2: Open Postman
```
┌──────────────────────────────┐
│       POSTMAN WINDOW         │
│                              │
│  [Import]   [+]   [...]      │  ← Click Import
│                              │
└──────────────────────────────┘
```

### Step 3: Click Import Button
```
┌──────────────────────────────┐
│  Click here ↓                │
│  ┌────────────────────────┐  │
│  │ Import                 │  │
│  └────────────────────────┘  │
└──────────────────────────────┘
```

### Step 4: Select the File
```
┌──────────────────────────────┐
│   Import From              │
│   ┌─────────────────────┐   │
│   │ [File] [Link] [Raw]│   │ ← Choose File
│   └─────────────────────┘   │
│                              │
│   [Browse...]                │
│                              │
│   Collection_GlobalJWT...    │ ← Your file
│                              │
│   [Import]                   │
└──────────────────────────────┘
```

### Step 5: Click Import
```
You'll see:

✅ Collection imported successfully!

Your collection now appears in left sidebar:
├─ Collections
│  └─ All APIs with Global JWT Auto-Refresh
│     ├─ 1. Generate Test JWT
│     ├─ 2. Create Category
│     ├─ 3. Get Categories
│     ├─ 4. Update Category
│     └─ 5. Delete Category
```

### Step 6: Set Environment Variables
```
Top right of Postman:
┌──────────────────────────────┐
│ Environment: [My Env] ▼      │ ← Select environment
└──────────────────────────────┘

Environment should have:
base_url = "http://localhost:8082"
jwt = "" (will auto-populate)
user_id = "1"
```

### Step 7: Generate First JWT
```
1. Click "1. Generate Test JWT" request
2. Click [Send] button

Console output:
🔐 [COLLECTION] JWT Expiry Check...
✓ New JWT generated and set globally
```

### Step 8: Use Any Other Request
```
1. Click "2. Create Category" (or any request)
2. Click [Send] button

Console output:
🔐 [COLLECTION] JWT Expiry Check...
✓ JWT valid - proceeding with request
```

**✅ DONE!** All requests now auto-refresh JWT!

---

## 🎯 Method 2: Add to Existing Collection (If You Have One)

If you want to use your own collection instead of importing:

### Step 1: Find Your Collection
```
Postman Left Sidebar:
Collections
├─ [Your Collection Name] ← Right-click here
│  ├─ Request 1
│  ├─ Request 2
│  └─ Request 3
```

### Step 2: Click Collection Menu
```
Right-click on collection name:

┌────────────────┐
│ Edit           │ ← Click this
│ Duplicate      │
│ Export         │
│ Share          │
│ Delete         │
└────────────────┘
```

### Step 3: Collection Settings Opens
```
┌─────────────────────────────────────┐
│  Collection Name: "My APIs"         │
│                                     │
│  Tabs:                              │
│  [Info] [Pre-request Script] [Tests]│
│                        ↑             │
│                  Click this          │
├─────────────────────────────────────┤
│                                     │
│  [Large text area]                  │
│   (Empty - paste script here)       │
│                                     │
│            [Save]                   │
├─────────────────────────────────────┤
```

### Step 4: Copy Script
Get the script from:
```
File: COLLECTION_LEVEL_JWT_SETUP.md
(Copy the entire JavaScript code)
```

### Step 5: Paste Script
```
┌─────────────────────────────────────┐
│  [Pre-request Script] ← In this tab │
│                                     │
│  let jwt = pm.environment.get...   │
│  function decodeJWT(token) {        │
│      ... (all the code)             │
│  }                                  │
│                                     │
│  console.log("🔐 [COLLECTION]...");│
│            [Save] ← Click to save  │
│                                     │
└─────────────────────────────────────┘
```

### Step 6: Click Save
```
After clicking Save:

✓ Script is now applied to entire collection!

Every request will automatically:
- Check JWT expiration
- Refresh if needed
- Proceed with fresh JWT
```

---

## 🎯 Method 3: Manual Setup for One Request (Testing)

If you want to try on just ONE request first:

### Step 1: Open a Request
```
Collections
└─ My Collection
   └─ My Request ← Click this
```

### Step 2: Go to Pre-request Script Tab
```
Request View:
┌─────────────────────────────┐
│ Pre-request Script │ Tests  │ ← Click here
│     ↓                       │
│  [text area]                │
│  (empty)                    │
└─────────────────────────────┘
```

### Step 3: Paste Script
```
Copy script from COLLECTION_LEVEL_JWT_SETUP.md
Paste into [text area]
```

### Step 4: Send Request
```
You'll see in console:
🔐 JWT Expiry Check...
✓ JWT valid - proceeding with request
```

---

## 📊 Comparison: Three Methods

```
Method 1: Import Collection
├─ Time: 30 seconds
├─ Setup: Click Import
├─ Best for: Getting started fast
└─ Result: Everything ready!

Method 2: Add to Your Collection
├─ Time: 5 minutes
├─ Setup: Paste script once
├─ Best for: Your existing requests
└─ Result: All requests get JWT management

Method 3: Single Request Script
├─ Time: 2 minutes
├─ Setup: Paste script in one request
├─ Best for: Testing before full setup
└─ Result: One request gets JWT management
```

---

## 🎯 Best Approach

```
START HERE ↓

Method 1: Import Collection
    ↓
    Works? ✓
    ↓
YES → Skip to "Verify It Works"

NO → Try Method 2: Add to Your Collection
    ↓
    Need help? → Read COLLECTION_LEVEL_JWT_SETUP.md
```

---

## ✅ Verify It Works

### Check 1: Request Sends Successfully
```
1. Send any request in collection
2. Response should come back
```

### Check 2: Console Shows Output
```
1. Press Ctrl+Alt+C (open console)
2. You should see:

🔐 [COLLECTION] JWT Expiry Check...
📋 JWT expiration check:
✓ JWT valid - proceeding with request
```

### Check 3: JWT Auto-Refresh Works
```
1. Wait 59 minutes (or modify script to test sooner)
2. Send another request
3. Console should show:

⏰ JWT is expired or about to expire!
🔄 Generating new JWT...
✓ New JWT generated and set globally
```

---

## 🎓 Understanding the Flow

```
Every time you send a request:

1. PREREQUEST SCRIPT RUNS (collection-level)
   ├─ Gets JWT from environment
   ├─ Checks expiration
   ├─ Refreshes if needed
   └─ Updates jwt {{variable}}
   
2. YOUR REQUEST SENDS
   ├─ Uses {{jwt}} in Cookie header
   ├─ API receives fresh JWT
   └─ Response returned
   
3. DONE ✓
   └─ You got your data with zero JWT management!
```

---

## 🛠️ After Setup: Using It

### To Use with Existing Requests:
```
For each request:

1. Add header:
   Cookie: access_token={{jwt}}

2. That's it!
   (JWT auto-refresh already applied at collection level)
```

### To Add New Requests:
```
1. Create new request in collection
2. Add Cookie header: access_token={{jwt}}
3. Done! JWT management automatic!
```

### To View JWT Status:
```
Before sending request:
Console (Ctrl+Alt+C) → Clear
Send request
Console shows: JWT expiration check + result
```

---

## 🚀 Starting the Process

### Pick Your Method:

**Fast Track (30 sec):**
```
1. Import Collection_GlobalJWT_AllRequests.json
2. Send requests
3. Done!
```

**Learning Track (5 min):**
```
1. Read: COLLECTION_LEVEL_JWT_SETUP.md
2. Read: GLOBAL_JWT_QUICK_START.md
3. Add script to your collection
4. Done!
```

**Manual Track (10 min):**
```
1. Open your collection
2. Right-click → Edit
3. Pre-request Script tab
4. Paste entire script
5. Click Save
6. Done!
```

---

## ✨ The Result

```
Before (❌ Manual):
Request 1 → Copy + Paste script
Request 2 → Copy + Paste script
Request 3 → Copy + Paste script
... x50 times! 😫

After (✅ Collection-level):
Collection → Add script ONCE
All Requests → Automatically use it! ✨
```

---

## 🎉 You're Ready!

Everything is set up. Now:

1. **Import the collection** OR **add script to your collection**
2. **Set environment variables**
3. **Send requests**
4. **JWT auto-refresh handles the rest!**

**No more manual token management!** 🚀

---

## 📁 Files Reference

| Need | File |
|------|------|
| Just import | `Collection_GlobalJWT_AllRequests.json` |
| See how | `GLOBAL_JWT_QUICK_START.md` |
| Detailed setup | `COLLECTION_LEVEL_JWT_SETUP.md` |
| Technical details | `JWT_EXPIRY_CHECK_GUIDE.md` |
| The script | `postman-jwt-expiry-check.js` |

---

**Ready? Let's go!** 🚀

Next Step:
```
1. Open Postman
2. Click Import
3. Select Collection_GlobalJWT_AllRequests.json
4. Send requests and watch JWT magic happen! ✨
```

