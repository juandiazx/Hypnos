package com.example.hypnosapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.hypnosapp.appactivity.MainActivity;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationHelper {

    public static final String CHANNEL_ID = "SleepAppChannel";
    private static Timer timer;

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Sleep App Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Description of the channel");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public static void startNotificationTimer(final Context context) {
        timer = new Timer();
        // Configura la tarea del temporizador para ejecutarse una vez al día a las 19:30
        Calendar now = Calendar.getInstance();
        Calendar notificationTime = Calendar.getInstance();
        notificationTime.set(Calendar.HOUR_OF_DAY, 19);
        notificationTime.set(Calendar.MINUTE, 30);
        notificationTime.set(Calendar.SECOND, 0);

        // Si la hora actual es después de las 19:30, programar para el día siguiente
        if (now.after(notificationTime)) {
            notificationTime.add(Calendar.DAY_OF_MONTH, 1);
        }

        long delay = notificationTime.getTimeInMillis() - now.getTimeInMillis();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Ejecuta el código para enviar las notificaciones
                createNotificationChannel(context);
                sendNotification(context);
            }
        }, delay); // Espera hasta la hora específica
    }


    private static void sendNotification(Context context) {
        // Crea un PendingIntent que abrirá la actividad MainActivity
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_IMMUTABLE);

        // Crea la notificación
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Título")
                .setContentText("Texto de la notificación.")
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentIntent(pendingIntent);

        // Lanza la notificación
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.notify(1, notificationBuilder.build());
    }
}
