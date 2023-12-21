package com.example.hypnosapp.mainpage;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.example.hypnosapp.R;

public class BandaCardiacaManager {
    private static final String TAG = BandaCardiacaManager.class.getSimpleName();

    private BluetoothGatt bluetoothGatt;
    private Context contexto;

    // Constructor que toma el contexto como parámetro
    public BandaCardiacaManager(Context context) {
        this.contexto = context;
    }

    // Método para conectar con la banda cardiaca
    public void conectarConBandaCardiaca(BluetoothDevice dispositivo) {
        // Obtener el dispositivo seleccionado durante el escaneo
        BluetoothDevice bandaCardiaca = dispositivo;

        // Establecer la conexión GATT con la banda cardiaca
        bluetoothGatt = bandaCardiaca.connectGatt(contexto, false, gattCallback);
    }

    // Método para leer datos de la banda cardiaca
    public void leerDatosBandaCardiaca() {
        if (bluetoothGatt != null) {
            // Obtener la característica de lectura
            BluetoothGattCharacteristic caracteristicaLectura = obtenerCaracteristicaLectura();

            // Leer el valor de la característica
            bluetoothGatt.readCharacteristic(caracteristicaLectura);
        }
    }

    // Método para activar notificaciones de ECG
    public void activarNotificacionesECG() {
        if (bluetoothGatt != null) {
            // Obtener la característica de notificación de ECG
            BluetoothGattCharacteristic caracteristicaNotificacionECG = obtenerCaracteristicaNotificacionECG();

            // Activar notificaciones para la característica
            bluetoothGatt.setCharacteristicNotification(caracteristicaNotificacionECG, true);
        }
    }

    // Callback para gestionar eventos GATT (ejemplo básico)
    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                // Banda cardiaca conectada, iniciar servicio de descubrimiento
                gatt.discoverServices();
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                // Banda cardiaca desconectada, manejar según sea necesario
                Log.d(TAG, "Banda cardiaca desconectada");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            // Servicios descubiertos, implementar según sea necesario
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Por ejemplo, activar notificaciones de ECG después de descubrir servicios
                activarNotificacionesECG();
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            // Datos leídos, implementar según sea necesario
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Procesar los datos leídos, por ejemplo, mostrar en la interfaz de usuario
                procesarDatosLectura(characteristic.getValue());
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            // Notificación recibida, implementar según sea necesario
            // Por ejemplo, procesar notificación de ECG
            procesarNotificacionECG(characteristic.getValue());
        }
    };

    // Métodos auxiliares para obtener características específicas
    private BluetoothGattCharacteristic obtenerCaracteristicaLectura() {
        // Lógica para obtener la característica de lectura según la UUID
        // Puede ser necesario buscarla entre los servicios descubiertos
        return null;  // Reemplaza con la lógica adecuada
    }

    private BluetoothGattCharacteristic obtenerCaracteristicaNotificacionECG() {
        // Lógica para obtener la característica de notificación de ECG según la UUID
        // Puede ser necesario buscarla entre los servicios descubiertos
        return null;  // Reemplaza con la lógica adecuada
    }

    // Método para procesar los datos leídos de la banda cardiaca
    private void procesarDatosLectura(byte[] data) {
        // Implementar lógica según sea necesario
        Log.d(TAG, "Datos leídos: " + new String(data));
    }

    // Método para procesar la notificación de ECG
    private void procesarNotificacionECG(byte[] data) {
        // Implementar lógica según sea necesario
        Log.d(TAG, "Notificación de ECG recibida: " + new String(data));

        // Actualizar el valor del TextView con el nuevo dato de ECG
        actualizarValorECG(new String(data));  // Asegúrate de convertir el byte array a String adecuadamente
    }

    // Método para actualizar dinámicamente el valor del TextView
    private void actualizarValorECG(String nuevoValor) {
        // Asegúrate de tener la referencia correcta al TextView
        TextView ecgValueTextView = (TextView) ((Activity) contexto).findViewById(R.id.ecgValueTextView);

        // Actualizar el texto del TextView
        if (ecgValueTextView != null) {
            ecgValueTextView.setText(nuevoValor);
        }
    }

    public void escanearDispositivos() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission((Activity) contexto, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            bluetoothAdapter.startDiscovery();
        }
    }
}
