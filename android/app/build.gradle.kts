plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.hypnosapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.hypnosapp"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }

}



dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    //firebase
    implementation("com.google.firebase:firebase-bom:32.3.1")
    implementation ("com.google.firebase:firebase-auth:22.1.2")
    implementation("com.google.firebase:firebase-database:20.2.2")
    implementation ("com.google.android.gms:play-services-auth:20.0.0")
    implementation ("com.google.firebase:firebase-core:20.0.1")
    implementation ("com.facebook.android:facebook-login:latest.release")
    //foto de perfil
    implementation("com.android.volley:volley:1.2.1")
    //recyclerview
    implementation("androidx.recyclerview:recyclerview:1.2.1")

}