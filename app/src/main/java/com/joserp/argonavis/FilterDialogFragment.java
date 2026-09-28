package com.joserp.argonavis;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.List;

public class FilterDialogFragment extends DialogFragment {

    private FilterDialogListener listener;
    private DBHelper db;
    private Spinner SP_Tipo, SP_Clasificacion;
    private RatingBar RB_Stars;
    private ImageButton B_Aceptar, B_BorrarCriterios;
    private List<String> List_Clasif;

    // Interface para comunicar con la Activity
    public interface FilterDialogListener {
        void onFilterApplied(String tipo, String clasif, String rate);
        void onFilterCanceled();
    }

    // Cambia la forma de asignar el listener
    public void setFilterDialogListener(FilterDialogListener listener) {
        this.listener = listener;
    }

    // Elimina el onAttach original que causaba el problema
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.filter_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = DBHelper.getInstance(requireContext());

        RB_Stars = view.findViewById(R.id.RB);
        SP_Tipo = view.findViewById(R.id.SP_Tipo);
        SP_Clasificacion = view.findViewById(R.id.SP_Clasif);
        B_Aceptar = view.findViewById(R.id.IB_Accept);
        B_BorrarCriterios = view.findViewById(R.id.IB_delete);

        /*
        //Recuperacion de datos
        Bundle args = getArguments();
        if (args != null) {
            String constelacion = args.getString("currentContellation");
        }
         */


        // Configuración inicial igual que en tu Activity
        List<String> list_Tipos = db.getTypes();
        list_Tipos.add(0, "");

        ArrayAdapter<String> adapterType = new ArrayAdapter<>(
                requireContext(),
                R.layout.textview_row,
                list_Tipos
        );

        SP_Tipo.setAdapter(adapterType);
        SP_Tipo.setSelection(0);
        RB_Stars.setRating(0);

        SP_Tipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                if (position != 0) {
                    List_Clasif = db.fromTypeGetClasses(SP_Tipo.getSelectedItem().toString());
                    ArrayAdapter<String> adapterClass = new ArrayAdapter<>(
                            requireContext(),
                            R.layout.textview_row,
                            List_Clasif
                    );
                    SP_Clasificacion.setAdapter(adapterClass);
                    List_Clasif.add(0, "");
                    SP_Clasificacion.setSelection(0);
                } else {
                    List_Clasif = db.getClasses();
                    ArrayAdapter<String> adapterClass = new ArrayAdapter<>(
                            requireContext(),
                            R.layout.textview_row,
                            List_Clasif
                    );
                    SP_Clasificacion.setAdapter(adapterClass);
                    List_Clasif.add(0, "");
                    SP_Clasificacion.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });

        B_Aceptar.setOnClickListener(v -> applyFilters());
        B_BorrarCriterios.setOnClickListener(v -> deleteFilter());


        // Botón de retroceso (si lo tienes en tu layout)
        view.findViewById(R.id.ibBack).setOnClickListener(v -> dismiss());
    }

    private void applyFilters() {
        String tipo = SP_Tipo.getSelectedItem() != null ? SP_Tipo.getSelectedItem().toString() : "";
        String clasif = SP_Clasificacion.getSelectedItem() != null ? SP_Clasificacion.getSelectedItem().toString() : "";
        String rate = String.valueOf((int) RB_Stars.getRating());

        listener.onFilterApplied(tipo, clasif, rate);
        dismiss();
    }

    private void deleteFilter() {
        String tipo = "";
        String clasif ="";
        String rate = "0";

        listener.onFilterApplied(tipo, clasif, rate);
        dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Limpieza si es necesaria
    }
}