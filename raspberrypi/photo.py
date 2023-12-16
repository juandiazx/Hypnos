import firebase_admin
import json
from firebase_admin import credentials, firestore, storage
from datetime import datetime
from picamera import PiCamera
import time
from io import BytesIO


#CAMERA DOCUMENTATION
#Hacer sudo pip install picamera en el virtual environment
#https://projects.raspberrypi.org/en/projects/getting-started-with-picamera/0

# Inicializa Firebase con el archivo de configuración
cred = credentials.Certificate('firebase-adminsdk.json')
firebase_admin.initialize_app(cred, {
    'storageBucket': 'hypnos-gti.appspot.com'
})

storage_bucket = storage.bucket()

# Referencia a la colección en la que deseas agregar datos
db = firestore.client()
documento_referencia = db.collection('users').document('lr3SPEtJqt493dpfWoDd').collection('nightsData').document('SQtNQ5lD9iceihHPvexs')

# Configuración de la cámara
camera = PiCamera()

# Array para almacenar los archivos de imagen
imagenes = []

uid_usuario = "lr3SPEtJqt493dpfWoDd"
def tomar_fotos():
    for _ in range(3):  # Tomar 3 fotos
        stream = BytesIO()
        camera.capture(stream, 'jpeg')
        imagenes.append(stream.getvalue())
        time.sleep(4)
        
    #Ver si esto afecta en algo malo al codigo, si debemos cerrarla despues de llamar a la funcion
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


        '''
        # Subir imágenes a Firestore
        for i, imagen in enumerate(imagenes):
            storage_ref = firebase_admin.storage.bucket().blob(f'imagen_{i}.jpg')
            storage_ref.upload_from_string(imagen, content_type='image/jpeg')
            print(f'Imagen {i} subida correctamente a Firestore.')'''


# Escribir en Firestore después de que ambos subprocesos hayan terminado
tomar_fotos()
subir_imagenes_storage()
