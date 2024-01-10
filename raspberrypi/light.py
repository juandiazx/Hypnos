import firebase_admin
from firebase_admin import credentials, firestore, storage
import RPi.GPIO as GPIO
import time

# Inicializa Firebase con el archivo de configuración
cred = credentials.Certificate('firebase-adminsdk.json')
firebase_admin.initialize_app(cred)

# Referencia a la colección en la que deseas agregar datos
db = firestore.client()
uid_usuario = "596Ev6PGfTTJDKnSUmZpVqn8tO92"

def read_light_settings_from_firestore(uid):
    user_ref = db.collection('users').document(uid)
    user_data = user_ref.get().to_dict()

    if user_data is not None and 'preferences' in user_data:
        preferences = user_data['preferences']
        light_settings = preferences.get('lightSettings')
        
        if light_settings is not None:
            return light_settings

    return None

def set_led_color(lightSettings):
    # Define GPIO pins for RGB LED (adjust these according to your wiring)
    RED_PIN = 17
    GREEN_PIN = 27
    BLUE_PIN = 22

    # Setup GPIO
    GPIO.setmode(GPIO.BCM)
    GPIO.setup(RED_PIN, GPIO.OUT)
    GPIO.setup(GREEN_PIN, GPIO.OUT)
    GPIO.setup(BLUE_PIN, GPIO.OUT)

    # Turn off all colors
    GPIO.output(RED_PIN, GPIO.LOW)
    GPIO.output(GREEN_PIN, GPIO.LOW)
    GPIO.output(BLUE_PIN, GPIO.LOW)

    # Set color based on lightSettings
    if lightSettings == 'COL':
        # Cold white
        GPIO.output(BLUE_PIN, GPIO.HIGH)
    elif lightSettings == 'WAR':
        # Warm white
        GPIO.output(RED_PIN, GPIO.HIGH)
        GPIO.output(GREEN_PIN, GPIO.HIGH)

# Example usage
if __name__ == "__main__":
    try:
        light_settings = read_light_settings_from_firestore(uid_usuario)
        
        if light_settings is not None:
            set_led_color(light_settings)
            time.sleep(5)
        else:
            print("Unable to retrieve light settings for the specified user.")

    except KeyboardInterrupt:
        pass
    finally:
        # Cleanup GPIO on exit
        GPIO.cleanup()
