#ifndef LEDLIGHT_H
#define LEDLIGHT_H

class LedLight {
public:
    // M�todo para tomar una medida
    void turnOn(){
        if(!isLightOn){
            isLightOn = true;
            digitalWrite(lightPin,HIGH);
        }
    };
    void turnOff(){
        if(isLightOn){
            isLightOn = false;
            digitalWrite(lightPin,LOW);
        }
    };

    // Método estático para obtener la instancia del LedLight (Singleton)
    static LedLight *getInstance(int pin){
        if (instance == nullptr) {
            instance = new LedLight(pin);
        }
        return instance;
    };

private:
    // Constructor privado para LedLight
    LedLight(int pin){
        lightPin = pin;
        isLightOn = false;
        pinMode(lightPin, OUTPUT);
    }; // Inicializa el objeto LedLight con el número de pin especificado
    
    // Instancia única del sensor de sonido
    static LedLight *instance;

    int lightPin;

    bool isLightOn;
};

LedLight* LedLight::instance = nullptr;

#endif


