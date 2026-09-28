package com.joserp.argonavis;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;


public class AdaptadorRecycler extends RecyclerView.Adapter<AdaptadorRecycler.MiHolder> {

    ArrayList listaResultados;
    Context context;
    private OnListListener mOnListListener;

    public AdaptadorRecycler(ArrayList listaResultados, Context context, OnListListener onListListener){
        this.listaResultados = listaResultados;
        this.context = context;
        this.mOnListListener = onListListener;
    }

    @Override
    public MiHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.row_main_list,parent,false);
        return new MiHolder(view, mOnListListener);
    }

    @Override
    public void onBindViewHolder(MiHolder holder, int position) {
        final Objeto obj = (Objeto) listaResultados.get(position);
        holder.cat.setText(obj.getCatalogo());
        //holder.ref.setText(obj.getReferencia());
        holder.name.setText(obj.getNombre());
        holder.clase.setText(obj.getTipo());

        if (Boolean.valueOf(obj.getToView()) == true){
            holder.toview.setVisibility(View.VISIBLE);
        } else {
            holder.toview.setVisibility(View.INVISIBLE);
        }

        if (Boolean.valueOf(obj.getVisto()) == true){
            holder.visto.setVisibility(View.VISIBLE);
        } else {
            holder.visto.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return listaResultados.size();
    }

    public class MiHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        TextView cat, ref, name, clase, toview, visto;
        OnListListener onListListener;

        public  MiHolder(View itemView, OnListListener onListListener){
            super(itemView);
            cat = (TextView) itemView.findViewById(R.id.TV_lista_CAT);
            //ref = (TextView) itemView.findViewById(R.id.TV_lista_REF);
            name = (TextView) itemView.findViewById(R.id.TV_lista_Name);
            clase = (TextView) itemView.findViewById(R.id.TV_Tipo_Class);
            toview = (TextView) itemView.findViewById(R.id.TV_ToView);
            visto = (TextView) itemView.findViewById(R.id.TV_YaVisto);

            this.onListListener = onListListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onListListener.OnListClic(getAbsoluteAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            onListListener.OnLongListClic(getAbsoluteAdapterPosition());
            view.setBackgroundColor(context.getColor(R.color.gris_inactivo)); //esta bien pero cuando en longclic no se seleccioana nada se queda marcado
            return false;
        }
    }


    public interface OnListListener{
        void OnListClic(int position);

        void OnLongListClic(int position);
    }



}
