package com.example.hypnosapp.historial;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


import androidx.appcompat.app.AppCompatActivity;

import androidx.viewpager.widget.ViewPager;


import com.example.hypnosapp.other.MenuManager;
import com.example.hypnosapp.R;
import com.google.android.material.tabs.TabLayout;

public class Historial extends AppCompatActivity {
    //private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historial);
//        recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Encuentra el TabLayout y el ViewPager
        TabLayout tabLayout = findViewById(R.id.tabLayoutHistorial);
        ViewPager viewPager = findViewById(R.id.viewPagerHistorial);

        // Crea un adaptador para manejar los fragmentos
        TabsHistorial adapter = new TabsHistorial(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        // Conecta el TabLayout con el ViewPager
        tabLayout.setupWithViewPager(viewPager);

        TabLayout.Tab tab = tabLayout.getTabAt(0); // Selecciona la tab "Semana" por defecto
        if (tab != null) {
            tab.select();
        }

        //FUNCIONALIDAD BOTONES MENUS
        MenuManager funcionMenu = new MenuManager();

        ImageView btnPerfilUsuario = findViewById(R.id.logoUsuarioHeader);
        btnPerfilUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPerfilUsuario(Historial.this);
            }
        });

        ImageView btnPantallaPrincipal = findViewById(R.id.btnPantallaPrincipal);
        btnPantallaPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPantallaPrincipal(Historial.this);
            }
        });

        ImageView btnAjustesDescanso = findViewById(R.id.btnAjustesDescanso);
        btnAjustesDescanso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirAjustesDescanso(Historial.this);
            }
        });

        ImageView btnPreferencias = findViewById(R.id.btnPreferencias);
        btnPreferencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirAcercaDe(Historial.this);
            }
        });
    }
}

