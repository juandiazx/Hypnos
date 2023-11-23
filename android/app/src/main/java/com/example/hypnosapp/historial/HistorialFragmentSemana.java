package com.example.hypnosapp.historial;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hypnosapp.R;
import com.example.hypnosapp.historial.AdaptadorDias;
import com.example.hypnosapp.historial.DiaModel;

import java.util.ArrayList;
import java.util.List;

public class HistorialFragmentSemana extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {
    private RecyclerView recyclerView;
    public AdaptadorDias adaptadorDias;

    public HistorialFragmentSemana() {
        // Constructor público vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.historial_semana, container, false);

//        // Configura el adaptador con la lista de datos de la actividad principal
//        AdaptadorDias adaptadorDiasSemana = new AdaptadorDias(getActivity(), ((Historial) getActivity()).getListaDias());
//        recyclerView.setAdapter(adaptadorDiasSemana);

        recyclerView = view.findViewById(R.id.recyclerViewHistorial);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Obtén la lista de datos de la actividad principal
        List<DiaModel> listaDias = ((Historial) getActivity()).getListaDias();

        // Inicializa y establece el adaptador con la lista de días
        adaptadorDias = new AdaptadorDias(getContext(), listaDias);
        recyclerView.setAdapter(adaptadorDias);

        return view;
    }

}