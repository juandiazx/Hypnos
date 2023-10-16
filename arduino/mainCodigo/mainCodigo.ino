#include <M5Stack.h>
// Incluimos librería
#include <DHT.h>

// Definimos el pin digital donde se conecta el sensor
#define DHTPIN 16
// Dependiendo del tipo de sensor
#define DHTTYPE DHT11
 
// Inicializamos el sensor DHT11
DHT dht(DHTPIN, DHTTYPE);

int state;
void setup(){
  M5.begin(); //Init M5Core. Initialize M5Core
  M5.Power.begin(); //Init Power module. Initialize the power module

   // Comenzamos el sensor DHT
  dht.begin();
  M5.Lcd.textsize = 5;
  M5.Lcd.print("Working for now"); // Print text on the screen (string) Print text on the screen (string)
}


void loop() {
  M5.Lcd.setCursor(0,0);
  M5.Lcd.print("                                                     ");
  float t = dht.readTemperature();
  M5.Lcd.setCursor(0,0);
  M5.Lcd.print(t);
  delay(400);
}