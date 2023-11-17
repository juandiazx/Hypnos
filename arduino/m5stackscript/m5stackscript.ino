//Incluimos librerías externas
//--------------------------------------------------------
#include <M5Stack.h>
#include <AsyncUDP.h>
#include <ArduinoJson.h>
#include <WiFi.h>
//--------------------------------------------------------

//Incluimos cabeceras de nuestras clases
//--------------------------------------------------------
#include "M5StackAbstract.h"
//--------------------------------------------------------

//Realizamos nuestras definiciones que no ocupan espacio, acciones de compilador
//--------------------------------------------------------------------------------------
#define udpPort 6230
//#define ssid "TP-LINK_6B36"
//#define password "89776513"
#define ssid "darkasa"
#define password "0Spoilerspls"
//--------------------------------------------------------------------------------------

M5StackAbstract* m5stackAbstract;

void setup() {
    Serial.begin(115200);
    m5stackAbstract = M5StackAbstract::getInstance(ssid,password,udpPort);
    M5.Speaker.begin(); //inicializamos sistema de sonido
}

void loop() {
    m5stackAbstract->printLogoWhiteBackground();
    bool previousButtonState = false;
    bool currentButtonState;
    while (1) {
        currentButtonState = M5.BtnA.read();
        if (currentButtonState != previousButtonState) {
            delay(50);

            // Vuelve a leer el estado del botón después del periodo de espera
            currentButtonState = M5.BtnA.read();

            // Si el estado actual del botón es el mismo que después del periodo de espera, es un cambio válido
            if (currentButtonState == previousButtonState) {
                Serial.println("se ha entrado al if del boton A");
                delay(3000);

                m5stackAbstract->switchLightM5StackAbstract();
                m5stackAbstract->startRestingTrackRoutine();
                m5stackAbstract->receiveSensorsData();

                while (1) {
                    //Cuando ya se ha recibido info de los dos endpoints
                    if (m5stackAbstract->receivedSensorsData == 2) {
                        m5stackAbstract->switchLightM5StackAbstract();
                        delay(2000);
                        m5stackAbstract->showDataInScreen();
                        delay(6000);
                        break;
                    }
                    delay(50);
                }
                break;
            }
        }
        previousButtonState = currentButtonState;
        delay(10);
    }
}