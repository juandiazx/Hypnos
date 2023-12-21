//Cuando recibe la orden de tomar medidas, que encienda el led por 6 segundos simulando antes de acostarse
//la pantalla que vaya oscureciendo entera poco a poco simulando la noche
//hasta que se apagan las luces como que ya se acosto y se comienza a tomar medidas
//cuando llega la orden de parar de tomar medidas, comienza a sonar alarma del M5Stack o ESP32 y
//se encienden las luces

#ifndef ESP32Abstract_H
#define ESP32Abstract_H

class ESP32Abstract {
public:
    static ESP32Abstract* getInstance(const char *ssid, const char *pass, int udp, int tempPIN, int soundPIN, int ledPIN) {
        if (instance == nullptr) {
            instance = new ESP32Abstract(ssid, pass, udp, tempPIN, soundPIN, ledPIN);
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

    int snoreCountComputation(int min, int max){
      //Un ronquido sera mas o menos un intervalo de 4-7 medidas
      saveSoundDetections();
      int counter = 0;
      snoreAmount = 0;
      for(int p1=0 ;p1 < soundDetectionsList.size();p1++){
        if(soundDetectionsList[p1]){
          counter++;
        }
        else if(!soundDetectionsList[p1] && counter >= min && counter <= max){
          snoreAmount++;
          counter = 0;
        }
        else if(!soundDetectionsList[p1] && counter < min){
          counter = 0;
        }
      }
      soundDetectionsList.clear();
      return snoreAmount;
    }

private:
    int udpPort;
    char ssid[32];
    char pass[64];
    AsyncUDP udp;
    static const int UDP_TX_PACKET_MAX_SIZE = 200;

    TemperatureSensor* temperatureSensor;
    SoundSensor* soundSensor;
    LedLight* ledLight;

    int snoreAmount=0;
    int frecuenciaMinimaSonido = 400; // Numero aleatorio que hace de minimo valor de medida para los ronquidos
    const unsigned int maxMeasurements = 256;
    int temperatura=0;
    std::vector<bool> soundDetectionsList;   

    ESP32Abstract(const char *ssidConstructor, const char *passConstructor, int udp, int TEMPERATUREPIN, int SOUNDPIN, int LEDPIN) {
        udpPort = udp;
        temperatureSensor = TemperatureSensor::getInstance(TEMPERATUREPIN);  // Crea una instancia del sensor de temperatura
        soundSensor = SoundSensor::getInstance(SOUNDPIN);  // Crea una instancia del sensor de sonido
        ledLight = LedLight::getInstance(LEDPIN);  // Crea una instancia del controlador de luz LED

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

    void saveSoundDetections() {
      int i=0;
      while(i < 50){
        int sonido = soundSensor->takeMeasurement();
        //Serial.println(sonido);
        if(sonido >= frecuenciaMinimaSonido) {
          soundDetectionsList.push_back(true);
        }
        else{
          soundDetectionsList.push_back(false);
        }
        delay(100);
        i++;
      }
    }


    void obtainSensorsData() {
        // Implementa la obtención de datos de los sensores (temperatureSensor, soundSensor, etc.)

        Serial.println("Se entro a obtainSensorsData, bucle infinito de toma de medidas");
        ledLight->turnOn();
        delay(3000);
        ledLight->turnOff();
        
        temperatura = temperatureSensor->takeMeasurement();
        while(temperatura > 45 || temperatura <3){
          temperatura = temperatureSensor->takeMeasurement();
        }
        saveSoundDetections();
        snoreAmount = snoreCountComputation(2,5);

        delay(3000);

        sendDataToM5Stack(udpPort);

    }

    void sendDataToM5Stack(int puerto) {
        // Implementa el envío de datos al M5Stack
        // Crear un objeto JSON para almacenar los datos
        Serial.print("Se enciende el led");
        ledLight->turnOn();
        delay(2000);
        ledLight->turnOff();
        Serial.println("esp32 denota que se hace de dia");

        StaticJsonDocument<200> jsonBuffer;
        char medidas[200];

        Serial.print("muestra de temperatura");
        Serial.println(temperatura);
        Serial.print("muestra de ronquidos");
        Serial.println(snoreAmount);

        //snoreSummary(&soundMeasurements[0], soundIndex);

        //jsonBuffer["averageTemperature"] = averageMeasurements(&temperatureMeasurements[0], tempIndex);
        jsonBuffer["averageTemperature"] = temperatura;
        jsonBuffer["snoreAmount"] = snoreAmount;

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