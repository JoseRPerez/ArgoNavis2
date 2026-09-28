package com.joserp.argonavis;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private List<BaseRow> items;
    private OnItemClickListener listener;
    private boolean showAddNewOption;
    private static final int TYPE_REGULAR = 0;
    private static final int TYPE_ADD_NEW = 1;

    public interface OnItemClickListener {
        void onItemClick(BaseRow item);
        void onAddNewClick();
    }

    public SearchAdapter(OnItemClickListener listener) {
        this.items = new ArrayList<>();
        this.listener = listener;
        this.showAddNewOption = false;
    }

    public void updateData(List<BaseRow> newItems, boolean showAddNew) {
        items = newItems;
        showAddNewOption = showAddNew;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (showAddNewOption && position == items.size()) {
            return TYPE_ADD_NEW;
        }
        return TYPE_REGULAR;
    }

    @Override
    public int getItemCount() {
        return items.size() + (showAddNewOption ? 1 : 0);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ADD_NEW) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.textview_row_2, parent, false);
            return new ViewHolder(view, true);
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.textview_row_1, parent, false);
        return new ViewHolder(view, false);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder.isAddNewOption) {
            holder.textView.setText("Agregar nuevo");
            //holder.textView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_dark));
            holder.itemView.setOnClickListener(v -> listener.onAddNewClick());
        } else {
            BaseRow item = items.get(position);
            holder.textView.setText(item.getNombre());
            //holder.textView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.primary_text_light));
            holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        boolean isAddNewOption;

        ViewHolder(View itemView, boolean isAddNewOption) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.text);
            this.isAddNewOption = isAddNewOption;
        }
    }
}