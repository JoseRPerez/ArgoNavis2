package com.joserp.argonavis;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.joserp.argonavis.AuxFun.Common_functions;
import com.joserp.argonavis.AuxFun.Constructor;
import com.joserp.argonavis.AuxFun.ToolbarListener;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ThirdFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThirdFragment extends Fragment implements MainActivity.OnCloseAppListener {

    private ToolbarListener toolbarListener;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Toolbar toolbar;

    private DBHelper db;
    private boolean mostrarDatos = false;
    private RecyclerView recyclerView;

    public ThirdFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ThirdFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ThirdFragment newInstance(String param1, String param2) {
        ThirdFragment fragment = new ThirdFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ToolbarListener) {
            toolbarListener = (ToolbarListener) context;
        } else {
            throw new RuntimeException(context.toString() + " debe implementar ToolbarListener");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = DBHelper.getInstance(getContext());

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_userlists, container, false);
        recyclerView = view.findViewById(R.id.userLists);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        populateList();

        // Obtener la Toolbar del fragmento
        toolbar = view.findViewById(R.id.toolbar);

        // Sincronizar la Toolbar con el NavigationDrawer
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).syncToolbarWithDrawer(toolbar);
        }

        ImageButton CreateNewListButtom = view.findViewById(R.id.newlist);
        CreateNewListButtom.setOnClickListener(v -> CreateNewUserList(this.getContext()));

        return  view;
    }


    /*
     public void CreateList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.dialogo);
        builder.setTitle("Crear Nueva Lista");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 48, 48, 48); // Ajusta los padding según sea necesario

        final EditText input = new EditText(getContext());
        input.setTextColor(getResources().getColor(R.color.gris3));
        input.setTextSize(22);
        input.setHintTextColor(getResources().getColor(R.color.main));
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Nombre de la Lista");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        input.setLayoutParams(params);
        layout.addView(input);

        builder.setView(layout);

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            String listName = input.getText().toString().trim();
            if (listName.isEmpty()) {
                Toast.makeText(getContext(), "El nombre de la lista no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }
            if (db.IsNameListInUserList(listName)) {
                Toast.makeText(getContext(), "Ya existe una lista con ese nombre", Toast.LENGTH_LONG).show();
            } else {
                db.AddNewUserList(listName, "false");
                Toast.makeText(getContext(), "Lista creada con éxito", Toast.LENGTH_SHORT).show();
                populateList();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.create().show();
    }
    */

    private void CreateNewUserList(Context context){
        // Inflar el layout personalizado
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_new_user_list2, null);

        // Configurar el EditText
        EditText input = dialogView.findViewById(R.id.ET_ListName);

        // Crear el AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        // Configurar los botones
        AlertDialog dialog_new_list = builder.create();
        ImageButton btnSave = dialogView.findViewById(R.id.Btn_add);

        btnSave.setOnClickListener(v -> {
            String listName = input.getText().toString().trim();
            if (listName.isEmpty()) {
                Common_functions.makeToast(context,"El nombre de la lista no puede estar vacío");
                return;
            }
            if (db.IsNameListInUserList(listName)) {
                Common_functions.makeToast(context, "Ya existe una lista con ese nombre");
            } else {
                db.AddNewUserList(listName, "false");
                Common_functions.makeToast(context, "Lista creada con éxito");
                dialog_new_list.cancel();
                populateList();
            }
        });

        dialog_new_list.show();
    }


    private void populateList() {
       recyclerView.setAdapter(new RowItemListAdapter(getData(), mostrarDatos, getContext()));
    }


    private List<rowList> getData() {
        Cursor cursor = null;
        try {
            cursor = db.getSavedLists_cursor();
            return Constructor.ConstructorLists(getContext(), cursor);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
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
        toolbarListener = null;
    }
}