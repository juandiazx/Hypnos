package com.example.hypnosapp.historial;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


import com.example.hypnosapp.appactivity.AjustesDeSuenyoActivity;
import com.example.hypnosapp.firebase.FirebaseHelper;
import com.example.hypnosapp.mainpage.ECGActivity;
import com.example.hypnosapp.model.Night;
import com.example.hypnosapp.utils.MenuManager;
import com.example.hypnosapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class Historial extends AppCompatActivity {
    private static final String TAG = "AjustesDeSuenyo";
    private FirebaseHelper firebaseHelper;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String userID;
    ImageView btnPerfilUsuario, btnPantallaPrincipal, btnAjustesDescanso, btnPreferencias;
    TextView lblErrorDates;
    Button btnSearch,inputDateFrom, inputDateTo, btnExportar;
    List<Night> listaNoches;
    private RecyclerView recyclerView;
    public AdaptadorNoches adaptadorNoches;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historial);

        //Menu buttons functionalities
        MenuManager funcionMenu = new MenuManager();

        //Instance of the database and the user
        firebaseHelper = new FirebaseHelper();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();

        btnPerfilUsuario = findViewById(R.id.logoUsuarioHeader);
        btnPantallaPrincipal = findViewById(R.id.btnPantallaPrincipal);
        btnAjustesDescanso = findViewById(R.id.btnAjustesDescanso);
        btnPreferencias = findViewById(R.id.btnPreferencias);
        btnSearch = findViewById(R.id.btnSearch);
        inputDateFrom = findViewById(R.id.inputDateFrom);
        inputDateTo = findViewById(R.id.inputDateTo);
        lblErrorDates = findViewById(R.id.lblErrorDates);
        //btnExportar = findViewById(R.id.btnExportar);


        btnPerfilUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPerfilUsuario(Historial.this);
            }
        });
        btnPantallaPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPantallaPrincipal(Historial.this);
            }
        });
        btnAjustesDescanso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirAjustesDescanso(Historial.this);
            }
        });
        btnPreferencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirAcercaDe(Historial.this);
            }
        });

        ImageView btnAbrirActivityECG = findViewById(R.id.logoCardiacoHeader);
        btnAbrirActivityECG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent para abrir la actividad ECG
                Intent intent = new Intent(Historial.this, ECGActivity.class);
                startActivity(intent);
            }
        });

        inputDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePickerDialogFrom();
            }
        });
        inputDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePickerDialogTo();
            }
        });

        recyclerView = findViewById(R.id.recyclerViewHistorial);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaNoches = new ArrayList<>();
//        listaNoches.add(new Night(new Date(), "breathing", 90, 25, 8));
//        listaNoches.add(new Night(new Date(), "breathing", 76, 15, 5));
//        listaNoches.add(new Night(new Date(), "breathing", 40, 35, 4));
        adaptadorNoches = new AdaptadorNoches(this, listaNoches);
        recyclerView.setAdapter(adaptadorNoches);

        firebaseHelper.getPagesFromAllNights(userID,
                new OnSuccessListener<Integer>() {
                    @Override
                    public void onSuccess(Integer pages) {
                        Log.e(TAG, "pages:" + pages);
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Error getting pages");
                    }
                });

        firebaseHelper.getFifteenNights(userID, 1,
                new OnSuccessListener<List<Night>>() {
                    @Override
                    public void onSuccess(List<Night> nights) {
                        listaNoches.clear();
                        listaNoches.addAll(nights);
                        adaptadorNoches.notifyDataSetChanged();

                        for (Night night : nights) {
                            Log.d(TAG, "Night: " + night.getDate() + ", Score: " + night.getScore());
                        }
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Error getting nights", e);
                    }
                });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String initialDate = inputDateFrom.getText().toString();
                String finalDate = inputDateTo.getText().toString();

                if(!areDatesCorrect(initialDate, finalDate)){
                    lblErrorDates.setVisibility(View.VISIBLE);
                }else{
                    lblErrorDates.setVisibility(View.GONE);
                    firebaseHelper.searchNights(userID, initialDate, finalDate,
                            new OnSuccessListener<List<Night>>() {
                                @Override
                                public void onSuccess(List<Night> nightList) {
                                    listaNoches.clear();
                                    listaNoches.addAll(nightList);
                                    adaptadorNoches.notifyDataSetChanged();

                                    for (Night night : nightList) {
                                        Log.d(TAG, "Night: " + night.getDate() + ", Score: " + night.getScore());
                                    }
                                }
                            },
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "Error getting nights", e);
                                }
                            });
                }
            }
        });


    }//onCreate

    private void mostrarDatePickerDialogFrom() {

        Calendar calendar = Calendar.getInstance(); // Obtiene una instancia del calendario con la fecha y hora actuales
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String fechaSeleccionada = dayOfMonth + "/" + (month + 1) + "/" + year;
                inputDateFrom.setText(fechaSeleccionada);
            }
        }, year, month, dayOfMonth); // Establece la fecha inicial como la fecha actual

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()); // Opcional: establece una fecha máxima (hasta la fecha actual)
        datePickerDialog.show();
    }

    private void mostrarDatePickerDialogTo() {

        Calendar calendar = Calendar.getInstance(); // Obtiene una instancia del calendario con la fecha y hora actuales
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String fechaSeleccionada = dayOfMonth + "/" + (month + 1) + "/" + year;
                inputDateTo.setText(fechaSeleccionada);
            }
        }, year, month, dayOfMonth); // Establece la fecha inicial como la fecha actual

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()); // Opcional: establece una fecha máxima (hasta la fecha actual)
        datePickerDialog.show();
    }

    private boolean areDatesCorrect(String fromDate, String toDate){

        //in order to transform Strings into dates:
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date fromDateTransformed;
        Date toDateTransformed;

        try {
            fromDateTransformed = sdf.parse(fromDate);
            toDateTransformed = sdf.parse(toDate);

            if (fromDateTransformed.after(toDateTransformed)) {
                Log.e(TAG,"La fecha de inicio es posterior a la fecha de fin.");
                return false;
            } else if (fromDateTransformed.before(toDateTransformed)) {
                Log.d(TAG,"La fecha de inicio es anterior a la fecha de fin.");
                return true;
            } else {
                Log.d(TAG,"Las fechas son iguales.");
                return true;
            }

        } catch (ParseException e) {
                e.printStackTrace();
                Log.e(TAG, "Error in converting String dates to Date dates");
                return false;
            }
    }

}//class




