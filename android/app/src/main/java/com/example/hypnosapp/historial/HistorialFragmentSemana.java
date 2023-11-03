package com.example.hypnosapp.historial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.hypnosapp.R;

public class HistorialFragmentSemana extends Fragment {
    public HistorialFragmentSemana() {
        // Constructor público vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el diseño del fragmento que deseas mostrar
        View view = inflater.inflate(R.layout.historial_semana, container, false);
        // Aquí puedes inicializar las vistas y realizar otras operaciones necesarias
        return view;
    }
}