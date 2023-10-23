package com.example.hypnosapp;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class PreinicioDeSesion extends AppCompatActivity {
    public PreinicioDeSesion binding;

    private CallbackManager mCallbackManager;
    private LoginButton btnFacebook;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static final String TAG = "FacebookLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preinicio_de_sesion);
        //binding = PreinicioDeSesionBinding.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.button_sign_in_facebook);
        loginButton.setPermissions(Arrays.asList("public_profile"));
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });
    }

    public void pulsaEmail(View view) {
        Intent intent = new Intent(PreinicioDeSesion.this, InicioDeSesion.class);
        startActivity(intent);
    }

    public void pulsaRegistrar(View view) {
        Intent intent = new Intent(PreinicioDeSesion.this, Registro.class);
        startActivity(intent);
    }

    public void pulsaIniciarConGoogle(View view) {
        GoogleHelper.iniciarConGoogle(this);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Facebook

        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GoogleHelper.RC_SIGN_IN) {
            GoogleHelper.manejoResultadoGoogle(this, data);
        }
    }

    //Facebook

    /*
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Intent intent = new Intent(PreinicioDeSesion.this, Registro.class);
        startActivity(intent);
    }
*/
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(PreinicioDeSesion.this, "Authentication aqui.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(PreinicioDeSesion.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
     /*    private void verificaSiUsuarioHaIniciadoSesion() {
        AuthHelper.verificaSiUsuarioValidado(auth, this, new Intent(this, MainActivity.class));
    }*/
}
