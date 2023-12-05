package com.example.hypnosapp.appactivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hypnosapp.R;
import com.example.hypnosapp.auth.AuthHelper;
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
    private TextView tvCorreo, tvContraseña, respuesta, tvRegistrate, tvOlvidarContraseña;
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
        tvOlvidarContraseña = binding.txtOlvidarContraseA;

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

        tvOlvidarContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Llama a la función para mostrar el diálogo emergente de "Olvidé mi contraseña".
                showOlvidarContrasenaDialog();
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
            destinationClass = Class.forName("com.example.hypnosapp.appactivity.Registro");
            Intent intent = new Intent(InicioDeSesion.this, destinationClass);
            InicioDeSesion.this.startActivity(intent);
            InicioDeSesion.this.finish();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void showOlvidarContrasenaDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.pop_up_olvidar_contra, null);
        // Configura el cuadro de diálogo con el diseño personalizado.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        final EditText correoOlvidaContra = dialogView.findViewById(R.id.correoOlvidaContra);
        Button btnEnviar = dialogView.findViewById(R.id.btnEnviar);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelarReautenticacion);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtén la dirección de correo electrónico ingresada por el usuario en el cuadro de diálogo.
                String emailAddress = correoOlvidaContra.getText().toString().trim();
                enviarCorreoOlvidarContra(emailAddress, dialog);
            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cierra el cuadro de diálogo al hacer clic en "Cancelar".
                dialog.dismiss();
            }
        });
    }

    private void enviarCorreoOlvidarContra(String emailAddress, AlertDialog dialog) {
        // Envía un correo electrónico para restablecer la contraseña.
        FirebaseAuth.getInstance().sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // El correo electrónico para restablecer la contraseña se ha enviado correctamente.
                            dialog.dismiss();
                            Toast.makeText(InicioDeSesion.this, "Se ha enviado un correo electrónico para restablecer la contraseña.", Toast.LENGTH_LONG).show();
                        } else {
                            // Ocurrió un error al enviar el correo electrónico para restablecer la contraseña.
                            Toast.makeText(InicioDeSesion.this, "Error al enviar el correo electrónico para restablecer la contraseña. Verifique la dirección de correo electrónico.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
