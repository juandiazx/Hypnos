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

    void sendDataToM5Stack() {
        // Implementa el envío de datos al M5Stack
    }

    void obtainSensorsData() {
        // Implementa la obtención de datos de los sensores (temperatureSensor, soundSensor, etc.)
    }

    void switchLightOnOff() {
        // Implementa el encendido/apagado de la luz (ledLight)
    }

private:
    int udpPort;
    TemperatureSensor* temperatureSensor;
    SoundSensor* soundSensor;
    LedLight* ledLight;

    ESP32(const char *ssid, const char *pass, int udp) {
        udpPort = udp;
        temperatureSensor = TemperatureSensor::getInstance();  // Crea una instancia del sensor de temperatura
        soundSensor = SoundSensor::getInstance();  // Crea una instancia del sensor de sonido
        ledLight = LedLight::getInstance();  // Crea una instancia del controlador de luz LED
        M5StackAbstract::getInstance(ssid, pass, udp);  // Crea una instancia del M5Stack
    }

    static ESP32* instance;
};

ESP32* ESP32::instance = nullptr;

#endif
