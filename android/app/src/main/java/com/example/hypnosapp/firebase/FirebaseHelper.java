package com.example.hypnosapp.firebase;

import com.example.hypnosapp.model.User;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FirebaseHelper {
    private static final String TAG = "FirebaseHelper";

    private final FirebaseFirestore db;

    public FirebaseHelper() {
        db = FirebaseFirestore.getInstance();
    }

    /*----------------------------------------------------------------------------------------
                getClock() --> HASHMAP[String hour, songLocation
                              bool isSongGradual, isClockAutomatic]
    ----------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------
                getClock() --> HASHMAP[String hour, songLocation
                              bool isSongGradual, isClockAutomatic]
    ----------------------------------------------------------------------------------------*/
    public void getClock(String userId, final OnSuccessListener<Map<String, Object>> successListener, final OnFailureListener failureListener) {
        // Retrieve clock settings from Firestore and return as a HashMap
        DocumentReference userDocRef = db.collection("users").document(userId);

        userDocRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // DocumentSnapshot data represents the user document
                                Map<String, Object> userData = document.getData();

                                if (userData != null && userData.containsKey("preferences")) {
                                    // Extract the preferences map from the user document
                                    Map<String, Object> preferences = (Map<String, Object>) userData.get("preferences");

                                    // Extract the clockSettings map from preferences
                                    if (preferences != null && preferences.containsKey("clockSettings")) {
                                        Map<String, Object> clockSettings = (Map<String, Object>) preferences.get("clockSettings");
                                        successListener.onSuccess(clockSettings);
                                        return;
                                    }
                                }
                            }
                            Log.d(TAG, "No clockSettings map found");
                            successListener.onSuccess(Collections.emptyMap()); // Return an empty map if clockSettings document doesn't exist
                        } else {
                            Log.e(TAG, "Error getting user document", task.getException());
                            failureListener.onFailure(task.getException());
                        }
                    }
                });
    }


    /*----------------------------------------------------------------------------------------
                   String hour, songLocation     ----> setClock()
               bool isSongGradual, isClockAutomatic
    ----------------------------------------------------------------------------------------*/
    public void setClock(String userId, String hour, String songLocation, boolean isSongGradual, boolean isClockAutomatic) {
        // Update clock settings in Firestore
        DocumentReference userDocRef = db.collection("users").document(userId);

        Map<String, Object> clockSettings = new HashMap<>();
        clockSettings.put("alarmHour", hour);
        clockSettings.put("isAutomatic", isClockAutomatic);
        clockSettings.put("isGradual", isSongGradual);
        clockSettings.put("toneLocation", songLocation);

        userDocRef
                .update("preferences.clockSettings", clockSettings)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Clock settings updated successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating clock settings", e));
    }

    /*----------------------------------------------------------------------------------------
                getLightSettings() --> returns WAR, COL or AUT depending on which
                option has been selected before.
    ----------------------------------------------------------------------------------------*/
    public void getLightSettings(String userId, final OnSuccessListener<String> successListener, final OnFailureListener failureListener) {
        // Retrieve light settings from Firestore and return the selectedLightCode
        DocumentReference userDocRef = db.collection("users").document(userId);

        userDocRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // DocumentSnapshot data represents the user document
                                Map<String, Object> userData = document.getData();

                                if (userData != null && userData.containsKey("preferences")) {
                                    // Extract the preferences map from the user document
                                    Map<String, Object> preferences = (Map<String, Object>) userData.get("preferences");

                                    // Extract the lightSettings string from preferences
                                    if (preferences != null && preferences.containsKey("lightSettings")) {
                                        String lightSettings = (String) preferences.get("lightSettings");
                                        successListener.onSuccess(lightSettings);
                                        return;
                                    }
                                }
                            }
                            Log.d(TAG, "No lightSettings document found");
                            successListener.onSuccess(null); // Return null if lightSettings document doesn't exist
                        } else {
                            Log.e(TAG, "Error getting user document", task.getException());
                            failureListener.onFailure(task.getException());
                        }
                    }
                });
    }

    /*----------------------------------------------------------------------------------------
               setLightAuto() --> Sets selectedLightCode to AUT in the database
    ----------------------------------------------------------------------------------------*/
    public void setLightAuto(String userId) {
        // Update lightSettings in Firestore to AUT
        updateLightSettings(userId, "AUT");
    }
    /*----------------------------------------------------------------------------------------
               setLightWarm() --> Sets selectedLightCode to WAR in the database
    ----------------------------------------------------------------------------------------*/
    public void setLightWarm(String userId) {
        // Update lightSettings in Firestore to WAR
        updateLightSettings(userId, "WAR");
    }
    /*----------------------------------------------------------------------------------------
               setLightCold() --> Sets selectedLightCode to COL in the database
    ----------------------------------------------------------------------------------------*/
    public void setLightCold(String userId) {
        // Update lightSettings in Firestore to COL
        updateLightSettings(userId, "COL");
    }

    /*----------------------------------------------------------------------------------------
            updateLightSettings() --> Sets selectedLightCode to the code received
    ----------------------------------------------------------------------------------------*/
    public void updateLightSettings(String userId, String selectedLightCode) {
        DocumentReference userDocRef = db.collection("users").document(userId);

        userDocRef
                .update("preferences.lightSettings", selectedLightCode)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Light settings updated successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating light settings", e));
    }
}


    /*

    functions for nightHistory

        int Page --> getFifteenNights() --> Fifteen Pages, if page = 0, returns the last fifteen,
        else if page = 1, returns the nights of 16 to 30 last days.

        getAllNights() --> int pages, returns the number of pages that will be shown on the pager

    */
