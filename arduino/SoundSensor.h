#ifndef SOUNDSENSOR_H
#define SOUNDSENSOR_H

#include "Sensor.h"

class SoundSensor : public Sensor {
public:
    // Constructor p�blico para el sensor de sonido
    SoundSensor(int pin); // Inicializa el objeto SoundSensor con el n�mero de pin especificado
    
    // M�todo para tomar una medida de sonido
    void takeSoundMeasurement();
};

#endif

