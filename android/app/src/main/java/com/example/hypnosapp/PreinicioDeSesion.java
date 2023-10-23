package com.example.hypnosapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class PreinicioDeSesion extends AppCompatActivity {

    public PreinicioDeSesion binding;
    private GoogleHelper googleHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preinicio_de_sesion);
        //binding = PreinicioDeSesionBinding.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());
        // Inicializa la instancia de GoogleHelper
        googleHelper = new GoogleHelper(PreinicioDeSesion.this);
    }

    public void pulsaEmail(View view) {
        Intent intent = new Intent(PreinicioDeSesion.this, InicioDeSesion.class);
        startActivity(intent);
    }

    public void pulsaRegistrar(View view) {
        Intent intent = new Intent(PreinicioDeSesion.this, Registro.class);
        startActivity(intent);
    }

    public void pulsaIniciarConGoogle(View view) {
        googleHelper.iniciarConGoogle();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == googleHelper.RC_GOOGLE_SIGN_IN) {
            googleHelper.manejoResultadoGoogle(data);
        }
    }

     /*    private void verificaSiUsuarioHaIniciadoSesion() {
        AuthHelper.verificaSiUsuarioValidado(auth, this, new Intent(this, MainActivity.class));
    }*/
}
