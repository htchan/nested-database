package com.htchan.marking;

import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.htchan.marking.baseModel.AbstractItem;
import com.htchan.marking.baseModel.Item;
import com.htchan.marking.baseModel.Task;
import com.htchan.marking.ui.AbstractItemView;

import java.util.ArrayList;

public class AbstraceItemAdapter extends Adapter {
    private class AbstractViewHolder extends ViewHolder {
        public AbstractItemView abstractItemView;
        public AbstractViewHolder(AbstractItemView abstractItemView) {
            super(abstractItemView);
            this.abstractItemView = abstractItemView;
        }
    }

    ArrayList<AbstractItem> items = new ArrayList<>();
    public AbstraceItemAdapter(ArrayList<AbstractItem> items) {
        this.items = items;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AbstractItemView abstractItemView = new AbstractItemView(parent.getContext());
        return new AbstractViewHolder(abstractItemView);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final AbstractViewHolder viewHolder = (AbstractViewHolder) holder;
        AbstractItem item = items.get(position);
        viewHolder.abstractItemView.setItem(item);
        switch (items.get(position).getTable()) {
            case Task.TABLE_NAME:
                viewHolder.abstractItemView.setLayout(Task.TABLE_NAME);
                viewHolder.abstractItemView.textView.setText(item.getTitle());
                viewHolder.abstractItemView.checkbox.setChecked(((Task) item).checked);
                viewHolder.abstractItemView.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        Task t = (Task) viewHolder.abstractItemView.getItem();
                        t.checked = b;
                        t.update();
                    }
                });
                break;
            default:
                viewHolder.abstractItemView.setLayout(Item.TABLE_NAME);
                viewHolder.abstractItemView.textView.setText(item.getTitle());
        }
        viewHolder.abstractItemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT));
    }
    @Override
    public int getItemCount() {
        return items.size();
    }
}
