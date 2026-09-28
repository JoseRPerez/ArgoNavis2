package com.joserp.argonavis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.joserp.argonavis.AuxFun.Common_functions;
import com.joserp.argonavis.AuxFun.VibrationHelper;

import java.util.ArrayList;

public class SimpleRowItemAdapter extends RecyclerView.Adapter<SimpleRowItemAdapter.ViewHolder> {
    Context context;
    ArrayList<row> rowItemsList;


    public SimpleRowItemAdapter(ArrayList rowItemsList, Context context) {
        this.rowItemsList = rowItemsList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        VibrationHelper.initialize(context);
        View itemView = LayoutInflater.from(context).inflate(R.layout.simple_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        row row = rowItemsList.get(position);
        holder.nombre.setText(row.getNombre());

        // Set the icon based on the object type and classification
        Common_functions.setIconForDsoRow(context, holder.icon, row);

        holder.nombre.setOnClickListener(view -> {
            Common_functions.vibrar("estandar");

            DSOActivity.loadNewDSO( ((row) rowItemsList.get(position)).ID, context);
        });
    }


    @Override
    public int getItemCount() {
        return rowItemsList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre;
        ImageView icon;


        public ViewHolder(View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.TV_n);
            icon = itemView.findViewById(R.id.iv_dso);
        }
    }


}