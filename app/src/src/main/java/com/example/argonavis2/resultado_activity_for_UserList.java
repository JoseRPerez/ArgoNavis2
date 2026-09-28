package com.joserp.argonavis;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class resultado_activity_for_UserList extends AppCompatActivity implements AdaptadorRecyclerUserList.OnListListener{

    private DBHelper db;

    private String name, UserListKey, TipoBusqueda;
    private Integer Scroll_Position;

    private TextView Titulo;
    private ListView lista;
    private ImageButton IB_Filtrar;

    RecyclerView listaReciclada;
    ArrayList lista_objects;

    ArrayList Constelaciones_posiciones;

    private Cursor cursor_objects;

    AdaptadorRecyclerUserList adaptadorRecycler;
    Context context =  this;


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
        IB_Filtrar = (ImageButton) findViewById(R.id.IB_filtrar);

        IB_Filtrar.setVisibility(View.INVISIBLE);

        name = getIntent().getExtras().getString("ListName");
        UserListKey = getIntent().getExtras().getString("ID_UserList");
        TipoBusqueda = getIntent().getExtras().getString("TipoBusqueda");

        //cambio size del titulo para que no aparezcan dos lineas en el titulo
        if (name.length() > 20){
            Titulo.setTextSize(20);
        }

        Titulo.setText(name);

        CargarLista();
    }

    private void vibrar(String duracion){
        VibrationHelper.vibrate(duracion);
    }


    @Override
    public void OnListClic(int position) {
        vibrar("estandar");

        Intent intent = new Intent(this, object_activity.class);
        String Obj_seleccionado = (String) ((Objeto) lista_objects.get(position)).ID;
        Scroll_Position = ((LinearLayoutManager) listaReciclada.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

        intent.putExtra("ID", Obj_seleccionado);
        startActivity(intent);
        }

    @Override
    public void OnListHeaderClic(int position) {
        vibrar("estandar");

        String Header_seleccionado = (String) lista_objects.get(position);
        ShowAvailableMaps(db.getConstelacionIDFromName(Header_seleccionado));
        Scroll_Position = ((LinearLayoutManager) listaReciclada.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

    }

    public void ShowAvailableMaps(String ConstelacionName){
        vibrar("estandar");

        //String Mapas = db.getMapsCalledForConstellation(ConstelacionName);

        String Mapas = db.getChartsInconstellation(db.getConstellationNameFromID(ConstelacionName));
        if (Mapas.equalsIgnoreCase("")){
            Mapas = db.getMapsCalledForConstellation(ConstelacionName);      //si no hay mapas metidos en la tabla constelacion entonces buscamos en todos los objetos de la lista
        }

        ArrayList<String> mapas = Common_functions.Comprobar_mapa(Mapas);

        if (mapas.size() != 0){
            openMap(mapas);
        } else {
            Toast.makeText(this, "No se han podido encontrar mapas", Toast.LENGTH_SHORT).show();
        }
    }


    public void openMap(ArrayList<String> mapas) {
        vibrar("estandar");

        if (mapas.size() == 1) {
            Intent i = new Intent(getApplicationContext(), object_image.class);
            i.putExtra("filepath", Common_functions.getFileURL("maps", mapas.get(0) + ".jpg"));
            startActivity(i);

        } else {
            AlertDialog.Builder options_maps = new AlertDialog.Builder(context);
            options_maps.setTitle("Escoja una opción");
            options_maps.setItems(mapas.toArray(new String[mapas.size()]), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent i2 = new Intent(getApplicationContext(), object_image.class);
                    i2.putExtra("filepath", Common_functions.getFileURL("maps", mapas.get(i) + ".jpg"));
                    startActivity(i2);
                }
            });

            AlertDialog builder = options_maps.create();
            builder.show();
        }
    }


    public void FondoNegro(int position){
        listaReciclada.findViewHolderForLayoutPosition(position).itemView.setBackgroundColor(context.getColor(R.color.negro));
    }


    @Override
    public void OnLongListClic(int position) {
        vibrar("estandar");

        //Scroll_Position = ((LinearLayoutManager) listaReciclada.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

        String Obj_seleccionado = (String) ((Objeto) lista_objects.get(position)).ID;
        String Obj_viewed = (String) ((Objeto) lista_objects.get(position)).getVisto();
        String Obj_toView = (String) ((Objeto) lista_objects.get(position)).getToView();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Escoja una opción");// add a list
        builder.setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        FondoNegro(position);
                    }
                }
        );

        if (TipoBusqueda.equals("Listas_Usuario")) {//layout de lista de usuario
            builder.setItems(new String[]{"(Des)Marcar como visto", "(Des)Marcar como pendiente", "Sacar de la Lista", "Wikipedia"}, new DialogInterface.OnClickListener() {
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
                        case 2:
                            AlertDialog.Builder builder = new AlertDialog.Builder(resultado_activity_for_UserList.this);
                            builder.setTitle("¿Sacar objeto de " + name + "?")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            vibrar("estandar");

                                            db.RemoveItemFromUserList(Obj_seleccionado, UserListKey);

                                            lista_objects.remove(position);
                                            adaptadorRecycler.notifyItemRemoved(position);
                                            FondoNegro(position);

                                        }
                                    });
                            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                    FondoNegro(position);
                                }
                            });
                            builder.setCancelable(false);


                            AlertDialog dialog_confimartion = builder.create();
                            dialog_confimartion.show();
                            break;
                        case 3:
                            Intent intent = new Intent(getApplicationContext(), wiki_info_activity.class);
                            String Obj_seleccionado = (String) ((Objeto) lista_objects.get(position)).ID;

                            Scroll_Position = ((LinearLayoutManager) listaReciclada.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

                            intent.putExtra("ID", Obj_seleccionado);
                            startActivity(intent);
                            break;
                    }
                }
            });

        } else { //layout de objetos pendientes
            builder.setItems(new String[]{"(Des)Marcar como visto", "Sacar de la Lista", "Wikipedia"}, new DialogInterface.OnClickListener() {
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
                            db.Add2View(Obj_seleccionado, "false");
                            lista_objects.remove(position);
                            adaptadorRecycler.notifyItemRemoved(position);
                            FondoNegro(position);
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

        }
        AlertDialog options_dialog = builder.create();
        options_dialog.show();
    }


    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
        RefreshList();
    }



    public void CargarLista(){
        switch (TipoBusqueda){
            case "Listas_Usuario":
                cursor_objects = db.getObjectsFromUserList(UserListKey);
                break;
            case "Lista_Rapida":
                cursor_objects = db.getToObserveList_cursor();
                break;
        }

        lista_objects = new ArrayList();
        Constelaciones_posiciones = new ArrayList();
        //Integer pos = -1;
        String OldCon = "";
        String con, cat, nombre, type, toview, visto, id;

        if (cursor_objects.getCount() != 0){ //la sentencia original era (cursor_objects != null) pero provoca error con cursores vacios
            cursor_objects.moveToFirst();  //{"CAT", "REF", "CON", "NAME", "TYPE", "CLASS", "TOVIEW", "VISTO", "_id"};

            do {
                //Integer con_id = Integer.valueOf(cursor_objects.getString(1));
                con = cursor_objects.getString(1);
                //if (pos != con_id){
                if (!OldCon.equalsIgnoreCase(con)){
                    //Creamos el item con el nombre de la constelacion
                    //String Name = db.getConstellationNameFromID(cursor_objects.getString(1));
                    //lista_objects.add(Name);
                    lista_objects.add(con);

                    //Creamos el item con la info del objeto ya en otra fila
                    //pos = con_id;
                    OldCon = con;
                }
                cat = cursor_objects.getString(0);
                nombre = cursor_objects.getString(2);
                type = cursor_objects.getString(3) + " (" + cursor_objects.getString(4) +")";
                toview = cursor_objects.getString(5);
                visto = cursor_objects.getString(6);
                id = cursor_objects.getString(7);

                Objeto obj = new Objeto(cat, nombre, type, toview, visto, id);
                lista_objects.add(obj);

            } while (cursor_objects.moveToNext());
            //Toast Num_Items =  Toast.makeText(getApplicationContext(), Integer.toString(cursor.getCount()) + " Elementos encontrados", Toast.LENGTH_SHORT);
        } else {
            Toast.makeText(getApplicationContext(), "La lista no contiene objetos", Toast.LENGTH_LONG).show();
        }
        cursor_objects.close();

        adaptadorRecycler = new AdaptadorRecyclerUserList(lista_objects, resultado_activity_for_UserList.this, this);
        listaReciclada.setAdapter(adaptadorRecycler);
        listaReciclada.setLayoutManager(new LinearLayoutManager(resultado_activity_for_UserList.this, LinearLayoutManager.VERTICAL,false));

        ((LinearLayoutManager) listaReciclada.getLayoutManager()).scrollToPosition(Scroll_Position);
    }



    public void RefreshList() {
        CargarLista();
    }


    public void back(View v){
        vibrar("estandar");
        finish();
    }

}