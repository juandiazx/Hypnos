package com.example.hypnosapp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.app.DatePickerDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hypnosapp.databinding.RegistroBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

public class Registro extends AppCompatActivity {

    public RegistroBinding binding;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private EditText etCorreo, etContraseña, etRepContraseña, etNombreApellido, etFecha;
    private TextView tvCorreo, tvContraseña, tvRepContraseña, tvNombreApellido, tvFecha, tvRespuesta, tvIniciaSesion;

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
        tvIniciaSesion = binding.tvIniciaSesion;
        tvRespuesta = binding.respuestaRegistro;

        etFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePickerDialog();
            }
        });

        tvIniciaSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pulsaIniciaSesion();
            }
        });
    }

    public void registroCorreo(View v) {

        String correo = etCorreo.getText().toString();;
        String contraseña = etContraseña.getText().toString();;
        String nombreCompleto = etNombreApellido.getText().toString();
        String fechaNacimiento = etFecha.getText().toString();

        if (AuthHelperViejo.verificaCredenciales(etCorreo, etContraseña, tvCorreo, tvContraseña) &&
                AuthHelperViejo.verificaCamposRegistro(etNombreApellido, etFecha, tvNombreApellido, tvFecha) &&
                AuthHelperViejo.verificaContraseña(etContraseña, etRepContraseña, tvRepContraseña)) {
            AuthHelperViejo.registrarUsuario(auth, correo, contraseña, nombreCompleto, fechaNacimiento, this, tvRespuesta, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //AuthHelper.manejoRespuestaFirebase(task, tvRespuesta, Registro.this, "com.example.hypnosapp.InicioDeSesion");
                    //AuthHelper.mostrarPopUpRegistro(Registro.this);
                }
            });
        }
    }

    private void mostrarDatePickerDialog() {

        Calendar calendar = Calendar.getInstance(); // Obtiene una instancia del calendario con la fecha y hora actuales
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String fechaSeleccionada = dayOfMonth + "/" + (month + 1) + "/" + year;
                etFecha.setText(fechaSeleccionada);
            }
        }, year, month, dayOfMonth); // Establece la fecha inicial como la fecha actual

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()); // Opcional: establece una fecha máxima (hasta la fecha actual)
        datePickerDialog.show();
    }

    private void pulsaIniciaSesion() {
        Class<?> destinationClass = null;
        try {
            destinationClass = Class.forName("com.example.hypnosapp.auth.InicioDeSesion");
            Intent intent = new Intent(Registro.this, destinationClass);
            Registro.this.startActivity(intent);
            Registro.this.finish();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
