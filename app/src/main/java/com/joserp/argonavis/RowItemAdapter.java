package com.joserp.argonavis;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import androidx.recyclerview.widget.RecyclerView;


import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.joserp.argonavis.AuxFun.Common_functions;
import com.joserp.argonavis.AuxFun.VibrationHelper;

import java.util.ArrayList;
import java.util.List;

public class RowItemAdapter extends RecyclerView.Adapter<RowItemAdapter.ViewHolder> {
    private final Context context;
    private List<row> rowItemsList; //era final pero lo he quitado para agregar la funcion updateData necesaria para el dialogFragment
    private final List<row> selectedItems = new ArrayList<>();
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private SelectionModeListener selectionModeListener;
    public boolean isSelectionModeEnabled = false;
    private DBHelper db;

    // En tu clase RowItemAdapter
    public void updateData(List<row> newData) {
        rowItemsList = newData;
        notifyDataSetChanged();
    }

    public void notifySelectionModeChanged() {
        if (selectionModeListener != null) {
            selectionModeListener.onSelectionModeChanged(isSelectionModeEnabled);
        }
    }

    public RowItemAdapter(List<row> rowItemsList,  Context context) {
        this.rowItemsList = rowItemsList;
        this.context = context;
        db = new DBHelper(context);
    }

    public void setSelectionModeListener(SelectionModeListener listener) {      //OK
        this.selectionModeListener = listener;
    }


    public interface SelectionModeListener {        //OK
        void onSelectionModeChanged(boolean isInSelectionMode);
    }

    private void enableSelectionMode(row row, int position) {
        isSelectionModeEnabled = true;
        selectedItems.add(row);
        notifyItemChanged(position);
        if (selectionModeListener != null) {
            selectionModeListener.onSelectionModeChanged(true);
        }
    }

    public void disableSelectionMode() {
        isSelectionModeEnabled = false;
        if (selectionModeListener != null) {
            selectionModeListener.onSelectionModeChanged(false);
        }
    }

    private void toggleItemSelection(row row, int position) {
        if (selectedItems.contains(row)) {
            selectedItems.remove(row);
        } else {
            selectedItems.add(row);
        }
        notifyItemChanged(position);
        if (selectedItems.isEmpty()) {
            disableSelectionMode();
        }
    }




    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        VibrationHelper.initialize(context);
        View itemView = LayoutInflater.from(context).inflate(R.layout.row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final row row = rowItemsList.get(position);

        holder.itemNameTextView.setText(row.getNombre());

        viewBinderHelper.bind(holder.swipeRevealLayout, row.ID);
        //viewBinderHelper.closeLayout(row.ID); //nuevo
        //holder.swipeRevealLayout.requestLayout(); //nuevo
        //holder.swipeRevealLayout.invalidate(); //nuevo
        viewBinderHelper.setOpenOnlyOne(true);

        //multi seleccion
        holder.itemView.setSelected(selectedItems.contains(row));
        holder.itemView.setBackgroundResource(R.drawable.selected_item_background_2);

        // Set the icon based on the object type and classification
        Common_functions.setIconForDsoRow(context, holder.iconImageView, row);


        holder.addToViewButton.setOnClickListener(view -> {
            Common_functions.vibrar("estandar");
            db.Add2View(row.ID, "true");
            Toast.makeText(context, "Añadido a pendiente", Toast.LENGTH_LONG).show();
        });


        holder.wikiButton.setOnClickListener(view -> {
            Common_functions.vibrar("estandar");
            Intent intent = new Intent(context, WikiActivity.class);
            intent.putExtra("DSO", row);
            context.startActivity(intent);
        });

        // Long click listener for enabling selection mode
        holder.itemNameTextView.setOnLongClickListener(view -> {
            if (!isSelectionModeEnabled) {
                enableSelectionMode(row, position);
                return true;
            }
            return false;
        });


        holder.itemNameTextView.setOnClickListener(view -> {    //en lugar de itemview tenia nombre
            Common_functions.vibrar("estandar");
            if (isSelectionModeEnabled) {
                toggleItemSelection(row, position);
            } else {
                openObjectDetails(row);
            }
        });
    }



    private void openObjectDetails(row row) {
        Intent intent = new Intent(context, DSOActivity.class);
        intent.putExtra("ID", row.ID);
        context.startActivity(intent);
    }


    public void saveSelection() {
        if (selectedItems.isEmpty()) {
            Toast.makeText(context, "No se han seleccionado elementos", Toast.LENGTH_SHORT).show();
            return;
        }
        showAddToListDialogForMultipleItems();
    }


    private void showAddToListDialogForMultipleItems() {
        List<String> spinnerData = db.getSavedLists();
        if (spinnerData.isEmpty()) {
            Toast.makeText(context, "No hay listas disponibles", Toast.LENGTH_SHORT).show();
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_with_spinner, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        Spinner spinner = dialogView.findViewById(R.id.sp_listSelect);
        //Button cancelButton = dialogView.findViewById(R.id.Btn_cancel);
        ImageButton addButton = dialogView.findViewById(R.id.Btn_add);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.my_spinner, spinnerData);
        adapter.setDropDownViewResource(R.layout.my_spinner);
        spinner.setAdapter(adapter);

        //cancelButton.setOnClickListener(v -> dialog.dismiss());

        addButton.setOnClickListener(v -> {
            String selectedList = spinner.getSelectedItem().toString();
            for (row item : selectedItems) {
                db.AddItem2UserList(item.ID, selectedList);
                disableSelectionMode();
            }
            Toast.makeText(context, "Añadidos a lista: " + selectedList, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return rowItemsList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public SwipeRevealLayout swipeRevealLayout;
        TextView itemNameTextView;
        ImageView iconImageView;
        ImageButton addToViewButton, wikiButton;


        public ViewHolder(View itemView) {
            super(itemView);

            swipeRevealLayout = itemView.findViewById(R.id.swipelayout);
            itemNameTextView = itemView.findViewById(R.id.TV_n);
            itemNameTextView.setSelected(true);   //para la marquesina
            addToViewButton = itemView.findViewById(R.id.IBtoview2);
            iconImageView = itemView.findViewById(R.id.iv_dso);
            wikiButton = itemView.findViewById(R.id.IBwiki2);
        }
    }
}