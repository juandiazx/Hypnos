//Incluimos librerías externas
//--------------------------------------------------------
#include <M5Stack.h>
#include <DHT.h>
//--------------------------------------------------------


//Incluimos cabeceras de nuestras clases
//--------------------------------------------------------
#include "SoundSensor.h"
#include "TemperatureSensor.h"
#include "LedLight.h"
#include "M5StackAbstract.h"
//--------------------------------------------------------

//Realizamos nuestras definiciones que no ocupan espacio, acciones de compilador
//--------------------------------------------------------------------------------------
#define TEMPERATUREPIN 17
#define SOUNDPIN 36
#define LEDPIN 2
#define udpPort 6230
#define ssid "TP-LINK_6CAE"
#define password "54346615"
//--------------------------------------------------------------------------------------


LedLight* ledLight;
SoundSensor* soundSensor;
TemperatureSensor* temperatureSensor;
M5StackAbstract* m5stackAbstract;

void setup(){
  // Inicializamos las instancias dentro de setup
  ledLight = LedLight::getInstance(LEDPIN);
  soundSensor = SoundSensor::getInstance(SOUNDPIN);
  temperatureSensor = TemperatureSensor::getInstance(TEMPERATUREPIN);
  m5stackAbstract = M5StackAbstract::getInstance(ssid,password,udpPort);
}


void loop() {
  m5stackAbstract->printLogoWhiteBackground();
  while(1){
    if(M5.BtnA.read()){
      delay(3000);

      m5stackAbstract->switchLightM5StackAbstract();
      ledLight->turnOn();

      delay(2000);

      ledLight->turnOff();

      delay(5000);

      int temperatura = temperatureSensor->takeMeasurement() ;
      int sonido = soundSensor->takeMeasurement();
      m5stackAbstract->switchLightM5StackAbstract();
      ledLight->turnOn();

      delay(2000);

      ledLight->turnOff();
      m5stackAbstract->showDataInScreen(sonido, temperatura);
      
      delay(5000);
      break;
    }
  }
}