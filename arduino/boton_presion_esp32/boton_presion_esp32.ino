//Incluimos librerías externas
//--------------------------------------------------------
#include <AsyncUDP.h>
#include <ArduinoJson.h>
#include <WiFi.h>
//--------------------------------------------------------

//Incluimos cabeceras de nuestras clases
//--------------------------------------------------------
#include "PressureSensor.h"
#include "ESP32Abstract.h"
//--------------------------------------------------------

//Realizamos nuestras definiciones que no ocupan espacio, acciones de compilador
//--------------------------------------------------------------------------------------
#define udpPort 6230
#define ssid "TP-LINK_6B36"
#define password "89776513"
//#define ssid "darkasa"
//#define password "0Spoilerspls"
#define PinDelSensor 4
#define PinDelLed 2

PressureSensor* pressureSensor;
ESP32Abstract* esp32Abstract;

void setup() {
  Serial.begin(115200);      // Inicializamos la comunicación serie a 115200
  //Serial.setTimeout(1000);  // Establece el timeout en 1000 milisegundos (1 segundo)
  esp32Abstract = ESP32Abstract::getInstance(ssid, password, udpPort, PinDelSensor, PinDelLed);
}

void loop() {
  esp32Abstract->listenForMessages();
  delay(4000);
}
