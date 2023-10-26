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
    m5stackAbstract->printLogoWhiteBackground();
    Serial.println("se ha mostrado el logo");
    while (1){
        if(M5.BtnA.read()){
          Serial.println("se ha entrado al if del boton A");
            delay(3000);

            m5stackAbstract->switchLightM5StackAbstract();
            m5stackAbstract->startRestingTrackRoutine();
            while(1){
                if(M5.BtnB.read()){
                    Serial.println("se ha entrado al if del boton B");
                    m5stackAbstract->stopRestingTrackRoutine();
                    m5stackAbstract->switchLightM5StackAbstract();
                    delay(2000);
                    m5stackAbstract->showDataInScreen();
                    while(1){
                        if(M5.BtnC.read()){
                          Serial.println("se ha entrado al if del boton C");
                            break;
                        }
                    }
                    break;
                }
            }
            break;
        }
    }
}
