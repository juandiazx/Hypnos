package com.example.hypnosapp.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringFormatting {

    public static String extractTitle(String url) {
        // Utilizamos una expresión regular para encontrar el valor del parámetro 'title'
        Pattern pattern = Pattern.compile("title=([^&]+)");
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            // Decodificamos la cadena encontrada para manejar caracteres especiales
            try {
                String decodedTitle = URLDecoder.decode(matcher.group(1), "UTF-8");

                // Eliminamos caracteres especiales, % y números
                String cleanTitle = decodedTitle.replaceAll("[^a-zA-Z ]", "");

                return cleanTitle;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static String extractNumberTitle(String texto) {
        // Patrón para encontrar un número en el texto
        Pattern patronNumero = Pattern.compile("\\d+");

        // Crear un objeto Matcher
        Matcher matcher = patronNumero.matcher(texto);

        // Verificar si se encontró un número
        if (matcher.find()) {
            // Devolver el número encontrado como entero
            return matcher.group();
        } else {
            // Devolver null si no se encontró ningún número
            return null;
        }
    }
}
