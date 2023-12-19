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
import com.example.hypnosapp.model.Night;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AdaptadorNoches extends RecyclerView.Adapter<AdaptadorNoches.ViewHolder>{

    private Context context;
    private List<Night> listaNoches;
    private int expandedPosition = -1; // Posición del elemento expandido, -1 significa ninguno

    public AdaptadorNoches(Context context, List<Night> listaNoches) {
        this.context = context;
        this.listaNoches = listaNoches;
    }

    @NonNull
    @Override
    public AdaptadorNoches.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.elemento_historial_padre, parent, false);
        return new AdaptadorNoches.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorNoches.ViewHolder holder, int position) {
        Night noche = listaNoches.get(position);
        holder.bind(noche, position);
    }

    @Override
    public int getItemCount() {
        return listaNoches.size();
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

        public void bind(Night noche, final int position) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd yyyy", Locale.getDefault());
            String formattedDate = dateFormat.format(noche.getDate());

            fechaTextView.setText(formattedDate);
            puntuacionRespiratoriaTextView.setText(String.valueOf(noche.getScore()));
            tiempoSuenioTextView.setText(String.valueOf(noche.getTime()));

            fechaCompletotv.setText(formattedDate);
            puntRespiratoriaCompletotv.setText(String.valueOf(noche.getScore()));
            tiempoSuenioCompletoTv.setText(String.valueOf(noche.getTime()));
            puntuacionTextoTextview.setText(noche.getBreathing());
            temperaturaMediaTextView.setText(String.valueOf(noche.getTemperature()));

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
                    for (int i = 0; i < listaNoches.size(); i++) {
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
