package com.example.hypnosapp.appactivity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.hypnosapp.PermissionManager;
import com.example.hypnosapp.R;

public class PermissionActivity extends AppCompatActivity {

    private PermissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar el PermissionManager con la referencia de la actividad
        permissionManager = new PermissionManager(this);

        // Verificar y solicitar permisos al entrar por primera vez
        permissionManager.checkAndRequestPermission();

        // Realizar la acción que requiere el permiso aquí
        // Por ejemplo, puedes agregar código para activar el WAKE_LOCK
    }

    // Este método se llama cuando el usuario concede o deniega los permisos solicitados
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Delegar la respuesta de permisos al PermissionManager
        permissionManager.handlePermissionResult(requestCode, grantResults);
    }

}
