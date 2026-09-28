package com.joserp.argonavis;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.joserp.argonavis.AuxFun.Common_functions;
import com.joserp.argonavis.AuxFun.VibrationHelper;

import java.util.List;


public class UserListSeqAdapter extends RecyclerView.Adapter<UserListSeqAdapter.MiHolder> {

    private final List<dsoRow> dsoRows;
    private final Context context;
    private DBHelper db;
    private String ID_Userlist;

    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();


    public UserListSeqAdapter(List<dsoRow> dsoRows, String ID_Userlist, Context context){
        this.dsoRows = dsoRows;
        this.ID_Userlist = ID_Userlist;
        this.context = context;
    }

    @Override
    public MiHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.row_dso_in_con,parent,false);
        db = DBHelper.getInstance(context);

        return new MiHolder(view);
    }

    @Override
    public void onBindViewHolder(MiHolder holder, int position) {
        final dsoRow dsoRow = dsoRows.get(position);

        viewBinderHelper.bind(holder.swipeRevealLayout, dsoRow.getID());
        viewBinderHelper.setOpenOnlyOne(true);


        holder.catalogTextView.setText(dsoRow.Catalogo);
        holder.nameTextView.setText(dsoRow.Nombre);
        holder.rateTextView.setText(String.valueOf(dsoRow.rate));

        // Set the icon based on the object type and classification
        Common_functions.setIconForDsoRow(context, holder.iconImageView, dsoRow);

        // Set visibility of "ToView" and "Visto" based on boolean values
        holder.toViewTextView.setVisibility(dsoRow.ToView ? View.VISIBLE : View.INVISIBLE);
        holder.seenTextView.setVisibility(dsoRow.Visto ? View.VISIBLE : View.INVISIBLE);

        holder.RL.setOnClickListener(v -> {
            vibrar("estandar");
            Intent intent = new Intent(context, DSOActivity.class);
            String selectedObjectId = dsoRows.get(position).ID;
            intent.putExtra("ID", selectedObjectId);
            context.startActivity(intent);
        });

        holder.BtnsacardeLista.setOnClickListener(view -> {
            vibrar("estandar");
            Dialogo_Confirmacion(position, "¿Sacar objeto de la lista?");
        });


    }

    public void Dialogo_Confirmacion(int position, String msg){
        final dsoRow item = (dsoRow) dsoRows.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(msg)
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        builder.create().dismiss();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dsoRows.remove(position);
                        notifyDataSetChanged();
                        Log.d("CACA", item.getID() + " " + ID_Userlist);
                        db.RemoveItemFromUserList(item.getID(), ID_Userlist);
                    }
                });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                builder.create().dismiss();
            }
        });
        builder.create().show();
    }


    @Override
    public int getItemCount() {return dsoRows.size(); }

    public class MiHolder extends RecyclerView.ViewHolder {
        TextView catalogTextView, nameTextView, toViewTextView, seenTextView, rateTextView;
        ImageView iconImageView;
        ImageButton BtnsacardeLista;
        RelativeLayout RL;
        public SwipeRevealLayout swipeRevealLayout;

        public  MiHolder(View itemView) {
            super(itemView);
            catalogTextView = itemView.findViewById(R.id.TV_lista_CAT);
            rateTextView = itemView.findViewById(R.id.TV_rate);
            nameTextView = itemView.findViewById(R.id.TV_lista_Name);
            nameTextView.setSelected(true);
            toViewTextView = itemView.findViewById(R.id.TV_ToView);
            seenTextView = itemView.findViewById(R.id.TV_YaVisto);
            iconImageView = itemView.findViewById(R.id.iv_dso);
            BtnsacardeLista = itemView.findViewById(R.id.ibOut);
            swipeRevealLayout = itemView.findViewById(R.id.swipelayout);
            RL = itemView.findViewById(R.id.RL);
        }
    }

    private void vibrar(String duracion) { VibrationHelper.vibrate(duracion); }

    public void moveItem(int fromPosition, int toPosition) {
        // Mover el elemento en la lista
        dsoRow item = dsoRows.remove(fromPosition);
        dsoRows.add(toPosition, item);
        // Notificar al RecyclerView del movimiento
        notifyItemMoved(fromPosition, toPosition);
    }


    // Metodo para obtener la lista de elementos
    public List<dsoRow> getItems() {
        return dsoRows; // Devuelve la lista actualizada
    }

}
