package com.example.hypnosapp.auth;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

public class AuthHelper {

    public static void iniciarSesion(FirebaseAuth auth, String correo, String contraseña,
                                     final OnCompleteListener<AuthResult> onComplete) {
        auth.signInWithEmailAndPassword(correo, contraseña)
                .addOnCompleteListener(onComplete);
    }

    public static void manejoRespuestaFirebase(Task<AuthResult> task, TextView respuesta, AppCompatActivity activity, String className) {
        if (task.isSuccessful()) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                if (user.isEmailVerified()) {
                    // Usuario autenticado y correo verificado, proceder con la lógica de la aplicación
                    try {
                        Class<?> destinationClass = Class.forName(className);
                        Intent intent = new Intent(activity, destinationClass);
                        activity.startActivity(intent);
                        activity.finish();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                // Usuario autenticado pero correo no verificado, mostrar un mensaje
                respuesta.setText("Por favor verifique su correo electronico");
            }
        }
        respuesta.setText("Ha ocurrido un problema, las credenciales de inicio no son correctas");
    }

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

        if (contraseña.equals(repContraseña)) {
            return true;
        }
        tvRepContraseña.setText("Las contraseñas no coinciden");
        return false;
    }

    public static void registrarUsuario(final FirebaseAuth auth, final String correo,
                                        final String contraseña, final String nombre, final String fechaNacimiento,
                                        Context appContext, TextView respuesta, final OnCompleteListener<AuthResult> onComplete) {

        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(correo)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()){
                            boolean check = task.getResult().getSignInMethods().isEmpty();
                            if (!check){
                                respuesta.setText("El correo ya está registrado");
                                Toast.makeText(appContext, "El correo ya está registrado", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                registrarNuevaCuenta(auth, correo, contraseña, nombre, fechaNacimiento, appContext, onComplete);
                            }
                        }
                    }
                });
    }

    private static void registrarNuevaCuenta(final FirebaseAuth auth, final String correo,
                                             final String contraseña, final String nombre, final String fechaNacimiento,
                                             final Context appContext, final OnCompleteListener<AuthResult> onComplete) {
        auth.createUserWithEmailAndPassword(correo, contraseña)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            enviarCorreoDeVerificacion(user, appContext);
                            mostrarPopUpRegistro((AppCompatActivity) appContext);
                            //almacenarInformacionUsuario(user, nombre, fechaNacimiento);
                            asignarDisplayName(user,nombre);
                        }
                        onComplete.onComplete(task);
                    }
                });
    }

    private static void enviarCorreoDeVerificacion(FirebaseUser user, Context appContext) {
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> emailVerificationTask) {
                        if (!emailVerificationTask.isSuccessful()) {
                            // Error al enviar el correo de verificación
                            Toast.makeText(appContext, "No se ha podido enviar el código de verificación",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void mostrarPopUpRegistro(AppCompatActivity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Se ha registrado correctamente")
                .setMessage("Correo de verificación enviado. Por favor, verifique su correo electrónico.")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        try {
                            Class<?> destinationClass = Class.forName("com.example.hypnosapp.InicioDeSesion");
                            Intent intent = new Intent(activity, destinationClass);
                            activity.startActivity(intent);
                            activity.finish();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .show();
    }

    private static void asignarDisplayName(FirebaseUser user, String nombre) {
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nombre)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User profile updated.");
                            } else {
                                Log.d(TAG, "Error en asignar displayname");
                            }
                        }
                    });
        }
    }

    private static void almacenarInformacionUsuario(FirebaseUser user, String nombre, String fechaNacimiento) {

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        String userID = user.getUid();

        Map<String, Object> userData = new HashMap<>();
        userData.put("nombre", nombre);
        userData.put("fechaNacimiento", fechaNacimiento);
        usersRef.child(userID).setValue(userData);

    }
}

