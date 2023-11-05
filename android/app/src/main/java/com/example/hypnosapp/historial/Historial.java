package com.example.hypnosapp.historial;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


import com.example.hypnosapp.other.MenuManager;
import com.example.hypnosapp.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class Historial extends AppCompatActivity {
    private List<DiaModel> listaDias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historial);

        listaDias = new ArrayList<>();
        listaDias.add(new DiaModel("05/11/2023", "88/100", "Muy buena", "24C", "8h 25min"));
        listaDias.add(new DiaModel("04/11/2023", "75/100", "Buena", "23C", "7h 45min"));
        listaDias.add(new DiaModel("03/11/2023", "90/100","Muy buena", "25C", "7h 55min"));


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

    // Agrega este método para obtener la lista de días
    public List<DiaModel> getListaDias() {
        return listaDias;
    }
}

