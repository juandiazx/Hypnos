//Cuando recibe la orden de tomar medidas, que encienda el led por 6 segundos simulando antes de acostarse
//la pantalla que vaya oscureciendo entera poco a poco simulando la noche
//hasta que se apagan las luces como que ya se acosto y se comienza a tomar medidas
//cuando llega la orden de parar de tomar medidas, comienza a sonar alarma del M5Stack o ESP32 y
//se encienden las luces

#ifndef ESP32_H
#define ESP32_H

class ESP32 {
public:
    static ESP32* getInstance(const char *ssid, const char *pass, int udp) {
        if (instance == nullptr) {
            instance = new ESP32(ssid, pass, udp);
        }
        return instance;
    }

    void listenForMessages() {
        while (udp.parsePacket()) {
            char packetBuffer[UDP_TX_PACKET_MAX_SIZE];
            int len = udp.read(packetBuffer, UDP_TX_PACKET_MAX_SIZE);
            if (len > 0) {
                packetBuffer[len] = '\0';
                // Procesar el mensaje recibido
                if (strcmp(packetBuffer, "START_TRACKING") == 0) {
                    // Iniciar la toma de medidas
                    obtainSensorsData();
                } else if (strcmp(packetBuffer, "STOP_TRACKING") == 0) {
                    // Detener la toma de medidas
                    sendDataToM5Stack(udpPort);
                }
            }
        }
    }

private:
    int udpPort;

    TemperatureSensor* temperatureSensor;
    SoundSensor* soundSensor;
    LedLight* ledLight;

    int snoreAmount;
    int frecuenciaMinimaSonido = 600; // Numero aleatorio que hace de minimo valor de medida para los ronquidos
    int averageTemperature;
    // Para tomar la temperatura cada 5 segundos
    unsigned int previousTempMeasurementTime = 0;
    const unsigned int tempMeasurementInterval = 5000;
    unsigned int tempIndex = 0;
    // Para tomar el sonido cada 100 milisegundos
    unsigned int previousSoundMeasurementTime = 0;
    const unsigned int soundMeasurementInterval = 100;
    unsigned int soundIndex=0;
    // Son los milisegundos que separan cada medida tomada
    const unsigned int maxMeasurements = 256;
    int temperatureMeasurements[maxMeasurements];
    int soundMeasurements[maxMeasurements];
    

    ESP32(const char *ssid, const char *pass, int udp, int TEMPERATUREPIN, int SOUNDPIN, int LEDPIN) {
        udpPort = udp;
        temperatureSensor = TemperatureSensor::getInstance(TEMPERATUREPIN);  // Crea una instancia del sensor de temperatura
        soundSensor = SoundSensor::getInstance(SOUNDPIN);  // Crea una instancia del sensor de sonido
        ledLight = LedLight::getInstance(LEDPIN);  // Crea una instancia del controlador de luz LED
        M5StackAbstract::getInstance(ssid, pass, udp);  // Crea una instancia del M5Stack
    }

    void multipleTemperatureMeasurements() {
        int temperatura = temperatureSensor->takeMeasurement();
        temperatureMeasurements[tempIndex] = temperatura;
        tempIndex++;
        previousTempMeasurementTime = millis();
    }

    void multipleSoundMeasurements() {
        int sonido = soundSensor->takeMeasurement();
        soundMeasurements[soundIndex] = sonido;
        soundIndex++;
        previousSoundMeasurementTime = millis();
    }

    void obtainSensorsData() {
        // Implementa la obtención de datos de los sensores (temperatureSensor, soundSensor, etc.)
        unsigned long currentTime = millis();

        if (currentTime - previousTempMeasurementTime >= tempMeasurementInterval  && tempIndex<<maxMeasurements) {
            int temperatura = temperatureSensor->takeMeasurement();

            temperatureMeasurements[tempIndex] = temperatura;
            tempIndex++;

            previousTempMeasurementTime = currentTime;
        }

        // Agrega aquí la medición del sonido (ronquidos) con una frecuencia diferente
        if (currentTime - previousSoundMeasurementTime >= soundMeasurementInterval && soundIndex < maxMeasurements) {
            int sonido = soundSensor->takeMeasurement();

            soundMeasurements[soundIndex] = sonido;
            soundIndex++;

            previousSoundMeasurementTime = currentTime;
        }
    }

    int averageMeasurements(int *measurements, int measurementsQuantity) {
        int summary = 0;
        for(int i=maxMeasurements; i; i--) {
            summary = summary + measurements[i];
        }
        return summary/measurementsQuantity;
    }

    // Si el valor del sonido supera un cierto umbral (por ejemplo, frecuenciaMinimaSonido), 
    // podrías considerarlo como un ronquido y aumentar el contador de ronquidos (snoreAmount).
    int SnoreSummary(JsonArray measurements) {
        int snores = 0;
        for(int i=maxMeasurements; i; i--) {
            if(measurements[i] > frecuenciaMinimaSonido) {
                snores++;
            } 
        }
        return snores;
    }

    void sendDataToM5Stack(const char *puerto) {
        // Implementa el envío de datos al M5Stack
        // Crear un objeto JSON para almacenar los datos
        StaticJsonDocument<200> jsonDoc;

        // Agrega los valores directamente al objeto JSON
        jsonDoc["averageTemperature"] = averageMeasurements(temperatureMeasurements, tempIndex);
        jsonDoc["SnoreAmount"] = SnoreSummary(soundMeasurements);

        // Serializar el objeto JSON en una cadena
        String jsonString;
        serializeJson(jsonDoc, jsonString);





        // Enviar los datos por UDP al M5Stack
        udp.beginPacket(direccionIP, puerto); // No se cual es la direccion ip del m5stack, 
        //y segun lo que he leido usar upd.remoteIP en este caso no es buena idea








        udp.write(jsonString);
        udp.endPacket();

        // Reiniciar las variables de las medidas
        tempIndex = 0;
    }

    static ESP32* instance;
};

ESP32* ESP32::instance = nullptr;

#endif