package com.example.hypnosapp.services;
// MqttHelper.java

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.hypnosapp.appactivity.AjustesDeSuenyoActivity;
import com.example.hypnosapp.firebase.FirebaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.security.AccessControlContext;
import java.util.Map;

public class MQTTHelper {
    private MqttClient mqttAndroidClient;
    private FirebaseHelper firebaseHelper;

    private FirebaseAuth firebaseAuth;

    private Context appContext;  // Agrega esta variable
    private FirebaseUser firebaseUser;

    private String userID;

    public MQTTHelper(Context inAppContext,String serverUri, String clientId, String subscriptionTopic) throws MqttException, InterruptedException {

        appContext = inAppContext.getApplicationContext();  // Obten el contexto de la aplicación


        mqttAndroidClient = new MqttClient(serverUri, clientId, new MemoryPersistence());


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();

        firebaseHelper = new FirebaseHelper();

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();


        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.d("Conexion completada", "Viva");
            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("Recibir",message.toString());
                //String mensaje = message.getPayload().toString();
                firebaseHelper.getClock(userID,
                        new OnSuccessListener<Map<String, Object>>() {
                            @Override
                            public void onSuccess(Map<String, Object> clockSettings) {
                                Log.d("SuccesClock","Sii");
                                Boolean isWithVibrations = (Boolean) clockSettings.get("isWithVibrations");
                                String toneLocation = (String) clockSettings.get("toneLocation");

                                startAlarmService(toneLocation, isWithVibrations);
                            }
                        },
                        new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                Log.e("ErrorRecibir",e.getMessage());
                            }
                        });
            }


            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d("DeliveryComplete","Viva");

            }
        });
    }

    public void connect() {
        try {
            mqttAndroidClient.connect();
        } catch (MqttException e) {
            Log.e("Error", "Error al conectar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void subscribeToTopic(String topic) {
        try {
            mqttAndroidClient.subscribe(topic, 0);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishToUidTopic() {
        String topic = "hypnos_rp_uid";
        try {
            mqttAndroidClient.publish(topic, userID.getBytes(), 0, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void startAlarmService(String alarmUrl,Boolean isWithVibration) {
        // Iniciar el servicio de alarma con la URL
        Intent serviceIntent = new Intent(appContext, AlarmService.class);
        serviceIntent.setData(Uri.parse(alarmUrl));
        serviceIntent.putExtra("isWithVibration", isWithVibration);
        appContext.startService(serviceIntent);
    }


    public void disconnect() {
        try {
            mqttAndroidClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
