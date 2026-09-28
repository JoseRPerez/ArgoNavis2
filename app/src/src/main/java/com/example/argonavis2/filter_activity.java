package com.joserp.argonavis;

import android.content.Intent;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.Spinner;

import java.util.ArrayList;

public class filter_activity extends AppCompatActivity {

    private DBHelper db;
    private Spinner SP_Tipo, SP_Clasificacion;
    private RatingBar RB_Stars;

    private String Constelacion_id;
    private ArrayList List_Tipos, List_Clasif;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_layout);

        //db = new DBHelper(this);
        db = DBHelper.getInstance(this);

        Constelacion_id = getIntent().getExtras().getString("Constelacion");

        List_Tipos = db.get_DSO_TypeListArraylist();

        List_Tipos.add(0,"-");

        RB_Stars = (RatingBar) findViewById(R.id.RB);
        SP_Tipo = (Spinner) findViewById(R.id.SP_Tipo);
        SP_Clasificacion = (Spinner) findViewById(R.id.SP_Clasif);

        ArrayAdapter<String> adapterType =  new ArrayAdapter<String>(this, R.layout.my_spinner, List_Tipos);

        SP_Tipo.setAdapter(adapterType);

        SP_Tipo.setSelection(0);
        RB_Stars.setRating(0);

        SP_Tipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

                if (SP_Tipo.getSelectedItemPosition() != 0){
                    List_Clasif = db.getClassesFromType(String.valueOf(db.getTypeIDFromTypeName(SP_Tipo.getSelectedItem().toString())));
                    ArrayAdapter<String> adapterClass = new ArrayAdapter<String>(filter_activity.this, R.layout.my_spinner, List_Clasif);
                    SP_Clasificacion.setAdapter(adapterClass);
                    List_Clasif.add(0,"-");
                    SP_Clasificacion.setSelection(0);
                } else {
                    List_Clasif =db.get_Clasification_list();
                    ArrayAdapter<String> adapterClass = new ArrayAdapter<String>(filter_activity.this, R.layout.my_spinner, List_Clasif);
                    SP_Clasificacion.setAdapter(adapterClass);
                    List_Clasif.add(0,"-");
                    SP_Clasificacion.setSelection(0);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });
    }

    public void ToFilter(View v){
        Intent i = getIntent();
        i.putExtra("Tipo", db.getTypeIDFromTypeName(SP_Tipo.getSelectedItem().toString()));
        i.putExtra("Clasif", db.getClassIDFromClassName(SP_Clasificacion.getSelectedItem().toString()));
        i.putExtra("Rate", String.valueOf((int) RB_Stars.getRating()));
        i.putExtra("Constelacion",Constelacion_id);

        setResult(RESULT_OK, i);
        finish();
    }

    public void back(View v){
        //Intent i = getIntent();
        //i.putExtra("key", Constelacion_id);
        //setResult(RESULT_CANCELED, i);
        finish();
    }
}
