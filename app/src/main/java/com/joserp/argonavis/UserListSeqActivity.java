package com.joserp.argonavis;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.joserp.argonavis.AuxFun.Common_functions;
import com.joserp.argonavis.AuxFun.Constructor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class UserListSeqActivity extends AppCompatActivity {
    private int scrollPosition, scrollOffset;
    RecyclerView recyclerView;
    private ItemTouchHelper itemTouchHelper;
    UserListSeqAdapter adaptadorRecycler;
    Context context = this;
    private LinearLayoutManager layoutManager;
    DBHelper db;
    Toolbar toolbar;
    TextView title;
    private static String name;
    private String UserListID;

    private boolean inOrderMode = false;

    private List<dsoRow> dsosList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);

        //prueba para ocultar barra navegacion android
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        name = getIntent().getExtras().getString("ListName");
        UserListID = getIntent().getExtras().getString("ID_UserList");

        scrollOffset = 0;
        scrollPosition = RecyclerView.NO_POSITION;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        title = findViewById(R.id.tvTitle);
        title.setText(name);
        //title.setSelected(true); // Necesario para que funcione la marquesina aunque en realidad no funciona

        recyclerView = findViewById(R.id.rvList);

        db = DBHelper.getInstance(this);

        ImageButton backButton = findViewById(R.id.ibBack);
        backButton.setOnClickListener(view -> {
            Common_functions.vibrar("corta");
            finish(); // Cierra la actividad actual.
        });

        // Inicializar el LayoutManager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (savedInstanceState != null) {
            scrollPosition = savedInstanceState.getInt("scrollPosition", 0);
            scrollOffset = savedInstanceState.getInt("scrollOffset", 0);// Restaura la posición y el desplazamiento del scroll
        }

        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                // Habilitar el movimiento hacia arriba y hacia abajo
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                return makeMovementFlags(dragFlags, 0);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                // Obtener el adaptador y mover el elemento
                UserListSeqAdapter adapter = (UserListSeqAdapter) recyclerView.getAdapter();
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                adapter.moveItem(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // No se necesita implementar esto ya que no estamos habilitando el deslizamiento
            }

            @Override
            public boolean isLongPressDragEnabled() {
                // Deshabilitar el arrastre con una pulsación larga, ya que queremos controlarlo con el botón
                return inOrderMode;
            }
        };

        itemTouchHelper = new ItemTouchHelper(callback);

        //cargar los datos iniciales
        //populateList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflar el menú en la Toolbar
        getMenuInflater().inflate(R.menu.menu_user_list_seq, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shareList:
                shareList();
                return true;
            case R.id.orderList:
                inOrderMode = !inOrderMode;
                if (inOrderMode) {
                    item.setTitle("Guardar Orden");
                    title.setText("Reordenar Lista");
                    // Habilitar el ItemTouchHelper
                    itemTouchHelper.attachToRecyclerView(recyclerView);
                } else {
                    item.setTitle("Reordenar");
                    title.setText(name);
                    // Deshabilitar el ItemTouchHelper
                    itemTouchHelper.attachToRecyclerView(null);
                    saveNewOrder();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public static File getFileUrl(String listName){
        return new File(Common_functions.getArgonavisFolder(), listName + "_exportada_" + Common_functions.getCurrentDateTime() + ".csv");
    }

    public void shareList(){
        //funcion asincrona para exportar listas de usuario
        Common_functions.vibrar("");

        new Thread(() -> {
            try {
                // Obtener la lista de datos a exportar
                ArrayList<String> exportList = db.getDataListToExport(UserListID);

                // Crear archivo CSV
                File file = getFileUrl(name);

                if (!file.exists()) {
                    file.createNewFile();
                }

                // Usar FileOutputStream para escribir en el archivo
                FileOutputStream fos = new FileOutputStream(file);

                // Escribir el nombre de la lista y un indicador "false"
                String firstLine = name + ",false\n";
                fos.write(firstLine.getBytes());

                // Escribir cada elemento de la lista en una línea
                for (String item : exportList) {
                    String line = item + "\n";
                    fos.write(line.getBytes());
                }

                // Cerrar el flujo
                fos.close();

                runOnUiThread(() -> Toast.makeText(this, "Lista exportada en la carpeta Argonavis", Toast.LENGTH_SHORT).show());

            } catch (IOException e) {
                e.printStackTrace();
                //visualizacion del error
                String errorMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
                runOnUiThread(() -> Toast.makeText(this, "Error al exportar la lista: " + errorMsg, Toast.LENGTH_SHORT).show());
            }
        }).start();
    }


    @Override
    public void onResume() {
        super.onResume();
        populateList(); // Actualiza la lista al volver desde vista secundaria
        if (scrollPosition != RecyclerView.NO_POSITION) {
            layoutManager.scrollToPositionWithOffset(scrollPosition, scrollOffset);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        scrollPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
        View firstVisibleView = recyclerView.getChildAt(1);
        scrollOffset = (firstVisibleView != null) ? firstVisibleView.getTop() : 0;      //propuesta de la IA
    }

    private List<dsoRow> getData() {
        dsosList = new ArrayList<>();
        Cursor cursor = db.getDsosInListNativeOrder(UserListID);
        return Constructor.ConstructorALLData(context,cursor);
    }


    public void populateList() {
        dsosList = getData();
        adaptadorRecycler = new UserListSeqAdapter(dsosList, UserListID, context);
        recyclerView.setAdapter(adaptadorRecycler);

        // Restaurar la posición del scroll después de cargar los datos
        if (scrollPosition != 0) {
            layoutManager.scrollToPositionWithOffset(scrollPosition, scrollOffset);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("scrollPosition", layoutManager.findFirstVisibleItemPosition());
        outState.putInt("scrollOffset", recyclerView.computeVerticalScrollOffset());
    }


    public void saveNewOrder() {
        //REVISAR
        List<dsoRow> updatedList = adaptadorRecycler.getItems();
        db.UpdateNewOrder(UserListID, updatedList);
        Toast.makeText(this, "Nuevo orden guardado", Toast.LENGTH_SHORT).show();

    }

}

