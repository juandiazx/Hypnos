package com.example.hypnosapp.services;
// MqttHelper.java

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MQTTHelper {
    private MqttClient mqttAndroidClient;

    public MQTTHelper(String serverUri, String clientId, String subscriptionTopic) throws MqttException {
        mqttAndroidClient = new MqttClient(serverUri, clientId, (MqttClientPersistence) MQTTHelper.getAppContext());

        // Configura y conecta el cliente MQTT
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        // Configuraciones adicionales si es necesario

        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                // Lógica después de que se completa la conexión
                // Por ejemplo, puedes suscribirte al tema aquí
                subscribeToTopic(subscriptionTopic);
            }

            @Override
            public void connectionLost(Throwable cause) {
                // Manejar la pérdida de conexión
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                // Lógica para manejar el mensaje MQTT recibido
                if (message.toString().equals("daytime")) {
                    // Obtener la URL de la base de datos (Firebase) y luego iniciar la alarma
                    String alarmUrl = getAlarmUrlFromFirebase();
                    if (alarmUrl != null && !alarmUrl.isEmpty()) {
                        startAlarmService(alarmUrl);
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // Lógica después de que se completa la entrega
            }
        });

        // Conectar al servidor MQTT
        connect();
    }

    private void connect() {
        // Implementar la lógica de conexión
    }

    private void subscribeToTopic(String topic) {
        // Implementar la lógica de suscripción
    }

    private String getAlarmUrlFromFirebase() {
        // Implementar la lógica para obtener la URL de la base de datos (Firebase)
        // Puedes usar Firebase Realtime Database o Firestore según tu configuración
        // Retorna la URL de la alarma o null si no hay URL guardada

        return "";
    }

    private void startAlarmService(String alarmUrl) {
        // Iniciar el servicio de alarma con la URL
        Intent serviceIntent = new Intent(MQTTHelper.getAppContext(), AlarmService.class);
        serviceIntent.setData(Uri.parse(alarmUrl));
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
