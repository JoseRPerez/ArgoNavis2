package com.joserp.argonavis;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.joserp.argonavis.AuxFun.Common_functions;
import com.joserp.argonavis.AuxFun.Constructor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class UserListNotSeqActivity extends AppCompatActivity {
    private int scrollPosition, scrollOffset;
    RecyclerView recyclerView;
    UserListNotSeqAdapter adaptadorRecycler;
    Context context = this;
    private LinearLayoutManager layoutManager;
    DBHelper db;
    Toolbar toolbar;
    private String name, UserListID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);

        //prueba para ocultar barra navegacion android
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        name = getIntent().getExtras().getString("ListName");
        UserListID = getIntent().getExtras().getString("ID_UserList");

        scrollOffset = 0;
        scrollPosition = RecyclerView.NO_POSITION;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView title = findViewById(R.id.tvTitle);
        title.setText(name);
        //title.setSelected(true); // Necesario para que funcione la marquesina aunque en realidad no funciona

        recyclerView = findViewById(R.id.rvList);

        ImageButton backButton = findViewById(R.id.ibBack);

        db = DBHelper.getInstance(this);

        backButton.setOnClickListener(view -> {
            Common_functions.vibrar("corta");
            finish(); // Cierra la actividad actual.
        });

        // Inicializar el LayoutManager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (savedInstanceState != null) {
            scrollPosition = savedInstanceState.getInt("scrollPosition", 0);
            scrollOffset = savedInstanceState.getInt("scrollOffset", 0);// Restaura la posición y el desplazamiento del scroll
        }

        //cargar los datos iniciales
        //populateList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflar el menú en la Toolbar
        getMenuInflater().inflate(R.menu.menu_user_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shareList:
                shareList();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

 public void shareList(){
        //funcion asincrona para exportar listas de usuario
        Common_functions.vibrar("");

        new Thread(() -> {
             try {
                 // Obtener la lista de datos a exportar
                 ArrayList<String> exportList = db.getDataListToExport(UserListID);

                 // Crear archivo CSV
                 File file = UserListSeqActivity.getFileUrl(name);

                 if (!file.exists()) {
                      file.createNewFile();
                 }

                 // Usar FileOutputStream para escribir en el archivo
                 FileOutputStream fos = new FileOutputStream(file);

                 // Escribir el nombre de la lista y un indicador "false"
                 String firstLine = name + ",false\n";
                 fos.write(firstLine.getBytes());

                 // Escribir cada elemento de la lista en una línea
                 for (String item : exportList) {
                     String line = item + "\n";
                     fos.write(line.getBytes());
                 }

                 // Cerrar el flujo
                 fos.close();

                 runOnUiThread(() -> Toast.makeText(this, "Lista exportada en la carpeta Argonavis", Toast.LENGTH_SHORT).show());

        } catch (IOException e) {
                 //visualizacion del error
                 String errorMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
                 runOnUiThread(() -> Toast.makeText(this, "Error al exportar la lista: " + errorMsg, Toast.LENGTH_SHORT).show());
             }
         }).start();
    }


    @Override
    public void onResume() {
        super.onResume();
        populateList(); // Actualiza la lista al volver desde vista secundaria
        if (scrollPosition != RecyclerView.NO_POSITION) {
            layoutManager.scrollToPositionWithOffset(scrollPosition, scrollOffset);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        scrollPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        View firstVisibleView = recyclerView.getChildAt(1);
        scrollOffset = (firstVisibleView != null) ? (firstVisibleView.getTop() - recyclerView.getPaddingTop()) : 0;
    }

    private ArrayList getData() {
        Cursor cursor;
        cursor = db.getDsosInListOrderByCon(UserListID);
        return Constructor.ConstructorWithConstellation(context, cursor);
    }

    public void populateList() {
        adaptadorRecycler = new UserListNotSeqAdapter(getData(),UserListID,false, context);
        recyclerView.setAdapter(adaptadorRecycler);

        // Restaurar la posición del scroll después de cargar los datos
        if (scrollPosition != 0) {
            layoutManager.scrollToPositionWithOffset(scrollPosition, scrollOffset);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("scrollPosition", layoutManager.findFirstVisibleItemPosition());
        outState.putInt("scrollOffset", recyclerView.computeVerticalScrollOffset());
    }
}

