#ifndef SENSOR_H
#define SENSOR_H

class Sensor {
public:
    // M�todo para tomar una medida
    void takeMeasurement();

protected:
    // Constructor protegido
    Sensor(int pin); // Inicializa el objeto Sensor con el n�mero de pin especificado
    int sensorPin; // N�mero de pin al que est� conectado el sensor
};

#endif


