package com.example.hypnosapp.mainpage;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MyBluetoothService {
    public static final int MESSAGE_READ = 0;
    private static final String TAG = "MyBluetoothService";
    private final Handler handler;
    private ConnectedThread connectedThread;

    public static final UUID MY_UUID = UUID.fromString("your_uuid_here");

    public static final int MESSAGE_TOAST = 1; // Puedes ajustar el valor según sea necesario

    public MyBluetoothService(Handler handler) {
        this.handler = handler;
    }

    public synchronized void start() {
        // Puedes implementar la lógica de inicio según tus necesidades
        // Por ejemplo, inicialización de BluetoothAdapter y búsqueda de dispositivos
    }

    public void connect(BluetoothSocket socket) {
        connectedThread = new ConnectedThread(socket);
        connectedThread.start();
    }

    public void write(byte[] bytes) {
        // Puedes implementar la lógica para enviar datos a través de ConnectedThread
        connectedThread.write(bytes);
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }

    // Resto de métodos según tus necesidades

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private final byte[] mmBuffer; // mmBuffer almacena los datos leídos

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error al obtener flujos", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            mmBuffer = new byte[1024];
        }

        public void run() {
            int numBytes; // bytes devueltos de read()

            // Sigue escuchando al InputStream hasta que ocurra una excepción.
            while (true) {
                try {
                    // Lee del InputStream.
                    numBytes = mmInStream.read(mmBuffer);

                    // Envía los bytes obtenidos a la actividad principal.
                    Message readMsg = handler.obtainMessage(
                            MyBluetoothService.MESSAGE_READ, numBytes, -1,
                            mmBuffer);
                    readMsg.sendToTarget();
                } catch (IOException e) {
                    Log.d(TAG, "InputStream desconectado", e);
                    break;
                }
            }
        }

        // Llama a este método desde la actividad principal para enviar datos al dispositivo remoto.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // Comparte el mensaje enviado con la actividad principal.
                Message writtenMsg = handler.obtainMessage(
                        MyBluetoothService.MESSAGE_READ, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Error al enviar datos", e);

                // Envía un mensaje de error de vuelta a la actividad principal.
                Message writeErrorMsg = handler.obtainMessage(MyBluetoothService.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast", "No se pudo enviar datos al otro dispositivo");
                writeErrorMsg.setData(bundle);
                handler.sendMessage(writeErrorMsg);
            }
        }

        // Llama a este método desde la actividad principal para cerrar la conexión.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "No se pudo cerrar el socket de conexión", e);
            }
        }

        public BluetoothAdapter getBluetoothAdapter() {
            // Agrega lógica para obtener el adaptador Bluetooth aquí
            return BluetoothAdapter.getDefaultAdapter();
        }
    }
}
