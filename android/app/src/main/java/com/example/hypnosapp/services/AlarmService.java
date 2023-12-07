package com.example.hypnosapp.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.example.hypnosapp.R;


/*
* HAY QUE HACER UNA FUNCION PARA ACTIVAR LA VIBRACION Y OTRA PARA DESACTIVARLA
* Y RECIBIR EL BOOLEANO DE SI VIBRACION EN LA INTENCION LINEA 32
* TAMBIEN DE SI ES GRADUAL O NO
*/
public class AlarmService extends Service {
    private MediaPlayer mediaPlayer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Uri alarmUri = intent.getData();
        if (alarmUri != null) {
            mediaPlayer = MediaPlayer.create(this, alarmUri);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
            showNotification();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        super.onDestroy();
    }

    private void showNotification() {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("alarm_channel", "Alarm Channel", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        PendingIntent stopServicePendingIntent = iniciarIntencionPararDespertador();
        Notification notification = buildNotification(stopServicePendingIntent);
        startForeground(1, notification);
    }

    private PendingIntent iniciarIntencionPararDespertador(){
        Intent stopServiceIntent = new Intent(this, AlarmStopReceiver.class);
        return PendingIntent.getBroadcast(this, 0, stopServiceIntent, PendingIntent.FLAG_IMMUTABLE);
    }

    private Notification buildNotification(PendingIntent intencionPendiente){
        // Construir la notificación con PendingIntent
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, "alarm_channel")
                    .setSmallIcon(R.drawable.luna_peque)
                    .setContentTitle("¡Es hora de despertar!")
                    .setContentText("Haz clic para detener la alarma")
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setOngoing(true)
                    .setContentIntent(intencionPendiente)  // Agregar el PendingIntent al hacer clic en la notificación
                    .build();
        }
        return notification;
    }

}
