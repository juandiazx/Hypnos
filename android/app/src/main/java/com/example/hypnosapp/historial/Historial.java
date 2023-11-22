package com.example.hypnosapp.historial;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


import com.example.hypnosapp.firebase.FirebaseHelper;
import com.example.hypnosapp.model.Night;
import com.example.hypnosapp.other.MenuManager;
import com.example.hypnosapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Historial extends AppCompatActivity {
    private List<DiaModel> listaDias;
    private static final String TAG = "AjustesDeSuenyo";
    private FirebaseHelper firebaseHelper;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String userID;
    ImageView btnPerfilUsuario, btnPantallaPrincipal, btnAjustesDescanso, btnPreferencias;

    Button btnSearch,inputDateFrom, inputDateTo;

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
        //userID = firebaseUser.getUid();
        userID = "lr3SPEtJqt493dpfWoDd"; // this is the only user of the database at the time

        btnPerfilUsuario = findViewById(R.id.logoUsuarioHeader);
        btnPantallaPrincipal = findViewById(R.id.btnPantallaPrincipal);
        btnAjustesDescanso = findViewById(R.id.btnAjustesDescanso);
        btnPreferencias = findViewById(R.id.btnPreferencias);
        btnSearch = findViewById(R.id.btnSearch);
        inputDateFrom = findViewById(R.id.inputDateFrom);
        inputDateTo = findViewById(R.id.inputDateTo);


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

        listaDias = new ArrayList<>();
        listaDias.add(new DiaModel("05/11/2023", "88/100", "Muy buena", "24C", "8h 25min"));
        listaDias.add(new DiaModel("04/11/2023", "75/100", "Buena", "23C", "7h 45min"));
        listaDias.add(new DiaModel("03/11/2023", "90/100","Muy buena", "25C", "7h 55min"));
        // Encuentra el TabLayout y el ViewPager
        TabLayout tabLayout = findViewById(R.id.tabLayoutHistorial);
        ViewPager viewPager = findViewById(R.id.viewPagerHistorial);
        // Crea un adaptador para manejar los fragmentos
        TabsHistorial adapter = new TabsHistorial(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        // Conecta el TabLayout con el ViewPager
        tabLayout.setupWithViewPager(viewPager);
        TabLayout.Tab tab = tabLayout.getTabAt(0); // Selecciona la tab "Semana" por defecto
        if (tab != null) {
            tab.select();
        }

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

                firebaseHelper.searchNights(userID, initialDate, finalDate,
                        new OnSuccessListener<List<Night>>() {
                            @Override
                            public void onSuccess(List<Night> nightList) {
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
        });
    }//onCreate
    public List<DiaModel> getListaDias() {
        return listaDias;
    }

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



}

