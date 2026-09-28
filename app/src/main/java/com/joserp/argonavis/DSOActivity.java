package com.joserp.argonavis;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.joserp.argonavis.AuxFun.Common_functions;
import com.joserp.argonavis.AuxFun.Constructor;
import com.joserp.argonavis.AuxFun.DownloadChartAVVSO;
import com.joserp.argonavis.AuxFun.GetMagFromAVVSO;
import com.joserp.argonavis.Defs.DSO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DSOActivity extends AppCompatActivity {
    private static DBHelper db;
    private static NestedScrollView scrollView;
    final Context context = this;
    private static TextView  cons, clasification, size, mag, title, desig, ar, dec;
    private static EditText chartET, notesET;
    private static TextView nameButton;
    private static RatingBar ratebar;
    private static ImageView photo;
    Toolbar toolbar;
    private RecyclerView randomList;
    private static String ID;
    private static DSO dso;
    private static ArrayList<String> mapas;
    private static ImageButton editMap, editinfo, avvso, downloadChart, btnDeepSearch, btnExpColGralInfo, btnExpColInfo;
    private static ImageButton btnExpFoto, vistoBtn, toviewBtn;
    private boolean editMapEnabled = false;
    private boolean editInfoEnabled = false;
    private static LinearLayout LLGRalInfo;
    private static CardView cardGralInfo, cardInfo, cardFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.object_layout);

        //prueba para ocultar barra navegacion android
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        ID = getIntent().getExtras().getString("ID");
        db = DBHelper.getInstance(context);

        scrollView = findViewById(R.id.SV);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        title = findViewById(R.id.tvTitle);

        photo = findViewById(R.id.IV_foto);
        nameButton = findViewById(R.id.name);

        ratebar = findViewById(R.id.RateBar);
        vistoBtn = findViewById(R.id.vistoBtn);
        toviewBtn = findViewById(R.id.toviewBtn);

        desig = findViewById(R.id.TV_Desig);
        clasification = findViewById(R.id.TV_Tipo);
        cons = findViewById(R.id.TV_Constelacion);

        btnDeepSearch = findViewById(R.id.IB_deepSearch);
        btnExpColGralInfo = findViewById(R.id.ib_expand_gral_info);
        btnExpColInfo = findViewById(R.id.ib_expand_info);
        btnExpFoto = findViewById(R.id.ib_expand_foto);


        editMap = findViewById(R.id.IB_editCarta);
        avvso = findViewById(R.id.ib_avvso);
        downloadChart = findViewById(R.id.IB_DownChart);

        size = findViewById(R.id.TV_Size);
        mag = findViewById(R.id.TV_Magnitud);
        ar = findViewById(R.id.TV_AR);
        dec = findViewById(R.id.TV_DEC);

        photo = findViewById(R.id.IV_foto);

        editinfo = findViewById(R.id.IB_editInfo);

        randomList = findViewById(R.id.RV_random);
        randomList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        chartET = findViewById(R.id.TV_carta);
        notesET = findViewById(R.id.TV_info);

        chartET.setEnabled(editMapEnabled);
        notesET.setEnabled(editInfoEnabled);

        LLGRalInfo = findViewById(R.id.LLgral_info);
        cardGralInfo = findViewById(R.id.cardGralInfo);
        cardInfo = findViewById(R.id.cardInfo);
        cardFoto = findViewById(R.id.foto);

        btnExpColGralInfo.setOnClickListener(view -> {
            Common_functions.vibrar("corta");
            if (LLGRalInfo.getVisibility() == GONE) {
                TransitionManager.beginDelayedTransition(cardGralInfo, new AutoTransition());
                LLGRalInfo.setVisibility(View.VISIBLE);
                btnExpColGralInfo.setImageResource(R.drawable.collapse);
            } else {
                TransitionManager.beginDelayedTransition(cardGralInfo, new AutoTransition());
                LLGRalInfo.setVisibility(GONE);
                btnExpColGralInfo.setImageResource(R.drawable.expand);
            }
        });

        btnExpColInfo.setOnClickListener(view -> {
            Common_functions.vibrar("corta");
            if (editinfo.getVisibility() == GONE) {
                TransitionManager.beginDelayedTransition(cardInfo, new AutoTransition());
                editinfo.setVisibility(View.VISIBLE);
                notesET.setVisibility(View.VISIBLE);
                btnExpColInfo.setImageResource(R.drawable.collapse);
            } else {
                TransitionManager.beginDelayedTransition(cardInfo, new AutoTransition());
                editinfo.setVisibility(GONE);
                notesET.setVisibility(GONE);
                btnExpColInfo.setImageResource(R.drawable.expand);
            }
        });

        btnExpFoto.setOnClickListener(view -> {
            Common_functions.vibrar("corta");
            if (photo.getVisibility() == GONE) {
                TransitionManager.beginDelayedTransition(cardFoto, new AutoTransition());
                photo.setVisibility(View.VISIBLE);
                btnExpColGralInfo.setImageResource(R.drawable.collapse);
            } else {
                TransitionManager.beginDelayedTransition(cardFoto, new AutoTransition());
                photo.setVisibility(GONE);
                btnExpColGralInfo.setImageResource(R.drawable.expand);
            }
        });


        ImageButton backButton = findViewById(R.id.ibBack);
        backButton.setOnClickListener(view -> {
            Common_functions.vibrar("corta");
            finish();
        });

        ImageButton btnRefreshRandomList = findViewById(R.id.IB_refresh);
        btnRefreshRandomList.setOnClickListener(view -> {
            Common_functions.vibrar("corta");
            PopulateRandomList();
        });

        btnDeepSearch.setOnClickListener(view -> {
            Common_functions.vibrar("corta");
            mapDeepSearch();
        });

        ratebar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                db.UpdateRateItem(ID, String.valueOf(rating));
                ratingBar.setRating(rating);
            }
        });

        photo.setOnClickListener(v -> ShowImage(v));


    }

    @Override
    protected void onResume() {
        super.onResume();
        //Para mostrar los cambios hechos en la pantalla de edicion
        BuildLayout(context);
        PopulateRandomList();
    }

    private void ShowImage(View v) {
        Common_functions.vibrar("estandar");
        Intent i = new Intent(getApplicationContext(), ImageActivity.class);
        i.putExtra("filepath", Common_functions.getPicUrl(dso.getCode()));
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflar el menú en la Toolbar
        getMenuInflater().inflate(R.menu.menu_object, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Common_functions.vibrar("corta");
        switch (item.getItemId()) {
            case R.id.openMap:
                openMap();
                return true;
            case R.id.openSkeye:
                openSkeye();
                return true;
            case R.id.newMemory:
                CreateNewMemory(context, ID, db, null); //paso el parametro para poder reutilizar la funcion en otras pantallas
                return true;
            case R.id.menu_view_memories:
                OpenMemories();
                return true;
            case R.id.menu_insert_in_list:
                Object2List();
                return true;
            case R.id.menu_edit_dso:
                EditThis();
                return true;
            case R.id.showWiki:
                openWiki();
                return true;
            case R.id.infoIA:
                openIA();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }



    public void getMagFromAVVSO(View view) {
        String star = dso.getRef();
        //String url = "https://app.aavso.org/webobs/results/?star=" + star.replace(" ", "+") + "&num_results=50";
        String url = "https://apps.aavso.org/v2/data/search/photometry/?target=" +
                star.replace(" ", "+") + "&start_date=&end_date=&observer=&obs_campaign=&submit=Search";

        GetMagFromAVVSO task = new GetMagFromAVVSO(context);
        task.execute(url);
    }

    public void downloadChartFromAVVSO(View v){
        Common_functions.vibrar("estandar");

        String StarName = dso.getRef();
        DownloadChartAVVSO task = new DownloadChartAVVSO(context, chartET, mapas, ID);
        task.execute(StarName);
    }



    private void openWiki() {
        Common_functions.vibrar("");
        Intent intent = new Intent(context, WikiActivity.class);
        intent.putExtra("DSO", dso);
        context.startActivity(intent);
    }

    private void openIA() {
        Common_functions.vibrar("");
        Intent intent = new Intent(context, ConsultaIA.class);
        intent.putExtra("DSO", dso);
        context.startActivity(intent);
    }




    public static void CreateNewMemory_old(Context context, String id, Runnable onSuccess) {
        // Crear un AlertDialog.Builder
        // Runnable es un callback, lo uso para actualizar la lista desde la actividad
        // de memories usando este metodo de DSOActivity.
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.dialogo);
        builder.setTitle("Nuevo Registro de Observación"); // Título del diálogo

        // Crear un EditText para que el usuario ingrese el texto
        final EditText input = new EditText(context);
        input.setTextColor(context.getResources().getColor(R.color.gris3));
        input.setTextSize(22);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        builder.setView(input); // Añadir el EditText al diálogo

        // Configurar el botón "Aceptar"
        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            String texto = input.getText().toString().trim(); // Obtener el texto ingresado
            if (!texto.isEmpty()) {
                // Guardamos el registro
                //db.saveNewMemory(id, Common_functions.getToday(), texto);
                if (onSuccess != null) {
                    onSuccess.run(); // <-- Ejecuta el callback después de guardar
                }
            } else {
                // Mostrar un mensaje si el texto está vacío
                Toast.makeText(context, "No se introdujo texto", Toast.LENGTH_SHORT).show();
            }
        });

        // Configurar el botón "Cancelar"
        builder.setNegativeButton("Cancelar", (dialog, which) -> {
            dialog.cancel(); // Cerrar el diálogo sin hacer nada
        });

        // Mostrar el diálogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    private void OpenMemories() {
        Common_functions.vibrar("estandar");
        Intent i = new Intent(this, MemoriesActivity.class);
        i.putExtra("ID", dso.getId());
        i.putExtra("Code", dso.getCode());
        startActivity(i);
    }

    private void openSkeye() {
        Common_functions.vibrar("corta");
        if (dso.hasCoordinates()) {
            safeLaunchSkEye(true,"");

        } else {
            // Pasamos identificador a skeye
            safeLaunchSkEye(false,"astro_object//any/" + dso.getCode());
        }
    }

    // The following two helper functions will help launch SkEye safely
    private void safeLaunchSkEye(boolean hasCoordenates, String objPath) {
        final Intent skEyeIntent = new Intent(hasCoordenates ? Intent.ACTION_VIEW : Intent.ACTION_SEARCH);
        final Uri.Builder uriBuilder = new Uri.Builder();

        if (hasCoordenates) {
            uriBuilder.scheme("skeye");
            skEyeIntent.putExtra("RA", Math.toRadians(dso.getAR()*15));
            skEyeIntent.putExtra("Declination", Math.toRadians(dso.getDEC()));
            skEyeIntent.setDataAndType(uriBuilder.build(), "text/astro_position");
        } else {
            uriBuilder.path(objPath);
            skEyeIntent.setDataAndType(uriBuilder.build(), "text/astro_object");
        }
        safeLaunchActivity(skEyeIntent, "SkEye v1.2 or higher");
    }

    private void safeLaunchActivity (Intent intent, String hint) {
        if (intent.resolveActivity(getPackageManager())!= null) {
            startActivity(intent);
        } else {
            // Aunque no se resuelva la actividad, intentamos lanzarla.
            // El sistema operativo podría ofrecer al usuario instalar la app si no está.
            startActivity(intent);
            Toast.makeText(this, "Please install " + hint, Toast.LENGTH_SHORT).show();
        }
    }



    private void openMap() {
        if (chartET.getText().toString().isEmpty()) {
            String maps = db.getChartsInconstellation(dso.getCons());
            ArrayList<String> mapList = Common_functions.getMapsPathIfExists(maps); // Obtener la lista de mapas
            //mapList contiene los filepath de los mapas

            if (mapList.isEmpty()) {
                Toast.makeText(this, "No hay mapas disponibles. Revise la carpeta maps.", Toast.LENGTH_SHORT).show();
            } else if (mapList.size() == 1) {
                showMap(mapList.get(0), dso.getID());
            } else {
                openMapsDialog(this, dso.getID(), mapList, this::showMap); // Uso de expresión lambda para mayor legibilidad
            }

        } else {
            if (mapas.size() == 1) {
                showMap(mapas.get(0), dso.getID());
            } else {
                openMapsDialog(this, dso.getID(), mapas, this::showMap);
            }
        }
    }

    // Metodo para obtener el valor seleccionado del Spinner
    public static String getSafeSpinnerValue(Spinner spinner) {
        return spinner.getSelectedItem() != null ? spinner.getSelectedItem().toString() : "";
    }

/*

    public static void CreateNewMemory(Context context, String id, DBHelper db, Runnable onSuccess) {
        if (context == null || db == null) return;

        // Ejecutar consulta de la BD en un hilo secundario (o usar Executor)
        new Thread(() -> {
            Map<String, List<String>> allData = db.getAllMemoryExtraData();
            Memory lastRecord = db.getLastMemory();

            List<String> places = allData != null && allData.containsKey("places") ? allData.get("places") : new ArrayList<>();
            List<String> telescopes = allData != null && allData.containsKey("telescopes") ? allData.get("telescopes") : new ArrayList<>();
            List<String> eyepieces = allData != null && allData.containsKey("eyepieces") ? allData.get("eyepieces") : new ArrayList<>();

            // Actualizar la UI en el hilo principal
            new Handler(Looper.getMainLooper()).post(() -> {
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.memory_layout, null);

                EditText registro = dialogView.findViewById(R.id.memory_text);
                RatingBar rateBarMap = dialogView.findViewById(R.id.RateBarMap);
                EditText spLugar = dialogView.findViewById(R.id.AC_lugar);
                EditText spTelescopio = dialogView.findViewById(R.id.AC_telescopio);
                EditText spOcular = dialogView.findViewById(R.id.AC_ocular);
                ImageButton btnAccept = dialogView.findViewById(R.id.btn_save);
                ImageButton btnTelescopio = dialogView.findViewById(R.id.btn_telescope);
                ImageButton btnLugar = dialogView.findViewById(R.id.btn_place);
                ImageButton btnOcular = dialogView.findViewById(R.id.btn_ocular);

                // Valores por defecto, los últimos usados
                if (lastRecord != null) {
                    if (lastRecord.telescope != null) spTelescopio.setText(lastRecord.telescope);
                    if (lastRecord.eyepiece != null) spOcular.setText(lastRecord.eyepiece);
                    if (lastRecord.place != null) spLugar.setText(lastRecord.place);
                }

                // Índices para el comportamiento cíclico
                final int[] telescopeIndex = {0};
                final int[] placeIndex = {0};
                final int[] eyepieceIndex = {0};

                // Sincronizar el índice buscando el texto exacto que ya aparece en el EditText
                if (!telescopes.isEmpty()) {
                    int idx = telescopes.indexOf(spTelescopio.getText().toString().trim());
                    if (idx != -1) {
                        telescopeIndex[0] = idx; // Si existe en la lista, empezamos en su posición actual
                    }
                }

                if (!places.isEmpty()) {
                    int idx = places.indexOf(spLugar.getText().toString().trim());
                    if (idx != -1) {
                        placeIndex[0] = idx;
                    }
                }

                if (!eyepieces.isEmpty()) {
                    int idx = eyepieces.indexOf(spOcular.getText().toString().trim());
                    if (idx != -1) {
                        eyepieceIndex[0] = idx;
                    }
                }

                // Listeners de los botones (mantienen la lógica cíclica intacta)
                btnTelescopio.setOnClickListener(v -> {
                    if (telescopes.isEmpty()) {
                        Toast.makeText(context, "No hay telescopios guardados", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    telescopeIndex[0] = (telescopeIndex[0] + 1) % telescopes.size();
                    spTelescopio.setText(telescopes.get(telescopeIndex[0]));
                });

                btnLugar.setOnClickListener(v -> {
                    if (places.isEmpty()) {
                        Toast.makeText(context, "No hay lugares guardados", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    placeIndex[0] = (placeIndex[0] + 1) % places.size();
                    spLugar.setText(places.get(placeIndex[0]));
                });

                btnOcular.setOnClickListener(v -> {
                    if (eyepieces.isEmpty()) {
                        Toast.makeText(context, "No hay oculares guardados", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    eyepieceIndex[0] = (eyepieceIndex[0] + 1) % eyepieces.size();
                    spOcular.setText(eyepieces.get(eyepieceIndex[0]));
                });


                AlertDialog dialogNewMemory = new AlertDialog.Builder(context)
                        .setView(dialogView)
                        .create();

                btnAccept.setOnClickListener(v -> {
                    String texto = registro.getText().toString().trim();
                    String telescopio = spTelescopio.getText().toString().trim();
                    String lugar = spLugar.getText().toString().trim();
                    String ocular = spOcular.getText().toString().trim();
                    String rate = String.valueOf(rateBarMap.getRating());

                    if (!texto.isEmpty()) {
                        // Guardar en hilo de fondo
                        new Thread(() -> {
                            //Guardamos el registro
                            db.saveNewMemory(id, Common_functions.getToday(), texto, lugar, telescopio, ocular);

                            new Handler(Looper.getMainLooper()).post(() -> {
                                dialogNewMemory.dismiss();
                                Common_functions.makeToast(context, "Registro Guardado");
                                if (onSuccess != null) {
                                    onSuccess.run(); // Ejecutamos la acción posterior si se definió
                                }
                            });
                        }).start();
                    }

                    if (rateBarMap.getRating() != 0) {
                        new Thread(() -> {
                            db.UpdateRateItem(id, rate);
                            // Actualizamos valor de rate en la ventana principal si se ha modificado el valor
                            ratebar.setRating(rateBarMap.getRating());
                        }).start();
                    }
                    dso.setVisto(true);
                    SetVistoIcon();
                    db.Add2Viewed(id, "true");
                    if (onSuccess != null) {
                        onSuccess.run(); // Ejecutamos la acción posterior si se definió
                    }
                });

                dialogNewMemory.show();
            });
        }).start();
    }

*/

    public static void CreateNewMemory(Context context, String id, DBHelper db, Runnable onSuccess) {
        if (context == null || db == null) return;

        // Ejecutar consulta inicial de la BD en un hilo secundario
        new Thread(() -> {
            Map<String, List<String>> allData = db.getAllMemoryExtraData();
            Memory lastRecord = db.getLastMemory();

            List<String> places = allData != null && allData.containsKey("places") ? allData.get("places") : new ArrayList<>();
            List<String> telescopes = allData != null && allData.containsKey("telescopes") ? allData.get("telescopes") : new ArrayList<>();
            List<String> eyepieces = allData != null && allData.containsKey("eyepieces") ? allData.get("eyepieces") : new ArrayList<>();

            // Actualizar la UI en el hilo principal
            new Handler(Looper.getMainLooper()).post(() -> {
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.memory_layout, null);

                EditText registro = dialogView.findViewById(R.id.memory_text);
                RatingBar rateBarMap = dialogView.findViewById(R.id.RateBarMap);
                EditText spLugar = dialogView.findViewById(R.id.AC_lugar);
                EditText spTelescopio = dialogView.findViewById(R.id.AC_telescopio);
                EditText spOcular = dialogView.findViewById(R.id.AC_ocular);
                ImageButton btnAccept = dialogView.findViewById(R.id.btn_save);
                ImageButton btnTelescopio = dialogView.findViewById(R.id.btn_telescope);
                ImageButton btnLugar = dialogView.findViewById(R.id.btn_place);
                ImageButton btnOcular = dialogView.findViewById(R.id.btn_ocular);

                // Cargamos el valor guardado del Rating
                    // Si accedemos desde mapa no existe dso pero si accedemos desde la vista de
                    // objeto si existe
                rateBarMap.setRating( db.getDSORateByID(id) );

                // rateBarMap.setRating(dso.getRate());

                // Valores por defecto, los últimos usados
                if (lastRecord != null) {
                    if (lastRecord.telescope != null) spTelescopio.setText(lastRecord.telescope);
                    if (lastRecord.eyepiece != null) spOcular.setText(lastRecord.eyepiece);
                    if (lastRecord.place != null) spLugar.setText(lastRecord.place);
                }

                // Índices para el comportamiento cíclico
                final int[] telescopeIndex = {0};
                final int[] placeIndex = {0};
                final int[] eyepieceIndex = {0};

                // Sincronizar el índice buscando el texto exacto que ya aparece en el EditText
                if (!telescopes.isEmpty()) {
                    int idx = telescopes.indexOf(spTelescopio.getText().toString().trim());
                    if (idx != -1) telescopeIndex[0] = idx;
                } else {
                    btnTelescopio.setVisibility(GONE);
                }

                if (!places.isEmpty()) {
                    int idx = places.indexOf(spLugar.getText().toString().trim());
                    if (idx != -1) placeIndex[0] = idx;
                } else {
                    btnLugar.setVisibility(GONE);
                }

                if (!eyepieces.isEmpty()) {
                    int idx = eyepieces.indexOf(spOcular.getText().toString().trim());
                    if (idx != -1) eyepieceIndex[0] = idx;
                } else {
                    btnOcular.setVisibility(GONE);
                }

                // Listeners de los botones cíclicos
                btnTelescopio.setOnClickListener(v -> {
                    telescopeIndex[0] = (telescopeIndex[0] + 1) % telescopes.size();
                    spTelescopio.setText(telescopes.get(telescopeIndex[0]));
                });

                btnLugar.setOnClickListener(v -> {
                    placeIndex[0] = (placeIndex[0] + 1) % places.size();
                    spLugar.setText(places.get(placeIndex[0]));
                });

                btnOcular.setOnClickListener(v -> {
                    eyepieceIndex[0] = (eyepieceIndex[0] + 1) % eyepieces.size();
                    spOcular.setText(eyepieces.get(eyepieceIndex[0]));
                });

                AlertDialog dialogNewMemory = new AlertDialog.Builder(context)
                        .setView(dialogView)
                        .create();

                // Botón de Guardar / Aceptar unificado
                btnAccept.setOnClickListener(v -> {
                    String texto = registro.getText().toString().trim();
                    String telescopio = spTelescopio.getText().toString().trim();
                    String lugar = spLugar.getText().toString().trim();
                    String ocular = spOcular.getText().toString().trim();
                    float ratingValue = rateBarMap.getRating();
                    String rate = String.valueOf(ratingValue);

                    // Validaciones iniciales
                    if (texto.isEmpty() && lugar.isEmpty() && ocular.isEmpty() && ratingValue == 0) {
                        Toast.makeText(context, "No hay información para guardar", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Ejecutamos el guardado de forma segura en un hilo secundario
                    new Thread(() -> {
                        // 1. Si hay texto, guardamos el registro completo de la memoria
                        if (!texto.isEmpty()) {
                            db.saveNewMemory(id, Common_functions.getToday(), texto, lugar, telescopio, ocular);
                        }

                        // 2. Si se modificó la puntuación, la actualizamos
                        if (ratingValue != 0) {
                            db.UpdateRateItem(id, rate);
                        }

                        // 3. Marcamos el objeto como visto en la base en cualquier caso
                        db.Add2Viewed(id, "true");

                        // 4. Actualizamos la UI en el hilo principal al terminar las operaciones de BD
                        new Handler(Looper.getMainLooper()).post(() -> {
                            dialogNewMemory.dismiss();

                            // Actualizar elementos visuales de la actividad principal de forma segura
                            if (ratingValue != 0 && ratebar != null) {
                                ratebar.setRating(ratingValue);
                            }

                            if (dso != null) {
                                dso.setVisto(true);
                                SetVistoIcon(); // Asumiendo que este metodo actualiza el icono en la UI
                            }

                            Common_functions.makeToast(context, "Información Guardada");

                            // Ejecutar la acción posterior una sola vez y de forma limpia
                            if (onSuccess != null) {
                                onSuccess.run();
                            }
                        });
                    }).start();
                });

                dialogNewMemory.show();
            });
        }).start();
    }

    public static void openMapsDialog(Context context, String dsoID, List<String> mapList, intoConstellationActivity.OnMapSelectedListener listener) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_grid, null);

        GridView gridView = dialogView.findViewById(R.id.gv_choices);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        // Configurar el adaptador para el GridView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.list_item, mapList);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedMap = mapList.get(position);
            listener.onMapSelected(selectedMap, dsoID); // Llamada al listener al seleccionar
        });

        dialog.show();
    }





    private void showMap(String map, String dsoID) {
        String mapPath = Common_functions.getMapUrl(map);
        if (mapPath != null) {
            Intent i = new Intent(getApplicationContext(), MapActivity.class);
            i.putExtra("filepath", mapPath);
            i.putExtra("filename", map);
            i.putExtra("dsoID", dsoID);
            startActivity(i);
        } else {
            Toast.makeText(this, "No se encuentra el mapa", Toast.LENGTH_SHORT).show();
        }
    }



    public static void BuildLayout(Context context) {
        dso = db.getDSOInfo(ID);

        mapas = new ArrayList<>(Arrays.asList(dso.getCarta().split("\\s*,\\s*")));   //separa cadena por comas ignorando espacios

        ComprobarImagen();
        ShowAVVSOButtonInfo();

        title.setText(dso.getCode());
        title.setSelected(true); // Esto activa el efecto marquesina

        ratebar.setRating(dso.getRate());
        desig.setText("Designación: " + dso.getDesig());
        cons.setText("Constelación: " + dso.getCons());
        size.setText("Tamaño Aparente: " + dso.getSize());
        mag.setText("Magnitud: " + dso.getMag());
        ar.setText("AR: " + dso.getAR());
        dec.setText("DEC: " + dso.getDEC());

        clasification.setText("Tipo: " + dso.getClassification());

        notesET.setText(dso.getInfo());
        chartET.setText(dso.getCarta());

        SetVistoIcon();
        SetToViewIcon(context);

    }



    private static void ShowAVVSOButtonInfo() {
        if (Objects.equals(dso.getTipo(), "Estrella")) {
            avvso.setVisibility(View.VISIBLE);
            downloadChart.setVisibility(View.VISIBLE);
        } else {
            avvso.setVisibility(GONE);
            downloadChart.setVisibility(GONE);
        }
    }


    private static void ComprobarImagen() {
        //Inhabilitar el boton y la imagen si no existe foto en carpeta
        String rutaIMG = Common_functions.getPicUrl(dso.getCode());

        if (rutaIMG == null && dso.getNombre().isEmpty() ){
            cardFoto.setVisibility(GONE);
        }

        if (rutaIMG == null && !dso.getNombre().isEmpty()){
            btnExpFoto.setVisibility(GONE);
            cardFoto.setVisibility(View.VISIBLE);
            nameButton.setText(dso.getNombre());
        }

        if (rutaIMG != null && dso.getNombre().isEmpty()){
            photo.setImageURI(Uri.parse(rutaIMG));
            btnExpFoto.setVisibility(View.VISIBLE);
            cardFoto.setVisibility(View.VISIBLE);
            nameButton.setVisibility(View.VISIBLE);
            nameButton.setText(dso.getCode());
        }

        if (rutaIMG != null && !dso.getNombre().isEmpty()) {
            photo.setImageURI(Uri.parse(rutaIMG));
            btnExpFoto.setVisibility(View.VISIBLE);
            cardFoto.setVisibility(View.VISIBLE);
            nameButton.setText(dso.getNombre());
        }
    }

    private void PopulateRandomList() {
        Cursor cursor = db.getTenRandom(ID);
        if (cursor != null) {
            ArrayList<BaseRow> dsoList = Constructor.BaseRowToArrayList(cursor);
            randomList.setAdapter(new SimpleRowItemAdapter(dsoList, context));
            randomList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        }
    }

    public void EditThis(){
        Common_functions.vibrar("corta");
        Intent i = new Intent(this, EditDSO.class);
        i.putExtra("Finalidad de la funcion", "EditarObjeto");
        i.putExtra("DSO", dso);
        startActivity(i);
    }

    public void CambiarColorAndSaveData( ImageButton boton, Boolean status, String Campo, EditText edittext){
        Common_functions.vibrar("");
        if (status){
            boton.setImageResource(R.drawable.save);
            boton.setColorFilter(getResources().getColor(R.color.granate));
        } else {
            //boton.setColorFilter(getResources().getColor(R.color.gris_oscuro));
            boton.clearColorFilter();
            boton.setImageResource(R.drawable.lock);
            SaveNewInfoField(Campo, edittext.getText().toString());
            dso.setField(Campo, edittext.getText().toString());
        }
    }


    public void SaveNewInfoField(String Campo, String NewInfo){
        db.updateDSOField(ID, Campo, NewInfo );
    }

    public void editCarta(View v){
        editMapEnabled = !editMapEnabled;
        chartET.setEnabled(editMapEnabled);
        CambiarColorAndSaveData(editMap, editMapEnabled, "CARTA", chartET);
        if (!editMapEnabled){
            mapas = new ArrayList<>(Arrays.asList(chartET.getText().toString().split("\\s*,\\s*")));   //separa cadena por comas ignorando espacios
        }

    }

    public void editInfo(View v){
        editInfoEnabled = !editInfoEnabled;
        notesET.setEnabled(editInfoEnabled);
        CambiarColorAndSaveData(editinfo, editInfoEnabled, "NOTES", notesET);
    }

    public void Object2List() {
        // Inflar el layout personalizado
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_with_spinner, null);

        // Obtener los datos del Spinner desde la base de datos
        List<String> datosSpinner = db.getSavedLists(); // Asume que tienes un metodo en dbHelper para obtener los datos

        // Configurar el Spinner
        Spinner spinner = dialogView.findViewById(R.id.sp_listSelect);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.textview_row, datosSpinner);
        adapter.setDropDownViewResource(R.layout.my_spinner);
        spinner.setAdapter(adapter);

        // Crear el AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Configurar los botones
        AlertDialog dialog = builder.create();

        ImageButton btnAccept = dialogView.findViewById(R.id.Btn_add);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el elemento seleccionado del Spinner
                String selectedList = (String) spinner.getSelectedItem();
                //Agregamos DSO a la lista
                db.AddItem2UserList(ID, selectedList);

                // Mostrar un Toast informativo
                Toast.makeText(context, "Agregado a la lista: " + selectedList, Toast.LENGTH_SHORT).show();

                dialog.dismiss(); // Cerrar el diálogo
            }
        });

        // Mostrar el diálogo
        dialog.show();
    }

    public void mapDeepSearch() {
        String maps = db.getMapsDeepSearch(ID);
        String cartas = dso.getCarta();

        if (!maps.isEmpty()) {
            if (!cartas.isEmpty()) {
                maps = maps + "," + cartas;
                maps = eliminarDuplicados(maps);
            }

            chartET.setText(maps);
            SaveNewInfoField("CARTA", maps);
            //actualizacmos el array maps
            mapas = new ArrayList<>(Arrays.asList(maps.split("\\s*,\\s*")));   //separa cadena por comas ignorando espacios
            Toast.makeText(this, "Mapas encontrados y agregados", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "No se encontraron mapas", Toast.LENGTH_SHORT).show();
        }
    }

    private String eliminarDuplicados(String nuevo) {
        // Convertir a array eliminando posibles espacios
        String[] elementos = nuevo.split(",");

        // Lista para almacenar el resultado sin duplicados
        List<String> listaNueva = new ArrayList<>();

        for (String elemento : elementos) {
            String item = elemento.trim(); // Eliminar espacios si los hubiera
            if (!item.isEmpty() && !listaNueva.contains(item)) {
                listaNueva.add(item);
            }
        }

        return String.join(",", listaNueva);
    }


    private static void SetVistoIcon() {
        if (dso.isVisto()) {
            vistoBtn.setImageResource(R.drawable.eye);
        } else {
            vistoBtn.setImageResource(R.drawable.no_visto);
        }
    }

    public void toggleVisto(View view) {
        dso.setVisto(!dso.isVisto());
        SetVistoIcon();
        db.Add2Viewed(ID, Boolean.toString( dso.isVisto()) );
    }

    public void togglePendiente(View view) {
        dso.setToView(!dso.isToView());
        SetToViewIcon(context);
        db.Add2View(ID, Boolean.toString( dso.isToView()) );
    }

    private static void SetToViewIcon(Context context) {
        if (dso.isToView()) {
            toviewBtn.setColorFilter(context.getResources().getColor(R.color.granate));
        } else {
            toviewBtn.setColorFilter(context.getResources().getColor(R.color.gris_oscuro));
        }
    }


    public static void loadNewDSO(String newID, Context context){
        ID = newID;
        BuildLayout(context);
        scrollView.smoothScrollTo(0,0);
    }

}
