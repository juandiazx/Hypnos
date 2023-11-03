package com.example.hypnosapp.appactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hypnosapp.R;

import java.util.Objects;

public class ConfirmarCambioActivity extends AppCompatActivity {

    EditText repiteCorreo, repiteContrasenya;
    Button aceptar, cancelar;
    String valor;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmar_cambio);

        Bundle extras = getIntent().getExtras();
        String emailNuevo = extras.getString("email");
        //String passNueva = extras.getString("contrasenya");


        repiteCorreo = findViewById(R.id.inputEmailNuevo);
        //repiteContrasenya = findViewById(R.id.inputPassNueva);
        aceptar = findViewById(R.id.btnAceptar);
        cancelar = findViewById(R.id.btnCancelar);



        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String correoRepetido = repiteCorreo.getText().toString();
                //String contrasenyaRepetida = repiteContrasenya.getText().toString();

                pulsaAceptar(correoRepetido, emailNuevo);
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pulsaCancelar();
            }
        });
    }

        private void pulsaAceptar(String correoRepetido, String emailNuevo){


            if(Objects.equals(correoRepetido, emailNuevo)){
                valor = "correcto";


               Toast.makeText(this, "E-mail correcto", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                intent.putExtra("valor",valor);
                setResult(RESULT_OK, intent);
                finish();
            }
            else {
                valor = "incorrecto";


                Toast.makeText(this, "El e-mail es incorrecto. Vuelva a introducirlo.", Toast.LENGTH_SHORT).show();
            }
        }

        private void pulsaCancelar(){
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }

    }

