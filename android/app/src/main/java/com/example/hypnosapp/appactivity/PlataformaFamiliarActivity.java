package com.example.hypnosapp.appactivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hypnosapp.R;
import com.example.hypnosapp.firebase.FirebaseHelper;

public class PlataformaFamiliarActivity extends AppCompatActivity {
    TextView texto, textoFecha;

    ImageView imgFamily;

    FirebaseHelper firebaseHelper = new FirebaseHelper();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plataforma_familiar);
        textoFecha = findViewById(R.id.textoFecha);
        imgFamily = findViewById(R.id.imgFamily);

        String userID = getIntent().getStringExtra("userID");

        firebaseHelper.cargarUltimaImagen(this, imgFamily,userID,textoFecha);

    }
}
