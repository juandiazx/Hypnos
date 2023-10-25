//Incluimos librerías externas
//--------------------------------------------------------
#include <M5Stack.h>
#include <AsyncUDP.h>
#include <ArduinoJson.h>
//--------------------------------------------------------


//Incluimos cabeceras de nuestras clases
//--------------------------------------------------------

#include "M5StackAbstract.h"
//--------------------------------------------------------

//Realizamos nuestras definiciones que no ocupan espacio, acciones de compilador
//--------------------------------------------------------------------------------------
#define udpPort 6230
//#define ssid "TP-LINK_6CAE"
//#define password "54346615"
#define ssid "darkasa"
#define password "0Spoilerspls"
//--------------------------------------------------------------------------------------


M5StackAbstract* m5stackAbstract;

void setup() {
    Serial.begin(115200);
    m5stackAbstract = M5StackAbstract::getInstance(ssid,password,udpPort);
}


void loop() {
    // Tomar medida de temperatura y sonido
    int temperature = temperatureSensor->takeMeasurement();
    int soundValue = soundSensor->takeMeasurement();

    // Imprimir los resultados por el puerto serie
    Serial.print("Temperatura: ");
    Serial.print(temperature);
    Serial.print("°C, Sonido: ");
    Serial.println(soundValue);

    // Encender el LED durante 3 segundos
    ledLight->turnOn();
    delay(3000);
    ledLight->turnOff();

    // Esperar un segundo antes de tomar la siguiente medida
    delay(1000);
}
