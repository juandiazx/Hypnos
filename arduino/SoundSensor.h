#ifndef SOUNDSENSOR_H
#define SOUNDSENSOR_H

#include "Sensor.h"

class SoundSensor : public Sensor {
public:
    // Constructor público para el sensor de sonido
    SoundSensor(int pin); // Inicializa el objeto SoundSensor con el número de pin especificado
    
    // Método para tomar una medida de sonido
    void takeSoundMeasurement();
};

#endif

