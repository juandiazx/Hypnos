#ifndef ESP32Abstract_H
#define ESP32Abstract_H

class ESP32Abstract {
public:
    static ESP32Abstract* getInstance(const char *ssid, const char *pass, int udp, int pinBoton, int pinLed) {
        if (instance == nullptr) {
            instance = new ESP32Abstract(ssid, pass, udp, pinBoton, pinLed);
            instance->openUDPConnection();
            pinMode(pinLed,OUTPUT);

            xTaskCreate(&listenForMessagesTask, "listenForMessagesTask", 4096, instance, 2, NULL);
            xTaskCreate(&obtainSensorsDataTask, "obtainSensorsDataTask", 4096, instance, 1, NULL);
        }
        return instance;
    }

private:
    int udpPort;
    char ssid[32];
    char pass[64];
    AsyncUDP udp;
    static const int UDP_TX_PACKET_MAX_SIZE = 200;
    unsigned long tiempoEmpieza; // Guardar el tiempo inicial
    unsigned long duration; 
    PressureSensor* pressureSensor;
    const unsigned int maxMeasurements = 256;  
    int pinLedField;

    ESP32Abstract(const char *ssidConstructor, const char *passConstructor, int udp, int pinBoton, int pinLed) {
        udpPort = udp;
        pressureSensor = PressureSensor::getInstance(pinBoton);
        pinLedField = pinLed;
        strncpy(ssid, ssidConstructor, sizeof(ssid) - 1);
        ssid[sizeof(ssid) - 1] = '\0';
        strncpy(pass, passConstructor, sizeof(pass) - 1);
        pass[sizeof(pass) - 1] = '\0';
        /*Serial.println("Wifi: ");
        Serial.print(ssid);
        Serial.print(pass);*/
    }

    void openUDPConnection(){
        //Serial.println("openUDPConnection");
        WiFi.mode(WIFI_STA);
        WiFi.begin(ssid, pass);
        if(WiFi.waitForConnectResult()!=WL_CONNECTED) {
            //Serial.println("Conectando a WiFi...");
            while(1) {
              vTaskDelay(1000 / portTICK_PERIOD_MS);
            }
        }
        //Serial.println("Conexión WiFi establecida");
        
        if(udp.listen(udpPort)) {
          
        }
    }

    static void listenForMessagesTask(void *pvParameters) {
        ESP32Abstract *espInstance = static_cast<ESP32Abstract *>(pvParameters);
        while (1) {
            espInstance->listenForMessages();
            vTaskDelay(4000 / portTICK_PERIOD_MS); 
        }
    }

    static void detectPressureTask(void *pvParameters) {
    ESP32Abstract *espInstance = static_cast<ESP32Abstract *>(pvParameters);
    while (1) {
        espInstance->pressureSensor->detectPressure();
        vTaskDelay(500 / portTICK_PERIOD_MS);  // Adjust the delay as needed
    }
}

    static void obtainSensorsDataTask(void *pvParameters) {
        ESP32Abstract *espInstance = static_cast<ESP32Abstract *>(pvParameters);
        while (1) {
            espInstance->obtainSensorsData();
            vTaskDelay(4000 / portTICK_PERIOD_MS);
        }
    }

    //ESTO SE REPITE CADA 4 SEGUNDOS Y PUEDE ESTAR CREANDO UN SOBREPROCESAMIENTO EN EL MICRO O PERDIDA DE DATOS U OCUPANDO MEMORIA
    void listenForMessages() {

        //Serial.println("listening for messages");
        udp.onPacket([this](AsyncUDPPacket &packet) {
          //Serial.println("llega algo");
        char packetBuffer[UDP_TX_PACKET_MAX_SIZE];
        int len = packet.length();
        
        // Leer los datos del paquete en el buffer
        for (int i = 0; i < len; i++) {
            packetBuffer[i] = packet.data()[i];
        }
        packetBuffer[len] = '\0';

        // Parsear el JSON recibido
        DynamicJsonDocument jsonDoc(200);  // Ajusta el tamaño según tus necesidades
        DeserializationError error = deserializeJson(jsonDoc, packetBuffer);

        if (!error) {
            //Serial.println("Tu puta madre");
            const char *message = jsonDoc["mensaje"];
            if (message) {
                if (strcmp(message, "START_TRACKING") == 0) {
                    obtainSensorsData();
                } 
                 else {
                    Serial.println("No se reconoce la orden del mensaje JSON");
                }
            }
        } else {
            Serial.println("Error al analizar los datos JSON recibidos");
        }
    });
    }


    void obtainSensorsData() {
        tiempoEmpieza = millis(); // Guardar el tiempo inicial
        duration = 17000;     // Duración en milisegundos (en este caso, 15 segundos)

        digitalWrite(pinLedField, HIGH); // Enciende el LED
        vTaskDelay(3000 / portTICK_PERIOD_MS);
        digitalWrite(pinLedField, LOW);

        // Start the detectPressure task
        xTaskCreate(&detectPressureTask, "detectPressureTask", 4096, this, 2, NULL);

        vTaskDelay(duration / portTICK_PERIOD_MS);

        // Stop the detectPressure task
        //vTaskDelete(NULL);

        //En el caso si se quedó pulsado y no se soltó
        if(pressureSensor->tiempoInicio !=0){
          pressureSensor->tiempoEncendido += millis() - pressureSensor->tiempoInicio; // Calcula el tiempo que estuvo encendido el LED
          //Serial.println(pressureSensor->tiempoEncendido);
        }

        //vTaskDelay(3000 / portTICK_PERIOD_MS)
        sendDataToM5Stack(udpPort);

        Serial.print(pressureSensor->tiempoEncendido);
        Serial.flush();  // Asegura que los datos se envíen completamente

        //Reseteamos los tiempos y estados del sensor de presion desde fuera
        pressureSensor->tiempoEncendido = 0;
        pressureSensor->tiempoInicio = 0;
        pressureSensor->estadoAnterior = LOW;
    }

    void sendDataToM5Stack(int puerto) {
        StaticJsonDocument<200> jsonBuffer;
        char medidas[200];

        jsonBuffer["sleepTime"] = pressureSensor->tiempoEncendido;
        // Serializar el objeto JSON en una cadena
        serializeJson(jsonBuffer, medidas);
  
        udp.broadcastTo(medidas, puerto); 
    }
    static ESP32Abstract* instance;
};

ESP32Abstract* ESP32Abstract::instance = nullptr;

#endif