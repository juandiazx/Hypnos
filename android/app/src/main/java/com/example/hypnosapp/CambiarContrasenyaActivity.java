package com.example.hypnosapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class CambiarContrasenyaActivity extends AppCompatActivity {
    EditText passNueva, passRepetida;
    Button aceptar, cancelar;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cambiar_contrasenya);


        passNueva = findViewById(R.id.inputPassNueva);
        passRepetida = findViewById(R.id.inputPassRepetida);
        aceptar = findViewById(R.id.btnAceptar);
        cancelar = findViewById(R.id.btnCancelar);



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
            cambiarContrasenya(passNueva);

            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
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


    private void cambiarContrasenya(String passNueva){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updatePassword(passNueva)
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

}

