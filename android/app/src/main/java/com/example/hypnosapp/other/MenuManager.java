package com.example.hypnosapp.other;

import android.content.Context;
import android.content.Intent;

import com.example.hypnosapp.appactivity.AcercaDeActivity;
import com.example.hypnosapp.appactivity.AjustesDeSuenyoActivity;
import com.example.hypnosapp.auth.PerfilUsuarioActivity;
import com.example.hypnosapp.historial.Historial;
import com.example.hypnosapp.mainpage.Pantalla_Principal;

public class MenuManager {

    public static void abrirPantallaPrincipal(Context context) {
        Intent intent = new Intent(context, Pantalla_Principal.class);
        context.startActivity(intent);
    }

    public static void abrirPerfilUsuario(Context context) {
        Intent intent = new Intent(context, PerfilUsuarioActivity.class);
        context.startActivity(intent);
    }

    public static void abrirAjustesDescanso(Context context) {
        Intent intent = new Intent(context, AjustesDeSuenyoActivity.class);
        context.startActivity(intent);
    }

    public static void abrirAcercaDe(Context context) {
        Intent intent = new Intent(context, AcercaDeActivity.class);
        context.startActivity(intent);
    }

    public static void abrirHistorial(Context context) {
        Intent intent = new Intent(context, Historial.class);
        context.startActivity(intent);
    }

}
