# 🎬 MovFlix — TMDB Movie/Show/Anime App (Phone-Only Build Guide)

Netflix-style Android app: Home, Shows, Movie, Anime tabs + Search + Detail page with official YouTube trailers.
Backend TMDB token ko chhupata hai aur caching karta hai — app kahin bhi, kisi bhi network se chalega.

---

## ⚠️ Pehle Ye 2 Kaam Karo

1. **TMDB token regenerate karo** — agar token kahin share ho gaya ho to
   TMDB → Settings → API → "Regenerate" dabao. Purana token mat use karna.
2. Ye app **legal catalog + trailers** dikhata hai. Copyrighted movies ka pirated
   streaming isme nahi hai (aur main wo banake bhi nahi de sakta — illegal hai).

---

## 🚀 STEP 1: Backend Deploy Karna (Render.com — FREE)

Phone ke browser me hi ho jayega:

1. **GitHub account** banao (github.com → Sign up)
2. GitHub pe **New Repository** banao naam `movflix-server`
3. Repo kholo → **"uploading an existing file"** link pe jao
4. `server/` folder ki files (`index.js`, `package.json`) upload karo → Commit changes
5. **render.com** kholo → **Sign up with GitHub**
6. Dashboard → **New → Web Service** → apna `movflix-server` repo connect karo
7. Settings:
   - **Build Command:** `npm install`
   - **Start Command:** `node index.js`
   - **Instance Type:** Free
8. **Environment Variables** me add karo:
   - Key: `TMDB_TOKEN`  Value: (apna naya TMDB Read Access Token)
9. **Deploy** dabao. 2-3 min me live ho jayega.
10. Upar **URL** milega jaise: `https://movflix-server.onrender.com`
11. Test karo: browser me `https://movflix-server.onrender.com/trending` kholo
    → JSON dikha to backend ✅ ready

⚠️ Render free tier 15 min inactive rehne pe so jata hai — pehli request pe
30-40 sec lag sakti hai, app me retry button bana hai isliye.

---

## 📱 STEP 2: App Code Me Backend URL Daalna

ZIP kholo → `app/build.gradle.kts` file me ye line dhundo:

```
buildConfigField("String", "BACKEND_URL", "\"https://YOUR-BACKEND.onrender.com/\"")
```

`YOUR-BACKEND` ki jagah apna Render URL daalo (slash ke saath), jaise:

```
buildConfigField("String", "BACKEND_URL", "\"https://movflix-server.onrender.com/\"")
```

---

## 🔨 STEP 3: APK Banana — GitHub Codespaces (Phone Browser Se)

Computer ki zaroorat NAHI. Sab phone pe:

1. GitHub pe **New Repository** banao naam `movflix-app` (Public)
2. Repo kholo → **"uploading an existing file"** → ZIP ka pura content upload karo:
   - `settings.gradle.kts`, `build.gradle.kts`, `gradle.properties`
   - `.devcontainer/` folder (devcontainer.json)
   - `app/` folder (poora)
   - **Commit changes**
3. Repo page pe **"Code"** button → **"Codespaces"** tab → **"Create codespace on main"**
   - Codespace khulne me 3-5 min (Android SDK auto-install hoga)
4. Terminal kholo (menu → Terminal → New Terminal) aur ye commands chalao:

```bash
cd app
gradle wrapper --gradle-version 8.7
chmod +x gradlew
./gradlew assembleDebug
```

   - Pehli baar 10-20 min lag sakta hai (Gradle + dependencies download honge)
   - `BUILD SUCCESSFUL` dikha = APK ready ✅

5. Explorer (left side files) me jao:
   `app/build/outputs/apk/debug/` → `app-debug.apk` pe **right-click → Download**
6. Phone me **Files/Downloads** kholo → `app-debug.apk` pe tap karo → **Install**
   - Play Protect warning aayega → **"Install anyway"** (ye normal hai,
     kyunki ye debug-signed APK hai, Play Store wali nahi)

7. App kholo → Home/Shows/Movie/Anime/Search sab test karo 🎉

---

## 🛠 Troubleshooting

| Problem | Solution |
|---|---|
| `SDK location not found` | Terminal me `echo $ANDROID_HOME` chalao. Khali aaye to: `export ANDROID_HOME=/usr/local/android-sdk` |
| Backend se data nahi aa raha | Browser me `https://your-url.onrender.com/trending` kholo check karne ke liye |
| Codespace band ho gaya | Free quota khatam — next month reset hota hai, ya multiple GitHub accounts (not recommended) |
| Build error | Poora error message copy karke batao, main fix kar dunga |

---

## 🚀 Aage Kya Kar Sakte Ho (Advanced)

- **Apna icon/name:** `app/src/main/res/mipmap/` icons replace karo, `strings.xml` me app_name badlo
- **Release APK:** `./gradlew assembleRelease` (Play Store ke liye AAB chahiye: `./gradlew bundleRelease`)
- **Custom domain:** Render pe apna domain lagao, phir wahi URL app me daalo
- **Room database:** Offline favorites save karne ke liye

---

## ❓ App Kaise Kaam Karta Hai

```
Android App  →  Render Backend  →  TMDB API
 (Retrofit)     (Node/Express)    (official data)
                     ↓
              10-min cache
```

TMDB token sirf server pe rehta hai — APK khol ke bhi koi token nahi nikaal sakta.
