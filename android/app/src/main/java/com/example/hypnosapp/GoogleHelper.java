package com.example.hypnosapp;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class GoogleHelper {
    public static final int RC_SIGN_IN = 123;


    //Se llama en pulsaIniciarConGoogle, en el onClick del boton de google
    public static void iniciarConGoogle(Activity activity) {
        Intent signInIntent = GoogleSignIn.getClient(activity, GoogleSignInOptions.DEFAULT_SIGN_IN).getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //Se llama en el onActivityResult de PreinicioDeSesion
    public static void manejoResultadoGoogle(Activity activity, Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                String token = account.getIdToken();
                if (token != null) {
                    Toast.makeText(activity, token, Toast.LENGTH_SHORT).show();
                    firebaseAuthWithGoogle(activity, token);
                } else {
                    // El idToken es nulo, maneja el error aquí.
                    Toast.makeText(activity, "El idToken es nulo", Toast.LENGTH_SHORT).show();
                }
            } else {
                // La cuenta de Google es nula, maneja el error aquí.
                Toast.makeText(activity, "La cuenta de Google es nula", Toast.LENGTH_SHORT).show();
            }
        } catch (ApiException e) {
            // Maneja otros errores aquí
            Toast.makeText(activity, "Error al iniciar sesión con Google: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //Se llama si el token != null en manejoResultadoGoogle
    private static void firebaseAuthWithGoogle(Activity activity, String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        // El usuario ha iniciado sesión con Google
                    } else {
                        // Maneja errores aquí
                        Toast.makeText(activity, "Error al autenticar con Firebase: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
