package com.joserp.argonavis;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import com.joserp.argonavis.AuxFun.Common_functions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.HarmCategory;
import com.google.ai.client.generativeai.type.SafetySetting;
import com.google.ai.client.generativeai.type.BlockThreshold;
// Estas son para ListenableFuture y Callbacks
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.FutureCallback;
import com.google.ai.client.generativeai.GenerativeModel;
import io.noties.markwon.Markwon;

public class ConsultaIA extends AppCompatActivity {

    private TextView TV_info;
    private BaseRow dso;
    private String dsoCat, dsoRef, api_key, code, modelName ;
    private FloatingActionButton saveBtn;
    private Button Btn_ciencia, Btn_moreInfo;
    private GenerativeModelFutures generativeModel;
    private ExecutorService executorService; // Para ejecutar en background

    private static final String TYPE_SCIENCE = "ciencia";
    private static final String TYPE_MORE_INFO = "mas_info";
    private static final String TYPE_STANDARD = "standard";
    private Markwon markwon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ia);

        //prueba para ocultar barra navegacion android
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        // Inicializa el ExecutorService para tareas en segundo plano
        executorService = Executors.newSingleThreadExecutor();

        markwon = Markwon.create(this);

        //Inicialización de las vistas
        TextView TV_titulo = findViewById(R.id.titulo);
        TV_info = findViewById(R.id.texto); // Inicializar TV_info aquí
        saveBtn = findViewById(R.id.saveBtn);
        Btn_ciencia = findViewById(R.id.btn_ciencia);
        Btn_moreInfo = findViewById(R.id.btn_moreInfo);

        //Obtencion de datos
        dso = (BaseRow) getIntent().getExtras().getSerializable("DSO");

        //Preparando los datos
        dsoCat = dso.getCat();
        dsoRef = dso.getRef();
//        api_key = getKey();
        api_key =  IaConfigHelper.getApiKey(this);
        modelName = IaConfigHelper.getModelName(this);

        if (dsoCat.equals("M")) {
            dsoCat = "Messier";
        }

        code = getCode();

        //Configuracion UI inicial
        TV_titulo.setText(dsoCat + " " + dsoRef);
        TV_info.setText("Preguntando...");

        //Listeners
        saveBtn.setOnClickListener(view -> {
            String texto = TV_info.getText().toString();
            //Guardar en la base de datos
            DBHelper db = DBHelper.getInstance(this);
            db.saveWikiInfo(dso.getID(), texto);
            Common_functions.makeToast(this, "Información guardada");
        });

        Btn_ciencia.setOnClickListener(view -> {AIconnect(TYPE_SCIENCE);});

        Btn_moreInfo.setOnClickListener(view -> {AIconnect(TYPE_MORE_INFO);});

        //Llamada inicial a la API
        AIconnect(TYPE_STANDARD);
    }

//    private String getKey() {
//        DBHelper db = DBHelper.getInstance(this);
//        return db.getIAKey();
//
//    }

    private String getCode() {
        if (dso.getTipo().equals("Estrella")) {
            return Common_functions.getStarCode(dsoCat, dsoRef);
        } else {
            return dsoCat + " " + dsoRef;
        }
    }

    private void AIconnect(String type) {
        // --- Configuración del Modelo Gemini ---
        if (api_key == null || api_key.isEmpty() || api_key.equals("YOUR_API_KEY_HERE")) {
            TV_info.setText("Error: API Key no configurada. Introduzca su API key en el menú lateral." +
                    "\nPara conseguir una nueva API Key infórmese en Google AI Studio");
            return;
        }
        if (generativeModel == null) {
            iniciarModelo();
        }

        if (generativeModel == null) {
            TV_info.setText("Error al inicializar el servicio de IA.");
            Log.e("ArgoNavis IA", "generativeModel es null después de intentar iniciarModelo()");
        }

        getAnswer(type);
    }

    private void iniciarModelo() {
        try {
            List<SafetySetting> safetySettings = Collections.singletonList(
                    new SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.ONLY_HIGH)
            );

            //String apiKey = IaConfigHelper.getApiKey(this);
            //String modelName = IaConfigHelper.getModelName(this);

            if (!api_key.isEmpty()) {
                generativeModel = GenerativeModelFutures.from(new GenerativeModel(
                        modelName,
                        api_key,
                        null,
                        safetySettings
                ));
            }

        } catch (Exception e) {
            Log.e("ArgoNavis IA", "Error al inicializar GenerativeModelFutures", e);
            TV_info.setText("Error al inicializar el servicio de IA.");
            generativeModel = null; // Asegurar que esté nulo si falla
        }
    }


    private void getAnswer(String type) {
        String query = "";

        if (type.equals("ciencia")) {
            query = "Necesito datos astrofísicos, coordenadas, información científica rigurosa y especializada sobre el objeto " + code;
        } else if (type.equals("mas_info")) {
            query = "Necesito todo tipo de información sobre el objeto " + code;
        } else {
            query = "De manera no muy extensa necesito información astronómica, astrofísica, histórica, curiosidades y de observación visual del objeto " + code;
        }

        // Construye el contenido del prompt
        Content content = new Content.Builder().addText(query).build();

        // Antes de la llamada, poner "Cargando..." o similar
        TV_info.setText("Obteniendo información de Google Gemini...");


        ListenableFuture<GenerateContentResponse> responseFuture = generativeModel.generateContent(content);
        Futures.addCallback(responseFuture, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String responseText;
                try {
                    responseText = result.getText();
                    if (responseText == null || responseText.isEmpty()) {
                        // Comprobar si hay retroalimentación por bloqueo
                        if (result.getPromptFeedback() != null && result.getPromptFeedback().getBlockReason() != null) {
                            responseText = "Respuesta bloqueada por seguridad: " + result.getPromptFeedback().getBlockReason().name();
                        } else {
                            responseText = "Respuesta recibida pero sin contenido textual.";
                        }
                    }
                } catch (IllegalStateException e) {
                    Log.w("ArgoNavis IA", "IllegalStateException al extraer texto: " + e.getMessage());
                    if (result.getPromptFeedback() != null && result.getPromptFeedback().getBlockReason() != null) {
                        responseText = "Respuesta bloqueada por seguridad: " + result.getPromptFeedback().getBlockReason().name();
                    } else if (result.getCandidates() != null && !result.getCandidates().isEmpty()) {
                        responseText = "La respuesta de Gemini no contenía texto directamente interpretable.";
                    } else {
                        responseText = "Respuesta vacía o no válida de Gemini.";
                    }
                } catch (Exception e) {
                    Log.e("ArgoNavis IA", "Excepción inesperada al procesar la respuesta de Gemini.", e);
                    responseText = "Error al procesar la respuesta de Gemini.";
                }

                final String finalResponseText = responseText;
                HandlerCompat.createAsync(Looper.getMainLooper()).post(() -> {
                    //TV_info.setText(finalResponseText);
                    markwon.setMarkdown(TV_info, finalResponseText);
                });
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("ArgoNavis IA", "Error en la llamada a Gemini API (onFailure)", t);
                HandlerCompat.createAsync(Looper.getMainLooper()).post(() -> {
                    TV_info.setText("Error de conexión con el servicio de IA: " + t.getMessage());
                });
            }
        }, executorService);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown(); // O shutdownNow() si necesitas interrumpir tareas activas
        }
    }

}