#ifndef M5STACKABSTRACT_H
#define M5STACKABSTRACT_H

class M5StackAbstract {
public:
    static M5StackAbstract* getInstance(const char *ssid, const char *pass, int udp) {
        if (instance == nullptr) {
            instance = new M5StackAbstract(ssid, pass, udp);
            instance->initializeM5StackAbstract();
        }
        return instance;
    }

    void switchLightM5StackAbstract() {
        int steps = 5;  // Número de pasos para la transición gradual de color (incluyendo el color inicial y final)
        uint32_t targetColor;
        isNight = !isNight;
        if (isNight) {
            M5.Lcd.fillScreen(0x000000);  // Negro (Noche)
            M5.Lcd.fillCircle(110, 60, 20, WHITE);
            M5.Lcd.fillCircle(120, 55, 20, BLACK);
        } else {
            M5.Lcd.fillScreen(0xFFFFFF);  // Blanco (Día)
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
    void showDataInScreen(int snoreAmount,int averageTemperature) {
        M5.Lcd.fillScreen(WHITE); // Borra la pantalla
        M5.Lcd.textsize = 3;
        M5.Lcd.setCursor(0,0); // Establece la posición del cursor en la pantalla
        M5.Lcd.setTextColor(0x164499); // Establece el color del texto
        M5.Lcd.println("Temperatura: " + String(averageTemperature) + " C"); // Muestra la temperatura en la pantalla
        M5.Lcd.println("Ronquidos: " + String(snoreAmount));
    }

    void printLogoWhiteBackground() {
        M5.Lcd.textsize = 6;
        M5.Lcd.fillScreen(WHITE);
        M5.Lcd.setTextColor(0x164499);  // Color del texto azul
        M5.Lcd.setCursor(55, 85);
        M5.Lcd.print("Hypnos");

        M5.Lcd.textsize = 2;
        M5.Lcd.setCursor(45, 200);
        M5.Lcd.print("Boton 1 para empezar");
        //Dibujar luna
        M5.Lcd.fillCircle(110, 60, 20, 0x164499); // Coordenadas (170, 50), radio 20, color del texto azul
        M5.Lcd.fillCircle(120, 55, 20, WHITE);    // Otra luna blanca más fina
    }

private:
    //int snoreAmount;
    //int averageTemperature;
    bool isNight = false;
    int udpPort;
    char ssidWifi[32]; // Array para almacenar el SSID, con un máximo de 32 caracteres incluyendo el carácter nulo '\0'
    char passwordWifi[64];
    uint16_t mainColor = 0x164499;


    M5StackAbstract(const char *ssid, const char *pass, int udp) {
        strncpy(ssidWifi, ssid, sizeof(ssidWifi) - 1); // Copia el SSID a ssidWifi, asegurando que no sobrepase el tamaño del array
        strncpy(passwordWifi, pass, sizeof(passwordWifi) - 1); // Copia la contraseña a passwordWifi, asegurando que no sobrepase el tamaño del array
        ssidWifi[sizeof(ssidWifi) - 1] = '\0'; // Asegura que ssidWifi tenga el carácter nulo al final
        passwordWifi[sizeof(passwordWifi) - 1] = '\0'; // Asegura que passwordWifi tenga el carácter nulo al final
        udpPort = udp;
    }

    void initializeM5StackAbstract() {
        M5.begin(); //Init M5Core. Initialize M5Core
        M5.Power.begin(); //Init Power module. Initialize the power module
    }
    void receiveSensorsData(){

    }
    void openUDPConnection(){
        
    }

    static M5StackAbstract* instance;
};

M5StackAbstract* M5StackAbstract::instance = nullptr;

#endif




