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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DiaFragment2 extends Fragment {

    public DiaFragment2() {
        // Constructor público vacío requerido
    }
    HalfDonutChart halfDonutChartAyer;
    TextView txtNumeroPuntuacionDescansoAyer, txtTituloDescansoAyer, txtTiempoSueñoHorasAyer, txtTemperaturaMediaNocheGradosAyer, txtRespiracionAyer;
    FirebaseHelper firebaseHelper = new FirebaseHelper();
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String userID;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el diseño del fragmento que deseas mostrar
        View view = inflater.inflate(R.layout.fragment_dia_2, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();
        txtNumeroPuntuacionDescansoAyer = view.findViewById(R.id.txtNumeroPuntuacionDescansoAyer);
        txtTituloDescansoAyer = view.findViewById(R.id.txtTituloDescansoAyer);
        txtTiempoSueñoHorasAyer = view.findViewById(R.id.txtTiempoSueñoHorasAyer);
        txtTemperaturaMediaNocheGradosAyer = view.findViewById(R.id.txtTemperaturaMediaNocheGradosAyer);
        txtRespiracionAyer = view.findViewById(R.id.txtRespiracionAyer);
        halfDonutChartAyer = view.findViewById(R.id.halfDonutChartAyer);

        firebaseHelper.getYesterdayNight(userID, new OnSuccessListener<Night>() {
            @Override
            public void onSuccess(Night night) {
                if (night != null) {
                    Log.d("FirebaseHelper", "Fecha YESTERDAY NIGHT: "+ night.getDate().toString() + " Puntuación: " + night.getScore());
                    //show score points:
                    txtNumeroPuntuacionDescansoAyer.setText(String.valueOf(night.getScore()));

                    //show date in title:
                    Date nightsDate = night.getDate();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String dateString = dateFormat.format(nightsDate);
                    txtTituloDescansoAyer.setText(txtTituloDescansoAyer.getText() + " " + dateString);

                    //show rest time:
                    txtTiempoSueñoHorasAyer.setText(String.valueOf(night.getTime()) + " h");

                    //show temperature:
                    txtTemperaturaMediaNocheGradosAyer.setText(String.valueOf(night.getTemperature()) + " ºC");

                    //show breathing:
                    txtRespiracionAyer.setText(night.getBreathing());
                } else {
                    Log.d("FirebaseHelper", "No se encontró información para YESTERDAY NIGHT.");
                    txtTituloDescansoAyer.setText("No hay datos de sueño");
                    halfDonutChartAyer.setVisibility(View.INVISIBLE);
                    txtNumeroPuntuacionDescansoAyer.setText("-");
                    txtTiempoSueñoHorasAyer.setText("-");
                    txtTemperaturaMediaNocheGradosAyer.setText("-");
                    txtRespiracionAyer.setText("-");
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("FirebaseHelper", "Ha habido un error con getYesterdayNight ----" + e);

            }
        });

        // Aquí puedes inicializar las vistas y realizar otras operaciones necesarias
        return view;
    }
}
