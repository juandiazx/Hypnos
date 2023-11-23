package com.example.hypnosapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ManejoPermisosUbicacion {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 123;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void solicitarPermisoUbicacion(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((AppCompatActivity) context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Explicar al usuario por qué se necesita el permiso
                mostrarExplicacionPermiso(context, "Necesitamos tu ubicación para proporcionar servicios personalizados.");
            } else {
                // Solicitar el permiso
                ActivityCompat.requestPermissions((AppCompatActivity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_LOCATION_PERMISSION);
            }
        }
    }

    private static void mostrarExplicacionPermiso(final Context context, String mensaje) {
        new AlertDialog.Builder(context)
                .setTitle("Solicitud de permiso")
                .setMessage(mensaje)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((AppCompatActivity) context,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_CODE_LOCATION_PERMISSION);
                    }
                })
                .show();
    }

    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, Context context) {
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, puedes realizar acciones relacionadas con la ubicación
                // Aquí puedes mostrar la notificación de bienvenida
            } else {
                // Permiso denegado, puedes mostrar un mensaje o tomar otra acción
            }
        }
    }
}
