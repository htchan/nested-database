package com.htchan.marking.ui.bottomsheet;

import android.content.Context;
import android.graphics.Color;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.htchan.marking.MainActivity;
import com.htchan.marking.R;
import com.htchan.marking.baseModel.AbstractItem;
import com.htchan.marking.baseModel.CustomizeItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CustomizeBottomSheet extends AbstractBottomSheetLayout<CustomizeItem> {
    private ArrayList<EditText> fields = new ArrayList<>();
    private LinearLayout extraRows;
    private EditText title;
    private String tableName;
    private long id;
    public CustomizeBottomSheet(Context context, String table) {
        super(context);
        inflate(context, R.layout.customize_bottom_sheet, this);
        extraRows = findViewById(R.id.extraRows);
        tableName = table;
        id = -1;
        title = findViewById(R.id.title);
        for (String key : CustomizeItem.TABLES.get(table)) {
            if (Arrays.asList(AbstractItem.COL_ID, AbstractItem.COL_TITLE, AbstractItem.COL_PARENT_TABLE, AbstractItem.COL_PARENT_ID).contains(key)) {
                continue;
            }
            EditText row = generateRow(MainActivity.mainActivity, key, "");
            fields.add(row);
            extraRows.addView(row);
        }
    }
    private EditText generateRow(Context context, String hint, String value) {
        EditText edit = new EditText(context);
        edit.setTextColor(Color.rgb(255, 255, 255));
        edit.setHintTextColor(Color.rgb(255, 255, 255));
        edit.setHint(hint);
        edit.setText(value);
        return edit;
    }
    @Override
    public void clear() {
        title.setText("");
        id = -1;
        for (EditText edit : fields) {
            edit.setText("");
        }
    }
    @Override
    public void setContent(CustomizeItem item) {
        id = item.getId();
        title.setText(item.getTitle());
        for (EditText edit : fields) {
            edit.setText(item.getValues(edit.getHint().toString()));
        }
    }
    @Override
    public CustomizeItem toItem() {
        HashMap<String, String> values = new HashMap<>();
        for (EditText edit : fields) {
            values.put(edit.getHint().toString(), edit.getText().toString().replace("\n", "\\n"));
        }
        if(id < 0) {
            AbstractItem parent = MainActivity.mainActivity.parentItem;
            CustomizeItem item = new CustomizeItem(tableName, title.getText().toString(), parent.getTable(), parent.getId(), values);
            return item;
        } else {
            CustomizeItem item = new CustomizeItem(tableName, id);
            item.fields = values;
            return item;
        }
    }
    @Override
    public boolean valid() {
        String text = title.getText().toString();
        if (title.getText().toString().trim().length() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
