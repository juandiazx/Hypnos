/*package com.example.hypnosapp.mainpage;

import android.Manifest;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.example.hypnosapp.R;

public class DeviceListActivity extends ListActivity {

    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> deviceListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_explorer);

        deviceListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        setListAdapter(deviceListAdapter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // El dispositivo no admite Bluetooth
            // Manejar esta situación según tus necesidades
            finish();
        }

        // Iniciar descubrimiento de dispositivos
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();

        // Registrar el BroadcastReceiver para descubrimiento de dispositivos
        registerReceiver(deviceDiscoveryReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        // Manejar la selección de un dispositivo de la lista
        getListView().setOnItemClickListener((parent, view, position, id) -> {
            String selectedDeviceAddress = deviceListAdapter.getItem(position);

            // Mostrar el TextView de "Conectado"
            TextView textViewConectado = findViewById(R.id.textViewConectado);
            textViewConectado.setVisibility(View.VISIBLE);
            // Simular la conexión al dispositivo con dirección "12:12:12:12:12:12"
            if (selectedDeviceAddress.equals("12:12:12:12:12:12")) {
                // Realizar acciones de conexión simulada
                // Puedes enviar datos simulados a la actividad anterior
                // y cambiar la visibilidad de las vistas según sea necesario
            }
        });
    }

    // BroadcastReceiver para manejar el descubrimiento de dispositivos Bluetooth
    private final BroadcastReceiver deviceDiscoveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Se ha encontrado un dispositivo
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceAddress = device.getAddress();

                // Verificar si el dispositivo ya está en la lista
                boolean isNewDevice = true;
                for (int i = 0; i < deviceListAdapter.getCount(); i++) {
                    if (deviceAddress.equals(deviceListAdapter.getItem(i))) {
                        isNewDevice = false;
                        break;
                    }
                }

                // Agregar el dispositivo a la lista si no está duplicado
                if (deviceName != null && isNewDevice) {
                    deviceListAdapter.add(deviceAddress);
                    deviceListAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Detener el descubrimiento y desregistrar el BroadcastReceiver al salir de la actividad
        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }
        unregisterReceiver(deviceDiscoveryReceiver);
    }
}
*/