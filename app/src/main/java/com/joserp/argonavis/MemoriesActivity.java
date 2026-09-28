package com.joserp.argonavis;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.joserp.argonavis.AuxFun.Common_functions;
import com.joserp.argonavis.AuxFun.Constructor;

import java.util.List;

public class MemoriesActivity extends AppCompatActivity {

    private DBHelper db;
    private TextView Titulo;
    Toolbar toolbar;
    private String dsoID, filterType, filterValue;
    RecyclerView recyclerView;
    private Context context = this;
    MemoryAdapter adaptadorRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);


        //prueba para ocultar barra navegacion android
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        db = DBHelper.getInstance((this));

        recyclerView = findViewById(R.id.rvList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Usa LinearLayoutManager o el que necesites

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Titulo = findViewById(R.id.tvTitle);
        Titulo.setSelected(true);

        dsoID = getIntent().getExtras().getString("ID");

        ImageButton backButton = findViewById(R.id.ibBack);
        backButton.setOnClickListener(view -> {
            Common_functions.vibrar("corta");
            finish(); // Cierra la actividad actual.
        });

        // Obtener parámetros del intent si se han pasado
        Intent intent = getIntent();
        filterType = intent.getStringExtra("FILTER_TYPE");
        filterValue = intent.getStringExtra("FILTER_VALUE");

        if (filterType == null) {
            filterType = ""; filterValue = "";
            Titulo.setText(getIntent().getExtras().getString("Code"));

        } else {
            Titulo.setText(filterType + ": " + filterValue);
        }

        PopulateList();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflar el menú en la Toolbar
        getMenuInflater().inflate(R.menu.menu_memories, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Common_functions.vibrar("corta");
        switch (item.getItemId()) {
            case R.id.btn_new:
                DSOActivity.CreateNewMemory(context, dsoID, db, this::PopulateList);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        PopulateList();
    }


    private void PopulateList() {
        List<Memory> memories = getData(filterType, filterValue);
        if (adaptadorRecycler == null) {
            adaptadorRecycler = new MemoryAdapter(memories, context);
            recyclerView.setAdapter(adaptadorRecycler);
        } else {
            adaptadorRecycler.rowItemList.clear();
            adaptadorRecycler.rowItemList.addAll(memories);
            adaptadorRecycler.notifyDataSetChanged();
        }
    }

    private List<Memory> getData(String filterType, String filterValue) {
        if (filterType.equals("Lugar")) {
            return Constructor.ConstructorMEMOFiltered(context, db.getMemoriesByLocation(filterValue), false);
        } else if (filterType.equals("Fecha")) {
            return Constructor.ConstructorMEMOFiltered(context, db.getMemoriesByDate(filterValue), true);
        } else {
            try (Cursor cursor = db.getMemories(dsoID)) {
                return Constructor.ConstructorMEMO(context, cursor);
            }
        }
    }

}
