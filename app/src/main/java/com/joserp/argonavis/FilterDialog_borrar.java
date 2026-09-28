package com.joserp.argonavis;

import android.content.Intent;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Spinner;

import java.util.List;

public class FilterDialog_borrar extends AppCompatActivity {

    private DBHelper db;
    private Spinner SP_Tipo, SP_Clasificacion;
    private RatingBar RB_Stars;

    private String Constelacion_id;
    //private ArrayList List_Clasif;
    //private ArrayList List_Tipos ;
    List<String> List_Clasif;

    private ImageButton B_Aceptar;

    //private CustomDialogListener listener = new main_activity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_layout);

        //db = new DBHelper(this);
        db = DBHelper.getInstance(this);


        Constelacion_id = getIntent().getExtras().getString("Constelacion");

        //List_Tipos = db.get_DSO_TypeListArraylist();
        List<String> list_Tipos = db.getTypes();

        list_Tipos.add(0,"");

        RB_Stars = findViewById(R.id.RB);
        SP_Tipo = findViewById(R.id.SP_Tipo);
        SP_Clasificacion = findViewById(R.id.SP_Clasif);
        B_Aceptar = findViewById(R.id.IB_Accept);

        ArrayAdapter<String> adapterType =  new ArrayAdapter<String>(this, R.layout.textview_row, list_Tipos);

        SP_Tipo.setAdapter(adapterType);

        SP_Tipo.setSelection(0);
        RB_Stars.setRating(0);

        SP_Tipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

                if (SP_Tipo.getSelectedItemPosition() != 0){
                    //List_Clasif = db.getClassesFromType(String.valueOf(db.getTypeIDFromTypeName(SP_Tipo.getSelectedItem().toString())));
                    List_Clasif = db.fromTypeGetClasses(SP_Tipo.getSelectedItem().toString());
                    ArrayAdapter<String> adapterClass = new ArrayAdapter<String>(FilterDialog_borrar.this, R.layout.textview_row, List_Clasif);
                    SP_Clasificacion.setAdapter(adapterClass);
                    List_Clasif.add(0,"");
                    SP_Clasificacion.setSelection(0);
                } else {
                    //List_Clasif = db.get_Clasification_list();
                    List_Clasif = db.getClasses();
                    ArrayAdapter<String> adapterClass = new ArrayAdapter<String>(FilterDialog_borrar.this, R.layout.textview_row, List_Clasif);
                    SP_Clasificacion.setAdapter(adapterClass);
                    List_Clasif.add(0,"");
                    SP_Clasificacion.setSelection(0);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });

        //Inicializamos el listener

    }


    public void ToFilter(View v){
        Intent i = new Intent();
        //Nueva version base de datos con nombres y no con IDs
        i.putExtra("Tipo", SP_Tipo.getSelectedItem().toString());
        i.putExtra("Clasif", SP_Clasificacion.getSelectedItem().toString());
        i.putExtra("Rate", String.valueOf((int) RB_Stars.getRating()));
        //i.putExtra("Constelacion", Constelacion_id );     //no es necesario

        setResult(RESULT_OK, i);
        finish();
    }





    public void back(View v){
        //las siguientes tres lineas estaban comentadas porque no era necesario usar el onresult
        //pero de nuevo lo he implementado
        Intent i = getIntent();
        setResult(RESULT_CANCELED, i);
        finish();
    }


}
