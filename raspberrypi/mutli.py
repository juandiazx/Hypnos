import firebase_admin
import socket
#import serial
import threading
import json
from firebase_admin import credentials, firestore, storage
import paho.mqtt.client as mqtt
from datetime import datetime
from picamera import PiCamera
import time
from io import BytesIO

cred = credentials.Certificate('firebase-adminsdk.json')
firebase_admin.initialize_app(cred, {
    'storageBucket': 'hypnos-gti.appspot.com'
})

storage_bucket = storage.bucket()

# Referencia a la colección en la que deseas agregar datos
db = firestore.client()
coleccion_referencia = db.collection('users')
uid_usuario = None

# Configura los sockets UDP para cada función
udp_socket_data = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
udp_socket_time = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

udp_host = '0.0.0.0'  # Escucha en todas las interfaces de red
udp_port_data = 6230   # Elige un número de puerto disponible para datos
udp_port_time = 6231   # Elige un número de puerto disponible para tiempo

udp_socket_data.bind((udp_host, udp_port_data))
udp_socket_time.bind((udp_host, udp_port_time))

'''
# Configura el objeto serial
ser = serial.Serial(
    port='/dev/ttyUSB0',  # Puerto serial en Raspberry Pi (puede variar)
    baudrate=115200,       # Velocidad de baudios, asegúrate de que coincida con la configuración del ESP32
    timeout=1              # Tiempo de espera para la lectura serial
)
'''
# Configuración de MQTT
mqtt_broker_address = "test.mosquitto.org"  # Cambia esto con la IP de tu Raspberry Pi
mqtt_topic_uid = "hypnos_rp_uid"
mqtt_topic = "hypnos_m5stack_topic"
mqtt_client = mqtt.Client()
count = 0

# Configuración de la cámara
camera = PiCamera()

# Variables para almacenar los datos de UDP y UART
datos_udp = None
datos_udp_time = None
datos_mqtt = None

# Array para almacenar los archivos de imagen
imagenes = []

def tomar_fotos():
    for _ in range(3):  # Tomar 3 fotos
        stream = BytesIO()
        camera.capture(stream, 'jpeg')
        imagenes.append(stream.getvalue())
        time.sleep(4)        
    camera.close()
    
def subir_imagenes_storage():
    try:
        for i, imagen in enumerate(imagenes):
            nombre_imagen = f'imagen_{i}_{int(time.time())}.jpg'

            # Construir la ruta completa en el bucket de almacenamiento
            ruta_storage = f'users/{uid_usuario}/{nombre_imagen}'

            # Obtener una referencia al objeto de almacenamiento
            storage_ref = storage_bucket.blob(ruta_storage)

            # Subir la imagen al almacenamiento
            storage_ref.upload_from_string(imagen, content_type='image/jpeg')

            print(f'Imagen {i} subida correctamente a Firestore con la ruta: {ruta_storage}')

    except Exception as e:
        print(f"Error al subir imágenes a Firestore: {e}")

def recibir_datos_udp():
    global datos_udp
    try:
        while True:
            data, addr = udp_socket_data.recvfrom(6230)
            data = data.decode('utf-8').strip()
            snoreScore = ""
            if data:
                try:
                    mensaje_json = json.loads(data)
                    if "snoreAmount" in mensaje_json:
                        if mensaje_json["snoreAmount"] <= 1:
                           snoreScore = "Buena"
                        elif mensaje_json["snoreAmount"] >=4:
                           snoreScore = "Mala"
                        else:
                           snoreScore = "Media"
                        datos_udp = {
                            'temperature': mensaje_json["averageTemperature"],
                            'snore': snoreScore
                        }
                        print('Datos UDP recibidos correctamente:', datos_udp)
                        break
                except json.JSONDecodeError:
                    print('Mensaje UDP no es un JSON válido:', data)

    except KeyboardInterrupt:
        udp_socket.close()
        print("\nSocket UDP cerrado.")

def recibir_datos_udp_time():
    global datos_udp_time
    try:
        while True:
            data, addr = udp_socket_time.recvfrom(6231)
            data = data.decode('utf-8').strip()
            if data:
                try:
                    mensaje_json = json.loads(data)
                    if "sleepTime" in mensaje_json:
                        
                        datos_udp_time = int(mensaje_json["sleepTime"])//1000
                        
                        print('Datos UDP Time recibidos correctamente:', datos_udp_time)
                        break
                except json.JSONDecodeError:
                    print('Mensaje UDP no es un JSON válido:', data)

    except KeyboardInterrupt:
        udp_socket.close()
        print("\nSocket UDP cerrado.")


def on_message(client, userdata, message):
    global uid_usuario, datos_mqtt,count
    payload = message.payload.decode('utf-8')

    if message.topic == mqtt_topic_uid:
        uid_usuario = payload
        print('UID recibido correctamente:', uid_usuario)
        mqtt_client.unsubscribe(mqtt_topic_uid)
    elif message.topic == mqtt_topic:
        datos_mqtt = payload
        print('Datos MQTT recibidos correctamente:', datos_mqtt)
    count +=1
    if count == 2:
        mqtt_client.disconnect()



def iniciar_hilo_mqtt():
    mqtt_client.connect(mqtt_broker_address)
    mqtt_client.subscribe([(mqtt_topic_uid,0),(mqtt_topic,0)])
    mqtt_client.on_message = on_message
    mqtt_client.loop_forever()


def enviar_mensaje_mqtt_daytime():
    try:
        mqtt_topic_daytime = "hypnos_rp_daytime"
        mensaje_daytime = "daytime"
        mqtt_client.connect(mqtt_broker_address)
        mqtt_client.publish(mqtt_topic_daytime, mensaje_daytime)
        print('Mensaje MQTT enviado correctamente al topic hypnos_rp_daytime.')
        mqtt_client.disconnect()
    except Exception as e:
        print(f"Error al enviar mensaje MQTT: {e}")
        
def escribir_en_firestore():
    global datos_udp, datos_udp_time, datos_mqtt
    if datos_udp is not None and datos_udp_time is not None and datos_mqtt is not None:
        # Combinar datos de UDP, UART y MQTT
        datos_combinados = {
            'breathing': datos_udp["snore"],
            'date':datetime.now(),
            'temperature':datos_udp["temperature"],
            'time': datos_udp_time,
            'score': int(datos_mqtt)
        }

        coleccion_referencia.document(uid_usuario).collection('nightsData').document('sUK71eLZ11HYDLJTEYiP').update(datos_combinados)
        print('Datos combinados agregados correctamente a Firestore en Firebase.')
        
        subir_imagenes_storage()
        
        enviar_mensaje_mqtt_daytime()


thread_udp = threading.Thread(target=recibir_datos_udp)
thread_uart = threading.Thread(target=recibir_datos_udp_time)
thread_mqtt = threading.Thread(target=iniciar_hilo_mqtt)
thread_foto = threading.Thread(target=tomar_fotos)

thread_udp.start()
thread_uart.start()
thread_mqtt.start()
thread_foto.start()

thread_udp.join()
thread_uart.join()
thread_mqtt.join()
thread_foto.join()

escribir_en_firestore()
