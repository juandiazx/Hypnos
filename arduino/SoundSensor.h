#ifndef SOUNDSENSOR_H
#define SOUNDSENSOR_H

#include "Sensor.h"

class SoundSensor : public Sensor {
public:
    // Método estático para obtener la instancia del sensor de sonido (Singleton)
    static SoundSensor* getInstance(int pin);

    // Método para tomar una medida de sonido
    void takeSoundMeasurement();

private:
    // Constructor privado para SoundSensor
    SoundSensor(int pin); // Inicializa el objeto SoundSensor con el número de pin especificado
    
    // Instancia única del sensor de sonido
    static SoundSensor* instance;
};

#endif

