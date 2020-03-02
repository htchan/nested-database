package com.htchan.marking;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.htchan.marking.model.AbstractItem;
import com.htchan.marking.ui.ItemView;

import java.util.ArrayList;

public class ItemAdapter extends Adapter {
    private class ItemViewHolder extends ViewHolder {
        public ItemView itemView;
        public ItemViewHolder(ItemView itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }

    ArrayList<AbstractItem> items = new ArrayList<>();
    public ItemAdapter(ArrayList<AbstractItem> items) {
        this.items = items;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemView itemView = new ItemView(parent.getContext());
        itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                                                                RecyclerView.LayoutParams.WRAP_CONTENT));
        return new ItemViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
        itemHolder.itemView.setTitle(items.get(position).title);
        itemHolder.itemView.setItem(items.get(position));
    }
    @Override
    public int getItemCount() {
        return items.size();
    }
}
