package com.example.hypnosapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.hypnosapp.appactivity.PermissionActivity;

import java.util.ArrayList;
import java.util.List;

public class PermissionManager {

    private static final int SOLICITUD_PERMISO_WAKE_LOCK = 0;
    private static final String PREFS_NAME = "PermissionPrefs";
    private static final String SHOULD_SHOW_PERMISSION_DIALOG = "ShouldShowPermissionDialog";

    private final PermissionActivity permissionActivity;

    public PermissionManager(PermissionActivity permissionActivity) {
        this.permissionActivity = permissionActivity;
    }

    public void checkAndRequestPermission() {
        String[] perms = {Manifest.permission.WAKE_LOCK};
        List<String> missingPerms = new ArrayList<>();

        for (String perm : perms) {
            if (ContextCompat.checkSelfPermission(permissionActivity, perm) != PackageManager.PERMISSION_GRANTED) {
                missingPerms.add(perm);
            }
        }

        if (!missingPerms.isEmpty() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowPermissionDialog()) {
                showPermissionRequestDialog();
            }
        }
    }

    private boolean shouldShowPermissionDialog() {
        SharedPreferences preferences = permissionActivity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(SHOULD_SHOW_PERMISSION_DIALOG, true);
    }

    private void setShowPermissionDialog(boolean shouldShow) {
        SharedPreferences preferences = permissionActivity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(SHOULD_SHOW_PERMISSION_DIALOG, shouldShow).apply();
    }

    private void showPermissionRequestDialog() {
        new AlertDialog.Builder(permissionActivity)
                .setTitle("Solicitud de permiso")
                .setMessage("Sin el permiso para WAKE_LOCK, no puedo realizar ciertas acciones.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermission();
                    }
                })
                .setNegativeButton("No mostrar de nuevo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setShowPermissionDialog(false);
                    }
                })
                .show();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(permissionActivity, new String[]{Manifest.permission.WAKE_LOCK}, SOLICITUD_PERMISO_WAKE_LOCK);
    }

    public void handlePermissionResult(int requestCode, int[] grantResults) {
        if (requestCode == SOLICITUD_PERMISO_WAKE_LOCK && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(permissionActivity, "Permiso concedido", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(permissionActivity, "Sin el permiso, no puedo realizar la acción", Toast.LENGTH_SHORT).show();
        }
    }
}
