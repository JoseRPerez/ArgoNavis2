package com.joserp.argonavis;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.joserp.argonavis.AuxFun.Common_functions;
import com.joserp.argonavis.AuxFun.Constructor;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity  {
    private DBHelper db;
    SpeedDialView speedDial;
    private String map, dsoID;
    SubsamplingScaleImageView imageView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.object_super_map);

        //prueba para ocultar barra navegacion android
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        db = DBHelper.getInstance(this);

        String filePath = "";

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            filePath = extras.getString("filepath"); // Obligatorio
            map = extras.getString("filename");     // Obligatorio
            dsoID = extras.getString("dsoID");     // Opcional (devuelve null si no existe)
        }

        imageView = findViewById(R.id.photo_view);
        setImage(map, filePath);

        speedDial = findViewById(R.id.speedDial);
        CargarBotonesFlotantes();

        // Manejar clics en las acciones
        speedDial.setOnActionSelectedListener(actionItem -> {
            int selectedOption = actionItem.getId();
            accionFlotante(selectedOption);
            return true;
        });


        // En tu onCreate() o donde inicialices el imageView:
        imageView.setOnImageEventListener(new SubsamplingScaleImageView.OnImageEventListener() {
            @Override
            public void onReady() {
            }

            @Override
            public void onImageLoaded() {
                // La imagen está lista, ahora sí podemos dibujar el marcador
                if (dsoID != null) {
                    drawGreenTarget();
                }
            }

            @Override
            public void onPreviewLoadError(Exception e) {
            }

            @Override
            public void onImageLoadError(Exception e) {
            }

            @Override
            public void onTileLoadError(Exception e) {
            }

            @Override
            public void onPreviewReleased() {
            }
        });
    }


    public void drawGreenTarget() {
        ViewGroup rootLayout = (ViewGroup) imageView.getParent();
        View greenSquare = new View(this);

        int greenSquareSize = 200;

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(greenSquareSize, greenSquareSize);

        // 1. Obtener dimensiones de pantalla
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        // 2. Verificar si la imagen está cargada
        if (!imageView.isReady()) {
            Log.e("DEBUG", "La imagen no está cargada aún");
            return;
        }

        // 3. Obtener dimensiones de la imagen original
        int imageWidth = imageView.getSWidth();
        int imageHeight = imageView.getSHeight();
        float imageAspectRatio = (float) imageWidth / imageHeight;
        int scaledImageHeight = (int) (screenWidth / imageAspectRatio);
        int verticalMargin = (screenHeight - scaledImageHeight) / 2;

        // 4. Obtener coordenadas del mapa y del objeto
        float[] mapData = db.getMapData(map);
        float ramin = mapData[0], ramax = mapData[1], decmin = mapData[2], decmax = mapData[3];
        float[] dsoCoords = db.getDSOCoords(dsoID);
        float ra = dsoCoords[0], dec = dsoCoords[1];

        // Solucion obvia para cuando ramin es mayor que ramax (cuando la carta atraviesa ra 0 h)
        // Lo ideal seria buscar una solucion matematicamente mas robusta
        if (ramin > ramax) {
            ramax += 24F;
            if (ra < ramin) {
                ra += 24;
            }
        }


        // 5. Calcular posición relativa
        float relX = (ra - ramin) / (ramax - ramin);
        float relY = (dec - decmin) / (decmax - decmin);


        // 6. Convertir a coordenadas de pantalla
        int targetX = (int) (screenWidth - (relX * screenWidth));
        int targetY = (int) (screenHeight - verticalMargin - (relY * scaledImageHeight));

        // 7. Ajustar centrado del marcador
        params.leftMargin = targetX - greenSquareSize/2;
        params.topMargin = targetY - greenSquareSize/2;

        GradientDrawable borderDrawable = new GradientDrawable();
        borderDrawable.setShape(GradientDrawable.RECTANGLE);
        //borderDrawable.setStroke(2, Color.GREEN); // Grosor y color del borde
        float density = getResources().getDisplayMetrics().density;
        int borderWidthPx = (int)(2 * density); // 2dp convertidos a píxeles
        borderDrawable.setStroke(borderWidthPx, Color.GREEN);
        //borderDrawable.setColor(Color.TRANSPARENT); // Relleno transparente

        borderDrawable.setColor(ContextCompat.getColor(this, R.color.green_90_transparent));

        // Aplicar el Drawable al View
        greenSquare.setBackground(borderDrawable);

        rootLayout.addView(greenSquare, params);

        // Ocultar después de 5 segundos
        new Handler().postDelayed(() -> rootLayout.removeView(greenSquare), 2000);
    }



    public void CargarBotonesFlotantes() {
        //borramos opciones si existen
        speedDial.clearActionItems();

        //Cargamos las opciones
        int[] icons = {R.drawable.deep_search, R.drawable.arrow_right, R.drawable.arrow_left,
                R.drawable.arrow_down, R.drawable.arrow_up};

        //Definimos colores de fondo de las opciones
        int[] colors = {R.color.selected_color, R.color.gris_inactivo, R.color.gris_inactivo,
                R.color.gris_inactivo, R.color.gris_inactivo};

        //Agregamos acciones dinámicamente
        for (int i = 0; i < icons.length; i++) {
            speedDial.addActionItem(
                    new SpeedDialActionItem.Builder(i, icons[i]) // ID y ícono
                            .setFabBackgroundColor(ContextCompat.getColor(this, colors[i])) // Color de fondo
                            .setFabImageTintColor(ContextCompat.getColor(this, R.color.blanco)) // Color del ícono
                            .setLabelColor(ContextCompat.getColor(this, R.color.blanco)) // Color del texto
                            .setLabelBackgroundColor(ContextCompat.getColor(this, R.color.gris_inactivo)) // Color de fondo del texto
                            .create()
            );
        }

    }

    private void accionFlotante(int selectedOption ) {
        if (selectedOption == 0) {
            showSearchDialog();
        } else {
            final String[] directionKeys = {"R", "L", "D", "U"};
            List<String> neighMaps = db.getNeighMapsDirection(map, directionKeys[selectedOption - 1]);
            openMap(neighMaps);
        }
        speedDial.close();  //cerramos las opciones
    }


    private void openMap(List<String> neighMaps) {
        if (neighMaps.size() == 1) {
            setImage(neighMaps.get(0), Common_functions.getMapUrl(neighMaps.get(0)));

        } else if (neighMaps.size() == 0){
            Common_functions.makeToast(this, "No se han asignado mapas cercanos");
        } else {
            showAndSelectMap(neighMaps);
        }
    }


    public void setImage(String mapName, String mapURL) {
        if (mapURL != null) {
            //imageView.setImage(ImageSource.uri(mapURL));
            map = mapName;
            imageView.setImage(ImageSource.uri(mapURL));
        } else {
            Common_functions.makeToast(this, "Mapa no encontrado");
        }
    }


    private void showAndSelectMap(List<String> neighMaps) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_grid, null);

        GridView gridView = dialogView.findViewById(R.id.gv_choices);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        // Configurar el adaptador para el GridView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item, neighMaps);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedMap = neighMaps.get(position);
            setImage(selectedMap, Common_functions.getMapUrl(selectedMap));
            //dialog.dismiss(); //no cierro el dialogo para poder cambiar rapido en caso de error
        });

        dialog.show();
    }



    public void showSearchDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.add_registry_on_map, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        //builder.setTitle("Buscar en la base de datos");

        AlertDialog dialog = builder.create();

        EditText etSearch = dialogView.findViewById(R.id.etSearch);
        RecyclerView rvResults = dialogView.findViewById(R.id.rvResults);

        SearchAdapter adapter = new SearchAdapter(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRow item) {
                // Manejar selección de ítem existente
                DSOActivity.CreateNewMemory(MapActivity.this,
                        String.valueOf(item.getID()), db, () -> dialog.dismiss());
            }

            @Override
            public void onAddNewClick() {
                // Llamar a la función CreateNewDSO
                CreateNewDSO(etSearch.getText().toString());
                dialog.dismiss();
            }
        });

        rvResults.setLayoutManager(new LinearLayoutManager(this));
        rvResults.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    adapter.updateData(new ArrayList<>(), false);
                } else {
                    searchInDatabase(query, adapter);
                }
            }
        });

        dialog.show();
    }


    // Metodo helper para configurar spinners
    private void setupSpinnerAdapter(Spinner spinner, List<String> data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.textview_row,
                data
        );
        adapter.setDropDownViewResource(R.layout.my_spinner);
        spinner.setAdapter(adapter);
    }


    @Override
    protected void onDestroy() {
        // Para evitar memory leaks
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }

    private void CreateNewDSO(String searchText) {
        // ID temporal del dso
        final long[] dsoID = {-1};

        // Inflar el layout personalizado
        View customView = LayoutInflater.from(this).inflate(R.layout.add_new_dso_in_map, null);

        // Configurar vistas
        LinearLayout llMain = customView.findViewById(R.id.llMain);
        LinearLayout llSecond = customView.findViewById(R.id.llSecond);
        EditText dsoRef = customView.findViewById(R.id.etRef);
        Spinner spCat = customView.findViewById(R.id.spCat);
        Spinner spCons = customView.findViewById(R.id.spConstelacion);
        Spinner spClasif = customView.findViewById(R.id.spClasificacion);
        Button btActions = customView.findViewById(R.id.btActions);
        Button btnNewMemory = customView.findViewById(R.id.btNewMemory);
        RatingBar ratingBar = customView.findViewById(R.id.RateBar);

        // Configurar valores iniciales
        dsoRef.setText(searchText);

        // Configurar adaptadores (mejorado para evitar recreación)
        setupSpinnerAdapter(spCat, db.getCatalogListArrayList());
        setupSpinnerAdapter(spCons, db.getConstellationsArraylist());
        setupSpinnerAdapter(spClasif, db.getClasses());

        // Crear y configurar el diálogo
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(customView)
                .setCancelable(true)
                .create();

        btActions.setOnClickListener(v -> {
            String ref = dsoRef.getText().toString().trim();

            if (ref.isEmpty()) {
                dsoRef.setError("La referencia no puede estar vacía");
                return;
            }

            String cat = spCat.getSelectedItem() != null ? spCat.getSelectedItem().toString() : "";
            String con = spCons.getSelectedItem() != null ? spCons.getSelectedItem().toString() : "";
            String clasif = spClasif.getSelectedItem() != null ? spClasif.getSelectedItem().toString() : "";
            String rate = String.valueOf(ratingBar.getRating());

            if (cat.isEmpty() || con.isEmpty() || clasif.isEmpty()) {
                Toast.makeText(this, "Seleccione todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            String tipo = db.getTypeFromClasification(clasif);

            // Guardamos los datos que necesitaremos luego
            final String finalCat = cat;
            final String finalRef = ref;
            final String finalCon = con;
            final String finalClasif = clasif;
            final String finalTipo = tipo;
            final String finalRate = String.valueOf(ratingBar.getRating());

            // Obtenemos las coordenadas
            obtenerCoordenadas(cat+ref, new getCoordinatesFromWeb.SesameCallback() {
                @Override
                public void onResult(String ra, String dec) {
                    // Una vez tenemos las coordenadas guardamos el objeto
                    dsoID[0] = db.NewEasyItemToDB3(finalCat, finalRef, "", "", finalCon, finalTipo,
                            finalClasif, "", "", ra, dec, finalRate);


                    progressDialog.dismiss();

                    runOnUiThread(() -> {
                        llMain.setEnabled(false);
                        llSecond.setEnabled(false);
                        btActions.setVisibility(View.GONE);
                        btnNewMemory.setVisibility(View.VISIBLE);
                    });
                }

                @Override
                public void onError(Exception e) {
                    dsoID[0] = db.NewEasyItemToDB3(finalCat, finalRef, "", "", finalCon, finalTipo,
                            finalClasif, "", "", "", "", finalRate);

                    progressDialog.dismiss();

                    runOnUiThread(() -> {
                        llMain.setEnabled(false);
                        llSecond.setEnabled(false);
                        btActions.setVisibility(View.GONE);
                        btnNewMemory.setVisibility(View.VISIBLE);
                    });
                }
            });
        });

        btnNewMemory.setOnClickListener(v -> {
            if (dsoID[0] != -1) {
                DSOActivity.CreateNewMemory(this, String.valueOf(dsoID[0]), db, () -> {
                    dialog.dismiss();
                    // Callback opcional después de cerrar
                });
            }
        });

        dialog.show();
    }

    // Modificar obtenerCoordenadas para aceptar callback
    private void obtenerCoordenadas(String objeto, getCoordinatesFromWeb.SesameCallback callback) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Obteniendo coordenadas...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new getCoordinatesFromWeb(callback).execute(objeto);
    }



    private void searchInDatabase(String text, SearchAdapter adapter) {
        // Ejecutar en un hilo secundario
        new Thread(() -> {
            // Aquí implementas tu búsqueda en SQLite
            Cursor cursor = db.getDSObyRef(text);
            List<BaseRow> results = Constructor.BaseRowToArrayList(cursor);
            cursor.close();

            // Determinar si debemos mostrar la opción "Agregar nuevo"
            boolean showAddNew = results.isEmpty() && !text.isEmpty();
            runOnUiThread(() -> adapter.updateData(results, showAddNew));
        }).start();
    }


}