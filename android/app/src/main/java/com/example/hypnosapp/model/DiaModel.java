package com.example.hypnosapp.model;
public class DiaModel {
    private String fecha;
    private String puntuacion;
    private String puntuacionTexto;
    private String temperaturaMedia;
    private String tiempoSuenio;

    public DiaModel(String fecha, String puntuacion, String puntuacionTexto, String temperaturaMedia, String tiempoSuenio) {
        this.fecha = fecha;
        this.puntuacion = puntuacion;
        this.puntuacionTexto = puntuacionTexto;
        this.temperaturaMedia = temperaturaMedia;
        this.tiempoSuenio = tiempoSuenio;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(String puntuacion) {
        this.puntuacion = puntuacion;
    }

    public String getPuntuacionTexto() {
        return puntuacionTexto;
    }

    public void setPuntuacionTexto(String puntuacionTexto) {
        this.puntuacionTexto = puntuacionTexto;
    }

    public String getTemperaturaMedia() {
        return temperaturaMedia;
    }

    public void setTemperaturaMedia(String temperaturaMedia) {
        this.temperaturaMedia = temperaturaMedia;
    }

    public String getTiempoSuenio() {
        return tiempoSuenio;
    }

    public void setTiempoSuenio(String tiempoSuenio) {
        this.tiempoSuenio = tiempoSuenio;
    }
}


