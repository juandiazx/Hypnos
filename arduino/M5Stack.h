#ifndef M5STACK_H
#define M5STACK_H

class M5Stack {
public:
    static M5Stack *getInstance(char ssid,char pass,int udp) {
        if (instance == nullptr) {
            instance = new M5Stack(ssid,pass,udp);
            instance->initializeM5Stack();
        }
        return instance;
    }

    void switchLightM5Stack() {
        if (isNight) {
            M5.Lcd.fillScreen(mainColor);
            M5.Lcd.setTextColor(WHITE);  // Color del texto blanco
            //Pon
        } else {
            M5.Lcd.fillScreen(WHITE);
            M5.Lcd.setTextColor(mainColor);  // Color del texto azul
        }
    }

    //Cuando boton A se pulsa por 1,5 segundos en loop()
    void startRestingTrackRoutine() {
        //
        // Aquí se reciben los datos por UTP y se guardan en snoreAmount y averageTemperature
    }

    //Cuando el boton B se pulsa por 1,5 segundos en loop()
    void stopRestingTrackRoutine(){

    }

    //Cuando el boton C se pulsa por 1,5 segundos en loop() se muestran lo
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
    int snoreAmount;
    int averageTemperature;
    bool isNight = false;
    int udpPort;
    char ssidWifi;
    char passwordWifi;
    uint32_t mainColor = 0x164499;


    M5Stack(char ssid,char pass,int udp){
        ssidWifi = ssid;
        passwordWifi = pass;
        udpPort = udp;
    }

    void initializeM5Stack() {
        M5.begin(); //Init M5Core. Initialize M5Core
        M5.Power.begin(); //Init Power module. Initialize the power module
        M5.Lcd.textsize = 5;
        M5.Lcd.fillScreen(WHITE);
        M5.Lcd.setTextColor(mainColor);  // Color del texto azul
        //Poner pantalla en blanca con nuestro logo y indicación para comenzar
    }

    void receiveSensorsData(){

    }

    static M5Stack* instance;
};

M5Stack* M5Stack::instance = nullptr;

#endif




