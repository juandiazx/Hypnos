#ifndef TEMPERATURESENSOR_H
#define TEMPERATURESENSOR_H

#include "Sensor.h"

class TemperatureSensor : public Sensor {
public:
    // Constructor p�blico para el sensor de temperatura
    TemperatureSensor(int pin); // Inicializa el objeto TemperatureSensor con el n�mero de pin especificado
    
    // M�todo para tomar una medida de temperatura
    void takeTemperatureMeasurement();
};

#endif

