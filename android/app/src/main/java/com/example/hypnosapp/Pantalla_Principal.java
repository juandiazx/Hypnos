package com.example.hypnosapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Pantalla_Principal extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Encuentra el TabLayout y el ViewPager
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);

        // Crea un adaptador para manejar los fragmentos
        TabsPaginaPrincipal adapter = new TabsPaginaPrincipal(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        // Conecta el TabLayout con el ViewPager
        tabLayout.setupWithViewPager(viewPager);

        TabLayout.Tab tab = tabLayout.getTabAt(2); // Selecciona la tab "Hoy" por defecto
        if (tab != null) {
            tab.select();
        }


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

