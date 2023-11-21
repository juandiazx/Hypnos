package com.example.hypnosapp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.hypnosapp.R;
import com.example.hypnosapp.appactivity.AcercaDeActivity;
import com.example.hypnosapp.mainpage.Pantalla_Principal;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class PreinicioDeSesion extends AppCompatActivity {
    public PreinicioDeSesion binding;
    private GoogleHelper googleHelper;
    LoginButton loginButtonFacebookEscondido;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static final String TAG = "FacebookLogin";
    Button btnGoogle, btnFacebook, btnEmail;
    TextView tvRegistrate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preinicio_de_sesion);

        btnGoogle = findViewById(R.id.btnGoogle);
        btnFacebook = findViewById(R.id.btnFacebook);
        btnEmail = findViewById(R.id.btnEmail);
        tvRegistrate = findViewById(R.id.tvRegistratePreinicio);

        //binding = PreinicioDeSesionBinding.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());
        // Inicializa la instancia de GoogleHelper
        googleHelper = new GoogleHelper(PreinicioDeSesion.this);

        //Llamada de inicio al método de facebook iniciar auth
        handleFacebookStart();

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pulsaIniciarConGoogle();
            }
        });

        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pulsaIniciarConFacebook();
            }
        });

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pulsaEmail();
            }
        });

        tvRegistrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pulsaRegistrar();
            }
        });
    }

    public void pulsaEmail() {
        Intent intent = new Intent(PreinicioDeSesion.this, InicioDeSesion.class);
        startActivity(intent);
    }

    public void pulsaRegistrar() {
        Intent intent = new Intent(PreinicioDeSesion.this, Registro.class);
        startActivity(intent);
    }

    public void pulsaIniciarConGoogle() {
        googleHelper.iniciarConGoogle();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Facebook

        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == googleHelper.RC_GOOGLE_SIGN_IN) {
            googleHelper.manejoResultadoGoogle(data);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser !=null && currentUser.isEmailVerified()){
            Intent intent = new Intent(PreinicioDeSesion.this, Pantalla_Principal.class);
            startActivity(intent);
        }
    }

    //----------------------------------------------------------------------------------------------------------
    //FACEBOOK
    //----------------------------------------------------------------------------------------------------------
    //Metodo onClick del boton personalizado de facebook, llama al boton escondido de facebook y hace click
    public void pulsaIniciarConFacebook() {
        loginButtonFacebookEscondido.performClick();
    }

    //Metodo inicial llamado en el onCreate()
    private void handleFacebookStart(){
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        loginButtonFacebookEscondido = findViewById(R.id.button_sign_in_facebook);
        loginButtonFacebookEscondido.setPermissions(Arrays.asList("public_profile","email"));
        loginButtonFacebookEscondido.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
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
                            if (user != null) {
                                try {
                                    AppCompatActivity thisActivity = PreinicioDeSesion.this;
                                    Class<?> destinationClass = Class.forName("com.example.hypnosapp.mainpage.Pantalla_Principal");
                                    Intent intent = new Intent(thisActivity, destinationClass);
                                    thisActivity.startActivity(intent);
                                    thisActivity.finish();
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(PreinicioDeSesion.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    //----------------------------------------------------------------------------------------------------------
    //FINALIZA FACEBOOK
    //----------------------------------------------------------------------------------------------------------
}
