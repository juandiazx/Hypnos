package com.example.hypnosapp.services;
// MqttHelper.java

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.example.hypnosapp.appactivity.AjustesDeSuenyoActivity;
import com.example.hypnosapp.firebase.FirebaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Map;

public class MQTTHelper {
    private MqttClient mqttAndroidClient;
    private String userId;
    private FirebaseHelper firebaseHelper;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private String userID;

    public MQTTHelper(String serverUri, String clientId, String subscriptionTopic) throws MqttException {
        mqttAndroidClient = new MqttClient(serverUri, clientId, (MqttClientPersistence) MQTTHelper.getAppContext());


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
                subscribeToTopic(subscriptionTopic);
            }

            @Override
            public void connectionLost(Throwable cause) {
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if (message.toString().equals("daytime")) {
                    obtainClockSettings();
                    //LLAMAR A LA INTENCION DE ALARMSERVICE CON LOS PARAMETROS
                    //startAlarmService();
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // Lógica después de que se completa la entrega
            }
        });
        connect();
    }

    private void connect() {
        // Implementar la lógica de conexión
    }

    private void subscribeToTopic(String topic) {
        // Implementar la lógica de suscripción
    }

    /*
    * NECESITAMOS LLAMAR A GETCLOCK()
    * PARA LLAMAR A LA INTENCION DE INICIAR EL DESPERTADOR
    * CON LOS BOOLEANOS DE VIBRACION Y EL STRING DE TONO
    * */
    private void obtainClockSettings(){
        firebaseHelper.getClock(userId,
                new OnSuccessListener<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> clockSettings) {
                        // Update UI with clock settings
                        //updateClockSettingsUI(clockSettings);
                    }
                },

                new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        /*Toast.makeText( AjustesDeSuenyoActivity.this,
                                "We couldn't obtain your alarm settings", Toast.LENGTH_SHORT).show();
                    */}
                });
    }

    private void startAlarmService(String alarmUrl,Boolean isWithVibration) {
        // Iniciar el servicio de alarma con la URL
        Intent serviceIntent = new Intent(MQTTHelper.getAppContext(), AlarmService.class);
        serviceIntent.setData(Uri.parse(alarmUrl));
        serviceIntent.putExtra("isWithVibration", isWithVibration);
        MQTTHelper.getAppContext().startService(serviceIntent);
    }


    private static Context getAppContext() {
        return null;
    }

    public void disconnect() {
        // Desconectar del servidor MQTT
        // Implementar la lógica de desconexión
    }
}
