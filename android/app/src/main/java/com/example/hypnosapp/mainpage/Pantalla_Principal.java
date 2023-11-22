package com.example.hypnosapp.mainpage;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.hypnosapp.firebase.FirebaseHelper;
import com.example.hypnosapp.model.Night;
import com.example.hypnosapp.other.MenuManager;
import com.example.hypnosapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

public class Pantalla_Principal extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    FirebaseHelper firebaseHelper = new FirebaseHelper();

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

        FloatingActionButton btnHistorial = findViewById(R.id.floatingActiveButtonCalendarioSemanal);
        btnHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { funcionMenu.abrirHistorial(Pantalla_Principal.this);}
        });


        String userID = "lr3SPEtJqt493dpfWoDd";


        firebaseHelper.getLastNight(userID, new OnSuccessListener<Night>() {
            @Override
            public void onSuccess(Night night) {
                if (night != null) {
                    Log.d("FirebaseHelper", "Fecha LAST NIGHT: "+ night.getDate().toString() + " Puntuación: " + night.getScore());
                } else {
                    Log.d("FirebaseHelper", "No se encontró información para LAST NIGHT.");
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("FirebaseHelper", "Ha habido un error con getLastNight! -----" + e);

            }
        });



        firebaseHelper.getYesterdayNight(userID, new OnSuccessListener<Night>() {
            @Override
            public void onSuccess(Night night) {
                if (night != null) {
                    Log.d("FirebaseHelper", "Fecha YESTERDAY NIGHT: "+ night.getDate().toString() + " Puntuación: " + night.getScore());
                } else {
                    Log.d("FirebaseHelper", "No se encontró información para YESTERDAY NIGHT.");
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("FirebaseHelper", "Ha habido un error con getYesterdayNight ----" + e);

            }
        });

    }



}

