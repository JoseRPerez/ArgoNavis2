package com.joserp.argonavis;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

import com.joserp.argonavis.AuxFun.Common_functions;
import com.joserp.argonavis.AuxFun.Constructor;
import com.joserp.argonavis.Defs.ChartData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingChartActivity extends AppCompatActivity {

    private DBHelper db;
    private Context context = this;
    private Spinner chartSpinner;
    private EditText etArriba, etIzquierda, etDerecha, etAbajo;
    private EditText etARMIn, etARMax, etDecMin, etDecMax;
    private ImageButton ibBack, ibSave;

    // Mapa para almacenar los datos de las cartas (nombre -> datos)
    private Map<String, ChartData> chartDataMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settins_chart_layout); // Asegúrate de que tu layout se llame activity_main.xml

        //prueba para ocultar barra navegacion android
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        // Inicializar la base de datos
        db = DBHelper.getInstance(this);

        // Obtener referencias a las vistas
        initializeViews();

        // Configurar el Spinner
        setupSpinner();
    }

    private void initializeViews() {
        // Referencias a los EditText del grid
        etArriba = findViewById(R.id.up);
        etIzquierda = findViewById(R.id.left);
        etDerecha = findViewById(R.id.right);
        etAbajo = findViewById(R.id.down);

        // Referencias a los EditText de coordenadas
        etARMIn = findViewById(R.id.minAR);
        etARMax = findViewById(R.id.maxAR);
        etDecMin = findViewById(R.id.minDEC);
        etDecMax = findViewById(R.id.maxDEC);

        // Referencia al Spinner
        chartSpinner = findViewById(R.id.chartSpinner);

        // Botones Toolbar
        ibBack = findViewById(R.id.ibback);
        ibSave = findViewById(R.id.ibsave);

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
            });

        ibSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

    }

    private void setupSpinner() {
        // Obtener los datos usando la clase Constructor
        Constructor.ChartDataResult result = Constructor.ConstructorCharts(db.getCharts());

        List<String> chartNames = result.chartNames;
        chartDataMap = result.chartDataMap;

        // Crear el adaptador para el Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.textview_row,
                chartNames
        );

        adapter.setDropDownViewResource(R.layout.my_spinner);
        chartSpinner.setAdapter(adapter);

        // Listener para cuando se selecciona un item
        chartSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedChart = (String) parent.getItemAtPosition(position);
                updateUIWithChartData(selectedChart);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada
            }
        });
    }

    private void updateUIWithChartData(String chartName) {
        ChartData data = chartDataMap.get(chartName);
        if (data != null) {
            // Actualizar los EditText del grid
            etArriba.setText(data.getArriba());
            etIzquierda.setText(data.getIzquierda());
            etDerecha.setText(data.getDerecha());
            etAbajo.setText(data.getAbajo());

            // Actualizar los EditText de coordenadas
            etARMIn.setText(String.valueOf(data.getArMin()));
            etARMax.setText(String.valueOf(data.getArMax()));
            etDecMin.setText(String.valueOf(data.getDecMin()));
            etDecMax.setText(String.valueOf(data.getDecMax()));
        }
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }

    public void saveChanges() {
        String selectedChart = chartSpinner.getSelectedItem().toString();
        String armin = etARMIn.getText().toString();
        String armax = etARMax.getText().toString();
        String decmin = etDecMin.getText().toString();
        String decmax = etDecMax.getText().toString();

        String izquierda = etIzquierda.getText().toString();
        String derecha = etDerecha.getText().toString();
        String arriba = etArriba.getText().toString();
        String abajo = etAbajo.getText().toString();

        boolean isUpdated = db.updateChart(selectedChart, armin, armax, decmin, decmax, izquierda, derecha, arriba, abajo);

        if (isUpdated){
            Common_functions.makeToast(context, "Cambios guardados");
        } else {
            Common_functions.makeToast(context, "Nueva Carta Insertada");
        }
    }

}
