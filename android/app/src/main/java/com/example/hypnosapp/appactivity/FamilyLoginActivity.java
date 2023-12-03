package com.example.hypnosapp.appactivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hypnosapp.R;
import com.example.hypnosapp.auth.PreinicioDeSesion;
import com.example.hypnosapp.firebase.FirebaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class FamilyLoginActivity extends AppCompatActivity {

    private EditText codigoAccesoEditText;

    private TextView tvCodigo;

    FirebaseHelper firebaseHelper = new FirebaseHelper();

    private Button botonAcceso;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acceso_familiar);

        //FIND VIEWS BY ID
        codigoAccesoEditText = findViewById(R.id.codigoAcceso);
        botonAcceso = findViewById(R.id.btnAccesoFamiliar);
        tvCodigo = findViewById(R.id.tvCodigo);

        botonAcceso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accessCode = codigoAccesoEditText.getText().toString();
                boolean flagContinuar = validarFormatoCodigoAcceso(accessCode);
                if (flagContinuar) {
                    firebaseHelper.checkFamilyAccessCode(Integer.parseInt(accessCode),
                            new OnSuccessListener<String>() {
                                @Override
                                public void onSuccess(String userId) {
                                    if (userId != null) {
                                        iniciarActividadPlataformaFamiliar(userId);
                                    } else {
                                        tvCodigo.setText("El código no coincide con ningún usuario");
                                    }
                                }
                            },
                            new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    tvCodigo.setText("Ha habido un fallo. Intenta nuevamente.");
                                    Log.e("FamilyLoginActivity", "Error al obtener el userId", e);
                                }
                            }
                    );
                }
            }
        });
    }



    private boolean validarFormatoCodigoAcceso(String codigoAccesoString) {
        if (codigoAccesoString.isEmpty()) {
            tvCodigo.setText("Introduce un código de acceso");
        } else if (!codigoAccesoString.matches("\\d+")) {
            // Si contiene alguna letra, indicar que solo puede contener dígitos
            tvCodigo.setText("El código solo puede contener dígitos");
        } else if (codigoAccesoString.length() != 10) {
            // Si no tiene 10 dígitos, indicar que debe contener 10 dígitos
            tvCodigo.setText("El código debe contener 10 dígitos");
        } else {
            tvCodigo.setText(null);
            return true;
        }
        return false;
    }

    private void iniciarActividadPlataformaFamiliar(String userID) {
        Intent intent = new Intent(FamilyLoginActivity.this, PlataformaFamiliarActivity.class);
        intent.putExtra("userID", userID); // Añadir userID como extra al Intent
        startActivity(intent);
    }


}
