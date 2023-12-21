package com.example.hypnosapp.mainpage;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DiaFragment3 extends Fragment {

    String baseTitleForNight;

    public DiaFragment3() {
        // Constructor público vacío requerido
    }

    HalfDonutChart halfDonutChartAnoche;
    TextView txtRestScoreLastNight, txtTituloDescansoAnoche, txtTiempoSueñoHorasAnoche, txtTemperaturaMediaNocheGradosAnoche, txtRespiracionAnoche;
    FirebaseHelper firebaseHelper = new FirebaseHelper();
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String userID;
    private ListenerRegistration nightDataListener;
    private LineChart graph;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el diseño del fragmento que deseas mostrar
        View view = inflater.inflate(R.layout.fragment_dia_3, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();
        txtRestScoreLastNight = view.findViewById(R.id.txtNumeroPuntuacionDescansoAnoche);
        txtTituloDescansoAnoche = view.findViewById(R.id.txtTituloDescansoAnoche);
        txtTiempoSueñoHorasAnoche = view.findViewById(R.id.txtTiempoSueñoHorasAnoche);
        txtTemperaturaMediaNocheGradosAnoche = view.findViewById(R.id.txtTemperaturaMediaNocheGradosAnoche);
        txtRespiracionAnoche = view.findViewById(R.id.txtRespiracionAnoche);
        halfDonutChartAnoche = view.findViewById(R.id.halfDonutChartAnoche);
        //grafica
        graph = view.findViewById(R.id.imagenGraficaSueñoDiario);
        // Configura la gráfica
        firebaseHelper.graphicConfig(userID, graph);

        firebaseHelper.getLastNightWithListener(userID, new OnSuccessListener<Night>() {
            @Override
            public void onSuccess(Night night) {
                if (night != null) {
                    updateNightsUI(night);
                } else {
                    Log.d("FirebaseHelper", "No se encontró información para LAST NIGHT.");
                    txtTituloDescansoAnoche.setText("No hay datos de sueño");
                    halfDonutChartAnoche.setVisibility(View.INVISIBLE);
                    txtRestScoreLastNight.setText("-");
                    txtTiempoSueñoHorasAnoche.setText("-");
                    txtTemperaturaMediaNocheGradosAnoche.setText("-");
                    txtRespiracionAnoche.setText("-");
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("FirebaseHelper", "Ha habido un error con getLastNight! -----" + e);
            }
        }, new NightDataChangeListener() {
            @Override
            public void onDataChange(Night night) {
                if (night != null) {
                    updateNightsUI(night);
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (nightDataListener != null) {
            nightDataListener.remove();
        }
    }

    private void updateNightsUI(Night night) {
        Log.d("FirebaseHelper", "Fecha LAST NIGHT: "+ night.getDate().toString() + " Puntuación: " + night.getScore());
        //show score points:
        txtRestScoreLastNight.setText(String.valueOf(night.getScore()));

        baseTitleForNight = "Puntuación descanso";

        //show date in title:
        Date nightsDate = night.getDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = dateFormat.format(nightsDate);
        txtTituloDescansoAnoche.setText(baseTitleForNight + " " + dateString);

        //show rest time:
        txtTiempoSueñoHorasAnoche.setText(String.valueOf(night.getTime()) + " h");

        //show temperature:
        txtTemperaturaMediaNocheGradosAnoche.setText(String.valueOf(night.getTemperature()) + " ºC");

        //show breathing:
        txtRespiracionAnoche.setText(night.getBreathing());

        // Set the score percentage to the HalfDonutChart
        float scorePercentage = (float) night.getScore() / 100; // Assuming the score is on a scale of 0 to 100
        halfDonutChartAnoche.setScorePercentage(scorePercentage);
    }

    public interface NightDataChangeListener {
        void onDataChange(Night night);
    }
}
