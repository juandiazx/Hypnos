package com.example.hypnosapp.mainpage;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.core.app.ActivityCompat;

import java.io.IOException;

public class ConnectThread extends Thread {
    private static final String TAG = "ConnectThread";
    private final BluetoothDevice mmDevice;
    private final MyBluetoothService bluetoothService;
    private BluetoothSocket mmSocket;
    private final Context context;

    public ConnectThread(BluetoothDevice device, MyBluetoothService service, Context context) {
        mmDevice = device;
        bluetoothService = service;
        this.context = context;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Intenta crear el BluetoothSocket en el constructor
        try {
            mmSocket = device.createRfcommSocketToServiceRecord(MyBluetoothService.MY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Error al crear el BluetoothSocket", e);
        }
    }

    public void run() {
        bluetoothService.getBluetoothAdapter().cancelDiscovery();

        if (mmSocket == null) {
            Log.e(TAG, "BluetoothSocket no inicializado. Conexión fallida.");
            return;
        }

        try {
            mmSocket.connect();
        } catch (IOException connectException) {
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "No se pudo cerrar el socket de conexión", closeException);
            }
            return;
        }

        bluetoothService.connect(mmSocket);
    }

    public void cancel() {
        try {
            if (mmSocket != null) {
                mmSocket.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "No se pudo cerrar el socket de conexión", e);
        }
    }

    // Método para asignar el evento de clic al botón de conexión
    public void assignClickEvent(Button btnConectarBandaCardiaca) {
        btnConectarBandaCardiaca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lógica para iniciar la conexión con la banda cardíaca
                startBluetoothConnection();
            }
        });
    }

    private void startBluetoothConnection() {
        // Iniciar la conexión con la banda cardíaca
        start();
    }
}
