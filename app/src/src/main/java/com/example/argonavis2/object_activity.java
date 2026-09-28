package com.joserp.argonavis;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
//import android.support.v7.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class object_activity extends AppCompatActivity {

    private DBHelper db;
    final Context context = this;
    private TextView TV_Name, TV_Cons, TV_Tipo, TV_Clasif, TV_Size, TV_Mag, TV_Code, TV_Other;
    //private TextView TV_CARTA; TV_Memoria, TV_Notes;

    private EditText TV_CARTA, TV_Memoria, TV_Notes;
    private Button BTN_Name;
    private Switch S_Visto, S_Pendiente;
    //private Spinner S_ToList;
    private RatingBar RateBar;
    private ImageView Foto;
    private ImageButton MapaBtn, BtnEditMapa, BtnEditInfo, BtnEditMemo, MoveToList;

    private String ID;


    private ArrayList ObjectInfo;
    private ArrayList Listado;
    private ArrayList<String> mapas;

    private static String APP_PATH = "Argonavis";
    private static String PICS_PATH = "pics";
    private static String MAPS_PATH = "maps";

    private Boolean editcarta = false;
    private Boolean editinfo = false;
    private Boolean editmemo = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.object_layout_new);
        VibrationHelper.initialize(this);


        ID = getIntent().getExtras().getString("ID");

        //db = new DBHelper(this);
        db = DBHelper.getInstance(this);

        ObjectInfo = db.getObjectInfo(ID);

        TV_Code = (TextView) findViewById(R.id.TV_id);
        TV_Other = (TextView) findViewById(R.id.TV_other);
        TV_Name = (TextView) findViewById(R.id.TV_name);

        //TV_Notes = (TextView) findViewById(R.id.TV_info);
        TV_Notes = (EditText) findViewById(R.id.TV_info);

        S_Pendiente = (Switch) findViewById(R.id.S_Pendiente);
        S_Visto = (Switch) findViewById(R.id.S_Visto);

        //S_ToList = (Spinner) findViewById(R.id.SP_lista);

        //TV_CARTA = (TextView) findViewById(R.id.TV_carta);
        TV_CARTA = (EditText) findViewById(R.id.TV_carta);

        RateBar = (RatingBar) findViewById(R.id.RateBar);

        Foto = (ImageView) findViewById(R.id.IV_foto);
        MapaBtn = (ImageButton) findViewById(R.id.IB_Map);
        MoveToList = (ImageButton) findViewById(R.id.IB_MoveToList);

        BTN_Name = (Button) findViewById(R.id.BTN_Name);

        TV_Cons = (TextView) findViewById(R.id.TV_Constelacion);
        TV_Tipo = (TextView) findViewById(R.id.TV_Tipo);
        TV_Clasif = (TextView) findViewById(R.id.TV_Clasif);
        TV_Size = (TextView) findViewById(R.id.TV_Size);
        TV_Mag = (TextView) findViewById(R.id.TV_Magnitud);
        //TV_Memoria = (TextView) findViewById(R.id.TV_memoria);
        TV_Memoria = (EditText) findViewById(R.id.TV_memoria);

        BtnEditMapa = (ImageButton) findViewById(R.id.IB_editCarta);
        BtnEditInfo = (ImageButton) findViewById(R.id.IB_editInfo);
        BtnEditMemo = (ImageButton) findViewById(R.id.IB_editMemo);

        TV_CARTA.setEnabled(editcarta);
        TV_Notes.setEnabled(editinfo);
        TV_Memoria.setEnabled(editmemo);

/*
         //Cargamos el spiner con las listas de usuario
        Listado = db.getSavedLists();
        Listado.add(0,"-");
        ArrayAdapter<String> adapterUserList =  new ArrayAdapter<String>(this, R.layout.my_spinner, Listado);
        S_ToList.setAdapter(adapterUserList);
        //Fin carga

        S_ToList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                if (S_ToList.getSelectedItemPosition() != 0) {
                    db.AddItem2UserList(ID, S_ToList.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });
*/


        RateBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                db.UpdateRateItem(ID, String.valueOf(rating));
                RateBar.setRating(rating);
            }
        });

        MontarLayout();
    }


    private void vibrar(String duracion){
        VibrationHelper.vibrate(duracion);
    }


    private void Comprobar_imagen() {
        //Inhabilitar el boton y la imagen si no existe foto en carpeta
        String ref;

        ref = ObjectInfo.get(1).toString() + ObjectInfo.get(2).toString();
        File imgFile = new File(Common_functions.getFileURL("pics",ref + ".jpg"));

        if (!imgFile.exists()) {
            Foto.setVisibility(View.GONE);
            BTN_Name.setVisibility(View.GONE);
            TV_Name.setVisibility(View.VISIBLE);
        } else {
            Foto.setImageURI(Uri.fromFile(imgFile));
            Foto.setVisibility(View.VISIBLE);
            BTN_Name.setVisibility(View.VISIBLE);
            TV_Name.setVisibility(View.GONE);
        } //Fin Inhabilitar boton imagen*/
    }


    private void Comprobar_mapa() {
        String ref;

        ref = ObjectInfo.get(8).toString();

        //Miramos dentro del campo mapas y comprobamos cada opcion separada por comas
        for (String map: ref.split(",")){
            File imgFile = new File(Common_functions.getFileURL("maps", map.trim() + ".jpg" ));
            if (imgFile.exists()){
                mapas.add(map.trim());
            }
        }

        //Intentamos cargar el mapa por defecto, el comportamiento del mapa por defecto es similar
        //al de la foto del objeto

        String map = ObjectInfo.get(1).toString() + ObjectInfo.get(2).toString();
        File imgFile = new File(Common_functions.getFileURL("maps", map + ".jpg" ));
        if (imgFile.exists()){
            mapas.add(map);
        }

        if (mapas.size() == 0){
            //MapaBtn.setClickable(false);
            MapaBtn.setColorFilter(getResources().getColor(R.color.gris_oscuro));
        } else {
            //MapaBtn.setClickable(true);
            MapaBtn.setColorFilter(getResources().getColor(R.color.Naranja));
        }

    }


    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
        ObjectInfo = db.getObjectInfo(ID);
        MontarLayout();
    }


    public void MontarLayout(){
        mapas = new ArrayList<String>();
        Comprobar_imagen();
        Comprobar_mapa();

        BTN_Name.setText(ObjectInfo.get(4).toString());
        TV_Code.setText(ObjectInfo.get(1).toString() + " " + ObjectInfo.get(2).toString());
        TV_Other.setText(ObjectInfo.get(3).toString());
        TV_Name.setText(ObjectInfo.get(4).toString());

        TV_Notes.setText(ObjectInfo.get(11).toString());

        RateBar.setRating(Float.parseFloat(ObjectInfo.get(13).toString()));

        TV_CARTA.setText(ObjectInfo.get(8).toString());

        TV_Cons.setText(db.getConstellationNameFromID(ObjectInfo.get(5).toString()));
        TV_Tipo.setText(db.getType(ObjectInfo.get(6).toString()));
        TV_Clasif.setText(db.getClasificacion(ObjectInfo.get(7).toString()));
        TV_Size.setText(ObjectInfo.get(10).toString());
        TV_Mag.setText(ObjectInfo.get(9).toString());
        TV_Memoria.setText(ObjectInfo.get(12).toString());

        S_Visto.setChecked(Boolean.parseBoolean(ObjectInfo.get(15).toString()));
        S_Pendiente.setChecked(Boolean.parseBoolean(ObjectInfo.get(14).toString()));
    }


    public void EditThis(View v){
        vibrar("estandar");
        Intent i = new Intent(this, edit_item.class);
        i.putExtra("ID", ID);
        i.putExtra("Catalogo", ObjectInfo.get(1).toString());
        i.putExtra("Referencia", ObjectInfo.get(2).toString());
        i.putExtra("Other", ObjectInfo.get(3).toString());
        i.putExtra("Name", ObjectInfo.get(4).toString());
        i.putExtra("Constelacion", ObjectInfo.get(5).toString());
        i.putExtra("Clasificacion", ObjectInfo.get(7).toString());
        i.putExtra("Carta", ObjectInfo.get(8).toString());
        i.putExtra("Magnitud", ObjectInfo.get(9).toString());
        i.putExtra("Size", ObjectInfo.get(10).toString());
        i.putExtra("Info", ObjectInfo.get(11).toString());
        i.putExtra("Memoria", ObjectInfo.get(12).toString());
        i.putExtra("Finalidad de la funcion", "EditarObjeto");

        //startActivityForResult(i,2);
        startActivity(i);
    }


    public void ShowImage(View v) {
        vibrar("estandar");
        String code = ObjectInfo.get(1).toString() + ObjectInfo.get(2).toString();
        Intent i = new Intent(getApplicationContext(), object_image.class);
        i.putExtra("filepath", Common_functions.getFileURL(PICS_PATH, code + ".jpg"));
        startActivity(i);
    }

    public void CambiarColorAndSaveData( ImageButton boton, Boolean status, String Campo, EditText edittext){
        vibrar("estandar");
        if (status){
            boton.setImageResource(android.R.drawable.ic_menu_save);
            boton.setColorFilter(getResources().getColor(R.color.granate));
        } else {
            boton.setColorFilter(getResources().getColor(R.color.gris_oscuro));
            boton.setImageResource(android.R.drawable.ic_lock_idle_lock);
            SaveNewInfoField(Campo, edittext.getText().toString());
        }
    }

    public void SaveNewInfoField(String Campo, String NewInfo){
        db.updateDSOField( ID, Campo, NewInfo );
    }

    public void editCarta(View v){
        //alert_dialog(v, ObjectInfo.get(8).toString() ,"CARTA", "Cartas de Navegación");
        editcarta = editcarta ? false: true;
        TV_CARTA.setEnabled(editcarta);
        CambiarColorAndSaveData(BtnEditMapa, editcarta, "CARTA", TV_CARTA);

    }

    public void editInfo(View v){
        //alert_dialog(v, ObjectInfo.get(11).toString(), "NOTES", "Notas de Observación");
        editinfo = editinfo ? false: true;
        TV_Notes.setEnabled(editinfo);
        CambiarColorAndSaveData(BtnEditInfo, editinfo, "NOTES", TV_Notes);
    }

    public void editMemo(View v){
        //alert_dialog(v, ObjectInfo.get(12).toString(), "MEMORY", "Memoria de Observación");
        editmemo = editmemo ? false: true;
        TV_Memoria.setEnabled(editmemo);
        CambiarColorAndSaveData(BtnEditMemo, editmemo, "MEMORY", TV_Memoria);
    }


    public void Object2List(View v) {
        vibrar("estandar");
        // Inflate the custom layout for the dialog
        LayoutInflater inflater = getLayoutInflater();
        View customView = inflater.inflate(R.layout.dialog_with_spinner, null);

        // Obtener los datos del Spinner desde la base de datos
        List<String> datosSpinner = db.getSavedLists(); // Asume que tienes un método en dbHelper para obtener los datos

        // Find the elements in the custom layout
        //TextView dialogTitle = customView.findViewById(R.id.dialog_title);
        Spinner spinner = customView.findViewById(R.id.sp_listSelect);
        Button btnCancel = customView.findViewById(R.id.Btn_cancel);
        Button btnAgregar = customView.findViewById(R.id.Btn_add);

        // ... Set up the spinner with your data ...
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.my_spinner, datosSpinner); //para que se muestre con letras en rojo y mas grande
        spinner.setAdapter(adapter);

        // Create the AlertDialog.Builder and set the custom view
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(customView);

        // Show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // Configurar acciones para los botones
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss(); // Cierra el diálogo al hacer clic en "Cancelar"
            }
        });

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrar("estandar");
                String listaSeleccionada = spinner.getSelectedItem().toString();

                db.AddItem2UserList(ID, listaSeleccionada );
                // Mostramos un mensaje
                Toast.makeText(object_activity.this, "Agregado a la lista: " + listaSeleccionada, Toast.LENGTH_SHORT).show();
                alertDialog.dismiss(); // Cierra el diálogo al hacer clic en "Cancelar"
            }
        });
    }




    public void alert_dialog(View view, String text, String field, String Mensaje){
        final Dialog dialog = new Dialog(object_activity.this);
        dialog.setContentView(R.layout.dialog_rename_user_list);

        final EditText nombrelista = (EditText) dialog.findViewById(R.id.ET_ListName);
        final TextView titulo = (TextView) dialog.findViewById(R.id.tv1);
        titulo.setText(Mensaje);
        titulo.setHint("Nueva información");
        nombrelista.setText(text);
        final Button ok_button = (Button) dialog.findViewById(R.id.Btn_cancel);
        final Button cancel_button = (Button) dialog.findViewById(R.id.Btn_add);


        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = nombrelista.getText().toString();
                db.updateField(field, ID, text);

            }
        });
        dialog.show();
    }


    public void openMap(View v) {
        vibrar("estandar");
        if (mapas.size() == 1){ //si solo hay un mapa introducido se muestra el mapa
            Intent i = new Intent(getApplicationContext(), object_image.class);
            i.putExtra("filepath", Common_functions.getFileURL(MAPS_PATH, mapas.get(0) + ".jpg"));
            startActivity(i);

        } else if (mapas.size() == 0){  //si no hay ningun mapa introducido se muestran los indices
            String Mapas = db.getChartsInconstellation(db.getConstellationNameFromID(ObjectInfo.get(5).toString()));
            String[] listado_files = new String[] {};
            String Carpeta = "";
            if (Mapas.equalsIgnoreCase("")){    //si hay mapas metidos en la tabla Cons usaremos esos
                listado_files = Common_functions.getFilesInFolder("indices");   //en caso contrario usamos los indices
                Carpeta = "indices";
            } else {
                listado_files = Mapas.split(",");
                Carpeta = "maps";
            }

            //String[] listado_files = Common_functions.getFilesInFolder("indices");
            if (listado_files == null || listado_files.length == 0){
                Toast.makeText(this, "No hay elementos dentro de la carpeta Indices", Toast.LENGTH_SHORT).show();

            } else if (listado_files.length == 1){
                Intent i = new Intent(getApplicationContext(), object_image.class);
                i.putExtra("filepath", Common_functions.getFileURL("indices", listado_files[0]));

                startActivity(i);
            } else {
                AlertDialog.Builder options_maps = new AlertDialog.Builder(context);
                options_maps.setTitle("Escoja una opción");
                String[] lista = listado_files; //hago esta copia porque si no no puedo usar la de fuera
                String Folder = Carpeta;
                options_maps.setItems(listado_files, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent i2 = new Intent(getApplicationContext(), object_image.class);
                        String url = Common_functions.getFileURL(Folder, lista[i]);
                        if (Folder.equalsIgnoreCase("maps")){
                            url = url + ".jpg";
                        }
                        i2.putExtra("filepath", url);
                        startActivity(i2);
                    }
                });

                AlertDialog builder = options_maps.create();
                builder.show();
            }


        } else {
            AlertDialog.Builder options_maps = new AlertDialog.Builder(context);
            options_maps.setTitle("Escoja una opción");
            options_maps.setItems(mapas.toArray(new String[mapas.size()]), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String filepath =  Environment.getExternalStorageDirectory() + File.separator + APP_PATH + File.separator + MAPS_PATH + File.separator + mapas.get(i) + ".jpg";
                    Intent i2 = new Intent(getApplicationContext(), object_image.class);
                    i2.putExtra("filepath", filepath);
                    startActivity(i2);
                }
            });

            AlertDialog builder = options_maps.create();
            builder.show();
        }

    }


    public void Add2View(View v) {
        vibrar("corta");
        db.Add2View(ID, Boolean.toString(S_Pendiente.isChecked()));
    }


    public void Add2Viewed(View v) {
        vibrar("corta");
        db.Add2Viewed(ID, Boolean.toString(S_Visto.isChecked()));

    }


    public void Back(View v){
        vibrar("estandar");
        finish();
    }
}
