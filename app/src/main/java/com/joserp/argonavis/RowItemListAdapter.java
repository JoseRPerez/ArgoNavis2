package com.joserp.argonavis;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.joserp.argonavis.AuxFun.Common_functions;

import java.lang.ref.WeakReference;
import java.util.List;

public class RowItemListAdapter extends RecyclerView.Adapter<RowItemListAdapter.ViewHolder> {
    private final Context context;
    private final List<rowList> rowItemsList;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    private final Boolean mostrarDatos;
    //boolean isNightMode;
    int SeqActivatedColor;
    int SeqDeactivatedColor;
    private DBHelper db;


    public RowItemListAdapter(List<rowList> rowItemsList, Boolean mostrarDatos, Context context) {
        this.rowItemsList = rowItemsList;
        this.context = context;
        this.mostrarDatos = mostrarDatos;

        db = DBHelper.getInstance(context);

        SeqDeactivatedColor = ContextCompat.getColor(context, R.color.gris_oscuro);
        SeqActivatedColor = ContextCompat.getColor(context, R.color.granate);
    }


    @Override
    public RowItemListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.row_userlist, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final rowList row = rowItemsList.get(position);
        viewBinderHelper.bind(holder.swipeRevealLayout, row.ID);
        viewBinderHelper.setOpenOnlyOne(true);

        holder.itemNameTextView.setText(row.getNombre());


        // Set the icon based on the object type and classification
        setIconForList(holder.iconImageView, row);

        holder.itemNameTextView.setOnClickListener(view -> {
            Common_functions.vibrar("estandar");

            Context context = holder.itemView.getContext();

            Boolean isSeqList = row.IsSeq;
            String name = row.Nombre;
            String id = row.ID;

            Intent i;

            //si se trata de una lista tipo secuencia sus objetos se mostraran como en una
            // constelacion en orden de introduccion, en caso contrario se muestra como la vista de
            // pendientes, ordenados por constelacion
            if (isSeqList) {
                i = new Intent(context, UserListSeqActivity.class);
                i.putExtra("ListName", name);
                i.putExtra("ID_UserList", id);
            } else {
                i = new Intent(context, UserListNotSeqActivity.class);
                i.putExtra("ListName", name);
                i.putExtra("ID_UserList", id);
            }
            context.startActivity(i);

        });


        holder.BtnDelete.setOnClickListener(view -> {
            Common_functions.vibrar("estandar");
            alertDialog(position, holder, "¿Eliminar Lista de Usuario?");
        });


        holder.BtnRename.setOnClickListener(view -> {
            Common_functions.vibrar("estandar");

            //alertDialogRenameList(row, holder);
            renameList(row, holder);
        });

        holder.BtnSwapSeq.setOnClickListener(view -> {
            Common_functions.vibrar("estandar");
            row.setSeq(!row.IsSeq);
            db.EditSeqField(row.ID, row.IsSeq);
            notifyItemChanged(position);
        });

    }


    @Override
    public int getItemCount() {
        return rowItemsList.size();
    }


    private AlertDialog currentAlertDialog;


    public void alertDialog(int position, RowItemListAdapter.ViewHolder holder, String msg){

        if (currentAlertDialog != null && currentAlertDialog.isShowing()) {
            currentAlertDialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(new WeakReference<>(context).get());
        builder.setTitle(msg);
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (currentAlertDialog != null)
                    currentAlertDialog.dismiss();
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (position >= 0 && position < rowItemsList.size()){
                    rowList item = rowItemsList.get(position);
                    db.RemoveUserListID(item.ID);
                    rowItemsList.remove(position);
                    notifyItemRemoved(position);
                    //notifyDataSetChanged();
                }
                if (currentAlertDialog != null)
                    currentAlertDialog.dismiss();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        if (currentAlertDialog != null)
                            currentAlertDialog.dismiss();
                    }
                });
        currentAlertDialog = builder.create();
        currentAlertDialog.show();
    }


    public void renameList(rowList item, RowItemListAdapter.ViewHolder holder){
        // Inflar el layout personalizado
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_rename_user_list, null);

        // Configurar el EditText
        EditText input = dialogView.findViewById(R.id.ET_ListName);

        // Crear el AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        // Configurar los botones
        AlertDialog dialog_rename = builder.create();
        ImageButton btnSave = dialogView.findViewById(R.id.Btn_add);

        btnSave.setOnClickListener(view -> {
            String newName = input.getText().toString().trim(); // Obtener el texto ingresado
            if (newName.isEmpty()) {
                Common_functions.makeToast(context, "Nombre no válido");

            } else {
                db.RenameList(item.ID, newName);
                item.setNombre(newName);
                dialog_rename.cancel();
                holder.itemNameTextView.setText(newName);
                Toast.makeText(context, "Nombre Actualizado", Toast.LENGTH_SHORT).show();
                holder.swipeRevealLayout.close(true);
            }
        });

        dialog_rename.show();
    }


    public void alertDialogRenameList(rowList item, RowItemListAdapter.ViewHolder holder){
        // Crear un AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.dialogo);
        builder.setTitle("Renombrar Lista"); // Título del diálogo

        // Crear un EditText para que el usuario ingrese el texto
        final EditText input = new EditText(context);
        input.setTextColor(context.getResources().getColor(R.color.main) );
        input.setTextSize(22);

        input.setInputType(InputType.TYPE_CLASS_TEXT); // Tipo de entrada de texto
        builder.setView(input); // Añadir el EditText al diálogo
        input.setText(item.Nombre);

        // Configurar el botón "Aceptar"
        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            String newName = input.getText().toString().trim(); // Obtener el texto ingresado
            if (newName.isEmpty()) {
                Toast.makeText(context, "Nombre no válido", Toast.LENGTH_SHORT).show();

            } else {
                db.RenameList(item.ID, newName);
                item.setNombre(newName);
                dialog.cancel();
                holder.itemNameTextView.setText(newName);
                Toast.makeText(context, "Nombre Actualizado", Toast.LENGTH_SHORT).show();
                holder.swipeRevealLayout.close(true);
            }
        });

        // Configurar el botón "Cancelar"
        builder.setNegativeButton("Cancelar", (dialog, which) -> {
            dialog.cancel(); // Cerrar el diálogo sin hacer nada
        });

        // Mostrar el diálogo
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void setIconForList(ImageView  iconImageView, rowList row) {
        if (row.IsSeq) {
            iconImageView.setImageResource(R.drawable.arrow_down);
        } else {
            iconImageView.setImageResource(R.drawable.noseqlist);
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTextView, dsoIn;
        ImageView iconImageView;
        ImageButton BtnRename, BtnDelete, BtnSwapSeq;
        public SwipeRevealLayout swipeRevealLayout;



        public ViewHolder(View itemView) {
            super(itemView);

            swipeRevealLayout = itemView.findViewById(R.id.swipelayout);

            iconImageView = itemView.findViewById(R.id.icon);
            itemNameTextView = itemView.findViewById(R.id.TV_name);
            itemNameTextView.setSelected(true);
            dsoIn =  itemView.findViewById(R.id.TV_number);
            BtnDelete = itemView.findViewById(R.id.B_delete);
            BtnRename = itemView.findViewById(R.id.B_edit);
            BtnSwapSeq = itemView.findViewById(R.id.B_swapSeq);
        }
    }

    /*
    private void vibrar(String duracion) {
        VibrationHelper.vibrate(duracion);
    }

     */



}
