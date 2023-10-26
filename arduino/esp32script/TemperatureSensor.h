#ifndef TEMPERATURESENSOR_H
#define TEMPERATURESENSOR_H

#include "Sensor.h"

class TemperatureSensor : public Sensor {
public:
    // Método estático para obtener la instancia del sensor de temperatura (Singleton)
    static TemperatureSensor *getInstance(int pin){
        if (instance == nullptr) {
            instance = new TemperatureSensor(pin);
        }
        return instance;
    };

    // Override método para tomar una medida de temperatura
    int takeMeasurement(){
        //Transformar esto a un while que tome muchas temperaturas y tambien humedad
        //que saque luego la media ponderada con la humedad
        return static_cast<int>(dht.readTemperature());
    };

private:
    // Constructor privado para TemperatureSensor
    TemperatureSensor(int pin) : sensorPin(pin), dht(pin, DHT11) {
        dht.begin();
    } // Inicializa el objeto TemperatureSensor con el número de pin especificado

    // Instancia única del sensor de temperatura
    static TemperatureSensor* instance;

    int sensorPin;
    DHT dht;
};
TemperatureSensor* TemperatureSensor::instance = nullptr;
#endif


//Ver si el DHT se declara asi y funciona

