package com.joserp.argonavis;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class main_activity extends AppCompatActivity {
    final private int REQUEST_CODE_ASK_PERMISSION=111;
    //DBHelper db;
    DBHelper db;

    private Cursor cursor;
    final Context context = this;

    private String ID, Tipo_Busqueda, Selected_list, Selected_item_name, Selected_item_id, Key;

    private ListView lista;
    SimpleCursorAdapter adapter;

    private EditText CampoBuscar;

    private ImageButton IB_DSO, IB_UserLists, IB_Buscar, IB_Filtrar, IB_NuevaLista;

    private TextView Titulo_ventana;

    private Integer Scroll_Position;
    private Integer Scroll_Position_offset;

    private Boolean all_constelaciones = false;
    private Boolean mostrar_datos, ConsNAME;

    //prueba para constelaciones ocultas
    private List<Integer> inViisibleConsIndexes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        VibrationHelper.initialize(this);

        solicitarPermiso();
        comprobarCarpetas();

        mostrar_datos = Boolean.parseBoolean(db.getAppData());//si es false no se muestran datos numericos
        ConsNAME = Boolean.parseBoolean(db.getTiposName());//si es false se muestran nombres alternativos

        Scroll_Position = 0;    //Indica la posicion del scroll a partir del cual se mostrara la lista
        Scroll_Position_offset = 0;    //Indica la posicion del scroll a partir del cual se mostrara la lista

        Tipo_Busqueda = "Por_Constelacion";

        CampoBuscar = (EditText) findViewById(R.id.ET_buscar);
        Titulo_ventana = (TextView) findViewById(R.id.TV_titulo);

        IB_DSO = (ImageButton) findViewById(R.id.IB_dso);
        IB_Buscar = (ImageButton) findViewById(R.id.IB_SearchObject);
        IB_UserLists = (ImageButton) findViewById(R.id.IB_UserLIsts);
        IB_Filtrar = (ImageButton) findViewById(R.id.IB_filtrar);
        IB_NuevaLista = (ImageButton) findViewById(R.id.IB_NewUserList);

        lista = (ListView) findViewById(R.id.LV_results);
        lista.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        CargarLista();



        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
                vibrar("estandar");
                setScrollersPosition(lista);

                if (Tipo_Busqueda.equals("Por_Nombre") || Tipo_Busqueda.equals("Filtrar_por_Nombre")){
                    Cursor itemValue = (Cursor) lista.getItemAtPosition(position);
                    Intent i = new Intent(getApplicationContext(), object_activity.class);
                    ID = itemValue.getString(1);

                    i.putExtra("ID", ID);

                    cerrar_teclado(view);
                    startActivity(i);

                } else if (Tipo_Busqueda.equals("Listas_Usuario")) {
                    Cursor itemValue = (Cursor) lista.getItemAtPosition(position);
                    //Intent i = new Intent(getApplicationContext(), resultado_activity_for_UserList.class);
                    Intent i;
                    Selected_item_name = itemValue.getString(0);
                    Selected_item_id = itemValue.getString(3);
                    String Selected_item_type = itemValue.getString(2);

                    switch (Selected_item_type){
                        case "true":   //Listas tipo secuenciales que se muestran como si fueran constelaciones
                            i = new Intent(getApplicationContext(), resultado_activity.class);
                            i.putExtra("Constelacion", Selected_item_name);
                            i.putExtra("ID_CONS", Selected_item_id);
                            i.putExtra("TipoBusqueda", Tipo_Busqueda);

                            break;
                        default:
                            i = new Intent(getApplicationContext(), resultado_activity_for_UserList.class);
                            i.putExtra("ListName", Selected_item_name);
                            i.putExtra("ID_UserList", Selected_item_id);
                            i.putExtra("TipoBusqueda", Tipo_Busqueda);

                            break;
                    }
                    startActivity(i);

                } else {  //se hace clic en una constelacion
                    if (all_constelaciones){
                        Cursor itemValue = (Cursor) lista.getItemAtPosition(position);

                        String cons_id = itemValue.getString(1); //la columna 2 del cursor es id
                        String status = itemValue.getString(2); //la columna 1 del cursor es visible

                        /*
                        int color = status.equals("0") ?  Color.RED : Color.GRAY;
                        ((TextView) view).setTextColor(color);

                        */
                        db.changeVisibleCons(cons_id, status.equals("1") ? "1" : "0");
                        CargarLista();

                    } else {
                        Cursor itemValue = (Cursor) lista.getItemAtPosition(position);

                        Intent i = new Intent(getApplicationContext(), resultado_activity.class);
                        Selected_item_name = itemValue.getString(0);
                        Selected_item_id = itemValue.getString(1);

                        i.putExtra("Constelacion", Selected_item_name);
                        i.putExtra("ID_CONS", Selected_item_id);
                        i.putExtra("TipoBusqueda", Tipo_Busqueda);

                        //CampoBuscar.clearFocus();
                        //cerrar_teclado(view);

                        startActivity(i);
                    }
                }
            }
        });


        //AL HACER LONG CLIC SOBRE UN ELEMENTO SE PREGUNTA SOBRE DISTINTAS ACCIONES
        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
                vibrar("estandar");
                setScrollersPosition(lista);

                view.setBackgroundColor(ContextCompat.getColor(context,R.color.gris_inactivo));
                final Cursor itemClicked = (Cursor) lista.getAdapter().getItem(position);
                final String List_ID = itemClicked.getString(1);


                if (Tipo_Busqueda.equals("Listas_Usuario")) {
                    //view.setBackgroundColor(ContextCompat.getColor(context,R.color.gris_inactivo));
                    //final Cursor itemClicked = (Cursor) lista.getAdapter().getItem(position);
                    //final String List_ID = itemClicked.getString(1);

                    final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Escoja una opción");// add a list
                    builder.setOnCancelListener(
                            new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialogInterface) {
                                    view.setBackgroundColor(ContextCompat.getColor(context,R.color.negro));
                                }
                            }
                    );
                    builder.setItems(new String[]{"Renombrar Lista", "Eliminar Lista"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    final Dialog rename_dialog = new Dialog(main_activity.this);
                                    rename_dialog.setContentView(R.layout.dialog_rename_user_list);

                                    rename_dialog.setOnCancelListener(
                                            new DialogInterface.OnCancelListener() {
                                                @Override
                                                public void onCancel(DialogInterface dialogInterface) {
                                                    view.setBackgroundColor(ContextCompat.getColor(context, R.color.negro)); //esta bien pero cuando en longclic no se seleccioana nada se queda marcado
                                                }
                                            }
                                    );

                                    final EditText nombrelista = (EditText) rename_dialog.findViewById(R.id.ET_ListName);
                                    final Button ok_button = (Button) rename_dialog.findViewById(R.id.Btn_cancel);
                                    final Button cancel_button = (Button) rename_dialog.findViewById(R.id.Btn_add);


                                    cancel_button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            rename_dialog.dismiss();
                                            view.setBackgroundColor(ContextCompat.getColor(context,R.color.negro)); //esta bien pero cuando en longclic no se seleccioana nada se queda marcado
                                        }
                                    });

                                    ok_button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String name = nombrelista.getText().toString();

                                            //Comprobacion cadena no esta vacia ni es nula
                                            if (name != null && !name.trim().isEmpty()) {
                                                db.RenameList(List_ID, name);
                                                rename_dialog.dismiss();
                                                CargarLista();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Nombre no válido", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                    rename_dialog.show();
                                    break;
                                case 1:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(main_activity.this);
                                    builder.setTitle("¿Eliminar Lista de Usuario?")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    db.RemoveUserListID(List_ID);
                                                    CargarLista();
                                                }
                                            });
                                    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialogInterface) {
                                            view.setBackgroundColor(ContextCompat.getColor(context,R.color.negro));
                                        }
                                    });
                                    AlertDialog dialog_confimartion = builder.create();
                                    dialog_confimartion.show();
                                    break;
                            }
                        }
                    });
                    AlertDialog options_dialog = builder.create();
                    options_dialog.show();
                }


                else if (Tipo_Busqueda.equals("Por_Nombre")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Escoja una opción");
                    builder.setOnCancelListener(
                            new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialogInterface) {
                                    view.setBackgroundColor(ContextCompat.getColor(context,R.color.negro));
                                }
                            }
                    );

                    builder.setItems(new String[] {"Marcar como visto", "Marcar como pendiente", "Wikipedia", "Añadir a Lista" ,"Eliminar"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    db.Add2Viewed(List_ID, "true");
                                    view.setBackgroundColor(ContextCompat.getColor(context,R.color.negro));
                                    break;
                                case 1:
                                    db.Add2View(List_ID, "true");
                                    view.setBackgroundColor(ContextCompat.getColor(context,R.color.negro));
                                    break;
                                case 4:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(main_activity.this);
                                    builder.setTitle("¿Eliminar objeto de la base de datos?")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    db.deleteItem(List_ID);

                                                    //cursor = db.BuscarObjByString_cursor(CampoBuscar.getText().toString());
                                                    //adapter.swapCursor(cursor);//esto actualiza el adpater con el cambio
                                                    CargarLista();
                                                }
                                            });
                                    builder.setNegativeButton("Cancelar", null);
                                    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialogInterface) {
                                            view.setBackgroundColor(ContextCompat.getColor(context, R.color.negro));
                                        }
                                    });

                                    AlertDialog dialog_confimartion = builder.create();
                                    dialog_confimartion.show();
                                    break;
                                case 2:
                                    Intent intent = new Intent(getApplicationContext(), wiki_info_activity.class);

                                    intent.putExtra("ID", List_ID);
                                    startActivity(intent);
                                    break;
                                case 3:
                                    cargarSpinner2(view, List_ID);
                            }
                        }
                    });
                    AlertDialog options_dialog = builder.create();
                    options_dialog.show();
                }
                return true;
            }
        });


        CampoBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CargarLista();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    // Dentro de MainActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                mostrar_datos = data.getBooleanExtra("mostrarDatos", false);
                ConsNAME = data.getBooleanExtra("tipo_nombres", false);
            }
        }
    }


    private void setScrollersPosition(ListView list){
        Scroll_Position = list.getFirstVisiblePosition();
        View v = list.getChildAt(0);
        Scroll_Position_offset = (v == null) ? 0 : (v.getTop() - list.getPaddingTop());
    }


    private void cargarSpinner2(View v, String selected_item_id) {
        final View view;
        view = v;
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
                view.setBackgroundColor(ContextCompat.getColor(context, R.color.negro));
            }
        });

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String listaSeleccionada = spinner.getSelectedItem().toString();

                db.AddItem2UserList(selected_item_id, listaSeleccionada );
                // Mostramos un mensaje
                Toast.makeText(main_activity.this, "Agregado a la lista: " + listaSeleccionada, Toast.LENGTH_SHORT).show();
                alertDialog.dismiss(); // Cierra el diálogo al hacer clic en "Cancelar"
            }
        });
    }


    private void comprobarCarpetas() {
        String[] folders_name = new String[] {"Backup", "maps", "pics","indices"};
        String url = Environment.getExternalStorageDirectory() + File.separator + "Argonavis";
        for (String folder : folders_name){
            File name_folder = new File(url, folder);
            if (!name_folder.exists()) {
                name_folder.mkdirs();
            }
        }
    }


    private void vibrar(String duracion){
        VibrationHelper.vibrate(duracion);
    }

    private void solicitarPermiso() {
        int permisoStorage = ActivityCompat.checkSelfPermission(main_activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permisoStorage != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSION);
            }
        } else {
            //db = DBHelper(this);  //forma antigua de acceder a la clase dbhelper sin singleton
            db = DBHelper.getInstance(this);
        }
    }


    private void abrir_teclado(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }


    private void cerrar_teclado(View view){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
        CargarLista();
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Salir de ArgoNavis")
                .setMessage("¿Estás seguro de que deseas salir?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        vibrar("estandar");
                        db.close();
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


    public void DSO_clic(View v) {
        Tipo_Busqueda = "Por_Constelacion";
        Titulo_ventana.setText("Constelaciones");
        Titulo_ventana.setVisibility(View.VISIBLE);
        IB_Filtrar.setVisibility(View.VISIBLE);
        IB_NuevaLista.setVisibility(View.GONE);
        CampoBuscar.setText("");

        IB_DSO.setColorFilter(getResources().getColor(R.color.granate));
        IB_UserLists.setColorFilter(getResources().getColor(R.color.gris_oscuro));
        IB_Buscar.setColorFilter(getResources().getColor(R.color.gris_oscuro));

        CampoBuscar.setVisibility(View.GONE);

        CargarLista();
        vibrar("estandar");
    }


    public void SearchOne(View v){
        Tipo_Busqueda = "Por_Nombre";

        Titulo_ventana.setVisibility(View.GONE);
        IB_Filtrar.setVisibility(View.VISIBLE);
        IB_NuevaLista.setVisibility(View.GONE);

        CampoBuscar.setVisibility(View.VISIBLE);
        CampoBuscar.setText("");

        CampoBuscar.requestFocus();

        //creo que en realidad es un incordio que se abra el teclado
        //abrir_teclado();

        IB_DSO.setColorFilter(getResources().getColor(R.color.gris_oscuro));
        IB_UserLists.setColorFilter(getResources().getColor(R.color.gris_oscuro));
        IB_Buscar.setColorFilter(getResources().getColor(R.color.granate));

        CargarLista();
        vibrar("estandar");
    }


    public void UserLists(View v) {
        Tipo_Busqueda = "Listas_Usuario";
        Titulo_ventana.setVisibility(View.VISIBLE);
        IB_Filtrar.setVisibility(View.GONE);
        IB_NuevaLista.setVisibility(View.VISIBLE);

        IB_DSO.setColorFilter(getResources().getColor(R.color.gris_oscuro));
        IB_UserLists.setColorFilter(getResources().getColor(R.color.granate));
        IB_Buscar.setColorFilter(getResources().getColor(R.color.gris_oscuro));
        Titulo_ventana.setText("Listas de Usuario");

        CampoBuscar.setVisibility(View.GONE);
        CargarLista();
        vibrar("estandar");
    }


    public void ShowToViewList(View v){
        Intent i = new Intent(getApplicationContext(), resultado_activity_for_UserList.class);

        i.putExtra("ListName", "Pendientes de Observar");
        i.putExtra("ID_UserList", "none");
        i.putExtra("TipoBusqueda", "Lista_Rapida");

        startActivity(i);
        vibrar("estandar");
    }


    public void CargarLista() {
        if (Tipo_Busqueda.equals("Por_Constelacion")){
            cursor = db.getConstellationsCursor(all_constelaciones, ConsNAME.toString());
        } else if (Tipo_Busqueda.equals("Por_Nombre")){
            cursor = db.BuscarObjByString_cursor(CampoBuscar.getText().toString());
        } else if (Tipo_Busqueda.equals("Listas_Usuario")){
            cursor = db.getSavedLists_cursor();
        } else if (Tipo_Busqueda.equals( "FiltrarConstelacion")){
            cursor = db.getConstellationsCursor_FilteredByString(CampoBuscar.getText().toString());
        } else if (Tipo_Busqueda.equals( "Filtrar_por_Nombre")){
            cursor = db.BuscarFilteredObjByString_cursor(CampoBuscar.getText().toString(), Key );
        }

        String[] from;
        int[] to;
        int layout;

        if (Tipo_Busqueda.equals("Por_Constelacion") && mostrar_datos){
            from = new String[]{"NAME","viewed", "dsoIn","_id"};
            to = new int[] {R.id.TV_rowlist, R.id.TV_vistos, R.id.TV_total};
            layout = R.layout.row_listview_cons;

        } else if (Tipo_Busqueda.equals("Listas_Usuario") && mostrar_datos){
            from = new String[] {"NAME", "dsoIn", "SEQ",  "_id"};
            to = new int[] {R.id.TV_name, R.id.TV_number};
            layout = R.layout.row_listview_userlists;

        } else {
            from = new String[]{"NAME","_id"};
            to = new int[] {R.id.TV_rowlist};
            layout = R.layout.row_listview;
        }

        //String[] from = new String[]{"NAME","_id"};
        //int[] to = new int[] {R.id.TV_rowlist};

        //adapter = new SimpleCursorAdapter(this, R.layout.row_listview, cursor, from, to, 0);
        adapter = new SimpleCursorAdapter(this, layout, cursor, from, to, 0);


        //prueba
        if (all_constelaciones && Tipo_Busqueda.equals("Por_Constelacion")){
            inViisibleConsIndexes.clear();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                int visibleValue = cursor.getInt(2); //la columna 2 es la columna visible
                if (visibleValue == 0){
                    inViisibleConsIndexes.add(cursor.getPosition());
                }
                cursor.moveToNext();
            }

            adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Cursor cursor, int Columnindex) {
                    if (inViisibleConsIndexes.contains((cursor.getPosition()))) { //la columna 2 es la columna Visible
                        ((TextView) view).setTextColor(Color.GRAY);
                    } else {
                        ((TextView) view).setTextColor(Color.RED);
                    }
                    return false;
                    }
            });
        }
        //fin prueba

        lista.setAdapter(adapter);
        //lista.setSelectionFromTop(Scroll_Position,Scroll_Position_offset);
        lista.setSelectionFromTop(Scroll_Position ,Scroll_Position_offset); //volviendo a esto

        db.close();
    }


    public void Filtrar(View v){
        if (Tipo_Busqueda.equals("Por_Constelacion")){
            /*
            // esto es lo que ha habido siempre y funciona. Filtra por nombre
            Titulo_ventana.setVisibility(View.GONE);
            CampoBuscar.setVisibility(View.VISIBLE);
            CampoBuscar.setText("");

            CampoBuscar.requestFocus();
            abrir_teclado();

            Tipo_Busqueda = "FiltrarConstelacion";
            */

            all_constelaciones = all_constelaciones ? false : true;
            CargarLista();
            vibrar("estandar");


        } else if (Tipo_Busqueda.equals("Por_Nombre") || Tipo_Busqueda.equals("Filtrar_por_Nombre")) {        //si se filtra desde la ventana de busqueda por nombre
            Tipo_Busqueda = "Filtrar_por_Nombre";
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Escoja una opción");
            builder.setCancelable(true);
            builder.setItems(new String[] {"Galaxia", "Nebulosa Planetaria", "Nebulosa" ,"Cúmulo Globular", "Cúmulo Abierto" , "Carbono/Doble" ,"Objeto Exótico", "Cualquiera"}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            Key = "TYPE = 1";
                            CargarLista();
                            break;
                        case 1:
                            Key = "CLASS = 11";
                            CargarLista();
                            break;
                        case 2:
                            Key = "TYPE = 3";
                            CargarLista();
                            break;
                        case 3:
                            Key = "CLASS = 8";
                            CargarLista();
                            break;
                        case 4:
                            Key = "CLASS = 9";
                            CargarLista();
                            break;
                        case 5:
                            Key = "TYPE = 4";
                            CargarLista();
                            break;
                        case 6:
                            Key = "TYPE = 5";
                            CargarLista();
                            break;
                        case 7:
                            Key = "TYPE LIKE \"%\"";
                            CargarLista();
                            break;
                    }
                }
            });
            AlertDialog options_dialog = builder.create();
            options_dialog.show();
        }
    }


    public void OpenNavBar(View v){
        Intent i = new Intent(getApplicationContext(), navbar_activity.class);
        i.putExtra("mostrar_datos", mostrar_datos);
        i.putExtra("tipo_nombres", ConsNAME );
        //startActivity(i);
        startActivityForResult(i, 1);
        vibrar("estandar");
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
                String name = nombrelista.getText().toString();
                //El siguiente codigo mira el estado del checkbox y si es true devuelve 1 y si esl false 0
                String seq = Boolean.valueOf(seq_list.isChecked()) ? "true" : "false";

                if ( name != null && !name.trim().isEmpty()) {//La cadena no esta vacia ni es nula
                    if (db.IsNameListInUserList(name)){//comprueba si la lista ya existe
                        Toast.makeText(getApplicationContext(),"La lista ya existe",Toast.LENGTH_LONG).show();
                    }
                    else {
                        db.AddNewUserList(name,seq);
                        Toast.makeText(getApplicationContext(),"Lista Creada",Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        CargarLista();
                    }

                } else {
                    Toast.makeText(getApplicationContext(),"Nombre no válido",Toast.LENGTH_LONG).show();
                }
            }
        });

        dialog.show();
    }
}