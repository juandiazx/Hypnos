package com.example.hypnosapp.appactivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.hypnosapp.firebase.FirebaseHelper;
import com.example.hypnosapp.other.MenuManager;
import com.example.hypnosapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

public class AjustesDeSuenyoActivity extends AppCompatActivity {
    ImageView btnPerfilUsuario, btnPantallaPrincipal, btnAjustesDescanso, btnPreferencias;
    EditText toneLocationClock, wakeUpHourClock, wakeUpHourGoal, sleepTimeGoal;
    Switch isGradualClock, isAutoClock, goalNotifications, warmLight, coldLight, autoLight;
    private FirebaseHelper firebaseHelper;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajustes_de_sueno);
        //Menu buttons functionalities
        MenuManager funcionMenu = new MenuManager();
        //Instance of the database and the user
        firebaseHelper = new FirebaseHelper();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();

        btnPerfilUsuario= findViewById(R.id.logoUsuarioHeader);
        isGradualClock = findViewById(R.id.switchGradualClock);
        isAutoClock = findViewById(R.id.switchAutoClock);
        goalNotifications = findViewById(R.id.switchNotifications);
        warmLight = findViewById(R.id.switchWarmLight);
        coldLight = findViewById(R.id.switchColdLight);
        autoLight = findViewById(R.id.switchAutoLight);
        toneLocationClock = findViewById(R.id.toneLocationClock);
        wakeUpHourClock = findViewById(R.id.wakeUpHourClock);
        wakeUpHourGoal = findViewById(R.id.wakeUpHourGoal);
        sleepTimeGoal = findViewById(R.id.sleepTimeGoal);
        btnPantallaPrincipal = findViewById(R.id.btnPantallaPrincipal);
        btnAjustesDescanso = findViewById(R.id.btnAjustesDescanso);
        btnPreferencias = findViewById(R.id.btnPreferencias);

        loadSleepSettings();
        setSwitchListeners();


        btnPerfilUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPerfilUsuario(AjustesDeSuenyoActivity.this);
            }
        });
        btnPantallaPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPantallaPrincipal(AjustesDeSuenyoActivity.this);
            }
        });
        btnAjustesDescanso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirAjustesDescanso(AjustesDeSuenyoActivity.this);
            }
        });
        btnPreferencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirAcercaDe(AjustesDeSuenyoActivity.this);
            }
        });
    }

    private void loadSleepSettings() {

        //String userId = userID;
        String userId = "lr3SPEtJqt493dpfWoDd"; // this is the only user of the database

        firebaseHelper.getClock(userId,
                new OnSuccessListener<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> clockSettings) {
                        // Update UI with clock settings
                        updateClockSettingsUI(clockSettings);
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText( AjustesDeSuenyoActivity.this,
                                "We couldn't obtain your alarm settings", Toast.LENGTH_SHORT).show();
                    }
                });

        firebaseHelper.getLightSettings(userId,
                new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String lightSettings) {
                        updateLightSettingsUI(lightSettings);
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // Handle failure here
                        Toast.makeText( AjustesDeSuenyoActivity.this,
                                "We couldn't obtain your light settings", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateClockSettingsUI(Map<String, Object> clockSettings) {
        if (clockSettings != null) {
            isGradualClock.setChecked((Boolean) clockSettings.get("isGradual"));
            isAutoClock.setChecked((Boolean) clockSettings.get("isAutomatic"));
            toneLocationClock.setText((String) clockSettings.get("toneLocation"));
        }
    }

    private void updateLightSettingsUI(String lightSettings) {
        if ("COL".equals(lightSettings)) {
            coldLight.setChecked(true);
            warmLight.setChecked(false);
            autoLight.setChecked(false);
        } else if ("WAR".equals(lightSettings)) {
            warmLight.setChecked(true);
            coldLight.setChecked(false);
            autoLight.setChecked(false);
        } else {
            autoLight.setChecked(true);
            warmLight.setChecked(false);
            coldLight.setChecked(false);
        }
    }

    private void setSwitchListeners() {
        warmLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Warm light activated, deactivate others
                    coldLight.setChecked(false);
                    autoLight.setChecked(false);

                    // Update setting in the database
                    firebaseHelper.setLightWarm(userID);
                }
            }
        });

        coldLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Cold light activated, deactivate others
                    warmLight.setChecked(false);
                    autoLight.setChecked(false);

                    // Update setting in the database
                    firebaseHelper.setLightCold(userID);
                }
            }
        });

        autoLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Auto light activated, deactivate others
                    warmLight.setChecked(false);
                    coldLight.setChecked(false);

                    // Update setting in the database
                    firebaseHelper.setLightAuto(userID);
                }
            }
        });
    }
}
