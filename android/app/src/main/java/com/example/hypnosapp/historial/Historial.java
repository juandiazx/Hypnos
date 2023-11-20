package com.example.hypnosapp.historial;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


import com.example.hypnosapp.firebase.FirebaseHelper;
import com.example.hypnosapp.other.MenuManager;
import com.example.hypnosapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class Historial extends AppCompatActivity {
    private List<DiaModel> listaDias;
    private static final String TAG = "AjustesDeSuenyo";
    private FirebaseHelper firebaseHelper;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String userID;
    ImageView btnPerfilUsuario, btnPantallaPrincipal, btnAjustesDescanso, btnPreferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historial);

        //Menu buttons functionalities
        MenuManager funcionMenu = new MenuManager();

        //Instance of the database and the user
        firebaseHelper = new FirebaseHelper();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        //userID = firebaseUser.getUid();
        userID = "lr3SPEtJqt493dpfWoDd"; // this is the only user of the database at the time

        btnPerfilUsuario = findViewById(R.id.logoUsuarioHeader);
        btnPantallaPrincipal = findViewById(R.id.btnPantallaPrincipal);
        btnAjustesDescanso = findViewById(R.id.btnAjustesDescanso);
        btnPreferencias = findViewById(R.id.btnPreferencias);

        btnPerfilUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPerfilUsuario(Historial.this);
            }
        });
        btnPantallaPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPantallaPrincipal(Historial.this);
            }
        });
        btnAjustesDescanso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirAjustesDescanso(Historial.this);
            }
        });
        btnPreferencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirAcercaDe(Historial.this);
            }
        });

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

        firebaseHelper.getPagesFromAllNights(userID,
                new OnSuccessListener<Integer>() {
                    @Override
                    public void onSuccess(Integer pages) {
                        Log.e(TAG, "pages:" + pages);
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Error getting pages");
                    }
                });
    }
    public List<DiaModel> getListaDias() {
        return listaDias;
    }


}

