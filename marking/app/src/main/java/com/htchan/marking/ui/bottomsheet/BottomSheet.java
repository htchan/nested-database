package com.htchan.marking.ui.bottomsheet;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.htchan.marking.MainActivity;
import com.htchan.marking.R;
import com.htchan.marking.baseModel.AbstractItem;
import com.htchan.marking.baseModel.CustomizeItem;
import com.htchan.marking.baseModel.DatabaseHelper;
import com.htchan.marking.baseModel.Item;
import com.htchan.marking.baseModel.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
                layouts.put(table, new ItemBottomSheet(context));
            } else if (table.equals(Task.TABLE_NAME)) {
                layouts.put(table, new TaskBottomSheet(context));
            } else {
                layouts.put(table, new CustomizeBottomSheet(context, table));
            }
        }
        currentLayout = 1;
        showLayout();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(layouts.get(tables.get(currentLayout)).valid()) {
                    switch (mode) {
                        case ADD:
                            add();
                            layouts.get(tables.get(currentLayout)).clear();
                            break;
                        case SAVE:
                            save();
                            MainActivity.mainActivity.hideKeyBoard();
                            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                            break;
                    }
                } else {
                    Toast.makeText(context, "Invalid Item", Toast.LENGTH_LONG).show();
                }
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
        if (! tables.get(currentLayout).equals("New Table")) {
            layouts.get(tables.get(currentLayout)).toItem().save();
            layouts.get(tables.get(currentLayout)).clear();
            Toast.makeText(context, "Item added successfully", Toast.LENGTH_SHORT).show();
            MainActivity.mainActivity.reloadItemsPanel();
        } else {
            String tableName = ((NewTableBottomSheet)layouts.get(tables.get(currentLayout))).tableName.getText().toString();
            List<String> fields = ((NewTableBottomSheet) layouts.get(tables.get(currentLayout))).getFields();
            List<String> types = new ArrayList<String>(Arrays.asList("LONG", "TEXT", "TEXT", "LONG"));
            for (int i = types.size(); i < fields.size(); ++i) {
                types.add("TEXT");
            }
            String sql = DatabaseHelper.DatabaseHelper().createTableStatement(tableName,
                    fields, types);
            DatabaseHelper.DatabaseHelper().saveTable(sql);
            CustomizeItem.TABLES.put(tableName, fields.subList(4, fields.size()));
            Log.i("info", CustomizeItem.TABLES.get(tableName).toString());
            tables.add(tableName);
            layouts.put(tableName, new CustomizeBottomSheet(context, tableName));
            Toast.makeText(context, "Table added successfully", Toast.LENGTH_LONG).show();
        }
    }
    private void save() {
        layouts.get(tables.get(currentLayout)).toItem().update();
        layouts.get(tables.get(currentLayout)).clear();
        Toast.makeText(context, "Item updated successfully", Toast.LENGTH_LONG).show();
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
    public Mode getMode() {return mode;}
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
