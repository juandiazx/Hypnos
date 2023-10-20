package com.example.hypnosapp;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class HypnosInicializacion extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        // Perform any application-wide setup specific to the "Hypnos" app here.
        // Initialize Firebase or other necessary tasks.
    }
}
