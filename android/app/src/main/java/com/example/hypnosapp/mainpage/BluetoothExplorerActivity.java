/*package com.example.hypnosapp.mainpage;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hypnosapp.R;

import java.util.ArrayList;
import java.util.Random;

public class BluetoothExplorerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_explorer);

        // Simular una lista falsa de dispositivos Bluetooth
        ArrayList<String> simulatedDevices = new ArrayList<>();
        simulatedDevices.add("Dispositivo 1 - 12:12:12:12:12:12");
        simulatedDevices.add("Dispositivo 2");
        // Agrega más dispositivos según sea necesario

        // Configurar el adaptador para la lista de dispositivos Bluetooth
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, simulatedDevices);
        ListView listView = findViewById(R.id.listDeviceBluetooth);
        listView.setAdapter(adapter);

        // Configurar el listener para la selección de dispositivos en la lista
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedDevice = simulatedDevices.get(position);
            // Verificar si la dirección MAC es "12:12:12:12:12:12"
            if (selectedDevice.contains("12:12:12:12:12:12")) {
                // Simular la conexión al dispositivo
                // Puedes enviar datos simulados a la actividad anterior
                // y cambiar la visibilidad de las vistas según sea necesario

                // Regresar a la actividad anterior (ECGActivity)
                finish();
            }
        });
    }
}
*/