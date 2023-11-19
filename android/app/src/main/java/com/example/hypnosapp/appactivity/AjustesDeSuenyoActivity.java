package com.example.hypnosapp.appactivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hypnosapp.other.MenuManager;
import com.example.hypnosapp.R;

public class AjustesDeSuenyoActivity extends AppCompatActivity {
    ImageView btnPerfilUsuario;
    EditText toneLocationClock, wakeUpHourGoal, sleepTimeGoal;
    Switch isGradualClock, isAutoClock, goalNotifications, warmLight, coldLight, autoLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajustes_de_sueno);

        //FUNCIONALIDAD BOTONES MENUS
        MenuManager funcionMenu = new MenuManager();

        btnPerfilUsuario= findViewById(R.id.logoUsuarioHeader);
        isGradualClock = findViewById(R.id.switchGradualClock);
        isAutoClock = findViewById(R.id.switchAutoClock);
        goalNotifications = findViewById(R.id.switchNotifications);
        warmLight = findViewById(R.id.switchWarmLight);
        coldLight = findViewById(R.id.switchColdLight);
        autoLight = findViewById(R.id.switchAutoLight);
        toneLocationClock = findViewById(R.id.toneLocationClock);
        wakeUpHourGoal = findViewById(R.id.wakeUpHourGoal);
        sleepTimeGoal = findViewById(R.id.sleepTimeGoal);

        btnPerfilUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPerfilUsuario(AjustesDeSuenyoActivity.this);
            }
        });

        ImageView btnPantallaPrincipal = findViewById(R.id.btnPantallaPrincipal);
        btnPantallaPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPantallaPrincipal(AjustesDeSuenyoActivity.this);
            }
        });

        ImageView btnAjustesDescanso = findViewById(R.id.btnAjustesDescanso);
        btnAjustesDescanso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirAjustesDescanso(AjustesDeSuenyoActivity.this);
            }
        });

        ImageView btnPreferencias = findViewById(R.id.btnPreferencias);
        btnPreferencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirAcercaDe(AjustesDeSuenyoActivity.this);
            }
        });

    }
}
