package com.joserp.argonavis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.joserp.argonavis.AuxFun.Common_functions;
import com.joserp.argonavis.AuxFun.VibrationHelper;

import java.io.File;
import java.util.List;


public class RowItemConAdapter extends RecyclerView.Adapter<RowItemConAdapter.ViewHolder> {
    List<rowCon> rowItemsList;
    Boolean mostrarDatos, showAll;
    Context context;
    private DBHelper db;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();


    public RowItemConAdapter(List<rowCon> rowItemsList, Boolean mostrarDatos, Boolean showAll, Context context) {
        this.rowItemsList = rowItemsList;
        this.context = context;
        this.mostrarDatos = mostrarDatos;
        this.showAll = showAll;

        db = DBHelper.getInstance(context);

    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        VibrationHelper.initialize(context);
        View itemView = LayoutInflater.from(context).inflate(R.layout.row_con, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.activarCon.setOnCheckedChangeListener(null); //esta linea debe ser la primera no cambiar

        rowCon row = rowItemsList.get(position);
        viewBinderHelper.bind(holder.swipeRevealLayout, row.ID);
        viewBinderHelper.setOpenOnlyOne(true);

        holder.nombre.setText(row.getNombre());
        holder.activarCon.setChecked(row.getVisible());
        holder.viewed.setText(row.getScore());

        if (showAll) {
            holder.icono.setVisibility(View.GONE);
            holder.activarCon.setVisibility(View.VISIBLE);
            holder.viewed.setVisibility(View.GONE);
        } else {
            holder.activarCon.setVisibility(View.GONE);
            if (mostrarDatos) {
                holder.icono.setVisibility(View.VISIBLE);
                holder.viewed.setVisibility(View.VISIBLE);
            } else {
                holder.viewed.setVisibility(View.GONE);
            }
        }


        holder.nombre.setOnClickListener(view -> {
            Common_functions.vibrar("estandar");

            if (!showAll) {
                String Connombre = row.Nombre;
                Intent i = new Intent(context, intoConstellationActivity.class);
                i.putExtra("Constelacion", Connombre);
                context.startActivity(i);

            }
        });


        // Configurar el OnCheckedChangeListener para el Switch.
        holder.activarCon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Common_functions.vibrar("corta");
                // Modificar el valor del objeto usado para llenar la RecyclerView.
                row.setVisible(isChecked);
                db.changeVisibleCons(row.ID, row.visible);
            }
        });


        holder.BtnInfo.setOnClickListener(view -> {
            Common_functions.vibrar("");

            String file = row.ID + ".jpg";

            File imgFile = new File(Common_functions.getFileURL("cons", file));
            if (!imgFile.exists()) {
                Toast.makeText(context, "No hay elementos dentro de la carpeta 'cons'", Toast.LENGTH_SHORT).show();
            } else {
                Intent i = new Intent(context, ImageActivity.class);
                i.putExtra("filepath", Common_functions.getFileURL("cons", file));

                context.startActivity(i);
            }
        });


        holder.BtnEdit.setOnClickListener(view -> {
            Common_functions.vibrar("");
            dialogo_edit_charts(row);

        });
    }

    private void dialogo_edit_charts(rowCon row) {
        // Crear el diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.dialogo);
        builder.setTitle("Cartas de " + row.Nombre);

        // Agregar el EditText
        final EditText editText = new EditText(context);
        builder.setView(editText);
        editText.setTextColor(context.getResources().getColor(R.color.main) );
        editText.setText(db.getChartsInconstellationID(row.ID));
        editText.setTextSize(22);

        // Agregar botones
        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String textoIngresado = editText.getText().toString();
                //db.UpdateConstellationChartsbyID(row.ID, textoIngresado);
                db.UpdateConstellationCharts(row.Nombre, textoIngresado);
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Mostrar el diálogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public int getItemCount() {
        return rowItemsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, viewed;
        ImageView icono;
        ImageButton BtnEdit, BtnInfo;
        public SwipeRevealLayout swipeRevealLayout;
        Switch activarCon;


        public ViewHolder(View itemView) {
            super(itemView);

            swipeRevealLayout = itemView.findViewById(R.id.swipelayout);

            nombre = itemView.findViewById(R.id.TV_row);
            activarCon = itemView.findViewById(R.id.sw);
            viewed = itemView.findViewById(R.id.TV_data);
            icono = itemView.findViewById(R.id.iv);
            BtnInfo = itemView.findViewById(R.id.B_info);
            BtnEdit = itemView.findViewById(R.id.B_edit);
        }
    }
}


