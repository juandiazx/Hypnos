package com.example.hypnosapp.appactivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hypnosapp.firebase.FirebaseHelper;
import com.example.hypnosapp.mainpage.ECGActivity;
import com.example.hypnosapp.mainpage.Pantalla_Principal;
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

import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AjustesDeSuenyoActivity extends AppCompatActivity {
    private static final String TAG = "AjustesDeSuenyo";

    MQTTHelper mqttHelper;

    private Context thisAppContext = this;

    private static final int PICK_RINGTONE_REQUEST = 1;
    ImageView btnPerfilUsuario, btnPantallaPrincipal, btnAjustesDescanso, btnPreferencias;
    EditText toneLocationClock, /*wakeUpHourGoal, sleepTimeGoal,*/ toneLocationClockText;
    Spinner wakeUpHourGoal, sleepTimeGoal;
    Switch isAutoClock, goalNotifications, warmLight, coldLight, autoLight;
    Button btnGuardarClock, btnGuardarGoals, botonVincularMQTT;
    private FirebaseHelper firebaseHelper;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String userID, selectedWakeUpHour, selectedIdelRestTime;

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
        isAutoClock = findViewById(R.id.switchAutoClock);
        goalNotifications = findViewById(R.id.switchNotifications);
        warmLight = findViewById(R.id.switchWarmLight);
        coldLight = findViewById(R.id.switchColdLight);
        //autoLight = findViewById(R.id.switchAutoLight);
        toneLocationClock = findViewById(R.id.toneLocationClock);
        toneLocationClockText = findViewById(R.id.toneLocationClockText);
        toneLocationClockText.setEnabled(false);
        toneLocationClockText.setFocusable(false);
        toneLocationClockText.setClickable(false);
        wakeUpHourGoal = findViewById(R.id.wakeUpHourGoal);
        sleepTimeGoal = findViewById(R.id.sleepTimeGoal);
        btnGuardarGoals = findViewById(R.id.guardarGoals);
        btnGuardarClock = findViewById(R.id.guardarClock);
        botonVincularMQTT = findViewById(R.id.vincularMQTT);
        btnPantallaPrincipal = findViewById(R.id.btnPantallaPrincipal);
        btnAjustesDescanso = findViewById(R.id.btnAjustesDescanso);
        btnPreferencias = findViewById(R.id.btnPreferencias);

        loadSleepSettings();
        setSwitchListeners();
        llenarSpinners();
        escuchadorSpinners();

        btnGuardarClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = userID;
                String songLocation = toneLocationClock.getText().toString();
                boolean isClockAutomatic = isAutoClock.isChecked();
                firebaseHelper.setClock(userId, songLocation, isClockAutomatic);
            }
        });
        btnGuardarGoals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        ImageView btnAbrirActivityECG = findViewById(R.id.logoCardiacoHeader);
        btnAbrirActivityECG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent para abrir la actividad ECG
                Intent intent = new Intent(AjustesDeSuenyoActivity.this, ECGActivity.class);
                startActivity(intent);
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

        botonVincularMQTT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    mqttHelper = new MQTTHelper(thisAppContext, "tcp://test.mosquitto.org:1883","jdiagut","hypnos_rp_daytime");
                    mqttHelper.connect();
                    mqttHelper.publishToUidTopic();
                    mqttHelper.subscribeToTopic("hypnos_rp_daytime");
                } catch (MqttException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        ImageView btnMaps = findViewById(R.id.ButtonMaps);
        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirMaps(AjustesDeSuenyoActivity.this);
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
            boolean isVibrationEnabled = isAutoClock.isChecked();

            if(selectedRingtoneUri != null){
                setUrlsText(selectedRingtoneUri);
            }
        }
    }


    private void setUrlsText(Uri selectedRingtoneUri){
        String urlString = selectedRingtoneUri.toString();
        String titleString;
        if (urlString.length() < 45){
            titleString = StringFormatting.extractNumberTitle(urlString);
        }
        else{
            titleString = StringFormatting.extractTitle(urlString);
        }

        toneLocationClock.setText(urlString);
        toneLocationClockText.setText(titleString);
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
            Boolean isWithVibrations = (Boolean) clockSettings.get("isWithVibrations");

            if (isWithVibrations != null) {
                isAutoClock.setChecked(isWithVibrations);
            } else {
                Log.e(TAG, "isWithVibrations is null");
            }

            String toneLocation = (String) clockSettings.get("toneLocation");
            if (toneLocation != null) {
                toneLocationClock.setText(toneLocation);
                if (toneLocation.length() < 45 && toneLocation.length() > 10){
                    toneLocationClockText.setText(StringFormatting.extractNumberTitle(toneLocation));
                }
                else{
                    toneLocationClockText.setText(StringFormatting.extractTitle(toneLocation));
                }

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
            //autoLight.setChecked(false);
        } else if ("WAR".equals(lightSettings)) {
            warmLight.setChecked(true);
            coldLight.setChecked(false);
            //autoLight.setChecked(false);
        }
//        } else {
//            //autoLight.setChecked(true);
//            warmLight.setChecked(false);
//            coldLight.setChecked(false);
//        }
    }
    private void setSwitchListeners() {
        warmLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Warm light activated, deactivate others
                    coldLight.setChecked(false);
                    //autoLight.setChecked(false);

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
                    //autoLight.setChecked(false);

                    // Update setting in the database
                    firebaseHelper.setLightCold(userID);
                }
            }
        });

//        autoLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    // Auto light activated, deactivate others
//                    warmLight.setChecked(false);
//                    coldLight.setChecked(false);
//
//                    // Update setting in the database
//                    firebaseHelper.setLightAuto(userID);
//                }
//            }
//        });

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

    private void llenarSpinners(){

        String userId = userID;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String[] hours = {"00:00", "00:15", "00:30", "00:45",
                "01:00", "01:15", "01:30", "01:45",
                "02:00","02:15", "02:30", "02:45",
                "03:00","03:15", "03:30", "03:45",
                "04:00","04:15", "04:30", "04:45",
                "05:00","05:15", "05:30", "05:45",
                "06:00","06:15", "06:30", "06:45",
                "07:00","07:15", "07:30", "07:45",
                "08:00","08:15", "08:30", "08:45",
                "09:00","09:15", "09:30", "09:45",
                "10:00","10:15", "10:30", "10:45",
                "11:00","11:15", "11:30", "11:45",
                "12:00","12:15", "12:30", "12:45",
                "13:00","13:15", "13:30", "13:45",
                "14:00","14:15", "14:30", "14:45",
                "15:00","15:15", "15:30", "15:45",
                "16:00","16:15", "16:30", "16:45",
                "17:00","17:15", "17:30", "17:45",
                "18:00","18:15", "18:30", "18:45",
                "19:00","19:15", "19:30", "19:45",
                "20:00","20:15", "20:30", "20:45",
                "21:00","21:15", "21:30", "21:45",
                "22:00","22:15", "22:30", "22:45",
                "23:00","23:15", "23:30", "23:45"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hours);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        wakeUpHourGoal.setAdapter(adapter);
        sleepTimeGoal.setAdapter(adapter);

        firebaseHelper.getIdealWakeUpHour(userId,
                new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String idealWakeUpHour) {

                        int selectionIndex = -1;
                        for (int i = 0; i < hours.length; i++) {
                            if (hours[i].equals(idealWakeUpHour)) {
                                selectionIndex = i;
                                break;
                            }
                        }
                        if (selectionIndex != -1) {
                            wakeUpHourGoal.setSelection(selectionIndex);
                        }
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
                        int selectionIndex = -1;
                        for (int i = 0; i < hours.length; i++) {
                            if (hours[i].equals(idealRestTime)) {
                                selectionIndex = i;
                                break;
                            }
                        }
                        if (selectionIndex != -1) {
                            sleepTimeGoal.setSelection(selectionIndex);
                        }
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText( AjustesDeSuenyoActivity.this,
                                "We couldn't obtain your ideal rest time", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void escuchadorSpinners(){
        wakeUpHourGoal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Actualizar la variable selectedHour cuando el usuario selecciona un nuevo ítem
                selectedWakeUpHour = (String) parentView.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        sleepTimeGoal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Actualizar la variable selectedHour cuando el usuario selecciona un nuevo ítem
                selectedIdelRestTime = (String) parentView.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

    }
}
