package com.example.hypnosapp.mainpage;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hypnosapp.R;
import com.ingenieriajhr.blujhr.BluJhr;

import java.util.ArrayList;
import java.util.List;
import android.widget.LinearLayout;


public class ECGManager_Ziven_Cardio extends AppCompatActivity {

    private boolean permisosOnBluetooth = false;
    private List<String> requiredPermissions = new ArrayList<>();
    private ArrayList<String> devicesBluetooth = new ArrayList<>();

    private BluJhr blue;
    private ListView listDeviceBluetooth;
    private LinearLayout viewConn;
    private Button buttonSend;
    private EditText edtTx;
    private TextView consola;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_explorer);

        blue = new BluJhr(this);
        blue.onBluetooth();

        listDeviceBluetooth = findViewById(R.id.listDeviceBluetooth);
        viewConn = findViewById(R.id.viewConn); // Ajusta esto según el ID real en tu archivo XML
        buttonSend = findViewById(R.id.buttonSend);  // Ajusta esto según el ID real en tu archivo XML
        edtTx = findViewById(R.id.edtTx);  // Ajusta esto según el ID real en tu archivo XML
        consola = findViewById(R.id.consola);  // Ajusta esto según el ID real en tu archivo XML

        listDeviceBluetooth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!devicesBluetooth.isEmpty()) {
                    blue.connect(devicesBluetooth.get(i));
                    blue.setDataLoadFinishedListener(new BluJhr.ConnectedBluetooth() {
                        @Override
                        public void onConnectState(BluJhr.Connected state) {
                            switch (state) {
                                case True:
                                    Toast.makeText(getApplicationContext(), "True", Toast.LENGTH_SHORT).show();
                                    listDeviceBluetooth.setVisibility(View.GONE);
                                    viewConn.setVisibility(View.VISIBLE);  // Corregido aquí
                                    rxReceived();
                                case Pending:
                                    Toast.makeText(getApplicationContext(), "Pending", Toast.LENGTH_SHORT).show();
                                    rxReceived();
                                case False:
                                    Toast.makeText(getApplicationContext(), "False", Toast.LENGTH_SHORT).show();
                                    rxReceived();
                                case Disconnect:
                                    Toast.makeText(getApplicationContext(), "Disconnect", Toast.LENGTH_SHORT).show();
                                    listDeviceBluetooth.setVisibility(View.VISIBLE);
                                    viewConn.setVisibility(View.GONE);  // Corregido aquí
                                    rxReceived();
                            }
                        }
                    });
                }
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blue.bluTx(edtTx.getText().toString());
            }
        });

        buttonSend.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                blue.closeConnection();
                return true;
            }
        });
    }

    private void rxReceived() {
        blue.loadDateRx(new BluJhr.ReceivedData() {
            @Override
            public void rxDate(String rx) {
                consola.setText(consola.getText().toString() + rx);
            }
        });
    }

    /**
     * Pedimos los permisos correspondientes, para Android 12 hay que pedir los siguientes admin y scan
     * en Android 12 o superior se requieren permisos diferentes
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (blue.checkPermissions(requestCode, grantResults)) {
            Toast.makeText(this, "Exit", Toast.LENGTH_SHORT).show();
            blue.initializeBluetooth();
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                blue.initializeBluetooth();
            } else {
                Toast.makeText(this, "Algo salió mal", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!blue.stateBluetoooth() && requestCode == 100) {
            blue.initializeBluetooth();
        } else {
            if (requestCode == 100) {
                devicesBluetooth = blue.deviceBluetooth();
                if (!devicesBluetooth.isEmpty()) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, devicesBluetooth);
                    listDeviceBluetooth.setAdapter(adapter); // Corregido aquí
                } else {
                    Toast.makeText(this, "No tienes vinculados dispositivos", Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}