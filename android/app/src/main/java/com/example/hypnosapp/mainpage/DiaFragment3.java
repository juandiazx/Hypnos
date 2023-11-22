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

import java.text.SimpleDateFormat;
import java.util.Date;

public class DiaFragment3 extends Fragment {

    public DiaFragment3() {
        // Constructor público vacío requerido
    }

    TextView txtRestScoreLastNight, txtTituloDescansoAnoche, txtTiempoSueñoHorasAnoche, txtTemperaturaMediaNocheGradosAnoche, txtRespiracionAnoche;
    FirebaseHelper firebaseHelper = new FirebaseHelper();
    String userID = "lr3SPEtJqt493dpfWoDd";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el diseño del fragmento que deseas mostrar
        View view = inflater.inflate(R.layout.fragment_dia_3, container, false);

        txtRestScoreLastNight = view.findViewById(R.id.txtNumeroPuntuacionDescansoAnoche);
        txtTituloDescansoAnoche = view.findViewById(R.id.txtTituloDescansoAnoche);
        txtTiempoSueñoHorasAnoche = view.findViewById(R.id.txtTiempoSueñoHorasAnoche);
        txtTemperaturaMediaNocheGradosAnoche = view.findViewById(R.id.txtTemperaturaMediaNocheGradosAnoche);
        txtRespiracionAnoche = view.findViewById(R.id.txtRespiracionAnoche);

        firebaseHelper.getLastNight(userID, new OnSuccessListener<Night>() {
            @Override
            public void onSuccess(Night night) {
                if (night != null) {
                    Log.d("FirebaseHelper", "Fecha LAST NIGHT: "+ night.getDate().toString() + " Puntuación: " + night.getScore());
                    //show score points:
                    txtRestScoreLastNight.setText(String.valueOf(night.getScore()));

                    //show date in title:
                    Date nightsDate = night.getDate();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String dateString = dateFormat.format(nightsDate);
                    txtTituloDescansoAnoche.setText(txtTituloDescansoAnoche.getText() + " " + dateString);

                    //show rest time:
                    txtTiempoSueñoHorasAnoche.setText(String.valueOf(night.getTime()) + " h");

                    //show temperature:
                    txtTemperaturaMediaNocheGradosAnoche.setText(String.valueOf(night.getTemperature()) + " ºC");

                    //show breathing:
                    txtRespiracionAnoche.setText(night.getBreathing());
                } else {
                    Log.d("FirebaseHelper", "No se encontró información para LAST NIGHT.");
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("FirebaseHelper", "Ha habido un error con getLastNight! -----" + e);

            }
        });

        // Aquí puedes inicializar las vistas y realizar otras operaciones necesarias
        return view;
    }
}
