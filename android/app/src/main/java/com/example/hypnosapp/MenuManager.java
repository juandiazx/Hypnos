package com.example.hypnosapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

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

}
