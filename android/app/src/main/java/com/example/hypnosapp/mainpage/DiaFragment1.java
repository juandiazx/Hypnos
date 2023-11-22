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

public class DiaFragment1 extends Fragment {

    public DiaFragment1() {
        // Constructor público vacío requerido
    }

    TextView txtRestScoreBeforeYesterday;
    FirebaseHelper firebaseHelper = new FirebaseHelper();
    String userID = "lr3SPEtJqt493dpfWoDd";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el diseño del fragmento que deseas mostrar
        View view = inflater.inflate(R.layout.fragment_dia_1, container, false);

        txtRestScoreBeforeYesterday = view.findViewById(R.id.txtNumeroPuntuacionDescansoDia1);

        firebaseHelper.getBeforeYesterdayNight(userID, new OnSuccessListener<Night>() {
            @Override
            public void onSuccess(Night night) {
                if (night != null) {
                    Log.d("FirebaseHelper", "Fecha BEFORE YESTERDAY NIGHT: "+ night.getDate().toString() + " Puntuación: " + night.getScore());
                    txtRestScoreBeforeYesterday.setText(String.valueOf(night.getScore()));
                } else {
                    Log.d("FirebaseHelper", "No se encontró información para BEFORE YESTERDAY NIGHT.");
                    txtRestScoreBeforeYesterday.setVisibility(View.GONE);
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
