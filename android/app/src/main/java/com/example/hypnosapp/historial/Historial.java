package com.example.hypnosapp.historial;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


import com.example.hypnosapp.firebase.FirebaseHelper;
import com.example.hypnosapp.model.Night;
import com.example.hypnosapp.other.MenuManager;
import com.example.hypnosapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.net.Uri;
import androidx.annotation.Nullable;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;





public class Historial extends AppCompatActivity {
    private List<DiaModel> listaDias;
    private static final String TAG = "AjustesDeSuenyo";
    private FirebaseHelper firebaseHelper;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String userID;
    ImageView btnPerfilUsuario, btnPantallaPrincipal, btnAjustesDescanso, btnPreferencias;
    TextView lblErrorDates;
    Button btnSearch,inputDateFrom, inputDateTo, btnExportar;
    private static final int REQUEST_CODE_WRITE_PERMISSION = 1;
    private static final int REQUEST_CODE_PICK_DIRECTORY = 2;
    List<Night> listaNoches;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historial);

        //Menu buttons functionalities
        MenuManager funcionMenu = new MenuManager();

        //Instance of the database and the user
        firebaseHelper = new FirebaseHelper();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        //userID = firebaseUser.getUid();
        userID = "lr3SPEtJqt493dpfWoDd"; // this is the only user of the database at the time

        btnPerfilUsuario = findViewById(R.id.logoUsuarioHeader);
        btnPantallaPrincipal = findViewById(R.id.btnPantallaPrincipal);
        btnAjustesDescanso = findViewById(R.id.btnAjustesDescanso);
        btnPreferencias = findViewById(R.id.btnPreferencias);
        btnSearch = findViewById(R.id.btnSearch);
        inputDateFrom = findViewById(R.id.inputDateFrom);
        inputDateTo = findViewById(R.id.inputDateTo);
        lblErrorDates = findViewById(R.id.lblErrorDates);
        btnExportar = findViewById(R.id.btnExportar);


        btnPerfilUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPerfilUsuario(Historial.this);
            }
        });
        btnPantallaPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirPantallaPrincipal(Historial.this);
            }
        });
        btnAjustesDescanso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirAjustesDescanso(Historial.this);
            }
        });
        btnPreferencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionMenu.abrirAcercaDe(Historial.this);
            }
        });

        inputDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePickerDialogFrom();
            }
        });

        inputDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePickerDialogTo();
            }
        });

        listaDias = new ArrayList<>();
        listaDias.add(new DiaModel("05/11/2023", "88/100", "Muy buena", "24C", "8h 25min"));
        listaDias.add(new DiaModel("04/11/2023", "75/100", "Buena", "23C", "7h 45min"));
        listaDias.add(new DiaModel("03/11/2023", "90/100","Muy buena", "25C", "7h 55min"));

        listaNoches = new ArrayList<>();
        listaNoches.add(new Night(new Date(), "breathing", 90, 25, 8));
        listaNoches.add(new Night(new Date(), "breathing", 76, 15, 5));
        listaNoches.add(new Night(new Date(), "breathing", 40, 35, 4));


        // Encuentra el TabLayout y el ViewPager
        TabLayout tabLayout = findViewById(R.id.tabLayoutHistorial);
        ViewPager viewPager = findViewById(R.id.viewPagerHistorial);
        // Crea un adaptador para manejar los fragmentos
        TabsHistorial adapter = new TabsHistorial(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        // Conecta el TabLayout con el ViewPager
        tabLayout.setupWithViewPager(viewPager);
        TabLayout.Tab tab = tabLayout.getTabAt(0); // Selecciona la tab "Semana" por defecto
        if (tab != null) {
            tab.select();
        }

        firebaseHelper.getPagesFromAllNights(userID,
                new OnSuccessListener<Integer>() {
                    @Override
                    public void onSuccess(Integer pages) {
                        Log.e(TAG, "pages:" + pages);
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Error getting pages");
                    }
                });

        firebaseHelper.getFifteenNights(userID, 1,
                new OnSuccessListener<List<Night>>() {
                    @Override
                    public void onSuccess(List<Night> nights) {
                        for (Night night : nights) {
                            Log.d(TAG, "Night: " + night.getDate() + ", Score: " + night.getScore());
                        }
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Error getting nights", e);
                    }
                });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String initialDate = inputDateFrom.getText().toString();
                String finalDate = inputDateTo.getText().toString();

                if(!areDatesCorrect(initialDate, finalDate)){
                    lblErrorDates.setVisibility(View.VISIBLE);
                }else{
                    lblErrorDates.setVisibility(View.GONE);
                    firebaseHelper.searchNights(userID, initialDate, finalDate,
                            new OnSuccessListener<List<Night>>() {
                                @Override
                                public void onSuccess(List<Night> nightList) {
                                    for (Night night : nightList) {
                                        Log.d(TAG, "Night: " + night.getDate() + ", Score: " + night.getScore());
                                    }
                                }
                            },
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "Error getting nights", e);
                                }
                            });
                }
            }
        });

/*
        btnExportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DEBUG", "Button clicked");
                checkWritePermissionAndPickDirectory();
            }
        });

 */

    }//onCreate
    public List<DiaModel> getListaDias() {
        return listaDias;
    }

    private void mostrarDatePickerDialogFrom() {

        Calendar calendar = Calendar.getInstance(); // Obtiene una instancia del calendario con la fecha y hora actuales
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String fechaSeleccionada = dayOfMonth + "/" + (month + 1) + "/" + year;
                inputDateFrom.setText(fechaSeleccionada);
            }
        }, year, month, dayOfMonth); // Establece la fecha inicial como la fecha actual

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()); // Opcional: establece una fecha máxima (hasta la fecha actual)
        datePickerDialog.show();
    }

    private void mostrarDatePickerDialogTo() {

        Calendar calendar = Calendar.getInstance(); // Obtiene una instancia del calendario con la fecha y hora actuales
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String fechaSeleccionada = dayOfMonth + "/" + (month + 1) + "/" + year;
                inputDateTo.setText(fechaSeleccionada);
            }
        }, year, month, dayOfMonth); // Establece la fecha inicial como la fecha actual

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()); // Opcional: establece una fecha máxima (hasta la fecha actual)
        datePickerDialog.show();
    }

    private boolean areDatesCorrect(String fromDate, String toDate){

        //in order to transform Strings into dates:
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date fromDateTransformed;
        Date toDateTransformed;

        try {
            fromDateTransformed = sdf.parse(fromDate);
            toDateTransformed = sdf.parse(toDate);

            if (fromDateTransformed.after(toDateTransformed)) {
                Log.e(TAG,"La fecha de inicio es posterior a la fecha de fin.");
                return false;
            } else if (fromDateTransformed.before(toDateTransformed)) {
                Log.d(TAG,"La fecha de inicio es anterior a la fecha de fin.");
                return true;
            } else {
                Log.d(TAG,"Las fechas son iguales.");
                return true;
            }

        } catch (ParseException e) {
                e.printStackTrace();
                Log.e(TAG, "Error in converting String dates to Date dates");
                return false;
            }
    }
/*
    public void onExportButtonClick(View view) {
        Log.d("DEBUG", "Button clicked");
        checkWritePermissionAndPickDirectory();
    }

    public void checkWritePermissionAndPickDirectory() {
        // Verifica si ya se tienen permisos de escritura
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Si ya se tienen permisos, procede con la selección del directorio
            Log.d("DEBUG", "Permission already granted");
            pickDirectory();
        } else {
            // Si no se tienen permisos, solicita permisos
            Log.d("DEBUG", "Requesting permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("DEBUG", "onRequestPermissionsResult() called");

        if (requestCode == REQUEST_CODE_WRITE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, procede con la selección del directorio
                Log.d("DEBUG", "Permission granted");
                pickDirectory();
            } else {
                // Permiso denegado
                Log.d("DEBUG", "Permission denied");
                // Puedes mostrar un mensaje al usuario informándole que la acción no se puede realizar sin el permiso
                Toast.makeText(this, "Permission denied. Cannot export PDF without write permission.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void pickDirectory() {
        Log.d("DEBUG", "pickDirectory() called");
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_CODE_PICK_DIRECTORY);
    }


    /*
    private void checkWritePermissionAndPickDirectory() {
        // Verifica si ya se tienen permisos de escritura
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Si ya se tienen permisos, procede con la selección del directorio
            pickDirectory();
        } else {
            // Si no se tienen permisos, solicita permisos
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
        }
    }
    private void pickDirectory() {
        Log.d("DEBUG", "pickDirectory() called");
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_CODE_PICK_DIRECTORY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("DEBUG", "onRequestPermissionsResult() called");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE: {
                // Si el permiso es concedido, procede con la selección del directorio
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickDirectory();
                } else {
                    // Permiso denegado, puedes manejarlo según tus necesidades
                    Toast.makeText(this, "Write permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // Puedes manejar otros casos de permisos aquí si es necesario
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_DIRECTORY && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri treeUri = data.getData();
                saveToChosenDirectory(treeUri,listaNoches);
            }
        }
    }
    private void saveToChosenDirectory(Uri treeUri,List<Night> nightList) {


        // Genera un sello de tiempo para agregar al nombre del archivo
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());

        // Construye la ruta del archivo PDF en el directorio seleccionado por el usuario
        String outputPath = getDocumentPathFromTreeUri(treeUri) + "/noches_" + timestamp + ".pdf";

        // Llama al método PDFExporter para generar el archivo PDF
        PDFExporter(nightList, outputPath);
    }

    private String getDocumentPathFromTreeUri(Uri treeUri) {
        String documentId = DocumentsContract.getTreeDocumentId(treeUri);
        String[] parts = documentId.split(":");
        String type = parts[0];
        String path = parts[1];

        if ("primary".equalsIgnoreCase(type)) {
            return Environment.getExternalStorageDirectory() + "/" + path;
        } else {
            return "/storage/" + type + "/" + path;
        }
    }

    private void PDFExporter(List<Night> nightList, String outputPath) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(20, 700);

                for (Night night : nightList) {
                    contentStream.showText("Night date: " + night.getDate());
                    contentStream.newLine();
                    // Agrega más información sobre la noche según sea necesario
                }
            }

            document.save(outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     */


/*
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDType1Font;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_DIRECTORY = 123;
    private static final int CREATE_WRITE_REQUEST_CODE = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Asocia el botón "EXPORTAR" con el método pickDirectory
        findViewById(R.id.export_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDirectory();
            }
        });
    }

    private void pickDirectory() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_CODE_PICK_DIRECTORY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_DIRECTORY && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri treeUri = data.getData();
                requestWritePermission(treeUri);
            }
        } else if (requestCode == CREATE_WRITE_REQUEST_CODE && resultCode == RESULT_OK) {
            // El usuario ha otorgado permisos, ahora puedes escribir en el almacenamiento externo
            if (data != null) {
                Uri treeUri = data.getData();
                saveToChosenDirectory(treeUri);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void requestWritePermission(Uri treeUri) {
        // Solicitar permisos de escritura utilizando MediaStore
        Intent intent = MediaStore.createWriteRequest(getContentResolver(), treeUri);
        startActivityForResult(intent, CREATE_WRITE_REQUEST_CODE);
    }

    private void saveToChosenDirectory(Uri treeUri) {
        List<Night> nightList = obtenerNochesDesdeLaBaseDeDatos();

        // Genera un sello de tiempo para agregar al nombre del archivo
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());

        // Construye la ruta del archivo PDF en el directorio seleccionado por el usuario
        String outputPath = getDocumentPathFromTreeUri(treeUri) + "/noches_" + timestamp + ".pdf";

        // Llama al método PDFExporter para generar el archivo PDF
        PDFExporter(nightList, outputPath);
    }

    private String getDocumentPathFromTreeUri(Uri treeUri) {
        String documentId = DocumentsContract.getTreeDocumentId(treeUri);
        String[] parts = documentId.split(":");
        String type = parts[0];
        String path = parts[1];

        if ("primary".equalsIgnoreCase(type)) {
            return Environment.getExternalStorageDirectory() + "/" + path;
        } else {
            return "/storage/" + type + "/" + path;
        }
    }

    private void PDFExporter(List<Night> nightList, String outputPath) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(20, 700);

                for (Night night : nightList) {
                    contentStream.showText("Night date: " + night.getDate());
                    contentStream.newLine();
                    // Agrega más información sobre la noche según sea necesario
                }
            }

            document.save(outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

 */
    /*
    import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;



    private void pickDirectory() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_CODE_PICK_DIRECTORY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_DIRECTORY && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri treeUri = data.getData();
                saveToChosenDirectory(treeUri);
            }
        }
    }

    private void saveToChosenDirectory(Uri treeUri) {
        List<Night> nightList = obtenerNochesDesdeLaBaseDeDatos();

        // Genera un sello de tiempo para agregar al nombre del archivo
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());

        // Construye la ruta del archivo PDF en el directorio seleccionado por el usuario
        String outputPath = getDocumentPathFromTreeUri(treeUri) + "/noches_" + timestamp + ".pdf";

        // Llama al método PDFExporter para generar el archivo PDF
        PDFExporter(nightList, outputPath);
    }

    private String getDocumentPathFromTreeUri(Uri treeUri) {
        String documentId = DocumentsContract.getTreeDocumentId(treeUri);
        String[] parts = documentId.split(":");
        String type = parts[0];
        String path = parts[1];

        if ("primary".equalsIgnoreCase(type)) {
            return Environment.getExternalStorageDirectory() + "/" + path;
        } else {
            return "/storage/" + type + "/" + path;
        }
    }

    private void PDFExporter(List<Night> nightList, String outputPath) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(20, 700);

                for (Night night : nightList) {
                    contentStream.showText("Night date: " + night.getDate());
                    contentStream.newLine();
                    // Agrega más información sobre la noche según sea necesario
                }
            }

            document.save(outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Otros métodos y lógica de tu aplicación
}

     */

    /*
    private void PDFExporter(List<Night> nightList, String outputPath){

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(20, 700);

                for (Night night : nightList) {
                    contentStream.showText("Night date: " + night.getDate());
                    contentStream.newLine();
                    // Agrega más información sobre la noche según sea necesario
                }
            }

            document.save(outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     */




}//class




