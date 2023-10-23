package com.example.hypnosapp;

import android.content.Context;
import android.content.Intent;

public class MenuManager {

    public static void abrirPantallaPrincipal(Context context) {
        Intent intent = new Intent(context, PreinicioDeSesion.class);
        context.startActivity(intent);
    }

}
