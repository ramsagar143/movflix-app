plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.movflix.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.movflix.app.v2" // Package ID change kiya taaki purana cache clear ho sake
        minSdk = 24
        targetSdk = 34
        versionCode = 3                      // Version badha diya
        versionName = "3.0"
        
        // Render Backend URL
        buildConfigField("String", "BACKEND_URL", "\"https://movflix-16ow.onrender.com/\"")
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("io.coil-kt:coil:2.6.0")

    // 👇 ADDED GLIDE DEPENDENCIES (Yeh add na hone ki wajah se crash ho raha tha) 👇
    implementation("com.github.bumptech.glide:glide:4.16.0")
}
