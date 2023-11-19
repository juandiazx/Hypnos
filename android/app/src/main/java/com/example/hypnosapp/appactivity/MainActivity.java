package com.example.hypnosapp.appactivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.hypnosapp.R;

public class MainActivity extends AppCompatActivity {

    private NotificationManager notificationManager;
    static final String CANAL_ID = "mi_canal";
    static final int NOTIFICACION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preinicio_de_sesion);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CANAL_ID, "Servicio de Música",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Canal para el servicio de música");
            notificationManager.createNotificationChannel(notificationChannel);
        }

        // Configurar la notificación y mostrarla automáticamente después de 20 segundos
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mostrarNotificacion();
            }
        }, 20000); // 20 segundos
    }

    private void mostrarNotificacion() {
        // Configurar la notificación
        NotificationCompat.Builder notificacion = new NotificationCompat.Builder(this, CANAL_ID)
                .setContentTitle("¡Bienvenido a Hypnos!")
                .setContentText("Inicia sesión o regístrate para empezar.")
                .setSmallIcon(R.drawable.luna_peque)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.img))
                .setWhen(System.currentTimeMillis() + 1000 * 60 * 60)
                .setContentInfo("más info")
                .setTicker("¡Descubre Hypnos, tu puerta a los sueños!");

        // Asociar una actividad a la notificación
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent intencionPendiente = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        notificacion.setContentIntent(intencionPendiente);

        // Mostrar la notificación
        notificationManager.notify(NOTIFICACION_ID, notificacion.build());
    }

    public void quitarNotificacion(View view) {
        // Eliminar la notificación
        notificationManager.cancel(NOTIFICACION_ID);
    }
}
