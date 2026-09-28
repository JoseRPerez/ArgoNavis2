package com.joserp.argonavis;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.joserp.argonavis.AuxFun.Common_functions;

import java.util.List;


public class InConstellationAdapter extends RecyclerView.Adapter<InConstellationAdapter.MiHolder> {

    List<dsoRow> listaResultados;
    Context context;
    private DBHelper db;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public InConstellationAdapter(List<dsoRow> listaResultados,  Context context){
        this.context = context;
        this.listaResultados = listaResultados;
        viewBinderHelper.setOpenOnlyOne(true);
        db = DBHelper.getInstance(this.context);

    }


    @Override
    public MiHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.row_main_list_in_cons, parent,false);
        return new MiHolder(view);
    }

    @Override
    public void onBindViewHolder(MiHolder holder, int position) {
        dsoRow obj = listaResultados.get(position);

        viewBinderHelper.bind(holder.swipeRevealLayout, obj.ID);

        holder.cat.setText(obj.Catalogo);
        holder.name.setText(obj.Nombre);
        holder.rate.setText(String.valueOf(obj.rate));

        Common_functions.setIconForDsoRow(context, holder.icon, obj);

        if (obj.ToView){
            holder.toview.setVisibility(View.VISIBLE);
        } else {
            holder.toview.setVisibility(View.INVISIBLE);
        }

        if (obj.Visto){
            holder.visto.setVisibility(View.VISIBLE);
        } else {
            holder.visto.setVisibility(View.INVISIBLE);
        }

        holder.LL.setOnClickListener(view -> {
            Common_functions.vibrar("");
            //Intent intent = new Intent(context, object_activity.class);
            Intent intent = new Intent(context, DSOActivity.class);
            intent.putExtra("ID", obj.ID);
//            intent.putExtra("position", position);    //no sirve
            context.startActivity(intent);
        });


        holder.ToggleVisto.setOnClickListener(view -> {
            Common_functions.vibrar("");
            obj.setVisto(!obj.getVisto());
            db.Add2Viewed(obj.ID, String.valueOf(obj.Visto));
            //notifyItemChanged(position);
            holder.visto.setVisibility(obj.getVisto() ? View.VISIBLE : View.INVISIBLE); //mejor rendimiento
        });

        holder.ToggleToView.setOnClickListener(view -> {
            Common_functions.vibrar("");
            obj.setToView(!obj.getToView());
            db.Add2View(obj.ID, String.valueOf(obj.ToView));
            //notifyItemChanged(position);
            holder.toview.setVisibility(obj.getToView() ? View.VISIBLE : View.INVISIBLE);   //mejor rendimiento
        });
    }


    @Override
    public int getItemCount() {
        return listaResultados.size();
    }


    // Define una interfaz para la comunicación
    public interface OnItemChangeListener {
        void onItemNameChanged(int position, String newName);
    }


    public class MiHolder extends RecyclerView.ViewHolder {
        TextView cat, name, toview, visto, rate;
        ImageView icon;
        ImageButton ToggleToView, ToggleVisto;
        LinearLayout LL;
        public SwipeRevealLayout swipeRevealLayout;


        public  MiHolder(View itemView) {
            super(itemView);

            swipeRevealLayout = itemView.findViewById(R.id.swipelayout);
            LL = itemView.findViewById(R.id.LL);

            cat = itemView.findViewById(R.id.TV_lista_CAT);
            rate = itemView.findViewById(R.id.TV_rate);
            name = itemView.findViewById(R.id.TV_lista_Name);
            name.setSelected(true); //necesario para el desplazamiento marquesina
            toview = itemView.findViewById(R.id.TV_ToView);
            visto = itemView.findViewById(R.id.TV_YaVisto);
            icon = itemView.findViewById(R.id.iv_dso);
            ToggleToView = itemView.findViewById(R.id.ibtoview);
            ToggleVisto = itemView.findViewById(R.id.ibtoviewed);
        }
    }

}
