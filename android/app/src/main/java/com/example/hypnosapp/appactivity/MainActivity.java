package com.example.hypnosapp.appactivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import com.example.hypnosapp.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.preinicio_de_sesion);

        // Inicia la actividad de permisos
        startActivity(new Intent(this, PermissionActivity.class));
        finish();

    }

}