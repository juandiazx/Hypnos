package com.example.hypnosapp.historial;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hypnosapp.R;

import java.util.List;

public class AdaptadorDias extends RecyclerView.Adapter<AdaptadorDias.ViewHolder> {
    private Context context;
    private List<DiaModel> listaDias; // Asegúrate de crear la clase DiaModel para representar la información de fecha y puntuación.

    public AdaptadorDias(Context context, List<DiaModel> listaDias) {
        this.context = context;
        this.listaDias = listaDias;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.elemento_historial, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DiaModel dia = listaDias.get(position);
        holder.bind(dia);
    }

    @Override
    public int getItemCount() {
        return listaDias.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Declarar las vistas del elemento del RecyclerView aquí, por ejemplo, TextViews para fecha y puntuación.
        TextView fechaTextView;
        TextView puntuacionRespiratoriaTextView;
        TextView puntuacionTextoTextview;
        TextView temperaturaMediaTextView;
        TextView tiempoSuenioTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            // Inicializar las vistas aquí mediante findViewById con las referencias correctas
            fechaTextView = itemView.findViewById(R.id.fechaElementoHistorial);
            puntuacionRespiratoriaTextView = itemView.findViewById(R.id.puntuacionRespiratoriaHistorial);
            puntuacionTextoTextview = itemView.findViewById(R.id.puntuacionTextoHistorial);
            temperaturaMediaTextView = itemView.findViewById(R.id.temperaturaMediaHistorial);
            tiempoSuenioTextView = itemView.findViewById(R.id.tiempoSuenioHistorial);
        }

        public void bind(DiaModel dia) {
            // Asigna los valores de fecha, puntuación, temperatura, tiempo de sueño, etc., a las vistas del elemento.
            fechaTextView.setText(dia.getFecha());
            puntuacionRespiratoriaTextView.setText(dia.getPuntuacion());
            puntuacionTextoTextview.setText(dia.getPuntuacionTexto());
            temperaturaMediaTextView.setText(dia.getTemperaturaMedia());
            tiempoSuenioTextView.setText(dia.getTiempoSuenio());
            // Asigna otros valores según sea necesario
        }
    }
}
