package com.example.hypnosapp;

import android.os.Bundle;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;

public class Pantalla_Principal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //FUNCIONALIDAD BOTONES MENUS
        MenuManager funcionMenu = new MenuManager();

        ImageView btnPerfilUsuario = findViewById(R.id.logoUsuarioHeader);
        btnPerfilUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPerfilUsuario(Pantalla_Principal.this);
            }
        });

        ImageView btnPantallaPrincipal = findViewById(R.id.btnPantallaPrincipal);
        btnPantallaPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPantallaPrincipal(Pantalla_Principal.this);
            }
        });

        ImageView btnAjustesDescanso = findViewById(R.id.btnAjustesDescanso);
        btnAjustesDescanso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirAjustesDescanso(Pantalla_Principal.this);
            }
        });

        ImageView btnPreferencias = findViewById(R.id.btnPreferencias);
        btnPreferencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirAcercaDe(Pantalla_Principal.this);
            }
        });


        /*

        ConstraintLayout constraintLayout = findViewById(R.id.frameLayout);

        // Set the custom drawable as the background of the FrameLayout
        frameLayout.setBackgroundResource(R.drawable.border_background);

         */





    }
}

