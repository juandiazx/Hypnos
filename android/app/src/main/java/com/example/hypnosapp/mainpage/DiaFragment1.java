package com.example.hypnosapp.mainpage;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.hypnosapp.R;
import com.example.hypnosapp.firebase.FirebaseHelper;
import com.example.hypnosapp.model.Night;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class DiaFragment1 extends Fragment {
    public DiaFragment1() {
        // Constructor público vacío requerido
    }
    HalfDonutChart halfDonutChartAnteAyer;
    TextView txtNumeroPuntuacionDescansoAnteAyer, txtTituloDescansoAnteAyer, txtTiempoSueñoHorasAnteAyer, txtTemperaturaMediaNocheGradosAnteAyer, txtRespiracionAnteAyer;
    FirebaseHelper firebaseHelper = new FirebaseHelper();
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String userID;
    private LineChart graph;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el diseño del fragmento que deseas mostrar
        View view = inflater.inflate(R.layout.fragment_dia_1, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();
        txtNumeroPuntuacionDescansoAnteAyer = view.findViewById(R.id.txtNumeroPuntuacionDescansoAnteAyer);
        txtTituloDescansoAnteAyer = view.findViewById(R.id.txtTituloDescansoAnteAyer);
        txtTiempoSueñoHorasAnteAyer = view.findViewById(R.id.txtTiempoSueñoHorasAnteAyer);
        txtTemperaturaMediaNocheGradosAnteAyer = view.findViewById(R.id.txtTemperaturaMediaNocheGradosAnteAyer);
        txtRespiracionAnteAyer = view.findViewById(R.id.txtRespiracionAnteAyer);
        halfDonutChartAnteAyer = view.findViewById(R.id.halfDonutChartAnteAyer);
        //grafica
        graph = view.findViewById(R.id.imagenGraficaSueñoDiario);
        // Configura la gráfica
        firebaseHelper.graphicConfig(userID, graph);

        firebaseHelper.getThirdLastNight(userID, new OnSuccessListener<Night>() {
            @Override
            public void onSuccess(Night night) {
                if (night != null) {
                    Log.d("FirebaseHelper", "Fecha BEFORE YESTERDAY NIGHT: " + night.getDate().toString() + " Puntuación: " + night.getScore());
                    //show score points:
                    txtNumeroPuntuacionDescansoAnteAyer.setText(String.valueOf(night.getScore()));

                    //show date in title:
                    Date nightsDate = night.getDate();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String dateString = dateFormat.format(nightsDate);
                    txtTituloDescansoAnteAyer.setText(txtTituloDescansoAnteAyer.getText() + " " + dateString);

                    //show rest time:
                    txtTiempoSueñoHorasAnteAyer.setText(String.valueOf(night.getTime()) + " h");

                    //show temperature:
                    txtTemperaturaMediaNocheGradosAnteAyer.setText(String.valueOf(night.getTemperature()) + " ºC");

                    //show breathing:
                    txtRespiracionAnteAyer.setText(night.getBreathing());

                    // Set the score percentage to the HalfDonutChart
                    float scorePercentage = (float) night.getScore() / 100; // Assuming the score is on a scale of 0 to 100
                    halfDonutChartAnteAyer.setScorePercentage(scorePercentage);
                } else {
                    Log.d("FirebaseHelper", "No se encontró información para BEFORE YESTERDAY NIGHT.");
                    txtTituloDescansoAnteAyer.setText("No hay datos de sueño");
                    halfDonutChartAnteAyer.setVisibility(View.INVISIBLE);
                    txtNumeroPuntuacionDescansoAnteAyer.setText("-");
                    txtTiempoSueñoHorasAnteAyer.setText("-");
                    txtTemperaturaMediaNocheGradosAnteAyer.setText("-");
                    txtRespiracionAnteAyer.setText("-");
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("FirebaseHelper", "Ha habido un error con getBeforeYesterdayNight ----" + e);
            }
        });

        // Aquí puedes inicializar las vistas y realizar otras operaciones necesarias
        return view;
    }
}