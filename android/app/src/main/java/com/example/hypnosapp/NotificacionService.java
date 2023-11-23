package com.example.hypnosapp;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.SystemClock;
import androidx.core.app.NotificationCompat;

public class NotificacionService extends IntentService {

    private static final String CANAL_ID = "mi_canal";
    private static final int NOTIFICACION_ID = 1;

    public NotificacionService() {
        super("NotificacionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Esperar 20 segundos (o el tiempo deseado)
        SystemClock.sleep(20000);

        // Configura la notificación
        mostrarNotificacion();
    }

    private void mostrarNotificacion() {
        // Configura la notificación
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CANAL_ID, "Canal de consejos",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Canal para consejos");
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificacion = new NotificationCompat.Builder(this, CANAL_ID)
                .setContentTitle("¡Consejo diario!")
                .setContentText(" Revisa las estadísticas de la última noche.")
                .setSmallIcon(R.drawable.luna_peque)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.img));
        // Otros detalles de la notificación...

        notificationManager.notify(NOTIFICACION_ID, notificacion.build());
    }
}

