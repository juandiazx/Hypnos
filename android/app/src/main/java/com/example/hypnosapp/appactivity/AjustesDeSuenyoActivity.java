package com.example.hypnosapp.appactivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hypnosapp.firebase.FirebaseHelper;
import com.example.hypnosapp.services.AlarmService;
import com.example.hypnosapp.services.MQTTHelper;
import com.example.hypnosapp.utils.MenuManager;
import com.example.hypnosapp.R;
import com.example.hypnosapp.utils.StringFormatting;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AjustesDeSuenyoActivity extends AppCompatActivity {
    private static final String TAG = "AjustesDeSuenyo";

    private static final int PICK_RINGTONE_REQUEST = 1;
    ImageView btnPerfilUsuario, btnPantallaPrincipal, btnAjustesDescanso, btnPreferencias;
    EditText toneLocationClock, wakeUpHourGoal, sleepTimeGoal, toneLocationClockText;
    Switch isGradualClock, isAutoClock, goalNotifications, warmLight, coldLight, autoLight;
    Button btnGuardarClock, btnGuardarGoals;
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

        //userID = "lr3SPEtJqt493dpfWoDd"; // this is the only user of the database at the time

        btnPerfilUsuario= findViewById(R.id.logoUsuarioHeader);
        isGradualClock = findViewById(R.id.switchGradualClock);
        isAutoClock = findViewById(R.id.switchAutoClock);
        goalNotifications = findViewById(R.id.switchNotifications);
        warmLight = findViewById(R.id.switchWarmLight);
        coldLight = findViewById(R.id.switchColdLight);
        autoLight = findViewById(R.id.switchAutoLight);
        toneLocationClock = findViewById(R.id.toneLocationClock);
        toneLocationClockText = findViewById(R.id.toneLocationClockText);
        toneLocationClockText.setEnabled(false);
        toneLocationClockText.setFocusable(false);
        toneLocationClockText.setClickable(false);
        wakeUpHourGoal = findViewById(R.id.wakeUpHourGoal);
        sleepTimeGoal = findViewById(R.id.sleepTimeGoal);
        btnGuardarGoals = findViewById(R.id.guardarGoals);
        btnGuardarClock = findViewById(R.id.guardarClock);
        btnPantallaPrincipal = findViewById(R.id.btnPantallaPrincipal);
        btnAjustesDescanso = findViewById(R.id.btnAjustesDescanso);
        btnPreferencias = findViewById(R.id.btnPreferencias);

        loadSleepSettings();
        setSwitchListeners();

        btnGuardarClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = userID;
                String songLocation = toneLocationClock.getText().toString();
                boolean isSongGradual = isGradualClock.isChecked();
                boolean isClockAutomatic = isAutoClock.isChecked();
                firebaseHelper.setClock(userId, songLocation, isSongGradual, isClockAutomatic);
            }
        });
        btnGuardarGoals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedWakeUpHour = wakeUpHourGoal.getText().toString();
                String selectedIdelRestTime = sleepTimeGoal.getText().toString();

                firebaseHelper.setIdealWakeUpHour(userID, selectedWakeUpHour);
                firebaseHelper.setIdealRestTime(userID, selectedIdelRestTime);

            }
        });
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

        //------------------------------------------------------
        //BOTON SELECCIONAR TONO DESPERTADOR
        //------------------------------------------------------

        Button selectAlarmButton = findViewById(R.id.selectAlarmButton);
        selectAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniciarSelectorTonos();
            }
        });

    }//onCreate

    private void iniciarSelectorTonos(){
        // Abre el selector de tonos
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
        startActivityForResult(intent, PICK_RINGTONE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_RINGTONE_REQUEST && resultCode == RESULT_OK) {

            Uri selectedRingtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            setUrlsText(selectedRingtoneUri);

            //Esto es solo por ahora, tendra que pasar a llamarse desde MQTT Helper
            startAlarmService(selectedRingtoneUri);
        }
    }


    private void setUrlsText(Uri selectedRingtoneUri){
        String urlString = selectedRingtoneUri.toString();
        String titleString = StringFormatting.extractTitle(urlString);
        toneLocationClock.setText(urlString);
        toneLocationClockText.setText(titleString);
    }

    private void startAlarmService(Uri selectedRingtoneUri){
        Intent serviceIntent = new Intent(AjustesDeSuenyoActivity.this, AlarmService.class);
        serviceIntent.setData(selectedRingtoneUri);
        //Como argumento debeN de entrar DOS BOOLEANOS, por si tiene que vibrar
        //y ser gradual para pasarselo en esta intencion
        startService(serviceIntent);
    }

    private void loadSleepSettings() {

        String userId = userID;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

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

        firebaseHelper.getIdealWakeUpHour(userId,
                new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String idealWakeUpHour) {
                        wakeUpHourGoal.setText(idealWakeUpHour);
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText( AjustesDeSuenyoActivity.this,
                                "We couldn't obtain your ideal wake up hour", Toast.LENGTH_SHORT).show();
                    }
                });

        firebaseHelper.getIdealRestTime(userId,
                new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String idealRestTime) {
                        sleepTimeGoal.setText(idealRestTime);
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText( AjustesDeSuenyoActivity.this,
                                "We couldn't obtain your ideal rest time", Toast.LENGTH_SHORT).show();
                    }
                });

        firebaseHelper.getNotifications(userId,
                new OnSuccessListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean notificationSetting) {
                        if(notificationSetting){
                            goalNotifications.setChecked(true);
                        } else{
                            goalNotifications.setChecked(false);
                        }
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText( AjustesDeSuenyoActivity.this,
                                "We couldn't obtain your sleep notifications settings", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void updateClockSettingsUI(Map<String, Object> clockSettings) {
        if (clockSettings != null) {
            Boolean isGradual = (Boolean) clockSettings.get("isGradual");
            Boolean isWithVibrations = (Boolean) clockSettings.get("isWithVibrations");

            if (isGradual != null) {
                isGradualClock.setChecked(isGradual);
            } else {
                Log.e(TAG, "isGradual is null");
            }

            if (isWithVibrations != null) {
                isAutoClock.setChecked(isWithVibrations);
            } else {
                Log.e(TAG, "isWithVibrations is null");
            }

            String toneLocation = (String) clockSettings.get("toneLocation");
            if (toneLocation != null) {
                toneLocationClock.setText(toneLocation);
                toneLocationClockText.setText(StringFormatting.extractTitle(toneLocation));
            } else {
                Log.e(TAG, "toneLocation is null");
            }
        } else {
            Log.e(TAG, "clockSettings is null");
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

        goalNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if(isChecked){
                    firebaseHelper.setNotifications(userID, true);
                } else{
                    firebaseHelper.setNotifications(userID, false);
                }
            }
        });
    }



}
