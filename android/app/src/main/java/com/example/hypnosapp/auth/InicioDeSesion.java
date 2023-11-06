package com.example.hypnosapp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hypnosapp.R;
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
    private ImageView imageViewPasswordToggle;
    private boolean isPasswordVisible = false;

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
        imageViewPasswordToggle = binding.imageViewPasswordToggle;

        tvRegistrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pulsaRegistrate();
            }
        });

        imageViewPasswordToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPasswordVisible = !isPasswordVisible;
                int visibility = isPasswordVisible ? View.VISIBLE : View.GONE;
                etContraseña.setInputType(isPasswordVisible ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                imageViewPasswordToggle.setImageResource(isPasswordVisible ? R.drawable.ic_visibility_eye : R.drawable.ic_visibility_eye_off);
            }
        });
    }

    public void inicioSesionCorreo(View v) {

        String correo = etCorreo.getText().toString();
        String contraseña = etContraseña.getText().toString();

        // si los parametros cumplen los requisitos, se hace el inicio de sesion
        if (AuthHelperViejo.verificaCredenciales(etCorreo, etContraseña, tvCorreo, tvContraseña)) {
            AuthHelperViejo.iniciarSesion(auth, correo, contraseña, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    AuthHelperViejo.manejoRespuestaFirebase(task, respuesta,InicioDeSesion.this,"com.example.hypnosapp.Pantalla_Principal");
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
