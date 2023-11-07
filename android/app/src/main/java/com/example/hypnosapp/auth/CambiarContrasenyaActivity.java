package com.example.hypnosapp.auth;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hypnosapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class CambiarContrasenyaActivity extends AppCompatActivity {
    EditText passNueva, passRepetida;
    Button aceptar, cancelar;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cambiar_contrasenya);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        passNueva = findViewById(R.id.inputEmailReautenticacion);
        passRepetida = findViewById(R.id.inputPassReautenticacion);
        aceptar = findViewById(R.id.btnAceptarReautenticacion);
        cancelar = findViewById(R.id.btnCancelarReautenticacion);



        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String contrasenyaNueva = passNueva.getText().toString();
                String contrasenyaRepetida = passRepetida.getText().toString();

                pulsaAceptar(contrasenyaRepetida, contrasenyaNueva);
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pulsaCancelar();
            }
        });
    }

    private void pulsaAceptar(String passRepe, String passNueva){


        if(Objects.equals(passRepe, passNueva)){

            Toast.makeText(this, "contraseña correcta", Toast.LENGTH_SHORT).show();
            reautenticarUsuario();
        }
        else {
            Toast.makeText(this, "Las contraseñas no coinciden. Vuelve a introducirlas", Toast.LENGTH_SHORT).show();
        }
    }

    private void pulsaCancelar(){
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }


    private void reautenticarUsuario() {

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.reautenticacion_popup, null);

        EditText inputemailRe = dialogView.findViewById(R.id.inputEmailReautenticacion);
        EditText inputpassRe = dialogView.findViewById(R.id.inputPassReautenticacion);
        Button btnAceptarRe = dialogView.findViewById(R.id.btnAceptarReautenticacion);
        Button btnCancelarRe = dialogView.findViewById(R.id.btnCancelarReautenticacion);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reautenticación");
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        btnAceptarRe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputemailRe.getText().toString();
                String pass = inputpassRe.getText().toString();

                AuthCredential credential = EmailAuthProvider.getCredential(email, pass);
                firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("REAUTENTICACION", "¡¡¡¡Usuario Reautenticado!!!!");
                        cambiarContrasenya(passNueva.getText().toString());
                        dialog.dismiss();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
            }
        });

        btnCancelarRe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void cambiarContrasenya(String passNueva){

        firebaseUser.updatePassword(passNueva)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("password", "User password updated.");
                        }
                        else{
                            Log.e("password", "No se ha cambiado la contraseña. Error: " + task.getException().getMessage());
                        }
                    }
                });
    }

}//class

