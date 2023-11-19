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

    private FirebaseFirestore db;

    public FirebaseHelper() {
        db = FirebaseFirestore.getInstance();
    }

    /*----------------------------------------------------------------------------------------
                getClock() --> HASHMAP[String hour, songLocation
                              bool isSongGradual, isClockAutomatic]
    ----------------------------------------------------------------------------------------*/
    public void getClock(String userId, final OnSuccessListener<Map<String, Object>> successListener, final OnFailureListener failureListener) {
        // Retrieve clock settings from Firestore and return as a HashMap
        DocumentReference userDocRef = db.collection("user").document(userId);

        userDocRef.collection("preferencesData")
                .document("clockSettings")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // DocumentSnapshot data represents the clockSettings map
                                Map<String, Object> clockSettings = document.getData();
                                successListener.onSuccess(clockSettings);
                            } else {
                                Log.d(TAG, "No such document");
                                successListener.onSuccess(Collections.emptyMap()); // Return an empty map if document doesn't exist
                            }
                        } else {
                            Log.e(TAG, "Error getting clock settings", task.getException());
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
        DocumentReference userDocRef = db.collection("user").document(userId);

        Map<String, Object> clockSettings = new HashMap<>();
        clockSettings.put("isAutomatic", isClockAutomatic);
        clockSettings.put("isGradual", isSongGradual);
        clockSettings.put("toneLocation", songLocation);

        userDocRef.collection("preferencesData")
                .document("clockSettings")
                .set(clockSettings)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Clock settings updated successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating clock settings", e));
    }


    /*----------------------------------------------------------------------------------------
                getLightSettings() --> returns WAR, COL or AUT depending on which
                option has been selected before.
    ----------------------------------------------------------------------------------------*/
    public void getLightSettings(String userId, final OnSuccessListener<String> successListener, final OnFailureListener failureListener) {
        final String[] selectedLightCode = {null}; // Using an array to make it effectively final

        // Retrieve light settings from Firestore and return the selectedLightCode
        DocumentReference userDocRef = db.collection("user").document(userId);

        userDocRef.collection("preferencesData")
                .document("lightSettings")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // DocumentSnapshot data represents the lightSettings value
                                selectedLightCode[0] = document.getString("lightSettings");
                                successListener.onSuccess(selectedLightCode[0]);
                            } else {
                                Log.d(TAG, "No such document");
                                successListener.onSuccess(null); // Return null if document doesn't exist
                            }
                        } else {
                            Log.e(TAG, "Error getting light settings", task.getException());
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
    private void updateLightSettings(String userId, String selectedLightCode) {
        DocumentReference userDocRef = db.collection("user").document(userId);

        Map<String, Object> lightSettings = new HashMap<>();
        lightSettings.put("lightSettings", selectedLightCode);

        userDocRef.collection("preferencesData")
                .document("lightSettings")
                .set(lightSettings)
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
