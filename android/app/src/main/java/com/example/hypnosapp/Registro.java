package com.example.hypnosapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hypnosapp.databinding.RegistroBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Registro extends AppCompatActivity {

    public RegistroBinding binding;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private EditText etCorreo, etContraseña, etRepContraseña, etNombreApellido, etFecha;
    private TextView tvCorreo, tvContraseña, tvRepContraseña, tvNombreApellido, tvFecha, tvRespuesta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RegistroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        etCorreo = binding.edtEmailRegistro;
        etContraseña = binding.edtContraseARegistro;
        etRepContraseña = binding.edtRepetirContraseA;
        etNombreApellido = binding.edtNombreApellidos;
        etFecha = binding.edtFechaNacimiento;
        tvCorreo = binding.tvemailre;
        tvContraseña = binding.tvcontrare;
        tvRepContraseña = binding.tvcontraredos;
        tvNombreApellido = binding.tvnombre;
        tvFecha = binding.tvfecha;
    }

    public void registroCorreo(View v) {

        String correo = etCorreo.getText().toString();;
        String contraseña = etContraseña.getText().toString();;
        String nombreCompleto = etNombreApellido.getText().toString();
        String fechaNacimiento = etFecha.getText().toString();

        if (AuthHelper.verificaCredenciales(etCorreo, etContraseña, tvCorreo, tvContraseña) &&
                AuthHelper.verificaCamposRegistro(etNombreApellido, etFecha, tvNombreApellido, tvFecha) &&
                AuthHelper.verificaContraseña(etContraseña, etRepContraseña, tvRepContraseña)) {
            AuthHelper.registrarUsuario(auth, correo, contraseña, nombreCompleto, fechaNacimiento, this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    AuthHelper.manejoRespuestaFirebase(task, tvRespuesta, Registro.this, "com.example.hypnosapp.InicioDeSesion");
                }
            });
        }
    }
}
