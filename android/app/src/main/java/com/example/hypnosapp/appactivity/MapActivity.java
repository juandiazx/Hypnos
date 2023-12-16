package com.example.hypnosapp.appactivity;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.example.hypnosapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        // Obtenemos el mapa de forma asíncrona (notificará cuando esté listo)
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);
    }

    @Override public void onMapReady(GoogleMap mapa) {
        LatLng UPV = new LatLng(39.481106, -0.340987); //Nos ubicamos en la UPV
        mapa.addMarker(new MarkerOptions().position(UPV).title("Marker UPV"));
        mapa.moveCamera(CameraUpdateFactory.newLatLng(UPV));
    }
}//class
