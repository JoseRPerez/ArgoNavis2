package com.joserp.argonavis.AuxFun;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.Toast;

import com.joserp.argonavis.DBHelper;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DownloadChartAVVSO extends AsyncTask<String, Void, String[]> {

    private Context context;

    private EditText TV_CARTA;
    private static String MAPS_PATH = "maps";
    private ArrayList<String> mapas;
    private DBHelper db;
    private static String DSO_id;



    public DownloadChartAVVSO(Context context, EditText TV_CARTA, ArrayList mapas, String DSO_id) {
        this.context = context;
        this.TV_CARTA = TV_CARTA;
        this.mapas = mapas;
        this.DSO_id = DSO_id;
        db = DBHelper.getInstance(context);

    }

    @Override
    protected String[] doInBackground(String... params) {
        boolean downloadSuccess = false;
        String starName = params[0];
        String formattedStarName = starName.replace(" ", "+");
        String jsonUrl = "https://app.aavso.org/vsp/api/chart/?format=json&fov=180&maglimit=12.0&star=" + formattedStarName + "&orientation=reversed&north=up&east=left";
        String imageFileName = "avvso_" + starName.trim() + ".png";

        HttpURLConnection jsonConnection = null;
        HttpURLConnection imageConnection = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            // Get JSON response from AAVSO API
            URL url = new URL(jsonUrl);
            jsonConnection = (HttpURLConnection) url.openConnection();
            jsonConnection.setRequestMethod("GET");
            jsonConnection.connect();

            //Read JSON Response
            String jsonResponse = new BufferedReader(new InputStreamReader(jsonConnection.getInputStream())).readLine();
            JSONObject json = new JSONObject(jsonResponse);
            String imageUrl = json.getString("image_uri");

            // Download image from the URL
            URL imageUri = new URL(imageUrl);
            imageConnection = (HttpURLConnection) imageUri.openConnection();
            imageConnection.connect();

            inputStream = imageConnection.getInputStream();
            File file = new File(Common_functions.getMapsPath(), imageFileName);
            outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            downloadSuccess = true;

        } catch (Exception e) {
            e.printStackTrace();
            return new String[] {"false", "Error: " + e.getMessage()};
        } finally {
            // Close resources in the finally block
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (jsonConnection != null) {
                jsonConnection.disconnect();
            }
            if (imageConnection != null){
                imageConnection.disconnect();
            }
        }

        return new String[]{String.valueOf(downloadSuccess), imageFileName};
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(context, "Descargando carta, esto puede llevar un tiempo...", Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onPostExecute(String[] result) {
        super.onPostExecute(result);

        if (Boolean.parseBoolean(result[0])) {
            Toast.makeText(context, "La carta se ha descargado en maps", Toast.LENGTH_LONG).show();

            String text = result[1] + "," + TV_CARTA.getText();
            TV_CARTA.setText(text);
            mapas.add(result[1]);
            db.updateField("Carta", DSO_id, text );

        } else {
            String errorMessage = result.length > 1 ? result[1] : "Error desconocido";
            Toast.makeText(context, "La carta no ha podido ser descargada: " + errorMessage, Toast.LENGTH_LONG).show();
        }
    }
}
