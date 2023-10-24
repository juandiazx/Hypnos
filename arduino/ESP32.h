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

    void sendDataToM5Stack(const char *direccionIP, const char *puerto) {
        // Implementa el envío de datos al M5Stack
         // Crear un objeto JSON para almacenar los datos
        StaticJsonDocument<512> jsonDoc;

        // Crear un array JSON para las medidas de temperatura
        JsonArray temperatureArray = jsonDoc.createNestedArray("temperatureMeasurements");
        for (int i = 0; i < measurementsCount; i++) {
          temperatureArray.add(temperatureMeasurements[i]);
        }

        // Crear un array JSON para las medidas de sonido
        JsonArray soundArray = jsonDoc.createNestedArray("soundMeasurements");
        for (int i = 0; i < measurementsCount; i++) {
          soundArray.add(soundMeasurements[i]);
        }

        // Serializar el objeto JSON en un buffer
        char buffer[512];
        size_t bufferSize = serializeJson(jsonDoc, buffer);

        // Enviar los datos por UDP al M5Stack
        udp.beginPacket(direccionIP, puerto); // Reemplazar la dirección IP con la del M5Stack (no se cual es)
        udp.write(buffer, bufferSize);
        udp.endPacket();

        // Reiniciar las variables de las medidas
        measurementsCount = 0;
    }

    void obtainSensorsData() {
        // Implementa la obtención de datos de los sensores (temperatureSensor, soundSensor, etc.)
        unsigned long currentTime = millis();

        if (currentTime - previousSensorMeasurementTime >= sensorMeasurementInterval) {
            int temperatura = temperatureSensor->takeMeasurement();
            int sonido = soundSensor->takeMeasurement();

            // Almacena las medidas en los arrays
            temperatureMeasurements[measurementIndex] = temperatura;
            soundMeasurements[measurementIndex] = sonido;

            // Actualiza el índice de la medida y asegura que no exceda el tamaño del array
            measurementIndex = (measurementIndex + 1) % sizeof(temperatureMeasurements);

            previousSensorMeasurementTime = currentTime;
        }
    }

    // void switchLightOnOff() {
    //     // Implementa el encendido/apagado de la luz (ledLight)
    // }
    //creo que no es necesario este metodo

private:
    int udpPort;

    TemperatureSensor* temperatureSensor;
    SoundSensor* soundSensor;
    LedLight* ledLight;

    unsigned int previousSensorMeasurementTime = 0;
    unsigned int sensorMeasurementInterval = 5000;
    // Son los milisegundos que separan cada medida tomada
    unsigned int maxMeasurements = 120;
    // 10 minutos de medidas
    int temperatureMeasurements[maxMeasurements];
    int soundMeasurements[maxMeasurements];
    unsigned int measurementIndex = 0;

    ESP32(const char *ssid, const char *pass, int udp, int TEMPERATUREPIN, int SOUNDPIN, int LEDPIN) {
        udpPort = udp;
        temperatureSensor = TemperatureSensor::getInstance(TEMPERATUREPIN);  // Crea una instancia del sensor de temperatura
        soundSensor = SoundSensor::getInstance(SOUNDPIN);  // Crea una instancia del sensor de sonido
        ledLight = LedLight::getInstance(LEDPIN);  // Crea una instancia del controlador de luz LED
        M5StackAbstract::getInstance(ssid, pass, udp);  // Crea una instancia del M5Stack
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
                    sendDataToM5Stack();
                }
            }
        }
    }

    static ESP32* instance;
};

ESP32* ESP32::instance = nullptr;

#endif
