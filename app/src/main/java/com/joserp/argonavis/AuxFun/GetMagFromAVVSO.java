package com.joserp.argonavis.AuxFun;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Html;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;


public class GetMagFromAVVSO extends AsyncTask<String, Void, String> {
    private Context context;
    private String StarName;

    public GetMagFromAVVSO(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... urls) {
        String result = "";
        StarName = urls[0];

        try {
            Document document = Jsoup.connect(urls[0]).get();
            Element table = document.select("table").first();

            if (table != null) {
                Elements tbody = table.select("tbody");
                if (!tbody.isEmpty()) {
                    Element firstRow = tbody.select("tr").first();
                    Elements cells = firstRow.select("td");

                    String[] date = cells.get(3).text().split("\\.");
                    String fecha = date[1] + " " + date[0].split(" ")[1] + " " + date[0].split(" ")[0];
                    String mag = cells.get(4).text();

                    result = "<b>Fecha:</b> " + fecha + "<br/><b>Magnitud:</b> " + mag;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = "Error al obtener datos";
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        dialog_info("Último registro en AAVSO", result);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(context, "La consulta puede llevar un tiempo", Toast.LENGTH_LONG).show();
    }


    private void dialog_info(String titulo, String mensaje) {
        // Crear el diálogo
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle(titulo);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setMessage(Html.fromHtml(mensaje, Html.FROM_HTML_MODE_LEGACY));
        }

        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Mostrar el diálogo
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }
}
