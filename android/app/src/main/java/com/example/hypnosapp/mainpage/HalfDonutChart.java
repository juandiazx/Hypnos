package com.example.hypnosapp.mainpage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class HalfDonutChart extends View {

    private Paint paint;
    private RectF rectF;

    public HalfDonutChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        rectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        int strokeWidth = 30; // Grosor del donut
        int halfStrokeWidth = strokeWidth / 2;
        int radius = Math.min(width, height) / 2 - halfStrokeWidth;

        rectF.set(width / 2 - radius, height / 2 - radius, width / 2 + radius, height / 2 + radius);

        // Dibuja la mitad rellena con un color azul claro
        paint.setColor(Color.parseColor("#809ED2F1")); // Color azul claro
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);

        // Dibuja el contorno del medio donut de 0 a 180 grados verticalmente
        canvas.drawArc(rectF, 180, 180, false, paint);
        //De 180 a 180 seria una puntuacion de 100. 180 a 147 seria una puntuacion de 82. Regla de tres: 180 * X / 100

        // Draw the outline with a darker blue color
        paint.setColor(Color.parseColor("#164499"));// Azul oscuro
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);

        // Dibuja el contorno del medio donut de 0 a 180 grados verticalmente
        canvas.drawArc(rectF, 180, 147, false, paint);
        //De 180 a 180 seria una puntuacion de 100. 180 a 147 seria una puntuacion de 82. Regla de tres: 180 * X / 100
    }
}



