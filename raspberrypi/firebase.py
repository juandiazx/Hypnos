import firebase_admin
import serial
import socket
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
udp_port = 12345       # Elige un número de puerto disponible
udp_socket.bind((udp_host, udp_port))


# Configurar el objeto serial
ser = serial.Serial(
    port='/dev/ttyUSB0',  # Cambia esto según el puerto asignado a tu ESP32
    baudrate=115200,
    timeout=1
)


try:
    while True:
        # Leer datos del ESP32
        data = ser.readline().decode('utf-8').strip()
        print(data,"-")
        # Verificar si se recibió algún dato antes de agregarlo a la colección
        if data:
            # Datos que deseas agregar
            datos = {
                'estamos_en_alta': data,
                'campo2': 'valor2',
                'campo3': 'valor3'
            }

            # Agrega los datos a la colección
            coleccion_referencia.add(datos)

            print('Datos agregados correctamente a Firestore en Firebase.')

except KeyboardInterrupt:
    # Manejar una interrupción de teclado (Ctrl+C)
    ser.close()
    print("\nComunicación serial cerrada.")
