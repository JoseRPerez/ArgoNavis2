package com.joserp.argonavis;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.joserp.argonavis.AuxFun.Common_functions;

import java.util.ArrayList;


public class ToViewActivity extends AppCompatActivity {
    private int scrollPosition, scrollOffset;
    private String titulo = "Pendientes de Observar";
    RecyclerView recyclerView;
    UserListNotSeqAdapter adaptadorRecycler;
    Context context = this;
    private LinearLayoutManager layoutManager;
    DBHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);

        //prueba para ocultar barra navegacion android
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        scrollOffset = 0;
        scrollPosition = RecyclerView.NO_POSITION;

        recyclerView = findViewById(R.id.rvList);
        TextView titleTextView = findViewById(R.id.tvTitle);
        titleTextView.setSelected(true); // Necesario para que funcione la marquesina
        titleTextView.setText(titulo);

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
        cursor = db.getToObserveList_cursor();
        return Constructor(cursor);
    }

    private ArrayList Constructor(Cursor cursor) {
        ArrayList list = new ArrayList<>();
        String OldCon = "";

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                String con = cursor.getString(1);
                if (!OldCon.equalsIgnoreCase(con)) {
                    list.add(con);
                    OldCon = con;
                }
                String cat_ref = cursor.getString(0);
                String name = cursor.getString(2);
                String tipo = cursor.getString(3);
                String clasif = cursor.getString(4);
                boolean toview = Boolean.parseBoolean(cursor.getString(5));
                boolean visto = Boolean.parseBoolean(cursor.getString(6));
                int rate = cursor.getInt(7);
                String id = cursor.getString(8);

                dsoRow dso = new dsoRow(cat_ref, name, tipo, clasif, toview, visto, rate, id);
                list.add(dso);

            } while (cursor.moveToNext());
        } else {
            Toast.makeText(getApplicationContext(), "No se encuentran elementos", Toast.LENGTH_LONG).show();
        }
        return list;
    }

    public void populateList() {
        adaptadorRecycler = new UserListNotSeqAdapter(getData(),"",true, context);
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
