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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.joserp.argonavis.AuxFun.Common_functions;

import java.util.ArrayList;


public class UserListNotSeqAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList listaResultados;
    Context context;
    Boolean isToviewList;

    private DBHelper db;
    String ID_Userlist;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();


    public UserListNotSeqAdapter(ArrayList listaResultados, String ID_Userlist, Boolean isToviewList, Context context) {
        this.listaResultados = listaResultados;
        this.ID_Userlist = ID_Userlist;
        this.isToviewList = isToviewList;
        this.context = context;
    }


    @Override
    public int getItemViewType(int position) {
        if (dsoRow.class.isInstance(listaResultados.get(position))) {
            return 0;   //se pasa un objeto
        }
        return 1;       //esto es constelacion
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                View view1 = LayoutInflater.from(context).inflate(R.layout.row_main_list_in_userlist, parent, false);
                return new HolderDSO(view1);
            case 1:
                View view2 = LayoutInflater.from(context).inflate(R.layout.row_con_header, parent, false);
                return new MiHolderConstelacion(view2);
        }
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        db = DBHelper.getInstance(viewHolder.itemView.getContext());

        switch (viewHolder.getItemViewType()) {
            case 0:
                /*final dsoRow obj = (dsoRow) listaResultados.get(position);
                HolderDSO holder = (HolderDSO) viewHolder;

                viewBinderHelper.bind(holder.swipeRevealLayout, obj.ID);
                viewBinderHelper.setOpenOnlyOne(true);

                holder.cat.setText(obj.Catalogo);
                holder.name.setText(obj.Nombre);
                holder.rate.setText(String.valueOf(obj.rate));


                if (obj.ToView) {
                    holder.toview.setVisibility(View.VISIBLE);
                } else {
                    holder.toview.setVisibility(View.INVISIBLE);
                }

                if (obj.Visto) {
                    holder.visto.setVisibility(View.VISIBLE);
                } else {
                    holder.visto.setVisibility(View.INVISIBLE);
                }

                // Set the icon based on the object type and classification
                Common_functions.setIconForDsoRow(context, holder.icon, obj);


                holder.BtnsacardeLista.setOnClickListener(view -> {
                    Common_functions.vibrar("estandar");

                    Dialogo_Confirmacion(position, "¿Sacar objeto de la lista?");
                });

                holder.LL.setOnClickListener(view -> {
                    Common_functions.vibrar("estandar");
                    Intent i = new Intent(context, DSOActivity.class);
                    i.putExtra("ID", ((dsoRow) listaResultados.get(position)).ID );
                    context.startActivity(i);
                });

                break;*/
                final dsoRow obj = (dsoRow) listaResultados.get(position);
                HolderDSO holder = (HolderDSO) viewHolder;

                viewBinderHelper.bind(holder.swipeRevealLayout, obj.ID);
                viewBinderHelper.setOpenOnlyOne(true);

                holder.cat.setText(obj.Catalogo);
                holder.name.setText(obj.Nombre);
                holder.rate.setText(String.valueOf(obj.rate));

                if (obj.ToView) {
                    holder.toview.setVisibility(View.VISIBLE);
                } else {
                    holder.toview.setVisibility(View.INVISIBLE);
                }

                if (obj.Visto) {
                    holder.visto.setVisibility(View.VISIBLE);
                } else {
                    holder.visto.setVisibility(View.INVISIBLE);
                }

                // Comprobación de límites dentro del mismo tipo de vista
                boolean esMismoTipoAnterior = (position > 0) && (getItemViewType(position - 1) == 0);
                boolean esMismoTipoSiguiente = (position < listaResultados.size() - 1) && (getItemViewType(position + 1) == 0);

                // Selección del fondo según si es primero, centro, último o único del grupo
                int backgroundDrawable;
                if (!esMismoTipoAnterior && esMismoTipoSiguiente) {
                    backgroundDrawable = R.drawable.bg_card_top;
                } else if (esMismoTipoAnterior && esMismoTipoSiguiente) {
                    backgroundDrawable = R.drawable.bg_card_middle;
                } else if (esMismoTipoAnterior && !esMismoTipoSiguiente) {
                    backgroundDrawable = R.drawable.bg_card_bottom;
                } else {
                    backgroundDrawable = R.drawable.bg_card_single;
                }

                // Aplicar fondo al contenedor principal (donde diste id: containerMain en el XML)
                holder.containerMain.setBackgroundResource(backgroundDrawable);

                // Añadir margen inferior solo al último elemento de la constelación para separarla de la siguiente
                RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
                int margin8dp = (int) (8 * context.getResources().getDisplayMetrics().density);
                lp.bottomMargin = !esMismoTipoSiguiente ? margin8dp : 0;
                holder.itemView.setLayoutParams(lp);

                // Iconos y clics
                Common_functions.setIconForDsoRow(context, holder.icon, obj);

                holder.BtnsacardeLista.setOnClickListener(view -> {
                    Common_functions.vibrar("estandar");
                    Dialogo_Confirmacion(position, "¿Sacar objeto de la lista?");
                });

                holder.LL.setOnClickListener(view -> {
                    Common_functions.vibrar("estandar");
                    Intent i = new Intent(context, DSOActivity.class);
                    i.putExtra("ID", ((dsoRow) listaResultados.get(position)).ID);
                    context.startActivity(i);
                });

                break;
            case 1:
                final String ConstellationName = listaResultados.get(position).toString();
                MiHolderConstelacion holder2 = (MiHolderConstelacion) viewHolder;
                holder2.con2.setText(ConstellationName);
                holder2.itemView.setOnClickListener(view -> {
                    Common_functions.vibrar("estandar");
                    ShowAvailableMaps(ConstellationName);
                });
                break;
        }
    }


    public void Dialogo_Confirmacion(int position, String msg){
        final dsoRow item = (dsoRow) listaResultados.get(position);

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
                        listaResultados.remove(position);
                        notifyDataSetChanged();
                        if (isToviewList) {
                            db.Add2View(item.getID(), "false");
                        } else {
                            Log.d("CACA", item.getID() + " " + ID_Userlist);
                            db.RemoveItemFromUserList(item.getID(), ID_Userlist);
                        }
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
    public int getItemCount() {
        return listaResultados.size();
    }


    public class HolderDSO extends RecyclerView.ViewHolder {
        TextView cat, name, toview, visto, rate;
        ImageButton BtnsacardeLista;
        ImageView icon;
        LinearLayout LL, containerMain;

        public SwipeRevealLayout swipeRevealLayout;
        public HolderDSO(View itemView) {
            super(itemView);

            swipeRevealLayout = itemView.findViewById(R.id.swipelayout);
            LL = itemView.findViewById(R.id.LL);
            cat = itemView.findViewById(R.id.TV_lista_CAT);
            name = itemView.findViewById(R.id.TV_lista_Name);
            name.setSelected(true);
            toview = itemView.findViewById(R.id.TV_ToView);
            visto = itemView.findViewById(R.id.TV_YaVisto);
            rate = itemView.findViewById(R.id.TV_rate);
            icon = itemView.findViewById(R.id.iv_dso);

            BtnsacardeLista = itemView.findViewById(R.id.ibOut);
            containerMain = itemView.findViewById(R.id.containerMain);
        }
    }


    public class MiHolderConstelacion extends RecyclerView.ViewHolder {
        TextView con2;

        public MiHolderConstelacion(View itemView) {
            super(itemView);
            con2 = itemView.findViewById(R.id.TV_cons_name);
        }
    }






    public void ShowAvailableMaps(String ConstelacionName){     //SIIIIII
        Common_functions.vibrar("");

        String Mapas = db.getChartsInconstellation(ConstelacionName);

        if (Mapas.isEmpty()){
            Toast.makeText(context, "Complete la información de la constelación", Toast.LENGTH_SHORT).show();
        } else {
            ArrayList<String> mapas = Common_functions.getMapsPathIfExists(Mapas);

            if (mapas.isEmpty()){
                Toast.makeText(context, "No se han podido encontrar mapas. Revise la carpeta maps", Toast.LENGTH_SHORT).show();
            } else {
                openMap(mapas);
            }
        }


    }


    public void openMap(ArrayList<String> mapas) {  //siiiiiii
        Common_functions.vibrar("estandar");

        if (mapas.size() == 1) {
            Intent i = new Intent(context, MapActivity.class);
            //i.putExtra("filepath", Common_functions.getFileURL("maps", mapas.get(0) + ".jpg"));
            i.putExtra("filepath", Common_functions.getMapUrl(mapas.get(0)));
            i.putExtra("filename", mapas.get(0));
            context.startActivity(i);

        } else {
            /*AlertDialog.Builder options_maps = new AlertDialog.Builder(context);
            options_maps.setTitle("Escoja una opción");
            options_maps.setItems(mapas.toArray(new String[mapas.size()]), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent i2 = new Intent(context, MapActivity.class);
                    i2.putExtra("filepath", Common_functions.getMapUrl(mapas.get(i)));
                    i2.putExtra("filename", mapas.get(i));
                    context.startActivity(i2);
                }
            });

            AlertDialog builder = options_maps.create();
            builder.show();*/
            DSOActivity.openMapsDialog(context, "", mapas, new intoConstellationActivity.OnMapSelectedListener() {
                @Override
                public void onMapSelected(String selectedMap, String dsoID) {
                    Intent i2 = new Intent(context, MapActivity.class);
                    i2.putExtra("filepath", Common_functions.getMapUrl(selectedMap));
                    i2.putExtra("filename", selectedMap);
                    context.startActivity(i2);
                }
            });
        }
    }


}
