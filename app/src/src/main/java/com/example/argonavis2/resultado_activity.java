package com.joserp.argonavis;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class resultado_activity extends AppCompatActivity implements AdaptadorRecycler.OnListListener{

    private DBHelper db;

    private String name, id_name;
    private String Filtro_Tipo, Filtro_Clasif, Filtro_Rating;
    private Integer Scroll_Position;

    private TextView Titulo;
    private Cursor cursor_objects;

    private String Tipo_Busqueda;
    //private ArrayList<String> mapas;

    RecyclerView listaReciclada;
    ArrayList lista_objects;
    AdaptadorRecycler adaptadorRecycler;

    Context context =  this;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                CargarLista(false);
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    Filtro_Tipo = data.getExtras().getString("Tipo");
                    Filtro_Clasif = data.getExtras().getString("Clasif");
                    Filtro_Rating = data.getExtras().getString("Rate");
                    Tipo_Busqueda = "Filtrado";

                    CargarLista(false);
                }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resultado_layout);
        VibrationHelper.initialize(this);

        //db = new DBHelper(this);
        db = DBHelper.getInstance(this);

        Scroll_Position = 0;    //Indica la posicion del scroll a partir del cual se mostrara la lista

        listaReciclada = (RecyclerView) findViewById(R.id.RV_ObjectsList);
        Titulo = (TextView) findViewById(R.id.TV_titulo);

        name = getIntent().getExtras().getString("Constelacion");
        id_name = getIntent().getExtras().getString("ID_CONS");
        Tipo_Busqueda = getIntent().getExtras().getString("TipoBusqueda");


        //cambio size del titulo para que no aparezcan dos lineas en el titulo
        if (name.length() > 20) {
            Titulo.setTextSize(20);
        }

        Titulo.setText(name);

        CargarLista(false); //pensar si el toast para el numero de elementos es importante o debe quitarse. No esta implementado ahora
    }


    private void vibrar(String duracion){
        VibrationHelper.vibrate(duracion);
    }


    @Override
    public void OnListClic(int position) {
        vibrar("estandar");;

        Intent intent = new Intent(this, object_activity.class);
        String Obj_seleccionado = (String) ((Objeto) lista_objects.get(position)).ID;

        Scroll_Position = ((LinearLayoutManager) listaReciclada.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

        intent.putExtra("ID", Obj_seleccionado);
        startActivity(intent);
    }


    public void FondoNegro(int position){
        listaReciclada.findViewHolderForLayoutPosition(position).itemView.setBackgroundColor(context.getColor(R.color.negro));
    }


    @Override
    public void OnLongListClic(int position) {
        vibrar("estandar");;

        //Scroll_Position = ((LinearLayoutManager) listaReciclada.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

        String Obj_seleccionado = (String) ((Objeto) lista_objects.get(position)).ID;
        String Obj_viewed = (String) ((Objeto) lista_objects.get(position)).getVisto();
        String Obj_toView = (String) ((Objeto) lista_objects.get(position)).getToView();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Escoja una opción");
        builder.setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        //listaReciclada.findViewHolderForLayoutPosition(position).itemView.setBackgroundColor(context.getColor(R.color.negro));
                        FondoNegro(position);
                    }
                }
        );

        builder.setItems(new String[] {"(Des)Marcar como visto", "(Des)Marcar como pendiente", "Wikipedia" ,"Eliminar"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        String temp = String.valueOf(Boolean.valueOf(Obj_viewed) ? false : true);
                        db.Add2Viewed(Obj_seleccionado, temp);
                        ((Objeto) lista_objects.get(position)).Visto = temp;
                        adaptadorRecycler.notifyItemChanged(position);
                        FondoNegro(position);
                        break;
                    case 1:
                        String temp2 = String.valueOf(Boolean.valueOf(Obj_toView) ? false : true);
                        db.Add2View(Obj_seleccionado, temp2);

                        ((Objeto) lista_objects.get(position)).ToView = temp2;
                        adaptadorRecycler.notifyItemChanged(position);
                        FondoNegro(position);

                        break;
                    case 3:
                        AlertDialog.Builder builder = new AlertDialog.Builder(resultado_activity.this);
                        builder.setTitle("¿Eliminar objeto de la base de datos?")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        vibrar("estandar");;

                                        db.deleteItem(Obj_seleccionado);

                                        lista_objects.remove(position);
                                        adaptadorRecycler.notifyItemRemoved(position);
                                        FondoNegro(position);

                                    }
                                });
                        AlertDialog dialog_confimartion = builder.create();
                        dialog_confimartion.show();
                        break;
                    case 2:
                        Intent intent = new Intent(getApplicationContext(), wiki_info_activity.class);
                        String Obj_seleccionado = (String) ((Objeto) lista_objects.get(position)).ID;

                        Scroll_Position = ((LinearLayoutManager) listaReciclada.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

                        intent.putExtra("ID", Obj_seleccionado);
                        startActivity(intent);
                        break;
                }
            }
        });
        AlertDialog options_dialog = builder.create();
        options_dialog.show();
    }


    @Override
    public void onRestart() {//al agregar este metodo funciona que actualice la lista pero me ha cambiado la posicion del scroll
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
        CargarLista(false);
    }


    public void CargarLista(Boolean MostrarNotificacion) {//Toma un buleano para indicar si tiene que mostrar los elementos que hay en la lista. De momento no funciona
        switch (Tipo_Busqueda) {
            case "Por_Constelacion":
                cursor_objects = db.getObjectsInConstellation_cursor(id_name);
                break;
            case "FiltrarConstelacion":
                cursor_objects = db.getObjectsInConstellation_cursor(id_name);
                break;
            /*case "Listas_Usuario":
                cursor_objects = db.getObjectsFromUserList(id_name);
                break;
            case "Por_Nombre":
                //
                break;
            //case "Lista_Rapida":  //Se muestra con el recicleradapter mediante la clase resultado_activity_from_userlists
            //    cursor_objects = db.getToObserveList_cursor();
            //    break;
            */
            case "Listas_Usuario":
                cursor_objects = db.getObjectsFromUserList(id_name);
                break;
            case "Filtrado":
                /*if (id_name.equals("null")) {      //Cuando id_name ES null SIGNIFICA QUE EL USUARIO ESTA EN LISTA TO_VIEW
                    cursor_objects = db.getComplexSearchInToViewList(Filtro_Tipo, Filtro_Clasif, Filtro_Rating, "true");//OBSOLETO la lista toview no se filtra
                } else {
                    cursor_objects = db.getComplexSearch(id_name, Filtro_Tipo, Filtro_Clasif, Filtro_Rating);
                }*/
                cursor_objects = db.getComplexSearch(id_name, Filtro_Tipo, Filtro_Clasif, Filtro_Rating);
                break;


        }

        lista_objects = new ArrayList();

        if (cursor_objects.getCount() != 0) { //la sentencia original era (cursor_objects != null) pero provoca error con cursores vacios
            cursor_objects.moveToFirst();  //"CAT", "REF", "NAME", "TYPE", "CLASS", "TOVIEW", "VISTO", "_id"

            do {
                String cat_ref = cursor_objects.getString(0);
                String name = cursor_objects.getString(1);
                //String type = db.getType(cursor_objects.getString(3)) + " (" + db.getClasificacion(cursor_objects.getString(4)) + ")";
                String type = cursor_objects.getString(2) + " (" + cursor_objects.getString(3) + ")";
                String toview = cursor_objects.getString(4);
                String visto = cursor_objects.getString(5);
                String id = cursor_objects.getString(6);

                Objeto obj = new Objeto(cat_ref, name, type, toview, visto, id);
                lista_objects.add(obj);

            } while (cursor_objects.moveToNext());
            //El siguiente toast no funciona
            //Toast Num_Items = Toast.makeText(getApplicationContext(), Integer.toString(lista_objects.size()) + " Elementos encontrados", Toast.LENGTH_SHORT);
        } else {
            Toast.makeText(getApplicationContext(), "La lista no contiene objetos", Toast.LENGTH_LONG).show();
        }

        cursor_objects.close();//probar para optimizar memoria

        adaptadorRecycler = new AdaptadorRecycler(lista_objects, resultado_activity.this, this);
        listaReciclada.setAdapter(adaptadorRecycler);
        listaReciclada.setLayoutManager(new LinearLayoutManager(resultado_activity.this, LinearLayoutManager.VERTICAL, false));

        ((LinearLayoutManager) listaReciclada.getLayoutManager()).scrollToPosition(Scroll_Position);    //Actualizamos la posicion cuando se vuelve de la object_activity tras clic
    }



    public void OpenFilterActivity(View v){
        vibrar("estandar");;

        Intent i = new Intent(getApplicationContext(), filter_activity.class);
        i.putExtra("Constelacion", id_name);
        startActivityForResult(i,1);
    }


    public void back(View v){
        vibrar("estandar");;

        finish();
    }


    public void ShowAvailableMaps(View v){
        vibrar("estandar");;

        String Mapas = db.getChartsInconstellation(name);
        if (Mapas.equalsIgnoreCase("")){
            Mapas = db.getMapsCalledForConstellation(id_name);      //si no hay mapas metidos en la tabla constelacion entonces buscamos en todos los objetos de la lista
        }
        //ArrayList<String> mapas = Comprobar_mapa(Mapas);
        ArrayList<String> mapas = Common_functions.Comprobar_mapa(Mapas);

        if (mapas.size() != 0){
            openMap(mapas);
        } else {
            Toast.makeText(this, "No se han podido encontrar mapas", Toast.LENGTH_SHORT).show();
            return;
        }
    }


    public void openMap(ArrayList<String> mapas) {
        vibrar("estandar");;

        if (mapas.size() == 1){
            Intent i = new Intent(getApplicationContext(), object_image.class);
            i.putExtra("filepath", Common_functions.getFileURL("maps",mapas.get(0) + ".jpg" ));
            startActivity(i);

        } else {
            AlertDialog.Builder options_maps = new AlertDialog.Builder(context);
            options_maps.setTitle("Escoja una opción");
            options_maps.setItems(mapas.toArray(new String[mapas.size()]), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent i2 = new Intent(getApplicationContext(), object_image.class);
                    i2.putExtra("filepath", Common_functions.getFileURL("maps",mapas.get(i) + ".jpg" ));
                    startActivity(i2);
                }
            });

            AlertDialog builder = options_maps.create();
            builder.show();
        }

    }


}
