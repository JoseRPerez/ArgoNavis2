package com.joserp.argonavis;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
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

import java.util.ArrayList;
import java.util.List;

public class intoConstellationActivity extends AppCompatActivity implements
        FilterDialogFragment.FilterDialogListener {
    private DBHelper db;
    private int scrollPosition, scrollOffset;
    private String constelacion;

    private String placeName = null;
    private String Type = "";
    private String Clasif = "";
    private String Rate = "";
    private Context context =  this;
    private LinearLayoutManager layoutManager;

    RecyclerView recyclerView;
    InConstellationAdapter adapterInCons;
    private List<dsoRow> dsosList;
    Toolbar toolbar;

    private Parcelable listState;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);

        //prueba para ocultar barra navegacion android
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        db = DBHelper.getInstance(context);

        constelacion = getIntent().getExtras().getString("Constelacion");

        scrollOffset = 0;
        scrollPosition = RecyclerView.NO_POSITION;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView title = findViewById(R.id.tvTitle);
        title.setSelected(true);  //necesario para que funcione la marquesina
        title.setText(constelacion);

        ImageButton backButton = findViewById(R.id.ibBack);
        backButton.setOnClickListener(view -> {
            Common_functions.vibrar("corta");
            finish(); // Cierra la actividad actual.
        });


        // Inicializar el LayoutManager
        recyclerView = findViewById(R.id.rvList);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (savedInstanceState != null) {
            scrollPosition = savedInstanceState.getInt("scrollPosition", 0);
            scrollOffset = savedInstanceState.getInt("scrollOffset", 0);// Restaura la posición y el desplazamiento del scroll
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        populateList();
        if (scrollPosition != RecyclerView.NO_POSITION) {
            layoutManager.scrollToPositionWithOffset(scrollPosition, scrollOffset);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        scrollPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
        View firstVisibleView = recyclerView.getChildAt(1);
        scrollOffset = (firstVisibleView != null) ? firstVisibleView.getTop() : 0;      //propuesta de la IA
    }




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("scrollPosition", layoutManager.findFirstVisibleItemPosition());
        outState.putInt("scrollOffset", recyclerView.computeVerticalScrollOffset());
    }

/*    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            // Recupera el estado guardado
            listState = savedInstanceState.getParcelable("KEY_RECYCLER_STATE");
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflar el menú en la Toolbar
        if (constelacion.equals("Meridiano")) {
            getMenuInflater().inflate(R.menu.menu_meridian, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_into_cons, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.updateMeridian:
                Common_functions.vibrar("corta");
                populateList();
                return true;
            //case R.id.selectPlace:
            //    Common_functions.vibrar("corta");
            //    openPlacesDialog();
            //    return true;
            case R.id.filterCon:
                Common_functions.vibrar("corta");
                openFilterDialog();
                return true;
            case R.id.showMaps:
                Common_functions.vibrar("corta");
                String maps = db.getChartsInconstellation(constelacion);
                if (maps.isEmpty()) {
                    Toast.makeText(this, "Complete la información de la constelación", Toast.LENGTH_SHORT).show();
                } else {
                    openMap(maps);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Todavia no tenemos implementado la configuracion de Places por lo que no pueden
    // establecerse ni su LAT ni su LON.
    // ** INCOMPLETO **
    /*
    private void openPlacesDialog() {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_grid, null);

        GridView gridView = dialogView.findViewById(R.id.gv_choices);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        // Configurar el adaptador para el GridView

        //Map<String, List<String>>  placesList = db.getPlacesData();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.list_item, placesList);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            placeName = placesList.get(position);
            populateList();
        });

        dialog.show();
    }
     */


    private void openFilterDialog() {
        FilterDialogFragment dialogFragment = new FilterDialogFragment();
        dialogFragment.setFilterDialogListener(this);
        dialogFragment.show(getSupportFragmentManager(), "FilterDialog");

    }


    // Metodo refactorizado para abrir el mapa, mejorando la claridad y manejo de casos
    private void openMap(String maps) {
        ArrayList<String> mapList = Common_functions.getMapsPathIfExists(maps); // Obtener la lista de mapas

        if (mapList.isEmpty()) {
            Toast.makeText(this, "No hay mapas disponibles. Revise la carpeta maps.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mapList.size() == 1) {
            onMapSelected(mapList.get(0), "");
        } else {
            new DSOActivity().openMapsDialog(this, null ,mapList,  this::onMapSelected); // Uso de expresión lambda para mayor legibilidad
        }
    }

    // Metodo refactorizado para lanzar la actividad del mapa
    private void onMapSelected(String map, String ID) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("filepath", Common_functions.getMapUrl(map));
        intent.putExtra("filename", map);
        intent.putExtra("dsoID", ID);
        startActivity(intent);
    }

    // Interfaz para manejar la selección del mapa (opcional, pero útil)
    public interface OnMapSelectedListener {
        void onMapSelected(String selectedMap, String ID);
    }

    // inicio prueba dialog fragment
    @Override
    public void onFilterApplied(String tipo, String clasif, String rate) {
        // Guarda los nuevos valores de filtro
        Type = tipo;
        Clasif = clasif;
        Rate = rate;

        // Actualiza la lista
        populateList();
    }

    @Override
    public void onFilterCanceled() {
        // Opcional: manejar cuando se cancela el diálogo
        Common_functions.makeToast(context, "Filtrado cancelado");
    }

    private void filtrarLista(String tipo, String clasif, String rate) {
        // Aquí implementa tu lógica de filtrado
        Common_functions.makeToast(context, "Tipo: " + tipo + ", Clasificación: " + clasif + ", Rate: " + rate);
        //if (tuAdaptador != null) {
        //    tuAdaptador.filtrar(tipo, clasif, Integer.parseInt(rate));
        //}
    }


    private List<dsoRow> getData() {
        Cursor cursor;

        if (constelacion.equals("Meridiano")) {
            cursor = getObjectsInTransitNow();
        } else {
            cursor = db.getComplexDSOSearchInCON(Type, Clasif, Rate, constelacion);
        }
        Type = "";
        Clasif = "";
        Rate = "0";
        return Constructor.ConstructorALLData(context, cursor);
    }

    private Cursor getObjectsInTransitNow() {
        // Metodo no implementado pues no se ha incluido todavia la opcioon de modificar los
        // paramaetros de los lugares de observacion
        //float[] coords = db.getPlaceCoordinates(placeName);
        //float lat = 37.438429F;
        float lon = -4.198042F;

        double tsl = SiderealTimeCalculator.calculateCorrectedLST(
                lon,
                "Europe/Madrid"
        );
        return db.getObjectsInTransitNow(tsl);
    }


    public void populateList() {
        dsosList = getData();
        adapterInCons = new InConstellationAdapter(dsosList, context);
        recyclerView.setAdapter(adapterInCons);

        // Restaurar la posición del scroll después de cargar los datos
        if (scrollPosition != 0) {
            layoutManager.scrollToPositionWithOffset(scrollPosition, scrollOffset);
        }
    }


}
