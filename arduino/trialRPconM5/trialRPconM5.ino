//Incluimos librerías externas
//--------------------------------------------------------
/*
#include <AsyncUDP.h>
#include <ArduinoJson.h>
#include <WiFi.h>
*/
#include <M5Stack.h>
//--------------------------------------------------------


//Incluimos cabeceras de nuestras clases
//--------------------------------------------------------

//--------------------------------------------------------

//Realizamos nuestras definiciones que no ocupan espacio, acciones de compilador
//--------------------------------------------------------------------------------------
#define udpPort 6230
#define ssid "TP-LINK_6B36"
#define password "89776513"
//--------------------------------------------------------------------------------------

/*
void receiveSensorsData() {
    udp.onPacket([this](AsyncUDPPacket &packet) {
        char packetBuffer[UDP_TX_PACKET_MAX_SIZE];
        int len = packet.length();
        
        // Leer los datos del paquete en el buffer
        for (int i = 0; i < len; i++) {
            packetBuffer[i] = packet.data()[i];
        }
        packetBuffer[len] = '\0';

        // Parsear el JSON recibido
        DynamicJsonDocument jsonDoc(200);  // Ajusta el tamaño según tus necesidades
        DeserializationError error = deserializeJson(jsonDoc, packetBuffer);

        if (!error) {
            // Acceder a los valores recibidos y almacenarlos según tus necesidades
            Serial.println("Recibe los datos");
            this->averageTemperature = jsonDoc["averageTemperature"];
            this->snoreAmount = jsonDoc["snoreAmount"];
            Serial.println(averageTemperature);
            Serial.println(snoreAmount);
        } else {
            Serial.println("Error al analizar los datos JSON recibidos");
        }
    });
}

void openUDPConnection(){
        WiFi.mode(WIFI_STA);
        WiFi.begin(ssidWifi, passwordWifi);
        if(WiFi.waitForConnectResult()!=WL_CONNECTED) {
            Serial.println("Conectando a WiFi...");
            while(1) {
              delay(1000);
            }
        }

        Serial.println("Conexión WiFi establecida");

        if(udp.listen(udpPort)) {
        Serial.print("UDP Listening on IP: ");
        Serial.println(WiFi.localIP());
        }
    }
*/
void setup() {
  M5.begin();
  Serial.begin(115200);  // Configura la velocidad de baudios según tus necesidades
  Serial.setTimeout(1000);  // Establece el timeout en 1000 milisegundos (1 segundo)

  // Inicializa la pantalla
  M5.Lcd.begin();
  M5.Lcd.fillScreen(TFT_BLACK);  // Rellena la pantalla de negro

  // Muestra un símbolo en la pantalla en el setup
  M5.Lcd.setCursor(100, 100);  // Establece la posición del cursor
  M5.Lcd.setTextColor(TFT_WHITE);  // Establece el color del texto
  M5.Lcd.setTextSize(3);  // Establece el tamaño del texto
  M5.Lcd.print("H");  // Imprime el símbolo en la pantalla
  delay(2000);  // Espera 2 segundos antes de borrar el símbolo
  M5.Lcd.fillScreen(TFT_BLACK);  // Limpia la pantalla
}


void loop() {
  // Envia datos por UART cuando se presiona el botón A
    // Datos a enviar por UART (puedes ajustar esto según tus necesidades)
    String dataToSend = "Hola desde M5Stack";

    // Enviar datos por la interfaz UART
    Serial.print(dataToSend);
    Serial.flush();  // Asegura que los datos se envíen completamente
    delay(2000);
  // Aquí puedes agregar más lógica según sea necesario
}

