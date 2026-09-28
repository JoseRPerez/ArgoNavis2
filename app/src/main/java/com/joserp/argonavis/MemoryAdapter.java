package com.joserp.argonavis;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.joserp.argonavis.AuxFun.Common_functions;

import java.util.List;
import java.util.Map;

public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.ViewHolder> {
    List<Memory> rowItemList;
    Context context;
    private DBHelper db;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public MemoryAdapter(List<Memory> rowItemList, Context context) {
        this.rowItemList = rowItemList;
        this.context = context;
        db = DBHelper.getInstance(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.row_memory_2, parent, false);
        return new ViewHolder(itemView);
    }

    public void setVisibility( String string, TextView text, LinearLayout layout) {
        if (string == null || string.isEmpty()) {
            layout.setVisibility(View.GONE);
        } else {
            text.setText(string);
            layout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Memory row = rowItemList.get(position);
        viewBinderHelper.bind(holder.swipeRevealLayout, row.id);
        viewBinderHelper.setOpenOnlyOne(true);

        holder.date.setText(row.getDSOTag());
        holder.text.setText(row.getMemory());

        setVisibility(row.getPlace(), holder.place, holder.ll_place);
        setVisibility(row.getTelescope(), holder.telescope, holder.ll_telescope);
        setVisibility(row.getEyepiece(), holder.eyepiece, holder.ll_eyepiece);

        /*
        holder.place.setText(row.getPlace());
        holder.telescope.setText(row.getTelescope());
        holder.eyepiece.setText(row.getEyepiece());
         */


        holder.BtnEditDate.setOnClickListener(view -> {
            Common_functions.vibrar("");
            opendialog(row, holder);
        });

        holder.BtnEditMemo.setOnClickListener(view -> {
            Common_functions.vibrar("");
            editMemory(row, holder);
        });


        holder.BtnRemove.setOnClickListener(view -> {
            Common_functions.vibrar("");
            confirmationDialog("¿Está seguro de eliminar este registro?", row, holder);
        });
    }

    private void confirmationDialog(String mensaje, Memory item, ViewHolder holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.dialogo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    //Memory item = rowItemList.get(position);
                    if ( db.deleteMemory(item.getID()) == 1 ) {
                        Toast.makeText(context, "Registro eliminado", Toast.LENGTH_SHORT).show();
                        int position = rowItemList.indexOf(item);
                        if (position != -1) {
                            rowItemList.remove(position);
                            notifyItemRemoved(position);
                        }
                    } else {
                        Toast.makeText(context, "Error al eliminar el registro", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    dialog.cancel();
                });
        // Mostrar el diálogo
        builder.create().show();
    }


    private void opendialog(Memory item, ViewHolder holder) {
        final EditText input = new EditText(context);
        input.setTextColor(context.getResources().getColor(R.color.gris3));
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setText(item.getDate());
        input.setTextSize(22);
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.dialogo)
                .setTitle("Fecha: dd/mm/aaaa")
                .setView(input)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    String newText = input.getText().toString();
                    // Actualizamos la base de datos
                    db.updateDateMemory(item.getID(), newText);
                    // Actualizamos el objeto en memoria
                    item.setDate(newText);
                    // Notificamos al adapter que el item ha cambiado
                    int position = holder.getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        holder.getBindingAdapter().notifyItemChanged(position);
                    }
                    dialog.cancel();
                    holder.swipeRevealLayout.close(true);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    dialog.cancel();

        });

        // Mostrar el diálogo
        builder.create().show();
    }


    public void editMemory(Memory row, ViewHolder holder) {

        Map<String, List<String>> allData = db.getAllMemoryExtraData();

        // Inflar el layout personalizado
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.memory_layout, null);
        TextView title = dialogView.findViewById(R.id.title);
        title.setText("Editar Registro");

        // Configurar el EditText
        EditText registro = dialogView.findViewById(R.id.memory_text);

        // Configurar los AutompleteTextView
        AutoCompleteTextView sp_lugar = dialogView.findViewById(R.id.AC_lugar);
        AutoCompleteTextView sp_telescopio = dialogView.findViewById(R.id.AC_telescopio);
        AutoCompleteTextView sp_ocular = dialogView.findViewById(R.id.AC_ocular);

        ArrayAdapter<String> adapterLugar = new ArrayAdapter<String>(context, R.layout.my_spinner, allData.get("places"));
        ArrayAdapter<String> adapterTelescopio = new ArrayAdapter<String>(context, R.layout.my_spinner, allData.get("telescopes"));
        ArrayAdapter<String> adapterOcular = new ArrayAdapter<String>(context, R.layout.my_spinner, allData.get("eyepieces"));

        sp_lugar.setAdapter(adapterLugar);
        sp_telescopio.setAdapter(adapterTelescopio);
        sp_ocular.setAdapter(adapterOcular);

        // Asginamos valores iniciales
        registro.setText(row.getMemory());
        sp_lugar.setText(row.getPlace());
        sp_telescopio.setText(row.getTelescope());
        sp_ocular.setText(row.getEyepiece());

        // Crear el AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        // Configurar los botones
        AlertDialog dialog_new_memory = builder.create();
        ImageButton btnAccept = dialogView.findViewById(R.id.btn_save);


        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String texto = registro.getText().toString().trim(); // Obtener el texto ingresado
                if (!texto.isEmpty()) {
                    // Guardamos el registro
                    String telescopio = sp_telescopio.getText().toString();
                    String lugar = sp_lugar.getText().toString();
                    String ocular = sp_ocular.getText().toString();

                    db.updateMemory(row.id, texto, lugar, telescopio, ocular);

                    //Actualizamos objeto en vista
                    row.setMemory(texto);
                    row.setPlace(lugar);
                    row.setTelescope(telescopio);
                    row.setEyepiece(ocular);

                    //Notificamos al adapter que el item ha cambiado
                    int position = holder.getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        holder.getBindingAdapter().notifyItemChanged(position);
                    }

                    dialog_new_memory.dismiss(); // Cerrar el diálogo
                    Common_functions.makeToast(context, "Registro Guardado");
                } else {
                    // Mostrar un mensaje si el texto está vacío
                    Toast.makeText(context, "El campo texto está vacío", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog_new_memory.show();
    }



    @Override
    public int getItemCount() { return rowItemList.size();}

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date, text, place, telescope, eyepiece;
        ImageButton BtnEditMemo, BtnEditDate, BtnRemove;
        LinearLayout ll_place, ll_telescope, ll_eyepiece;

        public SwipeRevealLayout swipeRevealLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            swipeRevealLayout = itemView.findViewById(R.id.swipelayout);

            date = itemView.findViewById(R.id.date);
            text = itemView.findViewById(R.id.memory);
            place = itemView.findViewById(R.id.place);
            telescope = itemView.findViewById(R.id.telescope);
            eyepiece= itemView.findViewById(R.id.eyepiece);

            BtnEditMemo = itemView.findViewById(R.id.edit_memo);
            BtnEditDate = itemView.findViewById(R.id.edt_date);
            BtnRemove = itemView.findViewById(R.id.BtnRemoveThis);

            ll_place = itemView.findViewById(R.id.ll1);
            ll_telescope = itemView.findViewById(R.id.ll2);
            ll_eyepiece = itemView.findViewById(R.id.ll3);

        }
    }




}
