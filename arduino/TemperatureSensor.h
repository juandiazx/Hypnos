#ifndef TEMPERATURESENSOR_H
#define TEMPERATURESENSOR_H

#include "Sensor.h"

class TemperatureSensor : public Sensor {
public:
    // Constructor público para el sensor de temperatura
    TemperatureSensor(int pin); // Inicializa el objeto TemperatureSensor con el número de pin especificado
    
    // Método para tomar una medida de temperatura
    void takeTemperatureMeasurement();
};

#endif

