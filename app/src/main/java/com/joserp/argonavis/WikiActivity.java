package com.joserp.argonavis;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;

import android.view.View;
import android.widget.TextView;

import com.joserp.argonavis.AuxFun.Common_functions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class WikiActivity extends AppCompatActivity {

    private TextView TV_info;
    private BaseRow dso;
    private String dsoCat, dsoRef;
    private FloatingActionButton saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_wiki_info);

        //prueba para ocultar barra navegacion android
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        dso = (BaseRow) getIntent().getExtras().getSerializable("DSO");

        dsoCat = dso.getCat();
        dsoRef = dso.getRef();

        TextView TV_titulo = findViewById(R.id.titulo);
        TV_info = findViewById(R.id.texto);

        if (dsoCat.equals("M")) {
            dsoCat = "Messier";//En la wikipedia el catalogo messier se busca como messier_13 y no M_13
        }

        String URLcode = getCode();

        TV_titulo.setText(dsoCat + " " + dsoRef);
        TV_info.setText("Buscando...");

        saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(view -> {
            String texto = TV_info.getText().toString();
            //Guardar en la base de datos
            DBHelper db = DBHelper.getInstance(this);
            db.saveWikiInfo(dso.getID(), texto);
            Common_functions.makeToast(this, "Información guardada");
        });

        obtenerWeb(URLcode);

    }

    private String getCode() {
        if (dso.getTipo().equals("Estrella")) {
            return getStarCode(); //En la wikipedia las estrellas se buscan como pi_andromedae y no pi_and
        } else {
            return dsoCat + "_" + dsoRef;
        }
    }

    private String getStarCode() {
        String[] info = dsoRef.split(" ");  //Se busca un patron pi And o 59 Eri

        if (info.length == 2){
            String starCode = info[0];
            String shortCon = info[1];

            switch (shortCon.toLowerCase()) {
                case "and": shortCon = "Andromedae"; break;
                case "ant": shortCon = "Antliae"; break;
                case "apu": shortCon = "Apodis"; break;
                case "aqr": shortCon = "Aquarii"; break;
                case "aql": shortCon = "Aquilae"; break;
                case "ara": shortCon = "Arae"; break;
                case "ari": shortCon = "Arietis"; break;
                case "aur": shortCon = "Aurigae"; break;
                case "boo": shortCon = "Bootis"; break;
                case "cae": shortCon = "Caeli"; break;
                case "cam": shortCon = "Camelopardi"; break;
                case "cnc": shortCon = "Cancri"; break;
                case "cvn": shortCon = "Canum Venaticorum"; break;
                case "cma": shortCon = "Canis Majoris"; break;
                case "cmi": shortCon = "Canis Minoris"; break;
                case "cap": shortCon = "Capricorni"; break;
                case "car": shortCon = "Carinae"; break;
                case "cas": shortCon = "Cassiopeiae"; break;
                case "cen": shortCon = "Centauri"; break;
                case "cep": shortCon = "Cephei"; break;
                case "cet": shortCon = "Ceti"; break;
                case "cha": shortCon = "Chamaeleontis"; break;
                case "cir": shortCon = "Circini"; break;
                case "col": shortCon = "Columbae"; break;
                case "com": shortCon = "Comae Berenices"; break;
                case "cra": shortCon = "Coronae Australis"; break;
                case "crb": shortCon = "Coronae Borealis"; break;
                case "crv": shortCon = "Corvi"; break;
                case "crt": shortCon = "Crateris"; break;
                case "cru": shortCon = "Crucis"; break;
                case "cyg": shortCon = "Cygni"; break;
                case "del": shortCon = "Delphini"; break;
                case "dor": shortCon = "Doradus"; break;
                case "dra": shortCon = "Draconis"; break;
                case "equ": shortCon = "Equulei"; break;
                case "eri": shortCon = "Eridani"; break;
                case "for": shortCon = "Fornacis"; break;
                case "gem": shortCon = "Geminorum"; break;
                case "gru": shortCon = "Gruis"; break;
                case "her": shortCon = "Herculis"; break;
                case "hor": shortCon = "Horologii"; break;
                case "hya": shortCon = "Hydrae"; break;
                case "hyi": shortCon = "Hydri"; break;
                case "ind": shortCon = "Indi"; break;
                case "lac": shortCon = "Lacertae"; break;
                case "leo": shortCon = "Leonis"; break;
                case "lmi": shortCon = "Leonis Minoris"; break;
                case "lep": shortCon = "Leporis"; break;
                case "lib": shortCon = "Librae"; break;
                case "lup": shortCon = "Lupi"; break;
                case "lyn": shortCon = "Lyncis"; break;
                case "lyr": shortCon = "Lyrae"; break;
                case "men": shortCon = "Mensae"; break;
                case "mic": shortCon = "Microscopii"; break;
                case "mon": shortCon = "Monocerotis"; break;
                case "mus": shortCon = "Muscae"; break;
                case "nor": shortCon = "Normae"; break;
                case "oct": shortCon = "Octantis"; break;
                case "oph": shortCon = "Ophiuchi"; break;
                case "ori": shortCon = "Orionis"; break;
                case "pav": shortCon = "Pavonis"; break;
                case "peg": shortCon = "Pegasi"; break;
                case "per": shortCon = "Persei"; break;
                case "phe": shortCon = "Phoenicis"; break;
                case "pic": shortCon = "Pictoris"; break;
                case "psc": shortCon = "Piscium"; break;
                case "psa": shortCon = "Piscis Austrini"; break;
                case "pup": shortCon = "Puppis"; break;
                case "pyx": shortCon = "Pyxidis"; break;
                case "ret": shortCon = "Reticuli"; break;
                case "sge": shortCon = "Sagittae"; break;
                case "sgr": shortCon = "Sagittarii"; break;
                case "sco": shortCon = "Scorpii"; break;
                case "scl": shortCon = "Sculptoris"; break;
                case "sct": shortCon = "Scuti"; break;
                case "ser": shortCon = "Serpentis"; break;
                case "sex": shortCon = "Sextantis"; break;
                case "tau": shortCon = "Tauri"; break;
                case "tel": shortCon = "Telescopii"; break;
                case "tri": shortCon = "Trianguli"; break;
                case "tra": shortCon = "Trianguli Australis"; break;
                case "tuc": shortCon = "Tucanae"; break;
                case "uma": shortCon = "Ursae Majoris"; break;
                case "umi": shortCon = "Ursae Minoris"; break;
                case "vel": shortCon = "Velorum"; break;
                case "vir": shortCon = "Virginis"; break;
                case "vol": shortCon = "Volantis"; break;
                case "vul": shortCon = "Vulpeculae"; break;
                default: shortCon = info[1];
            }
            return starCode + "_" + shortCon;
        } else {
            return dsoCat;
        }
    }

    private void obtenerWeb(String urlCode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder();

                try {
                    Document document;
                    document = Jsoup.connect("https://es.wikipedia.org/wiki/" + urlCode).get();
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