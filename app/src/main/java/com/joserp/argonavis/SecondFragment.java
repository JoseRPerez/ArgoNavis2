package com.joserp.argonavis;

import static com.joserp.argonavis.AuxFun.Common_functions.vibrar;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.joserp.argonavis.AuxFun.Common_functions;
import com.joserp.argonavis.AuxFun.Constructor;
import com.joserp.argonavis.AuxFun.ToolbarListener;

import java.util.List;

public class SecondFragment extends Fragment implements MainActivity.OnCloseAppListener,
        FilterDialogFragment.FilterDialogListener {


    private DBHelper db;
    private RecyclerView recyclerView;
    private String Type = "";
    private String Clasif = "";
    private String Rate = "";
    private EditText CampoBuscar;
    private RowItemAdapter rowItemAdapter;
    private ImageButton FilterButton;
    private ImageButton addSelectionToUserList;
    //private boolean inSelectionMode = false;


    public SecondFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(context instanceof ToolbarListener)) {
            throw new RuntimeException(context + " debe implementar ToolbarListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = DBHelper.getInstance(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_buscar_dso, container, false);
        recyclerView = view.findViewById(R.id.dsosList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        // Obtener la Toolbar del fragmento
        Toolbar toolbar = view.findViewById(R.id.toolbar);

        // Sincronizar la Toolbar con el NavigationDrawer
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).syncToolbarWithDrawer(toolbar);
        }

        FilterButton = view.findViewById(R.id.filtar);
        FilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrar("corta");
                openFilterDialog();
            }});

        //estas dos lineas estan pensadas para cambiar el comportamiento de los botones cuando haya varios objetos seleccionados
        addSelectionToUserList = view.findViewById(R.id.addSelectionToUserList);
        addSelectionToUserList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrar("corta");
                saveSelectionToList();
            }
        });



        CampoBuscar = view.findViewById(R.id.campobuscar);
        // Configurar el TextWatcher en el EditText
        CampoBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filtrar la lista de datos y actualizar el RecyclerView
                populateList(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });



        //Llenamos la recyclerview con los datos de DSOs
        populateList(false);


        /*  //no es necesario pues esta contenido en populateList
        // Configura el listener para el modo de selección
        rowItemAdapter.setSelectionModeListener(isInSelectionMode -> {
            if (isInSelectionMode) {
                // Mostrar el ícono cuando esté en modo de selección
                addSelectionToUserList.setVisibility(View.VISIBLE);
                FilterButton.setVisibility(View.GONE);
                //inSelectionMode = true;
            } else {
                // Ocultar el ícono cuando no esté en modo de selección
                addSelectionToUserList.setVisibility(View.GONE);
                FilterButton.setVisibility(View.VISIBLE);
                //inSelectionMode = false;
            }
        });
         */

        return  view;
    }


    public void saveSelectionToList () {
        rowItemAdapter.saveSelection();
    }



    private void openFilterDialog() {
        //funciona pero se va a cambiar
        // Intent intent = new Intent(getContext(), FilterDialog.class);
        // intent.putExtra("Constelacion", "");    //en la busqueda objeto no hay constelacion
                                                            //y filter_activity se comparte para el filtro constelacion y dso list
        // activityLauncher.launch(intent);

        FilterDialogFragment dialogFragment = new FilterDialogFragment();
        // Establece el listener directamente
        dialogFragment.setFilterDialogListener(this);

        // Pasa los filtros actuales al diálogo (opcional)
        // Bundle args = new Bundle();
        // args.putString("currentClasif", Clasif);
        // args.putString("currentRate", Rate);
        // args.putString("currentType", Type);
        // dialogFragment.setArguments(args);

        dialogFragment.show(getParentFragmentManager(), "FilterDialog");


    }

    /* ha sido sustituido por el dialogo fragment
    //Resultado de filter_dialog
    private final ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    Type = data.getStringExtra("Tipo");
                    Clasif = data.getStringExtra("Clasif");
                    Rate = data.getStringExtra("Rate");
                    // Actualizar la vista con los nuevos parámetros
                    populateList(false);
                }
            }
        });
     */


    private void populateList(boolean TextSearch) {
        // lo comentado funciona pero probamos algo nuevo
        // rowItemAdapter = new RowItemAdapter(getData(TextSearch), getContext());
        // recyclerView.setAdapter(rowItemAdapter);


        // Esta solucion esta bien pero como no se guarda el estado
        // en un viewModel se pierde el estado de la lista al cambiar de fragment
        /*
        List<row> filteredData = getData(TextSearch);
        if (rowItemAdapter == null) {
            rowItemAdapter = new RowItemAdapter(filteredData, getContext());
            recyclerView.setAdapter(rowItemAdapter);
        } else {
            rowItemAdapter.updateData(filteredData);
        }*/

        // Restaurar el listener de modo selección
        rowItemAdapter = new RowItemAdapter(getData(TextSearch), getContext());
        recyclerView.setAdapter(rowItemAdapter);

        // Configurar el listener CADA VEZ que se crea el adapter
        setupSelectionModeListener();
    }

    private void setupSelectionModeListener() {
        rowItemAdapter.setSelectionModeListener(isInSelectionMode -> {
            if (isInSelectionMode) {
                addSelectionToUserList.setVisibility(View.VISIBLE);
                FilterButton.setVisibility(View.GONE);
            } else {
                addSelectionToUserList.setVisibility(View.GONE);
                FilterButton.setVisibility(View.VISIBLE);
            }
        });
    }


    private List<row> getData(boolean TextSearch) {
        Cursor cursor;
        if (TextSearch) {
            cursor = db.searchByText(CampoBuscar.getText().toString());
        } else {
            cursor = db.getComplexDSOSearch(Type, Clasif, Rate);
        }

        return Constructor.ConstructorRow(getContext(), cursor);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Si hay items seleccionados, forzar modo selección
        if (rowItemAdapter != null && rowItemAdapter.isSelectionModeEnabled) {
            setupSelectionModeListener();
            rowItemAdapter.notifySelectionModeChanged();
        }
    }

    @Override
    public void onCloseApp() {
        // Cerrar la base de datos cuando se solicite
        if (db != null) {
            db.close();
        }
    }


    //NO ESTOY SEGURO QUE SEA NECESARIO
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



    // inicio prueba dialog fragment
    @Override
    public void onFilterApplied(String tipo, String clasif, String rate) {
        // Guarda los nuevos valores de filtro
        Type = tipo;
        Clasif = clasif;
        Rate = rate;

        // Actualiza la lista
        populateList(false);


    }

    @Override
    public void onFilterCanceled() {
        // Opcional: manejar cuando se cancela el diálogo
        Common_functions.makeToast(getContext(), "Filtrado cancelado");
    }

    private void filtrarLista(String tipo, String clasif, String rate) {
        // Aquí implementa tu lógica de filtrado
        Common_functions.makeToast(getContext(), "Tipo: " + tipo + ", Clasificación: " + clasif + ", Rate: " + rate);
        //if (tuAdaptador != null) {
        //    tuAdaptador.filtrar(tipo, clasif, Integer.parseInt(rate));
        //}
    }


    // fin prueba dialog fragment



}

