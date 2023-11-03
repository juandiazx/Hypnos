package com.example.hypnosapp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.example.hypnosapp.databinding.InicioDeSesionBinding;

public class InicioDeSesion extends AppCompatActivity {

    public InicioDeSesionBinding binding;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private EditText etCorreo, etContraseña;
    private TextView tvCorreo, tvContraseña, respuesta, tvRegistrate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = InicioDeSesionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseApp.initializeApp(this);

        etCorreo = binding.edtEmail;
        etContraseña = binding.edtContraseA;
        tvCorreo = binding.tvCorreo;
        tvContraseña = binding.tvContraseA;
        respuesta = binding.respuestaLogin;
        tvRegistrate = binding.tvRegistrate;

        tvRegistrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pulsaRegistrate();
            }
        });
    }

    public void inicioSesionCorreo(View v) {

        String correo = etCorreo.getText().toString();
        String contraseña = etContraseña.getText().toString();

        // si los parametros cumplen los requisitos, se hace el inicio de sesion
        if (AuthHelper.verificaCredenciales(etCorreo, etContraseña, tvCorreo, tvContraseña)) {
            AuthHelper.iniciarSesion(auth, correo, contraseña, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    AuthHelper.manejoRespuestaFirebase(task, respuesta,InicioDeSesion.this,"com.example.hypnosapp.Pantalla_Principal");
                }
            });
        }
    }

    private void pulsaRegistrate() {
        Class<?> destinationClass = null;
        try {
            destinationClass = Class.forName("com.example.hypnosapp.auth.Registro");
            Intent intent = new Intent(InicioDeSesion.this, destinationClass);
            InicioDeSesion.this.startActivity(intent);
            InicioDeSesion.this.finish();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
