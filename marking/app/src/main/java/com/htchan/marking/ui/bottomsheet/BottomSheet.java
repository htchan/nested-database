package com.htchan.marking.ui.bottomsheet;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.htchan.marking.MainActivity;
import com.htchan.marking.R;
import com.htchan.marking.model.AbstractItem;
import com.htchan.marking.model.DatabaseHelper;
import com.htchan.marking.model.Item;
import com.htchan.marking.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BottomSheet {
    public static enum Mode {ADD, SAVE};
    private Context context;
    public BottomSheetBehavior behavior;
    public int currentLayout;
    private LinearLayout content;
    private TextView tableTitle;
    private View add, left, right;
    private ArrayList<String> tables;
    private Map<String, AbstractBottomSheetLayout> layouts;
    private Mode mode;
    public BottomSheet(final Context context, View view) {
        this.context = context;
        content = view.findViewById(R.id.content);
        tableTitle = view.findViewById(R.id.tableTitle);
        add = view.findViewById(R.id.add);
        left = view.findViewById(R.id.left);
        right = view.findViewById(R.id.right);
        behavior = BottomSheetBehavior.from(view);
        mode = Mode.ADD;
        tables = new ArrayList<>();
        layouts = new HashMap<>();
        tables.add("New Table");
        layouts.put(tables.get(tables.size() -1), new NewTableBottomSheet(context));
        for(String table : DatabaseHelper.DatabaseHelper().getTables()) {
            tables.add(table);
            if(table.equals(Item.TABLE_NAME)) {
                layouts.put(tables.get(tables.size() - 1), new ItemBottomSheet(context));
            } else if (table.equals(Task.TABLE_NAME)) {
                layouts.put(tables.get(tables.size() - 1), new TaskBottomSheet(context));
            } else {
                layouts.put(tables.get(tables.size() - 1), new CustomizeBottomSheet(context, table));
            }
        }
        currentLayout = 1;
        showLayout();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO add the value to the table
                switch (mode) {
                    case ADD:
                        add();
                        break;
                    case SAVE:
                        save();
                        break;
                }
                MainActivity.mainActivity.reloadItemsPanel();
                MainActivity.mainActivity.hideKeyBoard();
                behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentLayout = (currentLayout + tables.size() - 1) % tables.size();
                Toast.makeText(context, Integer.toString(currentLayout), Toast.LENGTH_SHORT);
                showLayout();
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentLayout = (++currentLayout) % tables.size();
                Toast.makeText(context, Integer.toString(currentLayout), Toast.LENGTH_SHORT);
                showLayout();
            }
        });
    }
    private void add() {
        if (!tables.get(currentLayout).equals("New Table")) {
            if(layouts.get(tables.get(currentLayout)).valid()) {
                layouts.get(tables.get(currentLayout)).toItem().save();
                layouts.get(tables.get(currentLayout)).clear();
                Toast.makeText(context, "Item added successfully", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Invalid Item", Toast.LENGTH_LONG).show();
            }
        } else {

        }
    }
    private void save() {
        if(layouts.get(tables.get(currentLayout)).valid()) {
            layouts.get(tables.get(currentLayout)).toItem().update();
            layouts.get(tables.get(currentLayout)).clear();
            Toast.makeText(context, "Item updated successfully", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Invalid Item", Toast.LENGTH_LONG).show();
        }
        MainActivity.mainActivity.reloadItemsPanel();
    }

    private void showLayout() {
        tableTitle.setText(tables.get(currentLayout));
        content.removeAllViews();
        content.addView(layouts.get(tables.get(currentLayout)));
    }
    public void setMode(Mode mode) {
        this.mode = mode;
        switch (mode) {
            case ADD:
                ((Button) add).setText("Add");
                left.setVisibility(View.VISIBLE);
                right.setVisibility(View.VISIBLE);
                break;
            case SAVE:
                ((Button) add).setText("Save");
                left.setVisibility(View.GONE);
                right.setVisibility(View.GONE);
                break;
            default:
                return;
        }
    }
    public void setLayout(AbstractItem item) {
        currentLayout = tables.indexOf(item.getTable());
        layouts.get(tables.get(currentLayout)).setContent(item);
        showLayout();
    }
    public void clear() {
        for(String table : layouts.keySet()) {
            layouts.get(table).clear();
        }
    }
}
