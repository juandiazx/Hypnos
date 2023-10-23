package com.example.hypnosapp;

import android.os.Bundle;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;

public class Pantalla_Principal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        setContentView(R.layout.activity_main); // Make sure to replace this with your actual layout XML file

        FrameLayout frameLayout = findViewById(R.id.frameLayout); // Replace R.id.your_frame_layout with your FrameLayout's ID

        // Set the custom drawable as the background of the FrameLayout
        frameLayout.setBackgroundResource(R.drawable.border_background);

         */

        setContentView(R.layout.footer_header);



    }
}

