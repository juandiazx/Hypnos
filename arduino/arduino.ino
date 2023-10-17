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
//--------------------------------------------------------

//Realizamos nuestras definiciones que no ocupan espacio, acciones de compilador
//--------------------------------------------------------------------------------------
#define TEMPERATUREPIN 17
#define SOUNDPIN 36
#define LEDPIN 2
//--------------------------------------------------------------------------------------


LedLight* ledLight = LedLight::getInstance(LEDPIN);
SoundSensor* soundSensor = SoundSensor::getInstance(SOUNDPIN);
TemperatureSensor* temperatureSensor = TemperatureSensor::getInstance(TEMPERATUREPIN);

int state;
void setup(){
  M5.begin(); //Init M5Core. Initialize M5Core
  M5.Power.begin(); //Init Power module. Initialize the power module

  M5.Lcd.textsize = 5;
  M5.Lcd.print("Working for now"); // Print text on the screen (string) Print text on the screen (string)
}


void loop() {
  M5.Lcd.setCursor(0,0);
  M5.Lcd.print("                                                     ");

  M5.Lcd.setCursor(0,0);
  M5.Lcd.print(temperatureSensor->takeMeasurement());
  ledLight->turnOn();
  delay(1500);
  ledLight->turnOff();

  delay(400);
}