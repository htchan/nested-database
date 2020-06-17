package com.htchan.marking.ui.bottomsheet;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htchan.marking.MainActivity;
import com.htchan.marking.R;
import com.htchan.marking.baseModel.AbstractItem;
import com.htchan.marking.baseModel.CustomizeItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewTableBottomSheet extends AbstractBottomSheetLayout<CustomizeItem> {
    public TextView tableName;
    private ImageButton addButton;
    private LinearLayout extraRows;
    public ArrayList<EditText> rows = new ArrayList<>();
    public NewTableBottomSheet(Context context) {
        super(context);
        inflate(context, R.layout.new_table_bottom_sheet, this);
        tableName = findViewById(R.id.title);
        addButton = findViewById(R.id.addButton);
        extraRows = findViewById(R.id.extraRows);
        EditText row = generateEditText(MainActivity.mainActivity);
        extraRows.addView(row);
        rows.add(row);
        setAddButton();
    }
    private EditText generateEditText(Context context) {
        EditText edit = new EditText(context);
        edit.setTextColor(Color.rgb(255, 255, 255));
        edit.setHintTextColor(Color.rgb(255, 255, 255));
        edit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        edit.setHint("Field Name");
        return edit;
    }
    public List<String> getFields() {
        ArrayList<String> fields = new ArrayList<String>(Arrays.asList(AbstractItem.COL_ID, AbstractItem.COL_TITLE, AbstractItem.COL_PARENT_TABLE, AbstractItem.COL_PARENT_ID));
        for (EditText row : rows) {
            if (! row.getText().toString().trim().equals("")) {
                fields.add(row.getText().toString());
            }
        }
        return fields;
    }
    private void setAddButton() {
        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rows.size() < 6) {
                    EditText row = generateEditText(MainActivity.mainActivity);
                    extraRows.addView(row);
                    rows.add(row);
                }
            }
        });
    }
    @Override
    public void clear() {
        tableName.setText("");
        extraRows.removeAllViews();
        rows.clear();
        EditText row = generateEditText(MainActivity.mainActivity);
        extraRows.addView(row);
        rows.add(row);
    }
    @Override
    public void setContent(CustomizeItem item) {
        // there is no content allow to set
    }
    @Override
    public CustomizeItem toItem() {
        return null;
    }
    @Override
    public boolean valid() {
        if ((CustomizeItem.TABLES.containsKey(tableName)) ||
                (tableName.getText().toString().trim().length() <= 0)) {
            return false;
        }
        List<String> specialWords = Arrays.asList("ID", "TABLE", "TITLE", "PARENTTABLE", "PARENTID");
        boolean allEmpty = true;
        for (EditText row : rows) {
            if ((allEmpty) && (row.getText().toString().trim().length() > 0)) {
                allEmpty = false;
            }
            if ((specialWords.contains(row.getText().toString()))||(row.getText().toString().contains(" "))) {
                return false;
            }
        }
        return !allEmpty;
    }
}
