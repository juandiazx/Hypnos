package com.example.hypnosapp;

import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AuthHelper {

    public static boolean verificaCredenciales(EditText etCorreo, EditText etContraseña, TextView tvCorreo, TextView tvContraseña) {
        String correo = etCorreo.getText().toString();
        String contraseña = etContraseña.getText().toString();

        if (correo.isEmpty()) {
            tvCorreo.setText("Introduce un correo");
        } else if (!correo.matches(".+@.+[.].+")) {
            tvCorreo.setText("Correo no válido");
        } else if (contraseña.isEmpty()) {
            tvContraseña.setText("Introduce una contraseña");
        } else if (contraseña.length() < 6) {
            tvContraseña.setText("Debe contener al menos 6 caracteres");
        } else if (!contraseña.matches(".*[0-9].*")) {
            tvContraseña.setText("Debe contener al menos un número");
        } else if (!contraseña.matches(".*[A-Z].*")) {
            tvContraseña.setText("Debe contener al menos una letra mayúscula");
        } else if (!contraseña.matches(".*[a-z].*")) {
            tvContraseña.setText("Debe contener al menos una letra minúscula");
        } else {
            // Limpia los mensajes de error si todos los campos son válidos
            tvCorreo.setText(null);
            tvContraseña.setText(null);
            return true;
        }
        return false;
    }

    public static boolean verificaCamposRegistro(EditText etNombreApellido, EditText etFechaNac, TextView tvNombreApellido, TextView tvFechaNac) {
        String nombreApellido = etNombreApellido.getText().toString();
        String fechaNac = etFechaNac.getText().toString();

        if (nombreApellido.isEmpty()) {
            tvNombreApellido.setText("Introduce un nombre y apellido");
        } else if (fechaNac.isEmpty()) {
            tvFechaNac.setText("Introduce una fecha de nacimiento");
        } else if (!nombreApellido.matches("^[A-Za-z\\s]+$")) {
            tvNombreApellido.setText("Nombre/Apellido no válido");
        } else if (!fechaNac.matches("^\\d{2}/\\d{2}/\\d{4}$")) {
            tvFechaNac.setText("Fecha de Nacimiento no válida (formato dd/mm/yyyy)");
        } else {
            // Limpia los mensajes de error si todos los campos son válidos
            tvNombreApellido.setText(null);
            tvFechaNac.setText(null);
            return true;
        }

        return false;
    }

    public static boolean verificaContraseña(EditText etContraseña, EditText etRepContraseña, TextView tvRepContraseña) {
        String contraseña = etContraseña.getText().toString();
        String repContraseña = etRepContraseña.getText().toString();

        if(contraseña.equals(repContraseña)) {
            return true;
        }
        tvRepContraseña.setText("Las contraseñas no coinciden");
        return false;
    }

    public static void iniciarSesion(FirebaseAuth auth, String correo, String contraseña,
                                     final OnCompleteListener<AuthResult> onComplete) {
        auth.signInWithEmailAndPassword(correo, contraseña)
                .addOnCompleteListener(onComplete);
    }

    public static void registrarUsuario(final FirebaseAuth auth, final String correo,
                    final String contraseña, final String nombre, final String fechaNacimiento,
                        Context appContext, final OnCompleteListener<AuthResult> onComplete) {
        auth.createUserWithEmailAndPassword(correo, contraseña)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();

                            // Verifica si el correo electrónico ya ha sido verificado
                            if (!user.isEmailVerified()) {
                                // Envía un correo de verificación
                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> emailVerificationTask) {
                                                if (emailVerificationTask.isSuccessful()) {
                                                    // Envío de correo exitoso
                                                    Toast.makeText(appContext, "Correo de verificación enviado. Por favor, " +
                                                            "verifique su correo electrónico.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // Error al enviar el correo de verificación
                                                    Toast.makeText(appContext, "Correo de verificación enviado. Por favor, " +
                                                            "verifique su correo electrónico.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }

                            // Almacena información adicional en la base de datos en tiempo real de Firebase
                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                            String userID = user.getUid();
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("nombre", nombre);
                            userData.put("fechaNacimiento", fechaNacimiento);
                            usersRef.child(userID).setValue(userData);
                        }

                        onComplete.onComplete(task);
                    }
                });
    }


    //Se llama en inicio de sesion y registro para redirijir
    public static void manejoRespuestaFirebase(Task<AuthResult> task, TextView respuesta, AppCompatActivity activity, String className) {
        if (task.isSuccessful()) {
            // El usuario se autenticó correctamente, se redirige a la actividad supuesta
            try {
                Class<?> destinationClass = Class.forName(className);
                Intent intent = new Intent(activity, destinationClass);
                activity.startActivity(intent);
                activity.finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            respuesta.setText(task.getException().getLocalizedMessage());
        }
    }


    public static void verificaSiUsuarioHaIniciadoSesion(FirebaseAuth auth, AppCompatActivity activity, Intent intent) {
        if (auth.getCurrentUser() != null) {
            activity.startActivity(intent);
            activity.finish();
        }
    }

}
