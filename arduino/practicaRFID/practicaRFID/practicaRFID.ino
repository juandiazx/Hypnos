//instalar libreria de: https://github.com/miguelbalboa/rfid 

/*
······GPIO MAP······

RC522 - M5STACK

SDA - 21(SDA)
SCK - 18(SCK)
MOSI - 23(MO)
GND - G
MISO - 19(MI)
3.3V - 3V3

*/
#include <SPI.h>
#include <MFRC522.h>
#include <M5Stack.h>

#define SDA_PIN 21
#define RST_PIN 2

unsigned char data[12] = {'H', 'y', 'p', 'n', 'o', 's', 'A', 'c', 't', 'i', 'v', 'o'};
unsigned char *writeData = data;
unsigned char *str;
unsigned long lastTime = 0;  // Variable para almacenar el tiempo de la última ejecución
bool previousWriteToCardState = true;  // Variable para almacenar el estado anterior de writeToCard
bool writeToCard = false;

MFRC522 mfrc522(SDA_PIN, RST_PIN);

MFRC522::MIFARE_Key key;

//------------------------------------------------------------------
/*
                          printArray()
*/
//------------------------------------------------------------------
void printArray(byte *buffer, byte bufferSize) {
  for (byte i = 0; i < bufferSize; i++) {
    Serial.print((char)buffer[i]);
  }
}

//------------------------------------------------------------------
/*
                        authenticateCard()
*/
//------------------------------------------------------------------
void authenticateCard() {
  MFRC522::StatusCode status;
  byte trailerBlock = 7;

  status = (MFRC522::StatusCode)mfrc522.PCD_Authenticate(MFRC522::PICC_CMD_MF_AUTH_KEY_A, trailerBlock, &key, &(mfrc522.uid));

  if (status != MFRC522::STATUS_OK) {

    Serial.print(F("PCD_Authenticate() failed: "));
    Serial.println(mfrc522.GetStatusCodeName(status));

    //M5.Lcd.println(F("PCD_Authenticate() failed: "));

    return;
  } 
  else{
    Serial.println();
    Serial.println("Autenticación completada.");
  }
}

//------------------------------------------------------------------
/*
                        writeDataToCard()
*/
//------------------------------------------------------------------
void writeDataToCard(byte blockAddr) {
  Serial.print(F("Escribir datos en sector "));
  Serial.print(blockAddr);
  Serial.println(F(" ..."));

  printArray((byte *)data, 12);
  Serial.println();
  MFRC522::StatusCode status = (MFRC522::StatusCode)mfrc522.MIFARE_Write(blockAddr, (byte *)data, 16);

  if (status != MFRC522::STATUS_OK) {
    Serial.print(F("MIFARE_Write() failed: "));
    Serial.println(mfrc522.GetStatusCodeName(status));
  }

}

//------------------------------------------------------------------
/*
                        readDataFromCard()
*/
//------------------------------------------------------------------
void readDataFromCard(byte blockAddr) {
  byte buffer[18];
  byte size = sizeof(buffer);

  Serial.print(F("Leer datos del sector "));
  Serial.print(blockAddr);
  Serial.println(F(" ..."));

  MFRC522::StatusCode status = (MFRC522::StatusCode)mfrc522.MIFARE_Read(blockAddr, buffer, &size);
  if (status != MFRC522::STATUS_OK) {
    Serial.print(F("MIFARE_Read() failed: "));
    Serial.println(mfrc522.GetStatusCodeName(status));
  }

  Serial.print(F("Data in block "));
  Serial.print(blockAddr);
  Serial.println(F(":"));
  printArray(buffer, 12);

  verifyCode(buffer, 12);
}

//------------------------------------------------------------------
/*
                        verifyCode()
*/
//------------------------------------------------------------------
void verifyCode(byte *data, byte dataSize) {
  // Convertir los datos leídos a una cadena
  char buffer[dataSize + 1];
  memcpy(buffer, data, dataSize);
  buffer[dataSize] = '\0'; // Agregar el carácter nulo al final para formar una cadena de caracteres

  // Comparar con "HypnosActivo"
  if (strcmp(buffer, "HypnosActivo") == 0) {
    // Mostrar círculo verde en la pantalla del M5Stack
    M5.Lcd.fillScreen(TFT_BLACK);
    M5.Lcd.fillCircle(160, 120, 50, TFT_GREEN);
    previousWriteToCardState = true;
    delay(2000);
    M5.Lcd.fillScreen(TFT_BLACK);

  } else {
    // Mostrar círculo rojo en la pantalla del M5Stack
    M5.Lcd.fillScreen(TFT_BLACK);
    M5.Lcd.fillCircle(160, 120, 50, TFT_RED);
    previousWriteToCardState = true;
    delay(2000);
    M5.Lcd.fillScreen(TFT_BLACK);
  }
}

//------------------------------------------------------------------
/*
                      ****** SETUP() ******
*/
//------------------------------------------------------------------
void setup() {
  M5.begin();
  Serial.begin(9600);
  SPI.begin();
  mfrc522.PCD_Init();

  for (byte i = 0; i < 6; i++) {
    key.keyByte[i] = 0xFF;
  }

  M5.Lcd.setTextSize(3);
  M5.Lcd.setCursor(0, 0);
}

//------------------------------------------------------------------
/*
                      ****** LOOP() ******
*/
//------------------------------------------------------------------
void loop() {
  M5.update(); // Actualizar el estado del botón

  delay(50);

  if (M5.BtnA.wasPressed()) {
    writeToCard = true; // Indicar que se debe escribir en la tarjeta
    Serial.println();
    Serial.println("Se ha pulsado el boton para escribir");
    Serial.println();
  }

  unsigned long currentTime = millis();

  // Realizar acciones solo si ha pasado al menos 10 segundos desde la última ejecución
  if (currentTime - lastTime >= 1000) {
    lastTime = currentTime;  // Actualizar el tiempo de la última ejecución

    // Verificar si el estado de writeToCard ha cambiado
    if (writeToCard != previousWriteToCardState) {
      previousWriteToCardState = writeToCard;  // Actualizar el estado anterior
      if (writeToCard) {
        //Serial.println();
        //Serial.println("Acerca la tarjeta para escribir");
        M5.Lcd.fillScreen(TFT_BLACK);
        M5.Lcd.setCursor(0, 0);
        M5.Lcd.print("Acerca la tarjeta para escribir");
        
      } else {
        //Serial.println();
        //Serial.println("Acerca la tarjeta para leer");
        M5.Lcd.fillScreen(TFT_BLACK);
        M5.Lcd.setCursor(0, 0);
        M5.Lcd.print("Acerca la tarjeta para leer o pulsa el boton A para escribir");
      }
    }

    if (!mfrc522.PICC_IsNewCardPresent())
      return;

    if (!mfrc522.PICC_ReadCardSerial())
      return;

    Serial.println();
    Serial.println("¡Tarjeta detectada! Autenticando...");

    authenticateCard();

    byte blockAddr = 4;

    if (writeToCard) {
      Serial.println();
      Serial.println("Se ha llamado a escribir");
      writeDataToCard(blockAddr);
      writeToCard = false; // Restablecer la variable después de escribir
    } else {
      Serial.println();
      Serial.println("Se ha llamado a leer");
      readDataFromCard(blockAddr);
    }

    // Halt PICC
    mfrc522.PICC_HaltA();
    // Stop encryption on PCD
    mfrc522.PCD_StopCrypto1();
  }//tiempo 5 segundos
}



/*
#include <SPI.h>
#include <MFRC522.h>
#include <M5Stack.h>

#define SDA_PIN 21
#define RST_PIN 2

unsigned char data[12] = {'H', 'y', 'p', 'n', 'o', 's', 'A', 'c', 't', 'i', 'v', 'o'};
unsigned char *writeData = data;
unsigned char *str;

MFRC522 mfrc522(SDA_PIN, RST_PIN);

MFRC522::MIFARE_Key key;
*/

// //------------------------------------------------------------------
// /*
//                           printArray()
// */
// //------------------------------------------------------------------
// void printArray(byte *buffer, byte bufferSize) {
//   for (byte i = 0; i < bufferSize; i++) {
//     Serial.print((char)buffer[i]);
//   }
// }

// //------------------------------------------------------------------
// /*
//                         authenticateCard()
// */
// //------------------------------------------------------------------
// void authenticateCard() {
//   MFRC522::StatusCode status;
//   byte trailerBlock = 7;

//   status = (MFRC522::StatusCode)mfrc522.PCD_Authenticate(MFRC522::PICC_CMD_MF_AUTH_KEY_A, trailerBlock, &key, &(mfrc522.uid));
//   if (status != MFRC522::STATUS_OK) {
//     Serial.print(F("PCD_Authenticate() failed: "));
//     Serial.println(mfrc522.GetStatusCodeName(status));
//     return;
//   }
// }

// //------------------------------------------------------------------
// /*
//                         writeDataToCard()
// */
// //------------------------------------------------------------------
// void writeDataToCard(byte blockAddr) {
//   Serial.print(F("Escribir datos en sector "));
//   Serial.print(blockAddr);
//   Serial.println(F(" ..."));

//   printArray((byte *)data, 12);
//   Serial.println();
//   MFRC522::StatusCode status = (MFRC522::StatusCode)mfrc522.MIFARE_Write(blockAddr, (byte *)data, 16);

//   if (status != MFRC522::STATUS_OK) {
//     Serial.print(F("MIFARE_Write() failed: "));
//     Serial.println(mfrc522.GetStatusCodeName(status));
//   }
//   Serial.println();
// }

// //------------------------------------------------------------------
// /*
//                         readDataFromCard()
// */
// //------------------------------------------------------------------
// void readDataFromCard(byte blockAddr) {
//   byte buffer[18];
//   byte size = sizeof(buffer);

//   Serial.print(F("Leer datos del sector "));
//   Serial.print(blockAddr);
//   Serial.println(F(" ..."));

//   MFRC522::StatusCode status = (MFRC522::StatusCode)mfrc522.MIFARE_Read(blockAddr, buffer, &size);
//   if (status != MFRC522::STATUS_OK) {
//     Serial.print(F("MIFARE_Read() failed: "));
//     Serial.println(mfrc522.GetStatusCodeName(status));
//   }

//   Serial.print(F("Data in block "));
//   Serial.print(blockAddr);
//   Serial.println(F(":"));
//   printArray(buffer, 12);
//   Serial.println();

//   verifyCode(buffer, 12);
// }

// //------------------------------------------------------------------
// /*
//                         verifyCode()
// */
// //------------------------------------------------------------------
// void verifyCode(byte *data, byte dataSize) {
//   // Convertir los datos leídos a una cadena
//   char buffer[dataSize + 1];
//   memcpy(buffer, data, dataSize);
//   buffer[dataSize] = '\0'; // Agregar el carácter nulo al final para formar una cadena de caracteres

//   // Comparar con "HypnosActivo"
//   if (strcmp(buffer, "HypnosActivo") == 0) {
//     // Mostrar círculo verde en la pantalla del M5Stack
//     M5.Lcd.fillCircle(160, 120, 50, TFT_GREEN);
//   } else {
//     // Mostrar círculo rojo en la pantalla del M5Stack
//     M5.Lcd.fillCircle(160, 120, 50, TFT_RED);
//   }
// }

// //------------------------------------------------------------------
// /*
//                       ****** SETUP() ******
// */
// //------------------------------------------------------------------
// void setup() {
//   M5.begin();
//   Serial.begin(9600);
//   SPI.begin();
//   mfrc522.PCD_Init();

//   for (byte i = 0; i < 6; i++) {
//     key.keyByte[i] = 0xFF;
//   }
// }

// //------------------------------------------------------------------
// /*
//                       ****** LOOP() ******
// */
// //------------------------------------------------------------------
// void loop() {
//   if (!mfrc522.PICC_IsNewCardPresent())
//     return;

//   if (!mfrc522.PICC_ReadCardSerial())
//     return;

//   authenticateCard();

//   byte blockAddr = 4;
//   //writeDataToCard(blockAddr);
//   readDataFromCard(blockAddr);

//   // Halt PICC
//   mfrc522.PICC_HaltA();
//   // Stop encryption on PCD
//   mfrc522.PCD_StopCrypto1();
// }

