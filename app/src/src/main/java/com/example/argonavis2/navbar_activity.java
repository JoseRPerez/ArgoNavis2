package com.joserp.argonavis;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

/**
 * Created by Jose on 29/08/2020.
 */



public class navbar_activity extends AppCompatActivity {

    DBHelper db;

    final Context context = this;

    private Switch spinnerDatos;
    private Switch spinnerNmes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navbar_layout);
        VibrationHelper.initialize(this);

        //db = new DBHelper(this);
        db = DBHelper.getInstance(this);

        spinnerDatos = (Switch) findViewById(R.id.S_mostrarDatos);
        spinnerNmes = (Switch) findViewById(R.id.S_nombresCons);

        Boolean data_mostrarDatos = getIntent().getExtras().getBoolean("mostrar_datos");
        Boolean tipo_nombres = getIntent().getExtras().getBoolean("tipo_nombres");

        spinnerDatos.setChecked(data_mostrarDatos);
        spinnerNmes.setChecked(tipo_nombres);
    }

    private void vibrar(String duracion){
        VibrationHelper.vibrate(duracion);
    }


    public void back(View v){
        /*Intent intent = new Intent();
        intent.putExtra("mostrarDatos", spinnerDatos.isChecked());
        finish();*/
        vibrar("estandar");

        Intent returnIntent = new Intent();
        //returnIntent.putExtra("mostrarDatos", String.valueOf(spinnerDatos.isChecked()) );
        returnIntent.putExtra("mostrarDatos", spinnerDatos.isChecked() );
        returnIntent.putExtra("tipo_nombres", spinnerNmes.isChecked() );

        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void ShowToViewList(View v){
        vibrar("estandar");
        Intent i = new Intent(getApplicationContext(), resultado_activity.class);
        String Selected_const = "Pendientes de Observar";
        String Selected_id_const = "";

        i.putExtra("Constelacion", Selected_const);
        i.putExtra("ID_CONS", Selected_id_const);
        i.putExtra("TipoBusqueda", "Lista_Rapida");

        startActivity(i);
        back(v);
    }

    public void ShowViewedList(View v){
        Intent i = new Intent(getApplicationContext(), resultado_activity.class);
        String Selected_const = "Objetos Observados";
        String Selected_id_const = "";

        i.putExtra("Constelacion", Selected_const);
        i.putExtra("ID_CONS", Selected_id_const);
        i.putExtra("TipoBusqueda", "Objetos_Observados");

        startActivity(i);
        back(v);
    }

    public void ShowHideData(View v){
        db.setAppData("DATA_VISIBLE", Boolean.toString(spinnerDatos.isChecked()));
        vibrar("corta");
    }

    public void ChangeNamesCons(View v){
        db.setAppData("NAMES", Boolean.toString((spinnerNmes.isChecked())));
        vibrar("corta");
    }


    public void NewUserList(View v){
        vibrar("estandar");
        final Dialog dialog = new Dialog(this.context);
        dialog.setContentView(R.layout.dialog_new_user_list2);

        final EditText nombrelista = (EditText) dialog.findViewById(R.id.ET_ListName);
        final Button ok_button = (Button) dialog.findViewById(R.id.Btn_cancel);
        final Button cancel_button = (Button) dialog.findViewById(R.id.Btn_add);
        final CheckBox seq_list = (CheckBox) dialog.findViewById(R.id.CB_setSequential);

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrar("estandar");
                String name = nombrelista.getText().toString();
                String seq= Boolean.valueOf(seq_list.isChecked()) ? "true" : "false";

                if ( name != null && !name.trim().isEmpty()) {//La cadena no esta vacia ni es nula
                    if (db.IsNameListInUserList(name)){//comprueba si la lista ya existe
                        Toast.makeText(getApplicationContext(),"La lista ya existe",Toast.LENGTH_LONG).show();
                    }
                    else {
                        db.AddNewUserList(name, seq);
                        Toast.makeText(getApplicationContext(),"Lista Creada",Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }

                } else {
                    Toast.makeText(getApplicationContext(),"Nombre no válido",Toast.LENGTH_LONG).show();
                }
            }
        });

        dialog.show();
    }

    public void openDialogChartConstellation(View v) {
        vibrar("estandar");
        final Dialog dialog = new Dialog(this.context);
        dialog.setContentView(R.layout.dialog_setchartsconstelation);
        final Spinner spinner = (Spinner)dialog.findViewById(R.id.SP_constelaciones);

        ArrayAdapter<String> adap = new ArrayAdapter<String>(this.context, R.layout.my_spinner, db.getConstellationsArraylist());
        spinner.setAdapter(adap);

        final Button bt_save = (Button) dialog.findViewById(R.id.Btn_save);
        final Button bt_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        final EditText et_charts = (EditText) dialog.findViewById(R.id.ET_charts);

        et_charts.setText(db.getChartsInconstellation(spinner.getSelectedItem().toString()));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                et_charts.setText(db.getChartsInconstellation(spinner.getItemAtPosition(i).toString()));
                }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        bt_save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                vibrar("estandar");
                db.UpdateConstellationCharts(spinner.getSelectedItem().toString(), et_charts.getText().toString());
                //Notificamos con un toast
                Context context = getApplicationContext();
                CharSequence text = "Datos guardados";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                //fin toast
            }
        });

        dialog.show();
    }

    public void DelUserList(View v){
        vibrar("estandar");
        final Dialog dialog = new Dialog(this.context);
        dialog.setContentView(R.layout.dialog_del_user_list);
        final Spinner spinner = (Spinner)dialog.findViewById(R.id.SP_UL);
        ArrayAdapter<String> adap = new ArrayAdapter<String>(this.context, R.layout.my_spinner, db.getSavedLists());
        spinner.setAdapter(adap);

        final Button ok_button_remove = (Button) dialog.findViewById(R.id.Btn_Ok_remove);
        final Button cancel_button2 = (Button) dialog.findViewById(R.id.btn_cancel2);

        cancel_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ok_button_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrar("estandar");
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("¿Estás seguro de eliminar " + spinner.getSelectedItem().toString() + "?");

                builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog2, int which) {
                        // Do nothing but close the dialog
                        db.RemoveUserList(spinner.getSelectedItem().toString());
                        dialog2.dismiss();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog2, int which) {
                        dialog2.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

                dialog.dismiss();
            }
        });

        dialog.show();
    }


    public void NewItem(View v) {
        vibrar("estandar");
        Intent i = new Intent(this.getApplicationContext(), edit_item.class);

        i.putExtra("ID", db.Find_Next_MaxID_in_Catalog());
        i.putExtra("Catalogo", "");
        i.putExtra("Other", "");
        i.putExtra("Name", "");
        i.putExtra("Constelacion", "1");
        i.putExtra("Clasificacion", "1");
        i.putExtra("Carta", "");
        i.putExtra("Magnitud", "");
        i.putExtra("Size", "");
        i.putExtra("Info", "");
        i.putExtra("Memoria", "");
        i.putExtra("Finalidad de la funcion", "NuevoObjeto");
        startActivity(i);
    }


    public void CreateBackup(View v){
        vibrar("estandar");
        db.close();
        try {
            ArchivarZIP arch = new ArchivarZIP();

            final String URL_BBDD = Common_functions.getFileURL("root","argonavisdb.sqlite");
            final String ZipFile = Common_functions.getZipFileURL();

            arch.Zippear(URL_BBDD, ZipFile);

            //Notificamos con un toast
            Context context = getApplicationContext();
            CharSequence text = "Creado Backup en /Argonavis/Backup";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            //fin toast

        } catch (Exception e) {
            e.printStackTrace();
            //Notificamos con un toast
            Context context = getApplicationContext();
            CharSequence text = "No se ha podido crear el archivo de respaldo";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }


    public void OpenIndices(View v) {
        vibrar("estandar");
        String[] listado_files = Common_functions.getFilesInFolder("indices");

        if (listado_files == null || listado_files.length == 0) {
            Toast.makeText(this, "No hay elementos dentro de la carpeta Indices", Toast.LENGTH_SHORT).show();
            //return;   //no falla este return pero tampoco hace nada
        } else if (listado_files.length == 1) {
            Intent i = new Intent(getApplicationContext(), object_image.class);
            i.putExtra("filepath", Common_functions.getFileURL("indices", listado_files[0]));

            startActivity(i);

        } else {
            AlertDialog.Builder options_maps = new AlertDialog.Builder(context);
            options_maps.setTitle("Escoja una opción");
            options_maps.setItems(listado_files, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent i2 = new Intent(getApplicationContext(), object_image.class);
                    i2.putExtra("filepath", Common_functions.getFileURL("indices", listado_files[i]));
                    startActivity(i2);
                }
            });

            AlertDialog builder = options_maps.create();
            builder.show();
        }
    }


    public void Recalcular(View v){
        vibrar("estandar");
        String msg = db.recalcular();
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();

    }
}
