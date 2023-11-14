//Incluimos librerías externas
//--------------------------------------------------------
#include <DHT.h>
#include <AsyncUDP.h>
#include <ArduinoJson.h>
#include <WiFi.h>
#include <vector>
//--------------------------------------------------------

//Incluimos cabeceras de nuestras clases
//--------------------------------------------------------
#include "PressureSensor.h"
#include "ESP32Abstract.h"
//--------------------------------------------------------

//Realizamos nuestras definiciones que no ocupan espacio, acciones de compilador
//--------------------------------------------------------------------------------------
#define udpPort 6230
//#define ssid "TP-LINK_6B36"
//#define password "89776513"
#define ssid "darkasa"
#define password "0Spoilerspls"

#define PinDelSensor 4  // Pin del botón en el Wemos Lolin32
#define PinDelLed 2  // Pin del LED en el Wemos Lolin32

PressureSensor* pressureSensor;
ESP32Abstract* esp32Abstract;

void setup() {
  Serial.begin(115200);      // Inicializamos la comunicación serie a 115200
  esp32Abstract = ESP32Abstract::getInstance(ssid, password, udpPort, PinDelSensor, PinDelLed);
}

void loop() {
  esp32Abstract->listenForMessages();
  delay(4000);
}
