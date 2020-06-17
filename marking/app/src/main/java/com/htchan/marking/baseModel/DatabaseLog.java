package com.htchan.marking.baseModel;

import android.content.ContentValues;
import android.database.Cursor;

public class DatabaseLog {
    public static final String TABLE_NAME = "__logs";
    public static final String COL_TIMESTAMP = "timestamp";
    public static final String COL_OPERATION = "operation";
    public static final String COL_OBJ = "obj";
    private long timestamp;
    private String operation;
    private String object;
    public DatabaseLog(long timestamp, String operation, String object) {
        this.timestamp = timestamp;
        this.operation = operation;
        this.object = object;
    }
    public ContentValues toValues() {
        ContentValues values = new ContentValues();
        values.put(COL_TIMESTAMP, timestamp);
        values.put(COL_OPERATION, operation);
        values.put(COL_OBJ, object);
        return values;
    }
    public static DatabaseLog fromCursor(Cursor cursor) {
        long timestamp = cursor.getLong(cursor.getColumnIndex(COL_TIMESTAMP));
        String operation = cursor.getString(cursor.getColumnIndex(COL_OPERATION));
        String object = cursor.getString(cursor.getColumnIndex(COL_OBJ));
        DatabaseLog log = new DatabaseLog(timestamp, operation, object);
        return log;
    }
}
