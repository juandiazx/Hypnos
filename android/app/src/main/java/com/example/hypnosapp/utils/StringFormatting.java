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
}
