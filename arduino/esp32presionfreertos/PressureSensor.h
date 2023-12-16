#ifndef PRESSURESENSOR_H
#define PRESSURESENSOR_H

#include "Sensor.h"
#include <freertos/FreeRTOS.h>
#include <freertos/task.h>

class PressureSensor : public Sensor {
public:
    unsigned long tiempoEncendido = 0;
    unsigned long tiempoInicio = 0;  // Variable para almacenar el tiempo en que se encendió el LED
    int estadoAnterior = LOW;        // Variable para almacenar el estado anterior del botón

    static PressureSensor *getInstance(int BOTONPIN){
      if (instance == nullptr) {
        instance = new PressureSensor(BOTONPIN);
        xTaskCreate(&PressureSensor::taskFunction, "PressureTask", 2048, instance, 1, NULL)
      }
      return instance;
    };

  static void taskFunction(void *pvParameters) {
    PressureSensor *sensorInstance = static_cast<PressureSensor *>(pvParameters);
    while (1) {
      sensorInstance->detectPressure();
      vTaskDelay(500 / portTICK_PERIOD_MS);  // Adjust the delay as needed
    }
  }

  void detectPressure() {
    int estadoBoton = digitalRead(sensorPin);  // Leemos el estado del botón

    if (estadoBoton == HIGH && estadoAnterior == LOW) {
      tiempoInicio = millis();    // Guarda el tiempo en que se encendió el LED
      vTaskDelay(50 / portTICK_PERIOD_MS);  // Espera breve para evitar rebotes
    } 
    else if (estadoBoton == LOW && estadoAnterior == HIGH) {
      tiempoEncendido += millis() - tiempoInicio; // Calcula el tiempo que estuvo encendido el LED
      tiempoInicio = 0;
      vTaskDelay(50 / portTICK_PERIOD_MS);  // Espera breve para evitar rebotes
    }
    estadoAnterior = estadoBoton;
  }

  private:
    PressureSensor(int BOTONPIN){
      sensorPin = BOTONPIN;
      tiempoEncendido = 0;
      pinMode(sensorPin,INPUT);
    };
    static PressureSensor* instance;
  };

PressureSensor* PressureSensor::instance = nullptr;

#endif