#ifndef M5STACK_H
#define M5STACK_H

class M5Stack {
public:
    static M5Stack *getInstance() {
        if (instance == nullptr) {
            instance = new M5Stack();
            instance->initializeM5Stack();
        }
        return instance;
    }

    void switchLightOnOff(bool on) {
        //Si pulsamos boton se enciende, si pulsamos otra vez se apaga
        //Se envia por UTP al ESP32 y ahi se ordena al LED a apagarse o encenderse basado en si
        //ya estaba encendido o apagado
        if (on) {
            M5.Lcd.fillScreen(TFT_WHITE);
        } else {
            M5.Lcd.fillScreen(TFT_BLACK);
        }
    }

    void obtainSensorsData() {
        // Aquí se reciben los datos por UTP
    }

    void showDataInScreen() {
        // Aquí muestras los datos en la pantalla del dispositivo M5Stack
        /*
        M5.Lcd.fillScreen(BLACK); // Borra la pantalla
        M5.Lcd.setCursor(10, 10); // Establece la posición del cursor en la pantalla
        M5.Lcd.setTextColor(WHITE); // Establece el color del texto
        M5.Lcd.println("Temperatura: " + String(temperatura) + " C"); // Muestra la temperatura en la pantalla
        M5.Lcd.println("Sonido: " + String(sonido) + " Anlg"); // Muestra la humedad en la pan*/
    }

private:
    M5Stack();

    void initializeM5Stack() {
        M5.begin(); //Init M5Core. Initialize M5Core
        M5.Power.begin(); //Init Power module. Initialize the power module
        M5.Lcd.textsize = 5;
    }

    static M5Stack* instance;
};

M5Stack* M5Stack::instance = nullptr;

#endif




