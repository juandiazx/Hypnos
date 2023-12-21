package com.example.hypnosapp.mainpage;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.hypnosapp.R;
import com.example.hypnosapp.utils.MenuManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;

public class ECGActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter bluetoothAdapter;
    private Handler handler;
    public static final int MESSAGE_READ = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg); // Reemplaza con tu layout

        //Menu buttons functionalities
        MenuManager funcionMenu = new MenuManager();

        // Obtener el adaptador Bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Verificar si Bluetooth está disponible y habilitado
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            // Solicitar al usuario que habilite Bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Considera llamar a ActivityCompat#requestPermissions para solicitar los permisos faltantes.
                return;
            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }


        // Inicializar el handler para manejar los datos de la banda cardíaca
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MyBluetoothService.MESSAGE_READ:
                        int numBytes = msg.arg1;
                        byte[] data = Arrays.copyOf((byte[]) msg.obj, numBytes);
                        // Procesar los datos recibidos de la banda cardíaca
                        processDataFromHeartRateMonitor(data);
                        break;
                    // Agrega otros casos según sea necesario
                }
            }
        };

        // Iniciar la conexión con la banda cardíaca
        startBluetoothConnection();

        // FUNCIONALIDAD BOTONES MENUS

        ImageView btnPerfilUsuario = findViewById(R.id.logoUsuarioHeader);
        btnPerfilUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPerfilUsuario(ECGActivity.this);
            }
        });

        ImageView btnPantallaPrincipal = findViewById(R.id.btnPantallaPrincipal);
        btnPantallaPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPantallaPrincipal(ECGActivity.this);
            }
        });

        ImageView btnAjustesDescanso = findViewById(R.id.btnAjustesDescanso);
        btnAjustesDescanso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirAjustesDescanso(ECGActivity.this);
            }
        });

        ImageView btnPreferencias = findViewById(R.id.btnPreferencias);
        btnPreferencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirAcercaDe(ECGActivity.this);
            }
        });

        ImageView btnAbrirActivityECG = findViewById(R.id.logoCardiacoHeader);
        btnAbrirActivityECG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent para abrir la actividad ECG
                Intent intent = new Intent(ECGActivity.this, ECGActivity.class);
                startActivity(intent);
            }
        });

    }

    private void startBluetoothConnection() {
        // Obtener el dispositivo de la banda cardíaca (reemplaza con tu lógica)
        BluetoothDevice heartRateDevice = getHeartRateDevice();
        if (heartRateDevice != null) {
            // Iniciar el hilo de conexión
            ConnectThread connectThread = new ConnectThread(heartRateDevice, new MyBluetoothService(handler), this);
            connectThread.start();
        }
    }

    private BluetoothDevice getHeartRateDevice() {
        // Implementa la lógica para obtener el dispositivo de la banda cardíaca
        // Puedes usar la búsqueda de dispositivos o tener un dispositivo emparejado previamente
        // Retorna el dispositivo de la banda cardíaca o null si no se encuentra
        return null;
    }

    private void processDataFromHeartRateMonitor(byte[] data) {
        // Implementa la lógica para procesar los datos de la banda cardíaca
        // por ejemplo, actualiza la interfaz de usuario con la frecuencia cardíaca.
        Log.d("ECGActivity", "Data from Heart Rate Monitor: " + Arrays.toString(data));
    }

    // Agrega otros métodos según sea necesario

    private void manageMyConnectedSocket(BluetoothSocket socket) {
        // Lógica para manejar el socket conectado
    }
}
