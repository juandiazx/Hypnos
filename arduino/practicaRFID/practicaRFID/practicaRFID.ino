//instalar libreria de: https://github.com/miguelbalboa/rfid 


//PRUEBA PARA VER EL ID DE LA TAG

#include <SPI.h>
#include <MFRC522.h>
#include <M5Stack.h>

#define SDA_PIN 21
#define RST_PIN 2 //no lo he conectado

MFRC522 mfrc522(SDA_PIN, RST_PIN); //instancia de MFRC522

void printArray(byte *buffer, byte bufferSize) {
  for (byte i = 0; i < bufferSize; i++) {
    Serial.print(buffer[i] < 0x10 ? " 0" : " ");
    Serial.print(buffer[i], HEX);
  }
}

void setup() {
  
  Serial.begin(9600);
  SPI.begin(); //Función que inicializa SPI
  mfrc522.PCD_Init(); //Función  que inicializa RFID
}

void loop() {
  
  //Detectar targeta
  if(mfrc522.PICC_IsNewCardPresent())
  {
    if(mfrc522.PICC_ReadCardSerial())
    {
      Serial.print(F("Card UID: "));
      printArray(mfrc522.uid.uidByte, mfrc522.uid.size);
      Serial.println();

      //Finalizar lectura actual:
      mfrc522.PICC_HaltA();
    }
  }

delay(250);

}
