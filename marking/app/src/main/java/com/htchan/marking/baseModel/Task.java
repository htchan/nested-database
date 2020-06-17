package com.htchan.marking.baseModel;

import android.content.ContentValues;
import android.database.Cursor;

public class Task extends AbstractItem {
    public static final String TABLE_NAME = "task";
    public static final String COL_CHECKED = "checked";
    public static final String COL_DETAILS = "details";
    public boolean checked;
    public String details;
    public Task(long id) {
        fromCursor(DatabaseHelper.DatabaseHelper().getCursorById(TABLE_NAME, id));
        this.table = TABLE_NAME;
    }
    public Task(String title, String parentTable, long parentId, boolean checked, String details) {
        super(TABLE_NAME, -1, title, parentTable, parentId);
        this.checked = checked;
        this.details = details;
    }
    public String getDetails() {
        return details.replace("\\n", "\n");
    }
    @Override
    public void save() {
        DatabaseHelper.DatabaseHelper().saveRecord(TABLE_NAME, this);
    }
    @Override
    public void delete() {
        DatabaseHelper.DatabaseHelper().delete(TABLE_NAME, this);
    }
    @Override
    public void deleteChild() {
        DatabaseHelper.DatabaseHelper().deleteChild(TABLE_NAME, this);
    }
    @Override
    public void update() {
        DatabaseHelper.DatabaseHelper().updateRecord(TABLE_NAME, this);
    }
    @Override
    public void fromCursor(Cursor cursor) {
        super.fromCursor(cursor);
        this.checked = cursor.getInt(cursor.getColumnIndex(COL_CHECKED)) == 1 ? true : false;
        this.details = cursor.getString(cursor.getColumnIndex(COL_DETAILS));
    }
    @Override
    public ContentValues toValues() {
        ContentValues values = super.toValues();
        values.put(COL_CHECKED, checked ? 1 : 0);
        values.put(COL_DETAILS, details);
        return values;
    }
    @Override
    public String toString() {
        String output = super.toString();
        if (output.equals("encode error")) {
            return output;
        }
        try{
            return "table=" + encode(TABLE_NAME) + "&" +output + "&" +
                    COL_CHECKED + "=" + encode(Boolean.toString(checked)) + "&" +
                    COL_DETAILS + "=" + encode(details);
        } catch (Exception e) {
            return "encode error";
        }
    }
    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof  Task)) {
            return false;
        } else {
            Task task = (Task) obj;
            return this.id == task.id;
        }
    }
}
