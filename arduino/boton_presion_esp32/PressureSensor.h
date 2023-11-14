#ifndef PRESSURESENSOR_H
#define PRESSURESENSOR_H

#include "Sensor.h"

class PressureSensor : public Sensor {
public:
    int LedPin_2 = 2;
    unsigned long tiempoEncendido = 0;
    unsigned long tiempoInicio = 0;  // Variable para almacenar el tiempo en que se encendió el LED
    int estadoAnterior = LOW;        // Variable para almacenar el estado anterior del botón

    // Método estático para obtener la instancia del sensor de presión (Singleton)
    static PressureSensor *getInstance(int BOTONPIN, int LEDPIN_2){
        if (instance == nullptr) {
            instance = new PressureSensor(BOTONPIN, LEDPIN_2);
        }
        return instance;
    };


void detectPressure() {
  int estadoBoton = digitalRead(sensorPin);  // Leemos el estado del botón

  if (estadoBoton == HIGH && estadoAnterior == LOW) {
    digitalWrite(ledPin_2, HIGH); // Enciende el LED
    tiempoInicio = millis();    // Guarda el tiempo en que se encendió el LED
    Serial.println("Botón presionado - LED encendido");
    delay(50);  // Espera breve para evitar rebotes
  } else if (estadoBoton == LOW && estadoAnterior == HIGH) {
    digitalWrite(ledPin_2, LOW);  // Apaga el LED
    tiempoEncendido += millis() - tiempoInicio; // Calcula el tiempo que estuvo encendido el LED
    tiempoInicio = 0;
    Serial.println("El LED estuvo encendido durante: ");
    Serial.println(tiempoEncendido); //serial original
    Serial.println(" milisegundos");
    Serial.println("Botón liberado - LED apagado");
    delay(50);  // Espera breve para evitar rebotes
  }
  estadoAnterior = estadoBoton;
  Serial.println("Tiempos");
  Serial.println(tiempoEncendido);
  Serial.println(tiempoInicio);
  delay(500);
}

private:

    int ledPin_2;

    // Constructor privado para PressureSensor
    PressureSensor(int BOTONPIN, int LEDPIN_2){
        sensorPin = BOTONPIN;
        ledPin_2 = LEDPIN_2;
        tiempoEncendido = 0;
        pinMode(sensorPin,INPUT);
        pinMode(ledPin_2,OUTPUT);
    }; // Inicializa el objeto PressureSensor con el número de pin especificado

    // Instancia única del sensor de presión
    static PressureSensor* instance;
};

PressureSensor* PressureSensor::instance = nullptr;

#endif
