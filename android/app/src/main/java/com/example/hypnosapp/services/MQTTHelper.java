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
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.security.AccessControlContext;
import java.util.Map;

public class MQTTHelper {
    private MqttAndroidClient mqttAndroidClient;
    private String userId;
    private FirebaseHelper firebaseHelper;

    private FirebaseAuth firebaseAuth;

    private boolean isConnected = false;
    private Context appContext;  // Agrega esta variable
    private FirebaseUser firebaseUser;

    private String userID;

    public MQTTHelper(Context inAppContext,String serverUri, String clientId, String subscriptionTopic) throws MqttException {

        appContext = inAppContext.getApplicationContext();  // Obten el contexto de la aplicación


        mqttAndroidClient = new MqttAndroidClient(appContext, serverUri, clientId);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();

        firebaseHelper = new FirebaseHelper();

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();


        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                // Lógica después de que se completa la conexión
                // Por ejemplo, puedes suscribirte al tema aquí

                isConnected = true;

                if (reconnect) {
                    Log.d("Viva", "Reconexión exitosa");
                } else {
                    Log.d("Viva", "Conexión inicial exitosa");
                }

                Log.d("Viva","Hola");
                subscribeToTopic(subscriptionTopic);
                publishToUidTopic();
            }



            @Override
            public void connectionLost(Throwable cause) {
                connect();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if (topic.equals("hypnos_rp_daytime") && message.toString().equals("daytime")) {
                    firebaseHelper.getClock(userId,
                            new OnSuccessListener<Map<String, Object>>() {
                                @Override
                                public void onSuccess(Map<String, Object> clockSettings) {
                                    Boolean isWithVibrations = (Boolean) clockSettings.get("isWithVibrations");
                                    String toneLocation = (String) clockSettings.get("toneLocation");

                                    startAlarmService(toneLocation, isWithVibrations);
                                }
                            },
                            new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    // Manejar la falla en la obtención de configuraciones de alarma
                                }
                            });
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // Lógica después de que se completa la entrega
            }
        });
    }

    public void connect() {
        try {
            Log.d("Conectando", "Viva");
            mqttAndroidClient.connect();
            Log.d("YaConectado", "Viva");
        } catch (MqttException e) {
            Log.e("Error", "Error al conectar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void subscribeToTopic(String topic) {
        if (isConnected) {
            try {
                mqttAndroidClient.subscribe(topic, 0);
                Log.d("Suscrito", "Viva");
            } catch (MqttException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("Error", "Intento de suscripción antes de la conexión completa");
        }
    }

    public void publishToUidTopic() {
        if (isConnected) {
            String topic = "hypnos_rp_uid";
            try {
                mqttAndroidClient.publish(topic, userID.getBytes(), 0, false);
                Log.d("Publicado", "Viva");
            } catch (MqttException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("Error", "Intento de publicación antes de la conexión completa");
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
