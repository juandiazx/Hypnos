package com.example.hypnosapp.appactivity;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.hypnosapp.R;
import com.example.hypnosapp.firebase.FirebaseHelper;
import com.example.hypnosapp.model.Store;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback{
    private GoogleMap mapa;
    private final LatLng Gandia = new LatLng(38.9666700, -0.1833300); //Coordenadas Gandía
    StorageReference storageRef;
    FirebaseHelper firebaseHelper = new FirebaseHelper();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        storageRef = FirebaseStorage.getInstance().getReference();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        firebaseHelper.getStoreData(new OnSuccessListener<List<Store>>() {
            @Override
            public void onSuccess(List<Store> stores) {
                Log.d("PRUEBAAAAAAAAAAAA", stores.get(0).getName() + ",,,," + stores.get(1).getLocation());

                for(Store store : stores){
                    double latitud = store.getLocation().get(0);
                    double longitud = store.getLocation().get(1);

                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(new LatLng(latitud,longitud))
                            .title(store.getName())
                            .snippet(store.getWeb())
                            .anchor(0.5f, 0.5f);

                    descargarIconoStore(markerOptions, store.getName());
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Exception e) {
                Log.e("Obtener Stores de bbdd", "Error obteniendo Stores de bbdd");
            }
        });
        mapa = googleMap;
        mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(Gandia, 12)); //cuando se abre el mapa se coloca en Gandía
/*
        MarkerOptions markerOptions = new MarkerOptions()
                .position(tiendaHome) //get location
                .title("La Tienda Home") //get name
                .snippet("https://www.latiendahome.com/") //get web
                .anchor(0.5f, 0.5f);

        descargarIconoStore(markerOptions);
*/
        if (ActivityCompat.checkSelfPermission(this,
                ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mapa.setMyLocationEnabled(true);
            mapa.getUiSettings().setZoomControlsEnabled(true);
            mapa.getUiSettings().setCompassEnabled(true);
        }
    }

    private void descargarIconoStore(final MarkerOptions markerOptions, String nombre) {
        File localFile = null;
        try {
            localFile = File.createTempFile("image", "jpeg"); //nombre y extensión
        } catch (IOException e) {
            e.printStackTrace(); //Si hay problemas mostramos la causa
        }
        final String path = localFile.getAbsolutePath();
        Log.d("Almacenamiento", "creando fichero: " + path);

        StorageReference ficheroRef = storageRef.child("stores/"+nombre+"/storeIcon.jpg");
        ficheroRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.d("Almacenamiento", "Fichero bajado" + path);
                        // Reescala el icono descargado
                        Bitmap iGrande = BitmapFactory.decodeFile(path);
                        Bitmap icono = Bitmap.createScaledBitmap(iGrande,
                                iGrande.getWidth() / 4, iGrande.getHeight() /4, false);

                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icono));

                        // Añadir el marcador al mapa después de configurar el icono
                        mapa.addMarker(markerOptions);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("Almacenamiento", "ERROR: bajando fichero");
                    }
                });

    }

    /*
    public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mapa;
    private final LatLng Gandia = new LatLng(38.9666700, -0.1833300); //Coordenadas Gandía
    private final List<MarkerOptions> markerOptionsList = new ArrayList<>();
    StorageReference storageRef;
    String storagePath;
    FirebaseHelper firebaseHelper = new FirebaseHelper();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        storageRef = FirebaseStorage.getInstance().getReference();
        storagePath = "stores/ejemplo/tienda_home_icon.jpg";

        firebaseHelper.getStoreData(new OnSuccessListener<List<Store>>() {
            @Override
            public void onSuccess(List<Store> stores) {
                Log.d("PRUEBAAAAAAAAAAAA", stores.get(0).getName() + ",,," + stores.get(1).getLocation());

                // Crear MarkerOptions para cada tienda y agregarlos a la lista
                for (Store store : stores) {
                    LatLng storeLocation = new LatLng(store.getLocation().getLatitude(), store.getLocation().getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(storeLocation)
                            .title(store.getName())
                            .snippet(store.getWebsite())
                            .anchor(0.5f, 0.5f);

                    markerOptionsList.add(markerOptions);
                }

                // Llamar a getMapAsync después de obtener los datos de la tienda
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa);
                mapFragment.getMapAsync(MapActivity.this);
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Exception e) {
                Log.e("Obtener Stores de bbdd", "Error obteniendo Stores de bbdd");
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(Gandia, 12)); // cuando se abre el mapa se coloca en Gandía

        // Agregar marcadores al mapa
        for (MarkerOptions markerOptions : markerOptionsList) {
            mapa.addMarker(markerOptions);
        }

        if (ActivityCompat.checkSelfPermission(this,
                ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mapa.setMyLocationEnabled(true);
            mapa.getUiSettings().setZoomControlsEnabled(true);
            mapa.getUiSettings().setCompassEnabled(true);
        }
    }
}

     */

}//class



