package com.example.hypnosapp.historial;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hypnosapp.R;

import java.util.List;

public class AdaptadorDias extends RecyclerView.Adapter<AdaptadorDias.ViewHolder> {
    private Context context;
    private List<DiaModel> listaDias;
    private int expandedPosition = -1; // Posición del elemento expandido, -1 significa ninguno

    public AdaptadorDias(Context context, List<DiaModel> listaDias) {
        this.context = context;
        this.listaDias = listaDias;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.elemento_historial_padre, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DiaModel dia = listaDias.get(position);
        holder.bind(dia, position);
    }

    @Override
    public int getItemCount() {
        return listaDias.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView fechaTextView, puntuacionRespiratoriaTextView, puntuacionTextoTextview, temperaturaMediaTextView, tiempoSuenioTextView;
        TextView fechaCompletotv, puntRespiratoriaCompletotv, tiempoSuenioCompletoTv;
        ConstraintLayout reducidoView, completoView;
        boolean isExpanded = false; // Para rastrear el estado

        public ViewHolder(View itemView) {
            super(itemView);
            reducidoView = itemView.findViewById(R.id.elementoHistorialReducido);
            completoView = itemView.findViewById(R.id.elementoHistorialCompleto);

            fechaTextView = itemView.findViewById(R.id.fechaElementoHistorial);
            puntuacionRespiratoriaTextView = itemView.findViewById(R.id.puntuacionRespiratoriaHistorial);
            tiempoSuenioTextView = itemView.findViewById(R.id.tiempoSuenioHistorial);

            fechaCompletotv = itemView.findViewById(R.id.fechaElementoHistorialCompleto);
            puntRespiratoriaCompletotv = itemView.findViewById(R.id.puntuacionRespiratoriaHistorialCompleto);
            tiempoSuenioCompletoTv = itemView.findViewById(R.id.tiempoSuenioHistorialCompleto);
            puntuacionTextoTextview = itemView.findViewById(R.id.puntuacionTextoHistorial);
            temperaturaMediaTextView = itemView.findViewById(R.id.temperaturaMediaHistorial);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isExpanded = !isExpanded;
                    updateView();
                }
            });
        }

        private void updateView() {
            if (isExpanded) {
                reducidoView.setVisibility(View.GONE);
                completoView.setVisibility(View.VISIBLE);
            } else {
                reducidoView.setVisibility(View.VISIBLE);
                completoView.setVisibility(View.GONE);
            }
        }

        public void bind(DiaModel dia, final int position) {
            fechaTextView.setText(dia.getFecha());
            puntuacionRespiratoriaTextView.setText(dia.getPuntuacion());
            tiempoSuenioTextView.setText(dia.getTiempoSuenio());

            fechaCompletotv.setText(dia.getFecha());
            puntRespiratoriaCompletotv.setText(dia.getPuntuacion());
            tiempoSuenioCompletoTv.setText(dia.getTiempoSuenio());
            puntuacionTextoTextview.setText(dia.getPuntuacionTexto());
            temperaturaMediaTextView.setText(dia.getTemperaturaMedia());

            // Agrega un OnClickListener para alternar la visibilidad al hacer clic
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == expandedPosition) {
                        expandedPosition = -1; // Si se hace clic en un elemento ya expandido, se contrae
                    } else {
                        expandedPosition = position; // Si se hace clic en un elemento, se expande
                    }

                    // Oculta elementoHistorialCompleto para otros elementos antes de mostrar el actual
                    for (int i = 0; i < listaDias.size(); i++) {
                        if (i != position) {
                            notifyItemChanged(i);
                        }
                    }

                    notifyDataSetChanged(); // Notifica al adaptador que los datos han cambiado
                }
            });

            // Establece la visibilidad del diseño completo según si el elemento debe estar expandido o no
            completoView.setVisibility(position == expandedPosition ? View.VISIBLE : View.GONE);
            reducidoView.setVisibility(position == expandedPosition ? View.GONE : View.VISIBLE);
        }
    }
}

