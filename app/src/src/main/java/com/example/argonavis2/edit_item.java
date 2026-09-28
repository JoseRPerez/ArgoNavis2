package com.joserp.argonavis;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

public class edit_item extends AppCompatActivity {

    private DBHelper db;
    private EditText et_name, et_size, et_mag, et_carta, et_info, et_memoria;
    private EditText et_refencia, et_other;

    private Spinner sp_constelaciones, sp_clasificacion;

    private String Catalogo, Referencia, Other, Clasificacion, Magnitud, Size, Memoria;
    private String ID, Name, Info, Carta, Constelacion, Finalidad;
    private ArrayList constelaciones, Tipos, Catalogos;
    private AutoCompleteTextView AC_CAT;
    private Button Btn_delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_item_layout);
        VibrationHelper.initialize(this);


        //db = new DBHelper(this);
        db = DBHelper.getInstance(this);

        Finalidad = getIntent().getExtras().getString("Finalidad de la funcion");

        Btn_delete = (Button) findViewById(R.id.bt_remove);

        if (Finalidad.equals("NuevoObjeto") ){
            Btn_delete.setVisibility(View.GONE);
        }

        ID = getIntent().getExtras().getString("ID");

        Catalogo = getIntent().getExtras().getString("Catalogo");
        Referencia = getIntent().getExtras().getString("Referencia");
        Other = getIntent().getExtras().getString("Other");
        Name = getIntent().getExtras().getString("Name");
        Constelacion = getIntent().getExtras().getString("Constelacion");
        Clasificacion = getIntent().getExtras().getString("Clasificacion");
        Carta = getIntent().getExtras().getString("Carta");
        Magnitud = getIntent().getExtras().getString("Magnitud");
        Size = getIntent().getExtras().getString("Size");
        Info = getIntent().getExtras().getString("Info");
        Memoria = getIntent().getExtras().getString("Memoria");

        AC_CAT = (AutoCompleteTextView) findViewById(R.id.AC_CAT);
        et_refencia = (EditText) findViewById(R.id.et_referencia);
        et_other = (EditText) findViewById(R.id.et_other);
        et_name = (EditText) findViewById(R.id.et_name);
        et_size = (EditText) findViewById(R.id.et_size);
        et_mag = (EditText) findViewById(R.id.et_mag);
        et_info = (EditText) findViewById(R.id.et_info);
        et_memoria = (EditText) findViewById(R.id.et_memoria);
        et_carta = (EditText) findViewById(R.id.et_carta);

        sp_clasificacion = (Spinner) findViewById(R.id.sp_clasificacion);
        sp_constelaciones = (Spinner) findViewById(R.id.sp_constelaciones);

        AC_CAT.setText(Catalogo);
        et_refencia.setText(Referencia);

        if (!Other.isEmpty()){
            et_other.setText(Other);
        }
        if (!Name.isEmpty()){
            et_name.setText(Name);
        }
        if (!Size.isEmpty()){
            et_size.setText(Size);
        }
        if (!Magnitud.isEmpty()){
            et_mag.setText(Magnitud);
        }
        if (!Info.isEmpty()){
            et_info.setText(Info);
        }
        if (!Memoria.isEmpty()){
            et_memoria.setText(Memoria);
        }
        if (!Carta.isEmpty()){
            et_carta.setText(Carta);
        }

        constelaciones = db.getConstellationsArraylist();
        Tipos = db.getTypeListArraylist();
        Catalogos = db.getCatalogListArrayList();

        //Cargamos el spiner
        ArrayAdapter<String> adapterCons = new ArrayAdapter<String>(this, R.layout.my_spinner, constelaciones);
        ArrayAdapter<String> adapterType =  new ArrayAdapter<String>(this, R.layout.my_spinner, Tipos);
        ArrayAdapter<String> adapterCat = new ArrayAdapter<String>(this, R.layout.textview_row, Catalogos);
        //Fin carga
        AC_CAT.setAdapter(adapterCat);
        sp_constelaciones.setAdapter(adapterCons);
        sp_clasificacion.setAdapter(adapterType);

        ////////////////////////////////////////////////////////////////////////////////////////////
        //HAY QUE HACER UNA FUNCION QUE A PARTIR DEL NOMBRE OBTENGA EL ID, LAS LINEAS DE ABABJO PUEDEN TENER PROBLEMA SI ALGUIEN CAMBIA UN OBJETO O AGREGA A LA TABLA CLASIFICACION
        sp_constelaciones.setSelection(Integer.valueOf(Constelacion) - 1); //la posicion 0 del spinner corresponde al _id = 1 de la BD
        sp_clasificacion.setSelection(Integer.valueOf(Clasificacion) - 1);

    }

    public void SaveChanges(View v){
        VibrationHelper.vibrate("estandar");
        androidx.appcompat.app.AlertDialog dialog2 = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("¿Guardar los cambios?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog2, int which) {
                        String Cat = AC_CAT.getText().toString();
                        //String const_id = String.valueOf(sp_constelaciones.getSelectedItemPosition() + 1);
                        //String class_id = String.valueOf(sp_clasificacion.getSelectedItemPosition() + 1);

                        String const_id = db.getConstellationIDFromName(sp_constelaciones.getSelectedItem().toString());
                        String class_id = db.getClassIDFromClassName(sp_clasificacion.getSelectedItem().toString());
                        String type_id = db.getTypeFromClasification(class_id);

                        if (Finalidad.equals("NuevoObjeto") ){
                            db.NewItemToDB(ID, Cat, et_refencia.getText().toString(), et_other.getText().toString(), et_name.getText().toString(), const_id, type_id, class_id, et_carta.getText().toString(), et_mag.getText().toString(), et_size.getText().toString(), et_info.getText().toString(), et_memoria.getText().toString());
                        } else {
                            db.UpdateInfoItem(ID, Cat, et_refencia.getText().toString(), et_other.getText().toString(), et_name.getText().toString(), const_id, type_id, class_id, et_carta.getText().toString(), et_mag.getText().toString(), et_size.getText().toString(), et_info.getText().toString(), et_memoria.getText().toString());

                        }
                        Intent i = getIntent();
                        i.putExtra("Name", et_name.getText().toString() );
                        i.putExtra("Info", et_info.getText().toString() );
                        i.putExtra("Memoria", et_memoria.getText().toString() );

                        //setResult(RESULT_OK, i);
                        finish();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create();
        dialog2.show();
    }

    public void DeleteItem(final View v){
        androidx.appcompat.app.AlertDialog dialog3 = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("¿Seguro que desea eliminar el objeto?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog3, int which) {
                        db.deleteItem(ID);
                        //Intent i = new Intent(v.getContext(), edit_item.class);
                        //startActivity(i);
                        Intent i = new Intent(v.getContext(), main_activity.class);
                        startActivityForResult(i,3);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create();
        dialog3.show();
    }

    public void Back(View v){
        finish();
    }

}
