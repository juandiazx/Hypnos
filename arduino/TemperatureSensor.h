#ifndef TEMPERATURESENSOR_H
#define TEMPERATURESENSOR_H

#include "Sensor.h"

class TemperatureSensor : public Sensor {
public:
    // Método estático para obtener la instancia del sensor de temperatura (Singleton)
    static TemperatureSensor* getInstance(int pin);

    // Método para tomar una medida de temperatura
    void takeTemperatureMeasurement();

private:
    // Constructor privado para TemperatureSensor
    TemperatureSensor(int pin); // Inicializa el objeto TemperatureSensor con el número de pin especificado

    // Instancia única del sensor de temperatura
    static TemperatureSensor* instance;
};

#endif

