package com.example.hypnosapp.historial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.hypnosapp.R;

public class HistorialFragmentMes  extends Fragment {
    public HistorialFragmentMes() {
        // Constructor público vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el diseño del fragmento que deseas mostrar
        View view = inflater.inflate(R.layout.historial_mes, container, false);
        // Aquí puedes inicializar las vistas y realizar otras operaciones necesarias
        return view;
    }
}
