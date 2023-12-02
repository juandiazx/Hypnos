package com.example.hypnosapp.appactivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hypnosapp.R;
import com.example.hypnosapp.ManejoPermisosUbicacion;

public class PlataformaFamiliarActivity extends AppCompatActivity {
    TextView texto;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plataforma_familiar);
        texto = findViewById(R.id.textView23);

        String userID = getIntent().getStringExtra("userID");

        texto.setText(userID);
    }
}
