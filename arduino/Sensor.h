#ifndef SENSOR_H
#define SENSOR_H

class Sensor {
public:
    // M�todo para tomar una medida
    void takeMeasurement();

    // M�todos para obtener informaci�n sobre la medida
    int getRawValue() const; // Devuelve el valor crudo del sensor
    float getVoltage() const; // Devuelve el voltaje del sensor en volts

protected:
    // Constructor protegido
    Sensor(int pin); // Inicializa el objeto Sensor con el n�mero de pin especificado

private:
    int sensorPin; // N�mero de pin al que est� conectado el sensor
    int rawValue; // Valor crudo del sensor
    float voltage; // Voltaje del sensor en volts
};

#endif


