package com.joserp.argonavis;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;

import android.util.Log;
import android.widget.TextView;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;


public class wiki_info_activity extends AppCompatActivity {

    private TextView TV_titulo, TV_info;
    private DBHelper db;
    private ArrayList ObjectInfo;
    private String info, cat, ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_wiki_info);

        String id_object = getIntent().getExtras().getString("ID");
        //db = new DBHelper(this);
        db = DBHelper.getInstance(this);

        ObjectInfo = db.getObjectInfo(id_object);

        TV_titulo = (TextView) findViewById(R.id.titulo);
        TV_info = (TextView) findViewById(R.id.texto);

        cat = ObjectInfo.get(1).toString();
        ref = ObjectInfo.get(2).toString();

        if (cat.equalsIgnoreCase("M")){
            cat = "Messier";//En la wikipedia el catalogo messier se busca como messier_13 y no M_13
        }
        TV_titulo.setText(cat + " " + ref);
        TV_info.setText("Buscando...");

        obtenerWeb();

    }

    private void obtenerWeb() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder();

                try {
                    Document document;
                    document = Jsoup.connect("https://es.wikipedia.org/wiki/" + cat + "_" + ref).get();
                    Elements paragraphs = document.select("p, h2");

                    for (Element p : paragraphs){
                        if (p.tagName().equalsIgnoreCase("h2")){
                            builder.append("\n");
                        }
                            builder.append(p.text());
                            builder.append("\n");
                        }


                } catch (IOException e) {
                    builder.append("Error, no se encontró la web:\n").append(e.getMessage()).append("\n");
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TV_info.setText(builder.toString());
                    }
                });
            }
        }).start();
    }
}