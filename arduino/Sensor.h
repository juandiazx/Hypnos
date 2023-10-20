#ifndef SENSOR_H
#define SENSOR_H

class Sensor {
public:
    // M�todo para tomar una medida
    int takeMeasurement();

protected:
    int sensorPin; // N�mero de pin al que est� conectado el sensor
};

#endif


