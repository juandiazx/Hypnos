package com.example.hypnosapp.firebase;

import com.example.hypnosapp.model.Night;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseHelper {
    private static final String TAG = "FirebaseHelper";

    private final FirebaseFirestore db;

    public FirebaseHelper() {
        db = FirebaseFirestore.getInstance();
    }

    // Interfaz para manejar la carga exitosa de la Night o los errores
    public interface OnNightLoadedListener {
        void onNightLoaded(Night night);

        void onNightLoadError(Exception e);
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

    /*----------------------------------------------------------------------------------------------
        getAllNights() --> int pages, returns the number of pages that will be shown on the pager
    ------------------------------------------------------------------------------------------------*/
    public void getPagesFromAllNights(String userId, OnSuccessListener<Integer> successListener, OnFailureListener failureListener) {
        CollectionReference userNightsRef = db.collection("users").document(userId).collection("nightsData");

        userNightsRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int numberOfNights = task.getResult().size();
                        int pages = (int) Math.ceil((double) numberOfNights / 15);
                        successListener.onSuccess(pages);
                    } else {
                        Log.e(TAG, "Error getting nightsData documents", task.getException());
                        failureListener.onFailure(task.getException());
                    }
                });
    }

    /*----------------------------------------------------------------------------------------------
        int Page --> getFifteenNights() --> Fifteen Pages, if page equals 1, returns the last
        fifteen, else if page = 2, returns the nights from the last 16 to 30 days.
    ----------------------------------------------------------------------------------------------*/
    public void getFifteenNights(String userId, int page,
                                 final OnSuccessListener<List<Night>> successListener,
                                 final OnFailureListener failureListener) {
        CollectionReference nightsCollection = db.collection("users").document(userId).collection("nightsData");

        // Define the query to get the relevant nights
        Query query = nightsCollection.orderBy("date", Query.Direction.DESCENDING);

        // Execute the query
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Night> nightsList = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Convert each document to a Night object
                                Night night = document.toObject(Night.class);
                                nightsList.add(night);
                            }

                            // Sort the nightsList based on date (assuming it's a Date type)
                            Collections.sort(nightsList, (o1, o2) -> o2.getDate().compareTo(o1.getDate()));

                            // Determine the range of nights to return based on the page number
                            int startIdx = (page - 1) * 15;
                            int endIdx = startIdx + 15;

                            // Ensure the indices are within the bounds of the list
                            if (startIdx < nightsList.size()) {
                                endIdx = Math.min(endIdx, nightsList.size());
                                List<Night> selectedNights = nightsList.subList(startIdx, endIdx);
                                successListener.onSuccess(selectedNights);
                            } else {
                                // No nights found for the given page
                                successListener.onSuccess(Collections.emptyList());
                            }
                        } else {
                            failureListener.onFailure(task.getException());
                        }
                    }
                });
    }

    /*----------------------------------------------------------------------------------------------
                              UserID --> getLastNight() --> Night
    ----------------------------------------------------------------------------------------------*/
    public void getLastNight(String userId, final OnSuccessListener<List<Night>> successListener, final OnFailureListener failureListener) {
        CollectionReference nightsCollection = db.collection("users").document(userId).collection("nightsData");
        Query query = nightsCollection.orderBy("date", Query.Direction.DESCENDING);
        // Execute the query
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Night> nightsList = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Convert each document to a Night object
                                Night night = document.toObject(Night.class);
                                nightsList.add(night);
                            }
                            successListener.onSuccess(nightsList);
                        } else {
                            failureListener.onFailure(task.getException());
                        }
                    }
                });
    }

    /*----------------------------------------------------------------------------------------
                              String --> setIdealWakeUpHour()
    ----------------------------------------------------------------------------------------*/
    public void setIdealWakeUpHour(String userId, String selectedIdealWakeUpHour){
        DocumentReference userDocRef = db.collection("users").document(userId);

        Map<String, Object> wakeUpHour = new HashMap<>();
        wakeUpHour.put("preferences.goals.wakeUpTimeGoal", selectedIdealWakeUpHour);

        userDocRef.update(wakeUpHour)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "wakeUpTime updated successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating wakeUpTime", e));
    }

    /*----------------------------------------------------------------------------------------
                             getIdealWakeUpHour() --> String
    ----------------------------------------------------------------------------------------*/
    public void getIdealWakeUpHour(String userId, final OnSuccessListener<String> successListener, final OnFailureListener failureListener){
        DocumentReference userDocRef = db.collection("users").document(userId);

        userDocRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();

                            if(document.exists()) {
                                Map<String, Object> userData = document.getData();

                                if (userData != null && userData.containsKey("preferences")) {
                                    Map<String, Object> preferences = (Map<String, Object>) userData.get("preferences");

                                    if (preferences != null && preferences.containsKey("goals")) {
                                        Map<String, Object> goals = (Map<String, Object>) preferences.get("goals");

                                        if (goals != null && goals.containsKey("wakeUpTimeGoal")) {
                                            String wakeUpTimeGoal = (String) goals.get("wakeUpTimeGoal");
                                            successListener.onSuccess(wakeUpTimeGoal);
                                        }
                                    }
                                }
                            } else{
                                Log.d(TAG, "No wakeUpTimeGoal document found");
                                successListener.onSuccess(null);
                            }

                            }
                            else{
                                Log.e(TAG, "Error getting user document", task.getException());
                                failureListener.onFailure(task.getException());
                            }
                        }
        });
    }

    /*----------------------------------------------------------------------------------------
                             String --> setIdealRestTime()
    ----------------------------------------------------------------------------------------*/
    public void setIdealRestTime(String userId, String selectedIdealRestTime){
        DocumentReference userDocRef = db.collection("users").document(userId);

        Map<String, Object> idealRestTime = new HashMap<>();
        idealRestTime.put("preferences.goals.restTime", selectedIdealRestTime);

        userDocRef.update(idealRestTime)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "idealRestTime updated successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating wakeUpTime", e));
    }

    /*----------------------------------------------------------------------------------------
                             getIdealRestTime() --> String
    ----------------------------------------------------------------------------------------*/
    public void getIdealRestTime(String userId, final OnSuccessListener<String> successListener, final OnFailureListener failureListener){
        DocumentReference userDocRef = db.collection("users").document(userId);

        userDocRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();

                            if(document.exists()) {
                                Map<String, Object> userData = document.getData();

                                if (userData != null && userData.containsKey("preferences")) {
                                    Map<String, Object> preferences = (Map<String, Object>) userData.get("preferences");

                                    if (preferences != null && preferences.containsKey("goals")) {
                                        Map<String, Object> goals = (Map<String, Object>) preferences.get("goals");

                                        if (goals != null && goals.containsKey("restTime")) {
                                            String wakeUpTimeGoal = (String) goals.get("restTime");
                                            successListener.onSuccess(wakeUpTimeGoal);
                                        }
                                    }
                                }
                            } else{
                                Log.d(TAG, "No wakeUpTimeGoal document found");
                                successListener.onSuccess(null);
                            }

                        }
                        else{
                            Log.e(TAG, "Error getting user document", task.getException());
                            failureListener.onFailure(task.getException());
                        }
                    }
                });
    }

    /*----------------------------------------------------------------------------------------
                             bool --> setNotifications()
    ----------------------------------------------------------------------------------------*/
    public void setNotifications(String userId, boolean decision) {
        DocumentReference userDocRef = db.collection("users").document(userId);

        userDocRef
                .update("preferences.goals.sleepNotifications", decision)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "notifications updated successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating notifications", e));
    }

    /*----------------------------------------------------------------------------------------
                             getNotifications() --> bool
    ----------------------------------------------------------------------------------------*/
    public void getNotifications(String userId, final OnSuccessListener<Boolean> successListener, final OnFailureListener failureListener){
        DocumentReference userDocRef = db.collection("users").document(userId);

        userDocRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();

                            if(document.exists()) {
                                Map<String, Object> userData = document.getData();

                                if (userData != null && userData.containsKey("preferences")) {
                                    Map<String, Object> preferences = (Map<String, Object>) userData.get("preferences");

                                    if (preferences != null && preferences.containsKey("goals")) {
                                        Map<String, Object> goals = (Map<String, Object>) preferences.get("goals");

                                        if (goals != null && goals.containsKey("sleepNotifications")) {
                                            Boolean notifications = (Boolean) goals.get("sleepNotifications");
                                            successListener.onSuccess(notifications);
                                        }
                                    }
                                }
                            } else{
                                Log.d(TAG, "No wakeUpTimeGoal document found");
                                successListener.onSuccess(null);
                            }

                        }
                        else{
                            Log.e(TAG, "Error getting user document", task.getException());
                            failureListener.onFailure(task.getException());
                        }
                    }
                });
    }


}//class



    /*
    public void getLastNight(String userID, final OnNightLoadedListener listener) {

        CollectionReference nightsRef = db.collection("users").document(userID).collection("nightsData");

        // Consulta para obtener la noche más reciente basada en la fecha
        Query query = nightsRef.orderBy("date", Query.Direction.DESCENDING).limit(1);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (Night night : task.getResult().toObjects(Night.class)) {
                        //Date date = night.getDate();
                        Log.d(TAG, "SE CONTACTA CON FIREBASE");

                        listener.onNightLoaded(night);
                    }
                } else {
                    Log.e(TAG, "Error getting documents: ", task.getException());
                    listener.onNightLoadError(task.getException());
                }
            }
        });



    }

     */



