package com.example.hypnosapp.appactivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.hypnosapp.auth.PreinicioDeSesion;
import com.example.hypnosapp.utils.MenuManager;
import com.example.hypnosapp.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class PerfilUsuarioActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String nombreUsuario, correoUsuario;
    TextView nombre, nombreApellidos, correo;
    EditText inputNombreApellidos;

    public interface ReauthenticationListener {
        void onReauthenticationSuccess();
        void onReauthenticationFailure(String errorMessage);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //obtenemos la sesión y el usuario:
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //Obtención de datos del usuario:
        nombreUsuario = firebaseUser.getDisplayName();
        correoUsuario = firebaseUser.getEmail();
        Uri urlFoto = firebaseUser.getPhotoUrl();
        String proveedores = "";
        for (int n=0; n<firebaseUser.getProviderData().size(); n++){
            proveedores += firebaseUser.getProviderData().get(n).getProviderId()+", ";
        }

        if (proveedores.contains("google")) {
            //Si iniciamos sesión con Google:
            setContentView(R.layout.perfil_usuario_google_facebook);

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

            nombre = findViewById(R.id.nombreGoogleFacebook);
            nombre.setText(nombreUsuario);

            correo = findViewById(R.id.emailGoogleFacebook);
            correo.setText(correoUsuario);

        }
        else if(proveedores.contains("facebook")){
            //Si iniciamos sesión con Facebook:
            setContentView(R.layout.perfil_usuario_google_facebook);

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

            nombre = findViewById(R.id.nombreGoogleFacebook);
            nombre.setText(nombreUsuario);

            correo = findViewById(R.id.emailGoogleFacebook);
            correo.setText(correoUsuario);
        }
        else{
            //si iniciamos sesión con correo electrónico:
            setContentView(R.layout.perfil_usuario);

            nombreApellidos = findViewById(R.id.nombreApellidosPerfil);
            nombreApellidos.setText(nombreUsuario);

            inputNombreApellidos = findViewById(R.id.inputNombreApellidos);

            correo = findViewById(R.id.emailPerfil);
            correo.setText(correoUsuario);


            //Boton editar nombre:
            ImageView editarNombre = findViewById(R.id.editarNombre);
            editarNombre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nombreApellidos.setVisibility(View.INVISIBLE);
                    inputNombreApellidos.setVisibility(View.VISIBLE);

                    //para confirmar el cambio de nombre:
                    ImageView confirmarCambioNombre = findViewById(R.id.confirmarCambioNombre);
                    ImageView cancelarCambioNombre = findViewById(R.id.cancelarCambioNombre);

                    editarNombre.setVisibility(View.GONE);
                    confirmarCambioNombre.setVisibility(View.VISIBLE);
                    cancelarCambioNombre.setVisibility(View.VISIBLE);
                    confirmarCambioNombre.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String nombreNuevo = inputNombreApellidos.getText().toString();

                            actualizarNombreUsuario(nombreNuevo, firebaseUser);
                            confirmarCambioNombre.setVisibility(View.GONE);
                            cancelarCambioNombre.setVisibility(View.GONE);
                            editarNombre.setVisibility(View.VISIBLE);
                            inputNombreApellidos.setVisibility(View.INVISIBLE);
                            nombreApellidos.setVisibility(View.VISIBLE);
                        }
                    });

                    cancelarCambioNombre.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmarCambioNombre.setVisibility(View.GONE);
                            cancelarCambioNombre.setVisibility(View.GONE);
                            editarNombre.setVisibility(View.VISIBLE);
                            inputNombreApellidos.setVisibility(View.INVISIBLE);
                            nombreApellidos.setVisibility(View.VISIBLE);
                        }
                    });

                }
            });


            //Boton cambiarContrasenya:
            Button btnCambiarContrasenya = findViewById(R.id.btnCambiarContrasenya);
            btnCambiarContrasenya.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cambiarContrasenya();
                }
            });


        }

        //-------------------------------------------------------------------------------------
        // FUNCIONALIDAD BOTONES MENUS
        //-------------------------------------------------------------------------------------
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
        //-------------------------------------------------------------------------------------
        // FIN DE FUNCIONALIDAD BOTONES MENUS
        //-------------------------------------------------------------------------------------


        //funcionalidad del botón de cerrar sesión:
        Button botonCerrarSesion = findViewById(R.id.btnCerrarSesion);
        botonCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion(v);
            }
        });

    }//Fin onCreate()

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
    } //Fin cerrarSesion()
    private void reautenticarUsuario(final ReauthenticationListener listener) {
        if (firebaseUser != null) {

            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.reautenticacion_popup, null);

            EditText inputemailRe = dialogView.findViewById(R.id.inputEmailReautenticacion);
            EditText inputpassRe = dialogView.findViewById(R.id.inputPassReautenticacion);
            Button btnAceptarRe = dialogView.findViewById(R.id.btnAceptarReautenticacion);
            Button btnCancelarRe = dialogView.findViewById(R.id.btnCancelarReautenticacion);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Reautenticación");
            builder.setView(dialogView);
            AlertDialog dialog = builder.create();
            dialog.show();

            btnAceptarRe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = inputemailRe.getText().toString();
                    String pass = inputpassRe.getText().toString();

                    AuthCredential credential = EmailAuthProvider.getCredential(email, pass);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("REAUTENTICACION", "¡¡¡¡Usuario Reautenticado!!!!");
                                dialog.dismiss();
                                listener.onReauthenticationSuccess();
                            } else {
                                String errorMessage = "Error al reautenticar el usuario.";
                                Log.e("REAUTENTICACION", errorMessage);
                                listener.onReauthenticationFailure(errorMessage);
                            }
                        }
                    });
                }
            });
            btnCancelarRe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        else{
            Log.e("Reautenticacion", "El usuario es nulo");
        }
    }
    private void actualizarNombreUsuario(String nombre, FirebaseUser usuario){

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(nombre)
                .build();

        usuario.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("nombreUsuario", "¡Nombre de usuario actualizado!");
                            nombreUsuario = firebaseUser.getDisplayName();
                            nombreApellidos.setText(nombreUsuario);
                        }
                        else{
                            Log.e("nombreUsuario", "Error en actualizar nombre usuario");
                        }
                    }
                });
    }
    private void cambiarContrasenya(){
        reautenticarUsuario(new ReauthenticationListener() {
            @Override
            public void onReauthenticationSuccess() {
                popUpCambiarContrasenya();
            }

            @Override
            public void onReauthenticationFailure(String errorMessage) {
                Log.e("Reautenticacion", errorMessage);
            }
        });
    }
    private void popUpCambiarContrasenya(){
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.cambiar_contrasenya, null);

        EditText inputPassNueva = dialogView.findViewById(R.id.inputPassNueva);
        EditText inputPassRepe = dialogView.findViewById(R.id.inputPassRepe);
        Button btnAceptar = dialogView.findViewById(R.id.btnAceptarCambioContrasenya);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelarCambioContrasenya);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar correo");
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passNueva = inputPassNueva.getText().toString();
                String passRepe = inputPassRepe.getText().toString();

                if (passNueva.equals(passRepe)) {
                    Toast.makeText(PerfilUsuarioActivity.this, "Pass Correcta", Toast.LENGTH_SHORT).show();
                    Log.d("CambioContrasenya", "Contraseña repetida correctamente");

                    firebaseUser.updatePassword(passNueva)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("password", "User password updated.");
                                    }
                                    else{
                                        Log.e("password", "No se ha cambiado la contraseña. Error: " + task.getException().getMessage());
                                    }
                                }
                            });
                    dialog.dismiss();
                }else{
                    Toast.makeText(PerfilUsuarioActivity.this, "Contraseña incorrecta, vuelve a introducirla", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}//Class



