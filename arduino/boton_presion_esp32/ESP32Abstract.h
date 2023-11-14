//Cuando recibe la orden de tomar medidas, que encienda el led por 6 segundos simulando antes de acostarse
//la pantalla que vaya oscureciendo entera poco a poco simulando la noche
//hasta que se apagan las luces como que ya se acosto y se comienza a tomar medidas
//cuando llega la orden de parar de tomar medidas, comienza a sonar alarma del M5Stack o ESP32 y
//se encienden las luces

#ifndef ESP32Abstract_H
#define ESP32Abstract_H

class ESP32Abstract {
public:
    static ESP32Abstract* getInstance(const char *ssid, const char *pass, int udp, int BOTONPIN, int LEDPIN_2) {
        if (instance == nullptr) {
            instance = new ESP32Abstract(ssid, pass, udp, BOTONPIN, LEDPIN_2);
            instance->openUDPConnection();
        }
        return instance;
    }

    //ESTO SE REPITE CADA 4 SEGUNDOS Y PUEDE ESTAR CREANDO UN SOBREPROCESAMIENTO EN EL MICRO O PERDIDA DE DATOS U OCUPANDO MEMORIA
    void listenForMessages() {

        Serial.println("listening for messages");
        udp.onPacket([this](AsyncUDPPacket &packet) {
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
            Serial.println("No hay error al descomprimir el json");
            const char *message = jsonDoc["mensaje"];
            if (message) {
                if (strcmp(message, "START_TRACKING") == 0) {
                    // Recibido un mensaje para iniciar el seguimiento
                    obtainSensorsData();
                } 
                 else {
                    // Mensaje desconocido, podrías manejarlo de alguna manera
                    Serial.println("No se reconoce la orden del mensaje JSON");
                }
            }
        } else {
            Serial.println("Error al analizar los datos JSON recibidos");
        }
    });
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

    bool stopMeasurements;
    const unsigned int maxMeasurements = 256;  

    ESP32Abstract(const char *ssidConstructor, const char *passConstructor, int udp, int BOTONPIN, int LEDPIN_2) {
        udpPort = udp;
        pressureSensor = PressureSensor::getInstance(BOTONPIN, LEDPIN_2);

        strncpy(ssid, ssidConstructor, sizeof(ssid) - 1);
        ssid[sizeof(ssid) - 1] = '\0';

        strncpy(pass, passConstructor, sizeof(pass) - 1);
        pass[sizeof(pass) - 1] = '\0';
        Serial.println("Wifi: ");
        Serial.print(ssid);
        Serial.print(pass);
    }

    void openUDPConnection(){
        Serial.println("openUDPConnection");
        WiFi.mode(WIFI_STA);
        WiFi.begin(ssid, pass);
        if(WiFi.waitForConnectResult()!=WL_CONNECTED) {
            Serial.println("Conectando a WiFi...");
            while(1) {
              delay(1000);
            }
        }
        Serial.println("Conexión WiFi establecida");

        if(udp.listen(udpPort)) {
        Serial.print("UDP Listening on IP: ");
        Serial.println(WiFi.localIP());
        }
    }


    void obtainSensorsData() {

        tiempoEmpieza = millis(); // Guardar el tiempo inicial
        duration = 27000;     // Duración en milisegundos (en este caso, 15 segundos)

        // Implementa la obtención de datos de los sensores (temperatureSensor, soundSensor, etc.)

        Serial.println("Se entro a obtainSensorsData, bucle infinito de toma de medidas");
        delay(6000);

        Serial.println(pressureSensor->tiempoEncendido);
        delay(6000);
 
        while (millis() - tiempoEmpieza <= duration) {

        pressureSensor->detectPressure();
        
        Serial.println(pressureSensor->tiempoEncendido);
        
        }
        if(pressureSensor->tiempoInicio !=0){
          pressureSensor->tiempoEncendido += millis() - pressureSensor->tiempoInicio; // Calcula el tiempo que estuvo encendido el LED
          digitalWrite(pressureSensor->LedPin_2, LOW);  // Apaga el LED

        }
        
        delay(3000);

        sendDataToM5Stack(udpPort);
    }

    void sendDataToM5Stack(int puerto) {
        // Implementa el envío de datos al M5Stack
        // Crear un objeto JSON para almacenar los datos
        Serial.print("Datos enviados");
        delay(5000);
        Serial.println("esp32 denota que se hace de dia");

        StaticJsonDocument<200> jsonBuffer;
        char medidas[200];

        stopMeasurements = true;
        Serial.print("tiempo de descanso");
        Serial.println(pressureSensor->tiempoEncendido);

        jsonBuffer["Tiempo Descanso"] = pressureSensor->tiempoEncendido;
        pressureSensor->tiempoEncendido = 0;
        pressureSensor->tiempoInicio = 0;
        pressureSensor->estadoAnterior = LOW;
        // Serializar el objeto JSON en una cadena
        serializeJson(jsonBuffer, medidas);

        Serial.println("se intentan enviar los datos al m5stack");
        // Enviar los datos por UDP al M5Stack
        udp.broadcastTo(medidas, puerto); 
    }

    static ESP32Abstract* instance;
};

ESP32Abstract* ESP32Abstract::instance = nullptr;

#endif