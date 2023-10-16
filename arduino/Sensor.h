#ifndef SENSOR_H
#define SENSOR_H

class Sensor {
public:
    // Método para tomar una medida
    void takeMeasurement();

    // Métodos para obtener información sobre la medida
    int getRawValue() const; // Devuelve el valor crudo del sensor
    float getVoltage() const; // Devuelve el voltaje del sensor en volts

protected:
    // Constructor protegido
    Sensor(int pin); // Inicializa el objeto Sensor con el número de pin especificado

private:
    int sensorPin; // Número de pin al que está conectado el sensor
    int rawValue; // Valor crudo del sensor
    float voltage; // Voltaje del sensor en volts
};

#endif


