package com.example.hypnosapp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hypnosapp.R;

import java.util.Objects;

public class PopUpComprobarCorreoActivity extends AppCompatActivity {

    EditText repiteCorreo;
    Button aceptar, cancelar;

        @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmar_correo);

        Bundle extras = getIntent().getExtras();
        String emailNuevo = extras.getString("email");



        repiteCorreo = findViewById(R.id.inputEmailReautenticacion);
        aceptar = findViewById(R.id.btnAceptarReautenticacion);
        cancelar = findViewById(R.id.btnCancelarReautenticacion);



        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String correoRepetido = repiteCorreo.getText().toString();

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

               Toast.makeText(this, "E-mail correcto", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
            else {
                Toast.makeText(this, "El e-mail es incorrecto. Vuelva a introducirlo.", Toast.LENGTH_SHORT).show();
            }
        }

        private void pulsaCancelar(){
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }

    }

