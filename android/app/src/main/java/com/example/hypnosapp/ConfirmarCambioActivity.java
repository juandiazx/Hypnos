package com.example.hypnosapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ConfirmarCambioActivity extends AppCompatActivity {

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmar_cambio);

        Bundle extras = getIntent().getExtras();
        String emailNuevo = extras.getString("email");
        String passNueva = extras.getString("contrasenya");



    }
}
