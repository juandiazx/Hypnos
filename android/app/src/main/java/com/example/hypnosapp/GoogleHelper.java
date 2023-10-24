package com.example.hypnosapp;

import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class GoogleHelper {

    public static final int RC_GOOGLE_SIGN_IN = 123; // Use your own request code

    private AppCompatActivity activity;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth auth;

    public GoogleHelper(AppCompatActivity activity) {
        this.activity = activity;

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(activity, gso);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
    //Se llama en pulsaIniciarConGoogle, en el onClick del boton de google
    }

    public void iniciarConGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    public void manejoResultadoGoogle(Intent data) {
    //Se llama en el onActivityResult de PreinicioDeSesion
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            // Handle error
            // Por ejemplo, muestra un mensaje de error al usuario
            Toast.makeText(activity, "Error al autenticar con Google", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
    //Se llama si el token != null en manejoResultadoGoogle
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            try {
                                Class<?> destinationClass = Class.forName("com.example.hypnosapp.Pantalla_Principal");
                                Intent intent = new Intent(activity, destinationClass);
                                activity.startActivity(intent);
                                activity.finish();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Toast.makeText(activity, "Error al autenticar con Firebase", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}