package com.joserp.argonavis;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class AdaptadorRecyclerUserList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList listaResultados;
    Context context;
    private OnListListener mOnListListener;

    public AdaptadorRecyclerUserList(ArrayList listaResultados, Context context, OnListListener onListListener){
        this.listaResultados = listaResultados;
        this.context = context;
        this.mOnListListener = onListListener;
    }


    @Override
    public int getItemViewType(int position) {
        if(Objeto.class.isInstance(listaResultados.get(position))){
            return 0;   //se pasa un objeto
        }
        return 1;       //esto es constelacion
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        switch (viewType) {
            case 0:
                View view1 = LayoutInflater.from(context).inflate(R.layout.row_main_list, parent, false);
                return new MiHolder(view1, mOnListListener);
            case 1:
                View view2 = LayoutInflater.from(context).inflate(R.layout.row_main_list_constellation, parent, false);
                return new MiHolderConstelacion(view2, mOnListListener);
        }
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()){
            case 0:
                final Objeto obj = (Objeto) listaResultados.get(position);
                MiHolder holder = (MiHolder) viewHolder;
                holder.cat.setText(obj.getCatalogo());
                //holder.ref.setText(obj.getReferencia());
                holder.name.setText(obj.getNombre());
                holder.clasif.setText(obj.getTipo());

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
                break;
            case 1:
                final String ConstellationName = listaResultados.get(position).toString();
                MiHolderConstelacion holder2 = (MiHolderConstelacion) viewHolder;
                holder2.con2.setText(ConstellationName);
                break;
        }
    }


    @Override
    public int getItemCount() {
        return listaResultados.size();
    }


    public class MiHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView cat, ref, name, clasif, toview, visto;
        OnListListener onListListener;

        public MiHolder(View itemView, OnListListener onListListener) {
            super(itemView);
            cat = (TextView) itemView.findViewById(R.id.TV_lista_CAT);
            //ref = (TextView) itemView.findViewById(R.id.TV_lista_REF);
            name = (TextView) itemView.findViewById(R.id.TV_lista_Name);
            clasif = (TextView) itemView.findViewById(R.id.TV_Tipo_Class);
            toview = (TextView) itemView.findViewById(R.id.TV_ToView);
            visto =  (TextView) itemView.findViewById(R.id.TV_YaVisto);

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


    public class MiHolderConstelacion extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView con2;
        OnListListener onListHeaderListener;

        public MiHolderConstelacion(View itemView, OnListListener onListHeaderListener) {
            super(itemView);
            con2 = (TextView) itemView.findViewById(R.id.TV_cons_name);

            this.onListHeaderListener = onListHeaderListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            onListHeaderListener.OnListHeaderClic(getAbsoluteAdapterPosition());
        }
    }


    public interface OnListListener{
        void OnListClic(int position);

        void OnLongListClic(int position);

        void OnListHeaderClic(int position);
    }

}
