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
  int temperatura = temperatureSensor->takeMeasurement() ;
  int sonido = soundSensor->takeMeasurement();

  if(sonido > 550) ledLight->turnOn();
  else ledLight->turnOff();
  M5.Lcd.fillScreen(BLACK); // Borra la pantalla
  M5.Lcd.setCursor(10, 10); // Establece la posición del cursor en la pantalla
  M5.Lcd.setTextColor(WHITE); // Establece el color del texto
  M5.Lcd.println("Temperatura: " + String(temperatura) + " C"); // Muestra la temperatura en la pantalla
  M5.Lcd.println("Sonido: " + String(sonido) + " Anlg"); // Muestra la humedad en la pan

  delay(100);
}