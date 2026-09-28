package com.joserp.argonavis;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.util.Locale;

// USAMOS SESAME PARA OBTENER LOS DATOS DE RA Y DEC DE UN OBJETO
// SE USA EN LA ACTIVIDAD EDIT_DSO Y EN CREAR NUEVO OBJETO

public class getCoordinatesFromWeb extends AsyncTask<String, Void, Void> {
    private String ra;
    private String dec;
    private Exception exception;
    private SesameCallback callback;

    public interface SesameCallback {
        void onResult(String ra, String dec);
        void onError(Exception e);
    }

    public getCoordinatesFromWeb(SesameCallback callback) {
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(String... params) {
        String dso = params[0];
        String urlStr = "https://cds.unistra.fr/cgi-bin/Sesame/-oxpI/SNV?" + dso;

        try {
            // Realizar la solicitud HTTP
            java.net.URL url = new java.net.URL(urlStr);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(15000); // 15 segundos
            conn.setReadTimeout(15000);    // 15 segundos

            // Leer la respuesta
            java.io.InputStream in = new java.io.BufferedInputStream(conn.getInputStream());
            String response = convertStreamToString(in);

            // Parsear el XML con JSoup (que ya tienes en tus dependencias)
            Document doc = Jsoup.parse(response, "", Parser.xmlParser());

            // Buscar las etiquetas jradeg y jdedeg
            Element jradeg = doc.select("jradeg").first();
            Element jdedeg = doc.select("jdedeg").first();

            if (jradeg != null && jdedeg != null) {
                Float raFloat = Float.parseFloat(jradeg.text()) * 24 / 360;

                this.ra = String.format(Locale.US, "%.8f", raFloat);
                this.dec = jdedeg.text();
            } else {
                throw new Exception("No se encontraron las coordenadas en la respuesta");
            }

        } catch (Exception e) {
            this.exception = e;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (exception != null) {
            callback.onError(exception);
        } else {
            callback.onResult(ra, dec);
        }
    }

    private String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}