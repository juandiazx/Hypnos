package com.example.hypnosapp.appactivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hypnosapp.firebase.FirebaseHelper;
import com.example.hypnosapp.other.MenuManager;
import com.example.hypnosapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AjustesDeSuenyoActivity extends AppCompatActivity {

    FirebaseHelper firebaseHelper = new FirebaseHelper();
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajustes_de_sueno);

        //FUNCIONALIDAD BOTONES MENUS
        MenuManager funcionMenu = new MenuManager();

        ImageView btnPerfilUsuario = findViewById(R.id.logoUsuarioHeader);
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
        //FIN DE FUNCIONALIDAD BOTONES MENUS


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = firebaseUser.getUid();

        //PRUEBA
        Button btnPrueba = findViewById(R.id.btnPrueba);
        btnPrueba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseHelper.setIdealWakeUpHour(userId, "EstoEs una prueba");
            }
        });



    }
}
