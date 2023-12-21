package com.example.hypnosapp.mainpage;

import android.content.Intent;
import android.graphics.Color;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.hypnosapp.firebase.FirebaseHelper;
import com.example.hypnosapp.services.MQTTHelper;

import com.example.hypnosapp.utils.MenuManager;
import com.example.hypnosapp.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import org.eclipse.paho.client.mqttv3.MqttException;

public class Pantalla_Principal extends AppCompatActivity {

    private BandaCardiacaManager bandaCardiacaManager;

    private static final String TAG = "Gráficas";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Crear una instancia de BandaCardiacaManager y pasar el contexto
        bandaCardiacaManager = new BandaCardiacaManager(this);

        // Encuentra el TabLayout y el ViewPager
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);

        // Crea un adaptador para manejar los fragmentos
        TabsPaginaPrincipal adapter = new TabsPaginaPrincipal(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        // Conecta el TabLayout con el ViewPager
        tabLayout.setupWithViewPager(viewPager);

        TabLayout.Tab tab = tabLayout.getTabAt(2); // Selecciona la tab "Hoy" por defecto
        if (tab != null) {
            tab.select();
        }

        LineChart lineChart = findViewById(R.id.imagenGraficaSueñoDiario);

        // Configura la gráfica
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection("users").document("R279SubMuPfIJf608GXGWbFOoTC2").collection("nightsData");

        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Entry> scores = new ArrayList<>();
                    ArrayList<String> daysOfWeek = new ArrayList<>(); // To store the days of the week

                    // Parse and store the data
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Check if 'date' field exists and is of type Timestamp
                        if (document.contains("date") && document.get("date") instanceof Timestamp) {
                            Timestamp timestamp = (Timestamp) document.get("date");
                            String date = formatDate(timestamp.toDate()); // Convert timestamp to Date and then to formatted string
                            String dayOfWeek = getDayOfWeekFromDate(date);
                            Float score = document.getDouble("score").floatValue();
                            scores.add(new Entry(getTimestampFromDate(date), score != null ? score : 0f));
                            daysOfWeek.add(dayOfWeek);
                        } else {
                            // Log a warning or handle the case where 'date' is not a Timestamp
                            Log.w(TAG, "Invalid 'date' field in Firestore document");
                        }
                    }

                    // Sort the entries based on the timestamp
                    Collections.sort(scores, new Comparator<Entry>() {
                        @Override
                        public int compare(Entry entry1, Entry entry2) {
                            return Float.compare(entry1.getX(), entry2.getX());
                        }
                    });

// Create and configure the DataSet
                    LineDataSet set = new LineDataSet(scores, "Puntuación de sueño");
                    set.setFillAlpha(110);
                    set.setColor(Color.parseColor("#164499"));
                    set.setLineWidth(5f);
                    set.setValueTextSize(15f);

// Add the DataSet to the chart
                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(set);
                    LineData data = new LineData(dataSets);
                    lineChart.setData(data);

// Configure the appearance of the x-axis
                    XAxis xAxis = lineChart.getXAxis();
                    xAxis.setGranularity(1f);
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setLabelRotationAngle(0f);

// Manually set X-axis labels using timestamps
                    xAxis.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getAxisLabel(float value, AxisBase axis) {
                            long timestamp = (long) value;
                            String date = formatDate(new Date(timestamp));
                            return getDayOfWeekFromDate(date);
                        }
                    });

// Set the number of labels to match the number of data points
                    xAxis.setLabelCount(scores.size(), true);

// Hide Y-axis labels and grid lines
                    YAxis leftYAxis = lineChart.getAxisLeft();
                    leftYAxis.setDrawLabels(false);
                    leftYAxis.setDrawGridLines(false);

                    YAxis rightYAxis = lineChart.getAxisRight();
                    rightYAxis.setDrawLabels(false);
                    rightYAxis.setDrawGridLines(false);

// Hide X-axis grid lines
                    xAxis.setDrawGridLines(false);

// Invalidate the chart to refresh the appearance
                    lineChart.invalidate();

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });


        // FUNCIONALIDAD BOTONES MENUS
        MenuManager funcionMenu = new MenuManager();

        ImageView btnPerfilUsuario = findViewById(R.id.logoUsuarioHeader);
        btnPerfilUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPerfilUsuario(Pantalla_Principal.this);
            }
        });

        ImageView btnPantallaPrincipal = findViewById(R.id.btnPantallaPrincipal);
        btnPantallaPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPantallaPrincipal(Pantalla_Principal.this);
            }
        });

        ImageView btnAjustesDescanso = findViewById(R.id.btnAjustesDescanso);
        btnAjustesDescanso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirAjustesDescanso(Pantalla_Principal.this);
            }
        });

        ImageView btnPreferencias = findViewById(R.id.btnPreferencias);
        btnPreferencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirAcercaDe(Pantalla_Principal.this);
            }
        });

        FloatingActionButton btnHistorial = findViewById(R.id.floatingActiveButtonCalendarioSemanal);
        btnHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirHistorial(Pantalla_Principal.this);


            }
        });

        FloatingActionButton btnMaps = findViewById(R.id.floatingActiveButtonMaps);
        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirMaps(Pantalla_Principal.this);
            }
        });





        ImageView btnAbrirActivityECG = findViewById(R.id.logoCardiacoHeader);
        btnAbrirActivityECG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent para abrir la actividad ECG
                Intent intent = new Intent(Pantalla_Principal.this, ECGActivity.class);
                startActivity(intent);
            }
        });

    }

    // Function to get the day of the week from the date string
    private String getDayOfWeekFromDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy, h:mm:ss a z", Locale.getDefault());
        try {
            Date parsedDate = format.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parsedDate);

            // Use a different format for the day of the week
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            return dayFormat.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


    // Function to get a timestamp from the date string
    private float getTimestampFromDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy, h:mm:ss a z", Locale.getDefault());
        try {
            Date parsedDate = format.parse(date);
            return parsedDate.getTime(); // Convert to timestamp in milliseconds
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0f;
    }

    // Function to format Date to a string
    private String formatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy, h:mm:ss a z", Locale.getDefault());
        return format.format(date);
    }

}
