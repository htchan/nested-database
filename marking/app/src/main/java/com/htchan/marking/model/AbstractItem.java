package com.htchan.marking.model;


import android.content.ContentValues;
import android.database.Cursor;

public abstract class AbstractItem {
    public static final String COL_ID = "id";
    public static final String COL_TITLE="title";
    public static final String COL_PARENT_TABLE = "parentTable";
    public static final String COL_PARENT_ID = "parentId";
    protected String table;
    protected long id;
    public long parentId;
    public String title, parentTable;

    public AbstractItem() {
        this.table = null; this.id = -1;
        this.parentTable = null; this.parentId = -1;
        this.title = null;
    }
    public AbstractItem(String table, long id, String title, String parentTable, long parentId) {
        this.table = table;
        this.id = id;
        this.title = title;
        this.parentTable = parentTable;
        this.parentId = parentId;
    }

    public long getId() {return id;}
    public String getTable() {return table;}

    public void fromCursor(Cursor cursor) {
        cursor.moveToFirst();
        id = cursor.getLong(cursor.getColumnIndex(COL_ID));
        title = cursor.getString(cursor.getColumnIndex(COL_TITLE));
        parentTable = cursor.getString(cursor.getColumnIndex(COL_PARENT_TABLE));
        parentId = cursor.getLong(cursor.getColumnIndex(COL_PARENT_ID));
    }
    public ContentValues toValues() {
        ContentValues values = new ContentValues();
        values.put(COL_ID, id);
        values.put(COL_TITLE, title);
        values.put(COL_PARENT_TABLE, parentTable);
        values.put(COL_PARENT_ID, parentId);
        return values;
    }
    public abstract void save();
    public abstract void delete();
    public abstract void deleteChild();
    public abstract void update();
}
