package com.example.hypnosapp.appactivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hypnosapp.NotificationHelper;
import com.example.hypnosapp.PermissionManager;
import com.example.hypnosapp.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.preinicio_de_sesion);

        // Inicia el temporizador para enviar notificaciones a las 19:30
        NotificationHelper.createNotificationChannel(this);
        NotificationHelper.startNotificationTimer(this);

    }

}