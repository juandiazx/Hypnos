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

    packagingOptions {
        resources {
            excludes.add("META-INF/DEPENDENCIES")
        }
    }

}



dependencies {
    implementation ("com.google.firebase:firebase-storage")

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")


    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("androidx.core:core:1.12.0" ) // Ajusta la versión según la última disponible
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    //firebase
    implementation ("com.google.firebase:firebase-auth:22.3.0")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation ("com.google.android.gms:play-services-auth:20.7.0")
    implementation ("com.google.firebase:firebase-core:21.1.1")
   implementation ("com.facebook.android:facebook-login:latest.release")
    implementation("com.google.firebase:firebase-firestore:24.9.1")
    implementation ("com.google.firebase:firebase-bom:32.5.0")
    //foto de perfil
    implementation("com.android.volley:volley:1.2.1")
    //recyclerview
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    //pdfExporter
    implementation ("org.apache.pdfbox:pdfbox:2.0.27")



}