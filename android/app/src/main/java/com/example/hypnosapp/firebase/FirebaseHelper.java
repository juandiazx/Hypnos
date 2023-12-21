package com.example.hypnosapp.firebase;

import com.bumptech.glide.Glide;
import com.example.hypnosapp.mainpage.DiaFragment1;
import com.example.hypnosapp.mainpage.DiaFragment3;
import com.example.hypnosapp.model.Night;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import org.apache.commons.lang3.time.DateUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.example.hypnosapp.model.Store;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.Random;

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

    public interface OnUserExistsListener {
        void onUserExists(boolean exists);
    }

    public interface FamilyAccessIndexCallback {
        void onFamilyAccessIndexGenerated(int familyAccessIndex);
    }
    /*----------------------------------------------------------------------------------------
                String userId ---> checkIfUserExists() --> true if user exists on the user
                collection.
    ----------------------------------------------------------------------------------------*/
    public void checkIfUserExists(String userId, OnUserExistsListener listener) {
        DocumentReference userDocRef = db.collection("users").document(userId);
        userDocRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    boolean exists = documentSnapshot.exists();
                    listener.onUserExists(exists);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error checking if user exists", e);
                    listener.onUserExists(false); // Assume user doesn't exist in case of error
                });
    }
    /*----------------------------------------------------------------------------------------
                String userId, nombre, ---> addUserToUsers() --> adds the user to the user
                collection.
    ----------------------------------------------------------------------------------------*/
    public void addUserToUsers(String userId, String nombre, String email, String fechaNacimiento, int familyCode) {

        Map<String, Object> userData = new HashMap<>();
        userData.put("name", nombre);
        userData.put("birth", fechaNacimiento);
        userData.put("email", email);
        userData.put("familyAcessCode", Integer.toString(familyCode));

        db.collection("users")
                .document(userId)
                .set(userData)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "User added to collection successfully"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error adding user to collection", e));
    }
    /*----------------------------------------------------------------------------------------
                String userID ---> setDefaultSettings() --> stores a default preset on
                the new user's settings
    ----------------------------------------------------------------------------------------*/
    public void setDefaultPreferences(String userId) {

        Map<String, Object> defaultClockSettings = new HashMap<>();
        defaultClockSettings.put("isWithVibrations", true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            defaultClockSettings.put("toneLocation", "content://media/internal/audio/media/36");
        } else {
            defaultClockSettings.put("toneLocation", "content://media/external_primary/audio/media/1000000036?title=Fresh%20Start&canonical=1");
        }

        Map<String, Object> defaultGoals = new HashMap<>();
        defaultGoals.put("goBedTime", "22:00");
        defaultGoals.put("restTime", "8:00");
        defaultGoals.put("sleepNotifications", true);
        defaultGoals.put("wakeUpTimeGoal", "6:00");

        String defaultLightSetting = "AUT";

        DocumentReference userDocRef = db.collection("users").document(userId);

        Map<String, Object> defaultSettings = new HashMap<>();
        defaultSettings.put("clockSettings", defaultClockSettings);
        defaultSettings.put("lightSettings", defaultLightSetting);
        defaultSettings.put("goals", defaultGoals);

        // here is where you gotta modify
        userDocRef
                .update("preferences", defaultSettings)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Default preferences set successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Error setting default preferences", e));

    }

    public void setIncrementalFamilyID(FamilyAccessIndexCallback callback) {
//        CollectionReference usersCollectionRef = db.collection("users");
//        usersCollectionRef.orderBy("familyAccessCode", Query.Direction.DESCENDING).limit(1)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        QuerySnapshot querySnapshot = task.getResult();
//                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
//                            // Obtiene el primer documento (el de mayor 'familyAccessCode')
//                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
//
//                            // Verifica si familyAccessCode no es nulo
//                            Object familyAccessCodeObj = documentSnapshot.get("familyAccessCode");
//                            if (familyAccessCodeObj != null) {
//                                long lastFamilyAccessCode = (long) familyAccessCodeObj;
//                                long newFamilyAccessIndex = lastFamilyAccessCode + 1;
//
//                                // Llama al callback con el nuevo familyAccessIndex
//                                if (callback != null) {
//                                    callback.onFamilyAccessIndexGenerated(newFamilyAccessIndex);
//                                }
//                            } else {
//                                // Manejar el caso en el que familyAccessCode es nulo
//                                // Puedes asignar un valor predeterminado, lanzar una excepción, etc.
//                            }
//                        }
//                        // Genera un nuevo familyAccessIndex para el nuevo usuario
//                    } else {
//                        // Maneja la excepción si la consulta no es exitosa
//                        Exception exception = task.getException();
//                        if (exception != null) {
//                            // Maneja la excepción
//                        }
//                    }
//                });
        int lowerBound = 10000000;
        int upperBound = 99999999;
        Random random = new Random();
        int newFamilyAccessIndex = lowerBound + (int) (random.nextDouble() * (upperBound - lowerBound + 1));
        callback.onFamilyAccessIndexGenerated(newFamilyAccessIndex);
    }

    /*----------------------------------------------------------------------------------------
            String userID ---> setEmptyNights() --> stores a default preset of
            nights which will tell the user that no night has been monitorized before
    ----------------------------------------------------------------------------------------*/
    public void setEmptyNights(String userId) {
        // Create three empty nights for today, yesterday, and the day before yesterday
        for (int i = 0; i < 3; i++) {
            // Calculate the date for each night
            Date nightDate = DateUtils.addDays(new Date(), -i);

            // Create an empty night with the required fields
            Night emptyNight = new Night(nightDate, "No night has been monitorized yet", 0, 0, 0);

            // Set the date for the empty night
            emptyNight.setDate(nightDate);

            // Create a subcollection called "nightsData" and add the empty night
            db.collection("users")
                    .document(userId)
                    .collection("nightsData")
                    .add(emptyNight)
                    .addOnSuccessListener(documentReference -> Log.d("Firestore", "Empty night added successfully"))
                    .addOnFailureListener(e -> Log.e("Firestore", "Error adding empty night", e));
        }
    }

    /*----------------------------------------------------------------------------------------
                getClock() --> HASHMAP[String hour, songLocation
                              , isClockAutomatic]
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
               String toneLocation, isWithVibrations
    ----------------------------------------------------------------------------------------*/
    public void setClock(String userId, String songLocation, boolean isWithVibrations) {
        // Update clock settings in Firestore
        DocumentReference userDocRef = db.collection("users").document(userId);

        Map<String, Object> clockSettings = new HashMap<>();
        clockSettings.put("isWithVibrations", isWithVibrations);
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

    /*----------------------------------------------------------------------------------------
                         Date: From, Date: To --> searchNights()
    ----------------------------------------------------------------------------------------*/
    public void searchNights(String userId, String fromDate, String toDate,final OnSuccessListener<List<Night>> successListener,
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
                            List<Night>filteredNights = filterNights(nightsList, fromDate, toDate);
                            successListener.onSuccess(filteredNights);
                        }
                        else{
                            failureListener.onFailure(task.getException());
                        }
                    }
                });
    }

    /*----------------------------------------------------------------------------------------
                         Date: From, Date: To, List --> filterNights()
    ----------------------------------------------------------------------------------------*/
    private List<Night> filterNights(List<Night> nightList, String fromDate, String toDate) {

        List<Night> filteredList = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date fromDateTransformed;
        Date toDateTransformed;

        try {
            fromDateTransformed = sdf.parse(fromDate);
            toDateTransformed = sdf.parse(toDate);

            // Establecer la hora, minutos, segundos y milisegundos a cero para fromDateTransformed
            Calendar calFrom = Calendar.getInstance();
            calFrom.setTime(fromDateTransformed);
            calFrom.set(Calendar.HOUR_OF_DAY, 0);
            calFrom.set(Calendar.MINUTE, 0);
            calFrom.set(Calendar.SECOND, 0);
            calFrom.set(Calendar.MILLISECOND, 0);
            fromDateTransformed = calFrom.getTime();

            // Establecer la hora, minutos, segundos y milisegundos a cero para toDateTransformed
            Calendar calTo = Calendar.getInstance();
            calTo.setTime(toDateTransformed);
            calTo.set(Calendar.HOUR_OF_DAY, 23);
            calTo.set(Calendar.MINUTE, 59);
            calTo.set(Calendar.SECOND, 59);
            calTo.set(Calendar.MILLISECOND, 999);
            toDateTransformed = calTo.getTime();

            for (Night night : nightList) {
                Date nightsDate = night.getDate();

                // Establecer la hora, minutos, segundos y milisegundos a cero para nightsDate
                Calendar calNights = Calendar.getInstance();
                calNights.setTime(nightsDate);
                calNights.set(Calendar.HOUR_OF_DAY, 0);
                calNights.set(Calendar.MINUTE, 0);
                calNights.set(Calendar.SECOND, 0);
                calNights.set(Calendar.MILLISECOND, 0);
                nightsDate = calNights.getTime();

                if ((nightsDate.equals(fromDateTransformed) || nightsDate.after(fromDateTransformed))
                        && (nightsDate.equals(toDateTransformed) || nightsDate.before(toDateTransformed))) {
                    filteredList.add(night);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "Error in converting String dates to Date dates");
        }

        return filteredList;
    }

    /*----------------------------------------------------------------------------------------------
                                    getLastNightWithListener() --> Night
    ----------------------------------------------------------------------------------------------*/
    public void getLastNightWithListener(String userId, final OnSuccessListener<Night> successListener, final OnFailureListener failureListener, final DiaFragment3.NightDataChangeListener dataChangeListener) {

        CollectionReference nightsCollection = db.collection("users").document(userId).collection("nightsData");

        Query query = nightsCollection.orderBy("date", Query.Direction.DESCENDING);

        ListenerRegistration listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    failureListener.onFailure(e);
                    return;
                }

                if (snapshot != null && !snapshot.isEmpty()) {
                    List<Night> nightsList = new ArrayList<>();

                    for (QueryDocumentSnapshot document : snapshot) {
                        Night night = document.toObject(Night.class);
                        nightsList.add(night);
                    }

                    Night lastNight = searchLastNight(nightsList);
                    successListener.onSuccess(lastNight);

                    if (dataChangeListener != null) {
                        dataChangeListener.onDataChange(lastNight);
                    }
                } else {
                    successListener.onSuccess(null);
                }
            }
        });
    }

    /*----------------------------------------------------------------------------------------------
                     List --> searchLastNight() --> Night
    ----------------------------------------------------------------------------------------------*/
    private static Night searchLastNight(List<Night> nights) {
        Night thisNight = null;
        for (Night night : nights) {
            if (thisNight == null || night.getDate().compareTo(thisNight.getDate()) > 0) {
                thisNight = night;
            }
        }
        if (thisNight == null) {
            Log.d(TAG, "No hay registros de la noche del día antes.");
        }
        return thisNight;
    }

    /*----------------------------------------------------------------------------------------------
                                    getSecondLastNight() --> Night
    ----------------------------------------------------------------------------------------------*/
    public void getSecondLastNight(String userId, final OnSuccessListener<Night> successListener, final OnFailureListener failureListener){
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
                            Night secondLastNight = searchSecondLastNight(nightsList);
                            successListener.onSuccess(secondLastNight);
                        }
                        else{
                            failureListener.onFailure(task.getException());
                        }
                    }
                });
    }
    /*----------------------------------------------------------------------------------------------
                     List --> searchSecondLastNight() --> Night
    ----------------------------------------------------------------------------------------------*/
    private static Night searchSecondLastNight(List<Night> nights) {
        Night mostRecent = null;
        Night secondMostRecent = null;

        for (Night night : nights) {
            if (mostRecent == null || night.getDate().compareTo(mostRecent.getDate()) > 0) {
                secondMostRecent = mostRecent;
                mostRecent = night;
            } else if (secondMostRecent == null || night.getDate().compareTo(secondMostRecent.getDate()) > 0) {
                secondMostRecent = night;
            }
        }

        if (secondMostRecent == null) {
            Log.d(TAG, "No hay registros de la segunda noche más reciente.");
        }

        return secondMostRecent;
    }

    /*----------------------------------------------------------------------------------------------
                                    getThirdLastNight() --> Night
    ----------------------------------------------------------------------------------------------*/
    public void getThirdLastNight(String userId, final OnSuccessListener<Night> successListener, final OnFailureListener failureListener){
        CollectionReference nightsCollection = db.collection("users").document(userId).collection("nightsData");

        Query query = nightsCollection.orderBy("date", Query.Direction.DESCENDING);

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
                            Night thirdLastNight = searchThirdLastNight(nightsList);
                            successListener.onSuccess(thirdLastNight);
                        }
                        else{
                            failureListener.onFailure(task.getException());
                        }
                    }
                });
    }

    /*----------------------------------------------------------------------------------------------
                     List --> searchThirdLastNight() --> Night
    ----------------------------------------------------------------------------------------------*/
    private static Night searchThirdLastNight(List<Night> nights) {
        Night mostRecent = null;
        Night secondMostRecent = null;
        Night thirdMostRecent = null;

        for (Night night : nights) {
            if (mostRecent == null || night.getDate().compareTo(mostRecent.getDate()) > 0) {
                thirdMostRecent = secondMostRecent;
                secondMostRecent = mostRecent;
                mostRecent = night;
            } else if (secondMostRecent == null || night.getDate().compareTo(secondMostRecent.getDate()) > 0) {
                thirdMostRecent = secondMostRecent;
                secondMostRecent = night;
            } else if (thirdMostRecent == null || night.getDate().compareTo(thirdMostRecent.getDate()) > 0) {
                thirdMostRecent = night;
            }
        }

        if (thirdMostRecent == null) {
            Log.d(TAG, "No hay registros de la tercera noche más reciente.");
        }

        return thirdMostRecent;
    }

    /*----------------------------------------------------------------------------------------------
                                  codigoAcceso --> checkFamilyAccessCode() --> userID || null
    ----------------------------------------------------------------------------------------------*/
    public void checkFamilyAccessCode(String codigoAcceso, final OnSuccessListener<String> successListener, final OnFailureListener failureListener) {
        CollectionReference usersCollection = db.collection("users");

        Log.d("Puta",codigoAcceso);
        Query query = usersCollection.whereEqualTo("familyAccessCode", codigoAcceso);

        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("Task",task.toString());
                            Log.d("TaskGetResult",Integer.toString(task.getResult().size()));
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Encontrar el documento que coincide con el familyAccessCode
                                String userId = document.getId();
                                Log.d("Holaaaa",userId);
                                successListener.onSuccess(userId);
                                return;  // No es necesario continuar después de encontrar una coincidencia
                            }
                            // Si no se encontró ninguna coincidencia
                            successListener.onSuccess(null);
                        } else {
                            failureListener.onFailure(task.getException());
                        }
                    }
                });
    }

    /*----------------------------------------------------------------------------------------------
                              cargarUltimaImagen() --> Renders Family Image and Date of Creation
    ----------------------------------------------------------------------------------------------*/
    public static void cargarUltimaImagen(Context context, ImageView imageView, String userId, TextView fechaTextView) {
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://hypnos-gti.appspot.com");
        StorageReference storageRef = storage.getReference().child("users/" + userId);

        storageRef.listAll()
                .addOnSuccessListener(listResult -> {
                    List<StorageReference> items = listResult.getItems();

                    // Create CompletableFuture to track completion of all creation time requests
                    List<CompletableFuture<Long>> creationTimeFutures = new ArrayList<>();

                    // Log the creation times before sorting
                    for (StorageReference item : items) {
                        CompletableFuture<Long> creationTimeFuture = new CompletableFuture<>();
                        creationTimeFutures.add(creationTimeFuture);

                        getCreationTimeMillis(item,
                                creationTimeMillis -> {
                                    creationTimeFuture.complete(creationTimeMillis);
                                },
                                exception -> {
                                    exception.printStackTrace();
                                    creationTimeFuture.completeExceptionally(exception);
                                });
                    }

                    // Wait for all creation times to be obtained
                    CompletableFuture<Void> allCreationTimes = CompletableFuture.allOf(
                            creationTimeFutures.toArray(new CompletableFuture[0]));

                    allCreationTimes.thenRun(() -> {
                        // Sort items based on creation times
                        items.sort((first, second) -> {
                            try {
                                long result = Long.compare(creationTimeFutures.get(items.indexOf(second)).get(),
                                        creationTimeFutures.get(items.indexOf(first)).get());
                                System.out.println("Comparison result: " + result);
                                return Math.toIntExact(result);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return 0;
                            }
                        });

                        if (!items.isEmpty()) {
                            StorageReference lastImageRef = items.get(0);
                            lastImageRef.getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        Glide.with(context).load(uri).into(imageView);
                                        getFormattedCreationDateTime(lastImageRef, fechaTextView);
                                    })
                                    .addOnFailureListener(e -> {
                                        e.printStackTrace();
                                    });
                        } else {
                            fechaTextView.setText("No existe ninguna imagen de su familiar");
                        }
                    });

                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

    private static void getCreationTimeMillis(StorageReference reference, OnSuccessListener<Long> successListener, OnFailureListener failureListener) {
        reference.getMetadata()
                .addOnSuccessListener(storageMetadata -> {
                    long creationTimeMillis = storageMetadata.getCreationTimeMillis();
                    successListener.onSuccess(creationTimeMillis);
                })
                .addOnFailureListener(exception -> {
                    exception.printStackTrace();
                    failureListener.onFailure(exception);
                });
    }

    private static void getFormattedCreationDateTime(StorageReference reference, TextView fechaTextView) {
        getCreationTimeMillis(reference,
                creationTimeMillis -> {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                    String formattedDateTime = sdf.format(new Date(creationTimeMillis));

                    // Asignar la cadena al TextView
                    fechaTextView.setText("" + formattedDateTime);
                },
                exception -> {
                    exception.printStackTrace();
                });
    }

    /*----------------------------------------------------------------------------------------------
                                storeId --> getStoreData()
    ----------------------------------------------------------------------------------------------*/
    public void getStoreData(final OnSuccessListener<List<Store>> successListener, final OnFailureListener failureListener) {
        CollectionReference storesCollection = db.collection("stores");

        storesCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<Store> storesList = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()){
                        Store store = document.toObject(Store.class);
                        storesList.add(store);
                    }
                    successListener.onSuccess(storesList);
                } else{
                    failureListener.onFailure(task.getException());
                }
            }
        });
    }

    public void graphicConfig(String UID, LineChart graph) {
        graph.getDescription().setEnabled(false);
        graph.setTouchEnabled(true);
        graph.setDragEnabled(true);
        graph.setScaleEnabled(true);
        graph.setPinchZoom(true);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection("users").document(UID).collection("nightsData");

        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Entry> scores = new ArrayList<>();
                    ArrayList<String> daysOfWeek = new ArrayList<>(); // To store the days of the week

                    // Parse and store the data
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Check if 'date' field exists and is of type Timestamp
                        if (document.contains("date") && document.get("date") instanceof Timestamp) {
                            Timestamp timestamp = (Timestamp) document.get("date");
                            String date = formatDate(timestamp.toDate()); // Convert timestamp to Date and then to formatted string
                            String dayOfWeek = getDayOfWeekFromDate(date);
                            Float score = document.getDouble("score").floatValue();
                            scores.add(new Entry(getTimestampFromDate(date), score != null ? score : 0f));
                            daysOfWeek.add(dayOfWeek);
                        } else {
                            // Log a warning or handle the case where 'date' is not a Timestamp
                            Log.w(TAG, "Invalid 'date' field in Firestore document");
                        }
                    }

                    // Sort the entries based on the timestamp
                    Collections.sort(scores, new Comparator<Entry>() {
                        @Override
                        public int compare(Entry entry1, Entry entry2) {
                            return Float.compare(entry1.getX(), entry2.getX());
                        }
                    });

                    // Create and configure the DataSet
                    LineDataSet set = new LineDataSet(scores, "Puntuación de sueño");
                    set.setFillAlpha(110);
                    set.setColor(Color.parseColor("#164499"));
                    set.setLineWidth(5f);
                    set.setValueTextSize(15f);

                    // Add the DataSet to the chart
                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(set);
                    LineData data = new LineData(dataSets);
                    graph.setData(data);

                    // Configure the appearance of the x-axis
                    XAxis xAxis = graph.getXAxis();
                    xAxis.setGranularity(1f);
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setLabelRotationAngle(0f);

                    // Manually set X-axis labels using timestamps
                    xAxis.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getAxisLabel(float value, AxisBase axis) {
                            long timestamp = (long) value;
                            String date = formatDate(new Date(timestamp));
                            return getDayOfWeekFromDate(date);
                        }
                    });

                    // Set the number of labels to match the number of data points
                    xAxis.setLabelCount(scores.size(), true);

                    // Hide Y-axis labels and grid lines
                    YAxis leftYAxis = graph.getAxisLeft();
                    leftYAxis.setDrawLabels(false);
                    leftYAxis.setDrawGridLines(false);

                    YAxis rightYAxis = graph.getAxisRight();
                    rightYAxis.setDrawLabels(false);
                    rightYAxis.setDrawGridLines(false);

                    // Hide X-axis grid lines
                    xAxis.setDrawGridLines(false);

                    // Invalidate the chart to refresh the appearance
                    graph.invalidate();

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    // Function to get the day of the week from the date string
    private String getDayOfWeekFromDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy, h:mm:ss a z", Locale.getDefault());
        try {
            Date parsedDate = format.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parsedDate);

            // Use a different format for the day of the week
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            return dayFormat.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


    // Function to get a timestamp from the date string
    private float getTimestampFromDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy, h:mm:ss a z", Locale.getDefault());
        try {
            Date parsedDate = format.parse(date);
            return parsedDate.getTime(); // Convert to timestamp in milliseconds
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0f;
    }

    // Function to format Date to a string
    private String formatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy, h:mm:ss a z", Locale.getDefault());
        return format.format(date);
    }


    /*
    public void getYesterdayNight(String userId, final OnSuccessListener<Night> successListener, final OnFailureListener failureListener){
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
                            Date currentDate = new Date();
                            Night yesterdayNight = searchYesterdayNight(nightsList, currentDate);
                            successListener.onSuccess(yesterdayNight);
                        }
                        else{
                            failureListener.onFailure(task.getException());
                        }
                    }
                });
    }


     */



}//class







