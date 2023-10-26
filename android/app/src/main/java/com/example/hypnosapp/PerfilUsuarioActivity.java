package com.example.hypnosapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class PerfilUsuarioActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    TextView nombre;
    TextView correo;
    TextView contrasenya;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_usuario);


        //FUNCIONALIDAD BOTONES MENUS
        MenuManager funcionMenu = new MenuManager();

        ImageView btnPerfilUsuario = findViewById(R.id.logoUsuarioHeader);
        btnPerfilUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPerfilUsuario(PerfilUsuarioActivity.this);
            }
        });

        ImageView btnPantallaPrincipal = findViewById(R.id.btnPantallaPrincipal);
        btnPantallaPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPantallaPrincipal(PerfilUsuarioActivity.this);
            }
        });

        ImageView btnAjustesDescanso = findViewById(R.id.btnAjustesDescanso);
        btnAjustesDescanso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirAjustesDescanso(PerfilUsuarioActivity.this);
            }
        });

        ImageView btnPreferencias = findViewById(R.id.btnPreferencias);
        btnPreferencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                funcionMenu.abrirAcercaDe(PerfilUsuarioActivity.this);
            }
        });


        //obtenemos la sesión y el usuario:
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //funcionalidad del botón de cerrar sesión:
        Button botonCerrarSesion = findViewById(R.id.btnCerrarSesion);
        botonCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion(v);
            }
        });

        //Escribir los datos en la página de perfil:
        nombre = findViewById(R.id.inputNombre);
        correo = findViewById(R.id.inputEmail);
        contrasenya = findViewById(R.id.inputContrasenya);

        //Obtención de datos del usuario:
        String nombreUsuario = firebaseUser.getDisplayName();
        String correoUsuario = firebaseUser.getEmail();
        //String passUsuario;
        Uri urlFoto = firebaseUser.getPhotoUrl();
        String proveedores = "";
        for (int n=0; n<firebaseUser.getProviderData().size(); n++){
            proveedores += firebaseUser.getProviderData().get(n).getProviderId()+", ";
        }

        if (proveedores.contains("google")) {
            contrasenya.setText("google");
        }
        else if(proveedores.contains("facebook")){
            contrasenya.setText("facebook");
        }
        else{
            contrasenya.setText("correo");
        }



        nombre.setText(nombreUsuario);
        correo.setText(correoUsuario);
        //contrasenya.setText(proveedores);


        //PARA OBTENER LA FOTO DE PERFIL DEL USUARIO
        // Inicialización Volley
        RequestQueue colaPeticiones = Volley.newRequestQueue(this);
        ImageLoader lectorImagenes = new ImageLoader(colaPeticiones,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> cache =
                            new LruCache<String, Bitmap>(10);
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }
                });

        // Foto de usuario
        if (urlFoto != null) {
            NetworkImageView foto = (NetworkImageView) findViewById(R.id.fotoPerfil);
            foto.setImageUrl(urlFoto.toString(), lectorImagenes);
        }

/*
        //Boton confirmar cambios:
        Button btnConfirmarCambios = findViewById(R.id.btnConfirmarCambios);
        btnConfirmarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modificarDatosPerfil(v);
            }
        });

 */

    }

    public void cerrarSesion(View view){

        firebaseAuth.signOut();

        //Facebook Log Out Login Manager
        LoginManager.getInstance().logOut();

        Toast.makeText(this, "Se ha cerrado la sesión", Toast.LENGTH_SHORT).show();

        //después de cerrar sesión nos dirigirá a la pantalla de pre-inicio de sesión:
        Intent i = new Intent(getApplicationContext(), PreinicioDeSesion.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    private void modificarDatosPerfil(View view){

        String nombreNuevo = nombre.getText().toString();
        String emailNuevo = correo.getText().toString();
        String passNueva = contrasenya.getText().toString();

        UserProfileChangeRequest perfil = new UserProfileChangeRequest.Builder()
                .setDisplayName(nombreNuevo)
                //.setPhotoUri(Uri.parse("https://www.ejemplo.com/usuario/foto.jpg"))
                .build();

        firebaseUser.updateProfile(perfil).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Log.e("Perfil", "Acción incorrecta");
                }
            }
        });
        firebaseUser.updateEmail(emailNuevo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Log.e("E-mail", "Acción incorrecta");
                }
            }
        });
        firebaseUser.updatePassword(passNueva).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Log.e("Contraseña", "Acción incorrecta");
                }
            }
        });
        Toast.makeText(this, "Cambios confirmados", Toast.LENGTH_SHORT).show();
    }


}
