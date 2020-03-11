package com.htchan.marking;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.htchan.marking.model.AbstractItem;
import com.htchan.marking.model.Task;
import com.htchan.marking.ui.AbstractItemView;

import java.util.ArrayList;

public class AbstraceItemAdapter extends Adapter {
    private class AbstractViewHolder extends ViewHolder {
        public AbstractItemView abstractItemView;
        public AbstractViewHolder(AbstractItemView abstractItemView) {
            super(abstractItemView);
            this.abstractItemView = abstractItemView;
        }
        public View getView(int id) {
            return abstractItemView.findViewById(id);
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
                viewHolder.abstractItemView.setLayout(R.layout.task_view);
                ((TextView) viewHolder.getView(R.id.title)).setText(item.title);
                CheckBox checkbox = (CheckBox) viewHolder.getView(R.id.checked);
                checkbox.setChecked(((Task) item).checked);
                checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        Task t = (Task) viewHolder.abstractItemView.getItem();
                        t.checked = b;
                        t.update();
                    }
                });
                break;
            default:
                viewHolder.abstractItemView.setLayout(R.layout.item_view);
                ((TextView) viewHolder.getView(R.id.title)).setText(item.title);
        }
        viewHolder.abstractItemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT));
    }
    @Override
    public int getItemCount() {
        return items.size();
    }
}
