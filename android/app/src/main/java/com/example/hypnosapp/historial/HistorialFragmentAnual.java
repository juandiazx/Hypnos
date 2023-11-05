package com.example.hypnosapp.historial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hypnosapp.R;

import java.util.ArrayList;
import java.util.List;

public class HistorialFragmentAnual extends Fragment {
    private RecyclerView recyclerView;
    public AdaptadorDias adaptadorDias;
    private List<DiaModel> listaDias;
    public HistorialFragmentAnual() {
        // Constructor público vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.historial_anual, container, false);

//        // Configura el adaptador con la lista de datos de la actividad principal
//        AdaptadorDias adaptadorDiasAnual = new AdaptadorDias(getActivity(), ((Historial) getActivity()).getListaDias());
//        recyclerView.setAdapter(adaptadorDiasAnual);

        recyclerView = view.findViewById(R.id.recyclerViewHistorial);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listaDias = new ArrayList<>();
        listaDias.add(new DiaModel("05/11/2023", "88/100", "Muy buena", "24C", "8h 25min"));
        listaDias.add(new DiaModel("04/11/2023", "75/100", "Buena", "23C", "7h 45min"));
        listaDias.add(new DiaModel("03/11/2023", "90/100","Muy buena", "25C", "7h 55min"));

        // Inicializa y establece el adaptador con la lista de días
        adaptadorDias = new AdaptadorDias(getContext(), listaDias);
        recyclerView.setAdapter(adaptadorDias);

        return view;
    }
}