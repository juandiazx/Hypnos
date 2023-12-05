package com.example.hypnosapp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmStopReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Detener el servicio de alarma y ocultar la notificación
        Intent stopServiceIntent = new Intent(context, AlarmService.class);
        context.stopService(stopServiceIntent);
    }
}

