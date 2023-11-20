import firebase_admin
import socket
import serial
import threading
import json
from firebase_admin import credentials, firestore

# Inicializa Firebase con el archivo de configuración
cred = credentials.Certificate('firebase-adminsdk.json')
firebase_admin.initialize_app(cred)

# Referencia a la colección en la que deseas agregar datos
db = firestore.client()
coleccion_referencia = db.collection('stores')

# Configura el socket UDP
udp_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
udp_host = '0.0.0.0'  # Escucha en todas las interfaces de red
udp_port = 6230       # Elige un número de puerto disponible
udp_socket.bind((udp_host, udp_port))

# Configura el objeto serial
ser = serial.Serial(
    port='/dev/ttyUSB0',  # Puerto serial en Raspberry Pi (puede variar)
    baudrate=115200,       # Velocidad de baudios, asegúrate de que coincida con la configuración del ESP32
    timeout=1              # Tiempo de espera para la lectura serial
)

# Variables para almacenar los datos de UDP y UART
datos_udp = None
datos_uart = None

def recibir_datos_udp():
    global datos_udp
    try:
        while True:
            data, addr = udp_socket.recvfrom(6230)
            data = data.decode('utf-8').strip()

            if data:
                try:
                    mensaje_json = json.loads(data)
                    if "snoreAmount" in mensaje_json:
                        datos_udp = {
                            'temperature': mensaje_json["averageTemperature"],
                            'snores': mensaje_json["snoreAmount"]
                        }
                        print('Datos UDP recibidos correctamente:', datos_udp)
                        break
                except json.JSONDecodeError:  
                    print('Mensaje UDP no es un JSON válido:', data)

    except KeyboardInterrupt:
        udp_socket.close()
        print("\nSocket UDP cerrado.")

def recibir_datos_uart():
    global datos_uart
    try:
        while True:
            data = ser.readline().decode('utf-8').strip()

            # Verificar si se recibió algún dato antes de agregarlo a la colección
            if data:
                datos_uart = int(data)//1000
                print('Datos UART recibidos correctamente:', datos_uart)
                break

    except KeyboardInterrupt:
        ser.close()
        print("\nComunicación serial cerrada.")

def escribir_en_firestore():
    global datos_udp, datos_uart
    if datos_udp is not None and datos_uart is not None:
        # Combinar datos de UDP y UART
        datos_combinados = {
            'udp': datos_udp,
            'uart': datos_uart
        }

        coleccion_referencia.add(datos_combinados)
        print('Datos combinados agregados correctamente a Firestore en Firebase.')

# Iniciar subprocesos
thread_udp = threading.Thread(target=recibir_datos_udp)
thread_uart = threading.Thread(target=recibir_datos_uart)

# Iniciar subprocesos
thread_udp.start()
thread_uart.start()

# Esperar a que los subprocesos terminen (puedes eliminar esta línea si deseas que los subprocesos se ejecuten indefinidamente)
thread_udp.join()
thread_uart.join()

# Escribir en Firestore después de que ambos subprocesos hayan terminado
escribir_en_firestore()
