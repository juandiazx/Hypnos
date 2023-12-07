//instalar libreria de: https://github.com/miguelbalboa/rfid 

#include <SPI.h>
#include <MFRC522.h>
#include <M5Stack.h>

#define SDA_PIN 21
#define RST_PIN 2 //no lo he conectado

unsigned char data[16] = { 'H','E','S','T',' ','R','F','I','D',' ','M','F','R', '5','5','2'}; 
unsigned char *writeData = data;
unsigned char *str;

MFRC522 mfrc522(SDA_PIN, RST_PIN);

MFRC522::MIFARE_Key key;

void printArray (byte *buffer, byte bufferSize){
  
    for (byte i = 0; i < bufferSize; i++)
  {
    Serial.print((char)buffer[i]);
  }
  
}

void setup(){
  Serial.begin(9600);
  SPI.begin();
  mfrc522.PCD_Init();

  for(byte i = 0; i < 6; i++){
    key.keyByte[i] = 0xFF;
  }
}

void loop(){
  if(!mfrc522.PICC_IsNewCardPresent())
    return;

  if(!mfrc522.PICC_ReadCardSerial())
    return;
  
  MFRC522::StatusCode status;
  byte trailerBlock = 7;
  //byte sector = 1;
  byte blockAddr = 4;

  status = (MFRC522::StatusCode) mfrc522.PCD_Authenticate(MFRC522::PICC_CMD_MF_AUTH_KEY_A, trailerBlock, &key, &(mfrc522.uid));
  if(status != MFRC522::STATUS_OK){
    Serial.print(F("PCD_Authenticate() failed: "));
    Serial.println(mfrc522.GetStatusCodeName(status));
    return;
  }

  //Escribir:
  Serial.print(F("Escribir datos en sector "));
  Serial.print(blockAddr);
  Serial.println(F(" ..."));

  printArray((byte*)data, 16);
  Serial.println();
  status = (MFRC522::StatusCode) mfrc522.MIFARE_Write(blockAddr, (byte*) data, 16);
  if(status != MFRC522::STATUS_OK){
    Serial.print(F("MIFARE_Write() failed: "));
    Serial.println(mfrc522.GetStatusCodeName(status));
  }
  Serial.println();

  byte buffer[18];
  byte size = sizeof(buffer);

  //Leer datos del bloque:
  Serial.print(F("Leer datos del sector ")); 
  Serial.print(blockAddr);
  Serial.println(F(" ..."));
  status = (MFRC522::StatusCode) mfrc522.MIFARE_Read(blockAddr, buffer, &size);
  if (status != MFRC522::STATUS_OK) {
    Serial.print(F("MIFARE_Read() failed: "));
    Serial.println(mfrc522.GetStatusCodeName(status));
  }
  Serial.print(F("Data in block ")); Serial.print(blockAddr); Serial.println(F(":"));
  printArray(buffer, 16); Serial.println();

  // Halt PICC
  mfrc522.PICC_HaltA();
  // Stop encryption on PCD
  mfrc522.PCD_StopCrypto1();
}






























/*//RST          D9
//SDA(SS)      D10
//MOSI         D11
//MISO         D12
//SCK          D13

#include <SPI.h>
#include <MFRC522.h>

const int RST_PIN = 9;
const int SS_PIN = 10;

//Declaracion de cadena de caracteres
unsigned char data[16] = { 'T','E','S','T',' ','R','F','I','D',' ','M','F','R', '5','5','2'}; 
unsigned char *writeData = data; 
unsigned char *str;

MFRC522 mfrc522(SS_PIN, RST_PIN);

MFRC522::MIFARE_Key key;

void printArray(byte *buffer, byte bufferSize) {
  for (byte i = 0; i < bufferSize; i++) {
    Serial.print(buffer[i] < 0x10 ? " 0" : " ");
    Serial.print(buffer[i], HEX);
  }
}

void setup()
{
  Serial.begin(9600);
  SPI.begin();
  mfrc522.PCD_Init();

  for (byte i = 0; i < 6; i++) {
    key.keyByte[i] = 0xFF;
  }
}

void loop()
{
  if (!mfrc522.PICC_IsNewCardPresent())
    return;

  if (!mfrc522.PICC_ReadCardSerial())
    return;

  MFRC522::StatusCode status;
  byte trailerBlock = 7;
  byte sector = 1;
  byte blockAddr = 4;

  status = (MFRC522::StatusCode) mfrc522.PCD_Authenticate(MFRC522::PICC_CMD_MF_AUTH_KEY_A, trailerBlock, &key, &(mfrc522.uid));
  if (status != MFRC522::STATUS_OK) {
    Serial.print(F("PCD_Authenticate() failed: "));
    Serial.println(mfrc522.GetStatusCodeName(status));
    return;
  }

  // Write data to the block
  Serial.print(F("Escribir datos en sector "));
  Serial.print(blockAddr);
  Serial.println(F(" ..."));
  printArray((byte*)data, 16); Serial.println();
  status = (MFRC522::StatusCode) mfrc522.MIFARE_Write(blockAddr, (byte*)data, 16);
  if (status != MFRC522::STATUS_OK) {
    Serial.print(F("MIFARE_Write() failed: "));
    Serial.println(mfrc522.GetStatusCodeName(status));
  }
  Serial.println();

  byte buffer[18];
  byte size = sizeof(buffer);

  // Read data from the block (again, should now be what we have written)
  Serial.print(F("Leer datos del sector ")); Serial.print(blockAddr);
  Serial.println(F(" ..."));
  status = (MFRC522::StatusCode) mfrc522.MIFARE_Read(blockAddr, buffer, &size);
  if (status != MFRC522::STATUS_OK) {
    Serial.print(F("MIFARE_Read() failed: "));
    Serial.println(mfrc522.GetStatusCodeName(status));
  }
  Serial.print(F("Data in block ")); Serial.print(blockAddr); Serial.println(F(":"));
  printArray(buffer, 16); Serial.println();

  // Halt PICC
  mfrc522.PICC_HaltA();
  // Stop encryption on PCD
  mfrc522.PCD_StopCrypto1();
}*/






































/*

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
*/
