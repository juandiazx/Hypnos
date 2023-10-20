#ifndef SOUNDSENSOR_H
#define SOUNDSENSOR_H

#include "Sensor.h"

class SoundSensor : public Sensor {
public:
    // Método estático para obtener la instancia del sensor de sonido (Singleton)
    static SoundSensor *getInstance(int pin){
        if (instance == nullptr) {
            instance = new SoundSensor(pin);
        }
        return instance;
    };

    // Override método para tomar una medida de sonido
    int takeMeasurement(){
        return analogRead(sensorPin);
    };

private:
    // Constructor privado para SoundSensor
    SoundSensor(int pin){
        sensorPin = pin;
        pinMode(sensorPin,INPUT);
    }; // Inicializa el objeto SoundSensor con el número de pin especificado
    
    // Instancia única del sensor de sonido
    static SoundSensor* instance;
};

SoundSensor* SoundSensor::instance = nullptr;

#endif

