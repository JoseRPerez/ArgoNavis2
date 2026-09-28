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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.joserp.argonavis.AuxFun.Constructor;
import com.joserp.argonavis.AuxFun.ToolbarListener;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FirstFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirstFragment extends Fragment implements MainActivity.OnCloseAppListener {

    private ToolbarListener toolbarListener;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SHOW_ALTERNATIVE_NAME = "showAlternativeName";
    private static final String ARG_SHOW_ALL_CONSTELLATIONS = "showAllConstellations";
    private static final String ARG_SHOW_DETAILS = "showDetails";
    private static final boolean DEFAULT_SHOW_DEFAULT_NAME = true;
    private static final boolean DEFAULT_SHOW_ALL_CONSTELLATIONS = false;
    private static final boolean DEFAULT_SHOW_DETAILS = false;
    RecyclerView recyclerView;

    //private Toolbar toolbar;

    private DBHelper db;
    private static boolean showLatinNames = true;
    private static boolean showAllConstellations = false;
    private static boolean showData = false;

    private static boolean showDetails;

    public FirstFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param showAlternativeName param.
     * @param showAllConstellations param.
     * @param showDetails param.
     * @return A new instance of fragment FirstFragment.
     */
    public static FirstFragment newInstance(boolean showAlternativeName, boolean showAllConstellations, boolean showDetails) {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_SHOW_ALTERNATIVE_NAME, showAlternativeName );
        args.putBoolean(ARG_SHOW_ALL_CONSTELLATIONS, showAllConstellations );
        args.putBoolean(ARG_SHOW_DETAILS, showDetails);
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
        db = new DBHelper(getContext());          //sin usar singleton pero siempre ha funcionado
        //db = DBHelper.getInstance(getContext());    //uso del singleton
        if (getArguments() != null) {
            showLatinNames = getArguments().getBoolean(ARG_SHOW_ALTERNATIVE_NAME, DEFAULT_SHOW_DEFAULT_NAME);
            showAllConstellations = getArguments().getBoolean(ARG_SHOW_ALL_CONSTELLATIONS, DEFAULT_SHOW_ALL_CONSTELLATIONS);
            showDetails = getArguments().getBoolean(ARG_SHOW_DETAILS, DEFAULT_SHOW_DETAILS);
        } else {
            showLatinNames = DEFAULT_SHOW_DEFAULT_NAME;
            showAllConstellations = DEFAULT_SHOW_ALL_CONSTELLATIONS;
            showDetails = DEFAULT_SHOW_DETAILS;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.constList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        populateList();

        // Obtener la Toolbar del fragmento
        Toolbar toolbar = view.findViewById(R.id.toolbar);

        // Sincronizar la Toolbar con el NavigationDrawer
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).syncToolbarWithDrawer(toolbar);
        }

        ImageButton SelectConstellation = view.findViewById(R.id.toggleConstellationsButton);
        SelectConstellation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrar("corta");
                showAllConstellations = !showAllConstellations;
                populateList();
            }});

        ImageButton ShowData = view.findViewById(R.id.showData);
        ShowData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrar("corta");
                showData = !showData;
                populateList();
            }});

        return  view;
    }

    @Override
    public void onCloseApp() {
        // Cerrar la base de datos cuando se solicite
        if (db != null) {
            db.close();
        }
    }

    private void populateList(){
        recyclerView.setAdapter(new RowItemConAdapter(getData(showAllConstellations), showData, showAllConstellations, getContext()));
    }

    private List<rowCon> getData(boolean showAllConstellations) {
        try (Cursor cursor = db.getConstellation(showAllConstellations)) {
            return Constructor.ConstructorCONS(getContext(), cursor);
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