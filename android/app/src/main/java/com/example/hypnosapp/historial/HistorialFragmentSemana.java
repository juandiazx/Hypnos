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

public class HistorialFragmentSemana extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback{
    private RecyclerView recyclerView;
    public AdaptadorDias adaptadorDias;
    public HistorialFragmentSemana() {
        // Constructor público vacío requerido
    }
    private static final int REQUEST_CODE_WRITE_PERMISSION = 1;
    private static final int REQUEST_CODE_PICK_DIRECTORY = 2;
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

        Button btnExportar = view.findViewById(R.id.btnExportar);
        btnExportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lógica para el clic del botón en este fragmento
                Log.d("DEBUG", "Button clicked in fragment");
                checkWritePermissionAndPickDirectory();
            }
        });

        return view;
    }

    private void checkWritePermissionAndPickDirectory() {
        // Verifica si ya se tienen permisos de escritura
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Si ya se tienen permisos, procede con la selección del directorio
            Log.d("DEBUG", "Permission already granted");
            pickDirectory();
        } else {
            // Muestra un diálogo explicativo
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(requireActivity())
                            .setTitle("Permiso necesario")
                            .setMessage("Se necesita el permiso de escritura para exportar el PDF.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d("DEBUG", "OK button clicked in dialog");
                                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_PERMISSION);
                                }
                            })
                            .show();

                }
            });

        }
    }

    @Override
    @SuppressLint("Override")
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("DEBUG", "onRequestPermissionsResult() called");

        if (requestCode == REQUEST_CODE_WRITE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, procede con la selección del directorio
                Log.d("DEBUG", "Permission granted");
                pickDirectory();
            } else {
                // Permiso denegado
                Log.d("DEBUG", "Permission denied");
                // Puedes mostrar un mensaje al usuario informándole que la acción no se puede realizar sin el permiso
                Toast.makeText(requireContext(), "Permission denied. Cannot export PDF without write permission.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void pickDirectory() {
        Log.d("DEBUG", "pickDirectory() called");
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_CODE_PICK_DIRECTORY);
    }

}
