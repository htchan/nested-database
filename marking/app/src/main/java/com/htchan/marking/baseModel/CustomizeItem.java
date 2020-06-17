package com.htchan.marking.baseModel;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.HashMap;
import java.util.List;

public class CustomizeItem extends AbstractItem {
    public static HashMap<String, List<String>> TABLES = new HashMap();
    public HashMap<String, String> fields = new HashMap();
    public CustomizeItem(String table, long id) {
        this.table = table;
        fromCursor(DatabaseHelper.DatabaseHelper().getCursorById(table, id));
    }
    public CustomizeItem(String table, String title,  String parentTable, long parentId, HashMap<String, String> fields) {
        super(table, -1, title, parentTable, parentId);
        for (String key : TABLES.get(table)) {
            this.fields.put(key, fields.get(key));
        }
    }
    public String getValues(String field) {
        return fields.get(field).replace("\\n", "\n");
    }
    @Override
    public void save() {
        DatabaseHelper.DatabaseHelper().saveRecord(table, this);
    }
    @Override
    public void delete() {
        DatabaseHelper.DatabaseHelper().delete(table, this);
    }
    @Override
    public void deleteChild() {
        DatabaseHelper.DatabaseHelper().deleteChild(table, this);
    }
    @Override
    public void update() {
        DatabaseHelper.DatabaseHelper().updateRecord(table, this);
    }
    @Override
    public void fromCursor(Cursor cursor) {
        super.fromCursor(cursor);
        for (String key : TABLES.get(table)) {
            fields.put(key, cursor.getString(cursor.getColumnIndex(key)));
        }
    }
    @Override
    public ContentValues toValues() {
        ContentValues values = super.toValues();
        for (String key : TABLES.get(table)) {
            values.put(key, fields.get(key));
        }
        return values;
    }
    @Override
    public String toString() {
        String output = super.toString();
        if (output.equals("encode error")) {
            return output;
        }
        try{
            output = "table=" + encode(table) + "&" +output;
            for (String key : fields.keySet()) {
                output += "&" + key + "=" + encode(fields.get(key));
            }
            return output;
        } catch (Exception e) {
            return "encode error";
        }
    }
    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof CustomizeItem)) {
            return false;
        } else {
            CustomizeItem item = (CustomizeItem) obj;
            return ((this.table == item.table) && (this.id == item.id));
        }
    }
}
