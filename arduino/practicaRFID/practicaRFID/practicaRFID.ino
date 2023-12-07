//instalar libreria de: https://github.com/miguelbalboa/rfid 

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
    return;
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
  Serial.println();
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
  Serial.println();

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
    M5.Lcd.fillCircle(160, 120, 50, TFT_GREEN);
  } else {
    // Mostrar círculo rojo en la pantalla del M5Stack
    M5.Lcd.fillCircle(160, 120, 50, TFT_RED);
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
}

//------------------------------------------------------------------
/*
                      ****** LOOP() ******
*/
//------------------------------------------------------------------
void loop() {
  if (!mfrc522.PICC_IsNewCardPresent())
    return;

  if (!mfrc522.PICC_ReadCardSerial())
    return;

  authenticateCard();

  byte blockAddr = 4;
  //writeDataToCard(blockAddr);
  readDataFromCard(blockAddr);

  // Halt PICC
  mfrc522.PICC_HaltA();
  // Stop encryption on PCD
  mfrc522.PCD_StopCrypto1();
}
