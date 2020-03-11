package com.htchan.marking.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.strictmode.SqliteObjectLeakedViolation;

import com.htchan.marking.ui.bottomsheet.AbstractBottomSheetLayout;

import java.lang.reflect.AnnotatedElement;
import java.sql.SQLData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "storage.db";
    private static DatabaseHelper database = null;

    private ArrayList<String> tables = new ArrayList<>();

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }
    static public DatabaseHelper DatabaseHelper(Context context) {
        if(database == null) {
            database = new DatabaseHelper(context);
        }
        return database;
    }
    static public DatabaseHelper DatabaseHelper() {
        return database;
    }
    public void createTable(SQLiteDatabase db, String table, List<String> values, List<String> types) {
        if (values.size() != types.size()) {
            return;
        }
        String sql = "CREATE TABLE IF NOT EXISTS " + table + " (";
        for (int i = 0; i < values.size(); ++i) {
            sql += values.get(i) + " " + types.get(i);
            if(i < values.size() - 1) {
                sql += ", ";
            }
        }
        sql += ")";
        db.execSQL(sql);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db, Item.TABLE_NAME,
                    Arrays.asList(Item.COL_ID, Item.COL_TITLE, Item.COL_PARENT_TABLE, Item.COL_PARENT_ID),
                    Arrays.asList("LONG", "TEXT", "TEXT", "LONG"));
        ContentValues values = new Item("Main", Item.TABLE_NAME, -1).toValues();
        createTable(db, Task.TABLE_NAME,
                Arrays.asList(Task.COL_ID, Task.COL_TITLE, Task.COL_PARENT_TABLE, Task.COL_PARENT_ID, Task.COL_CHECKED, Task.COL_DETAILS),
                Arrays.asList("LONG", "TEXT", "TEXT", "LONG", "INTEGER", "TEXT"));
        values.put(Item.COL_ID, 0);
        db.insert(Item.TABLE_NAME, null, values);
        //createTable(Task.CreateTableSql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    public ArrayList<String> getTables() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        if(cursor.moveToFirst()) {
            do {
                String table = cursor.getString(cursor.getColumnIndex("name"));
                if(!tables.contains(table) && !table.equals("android_metadata")) {
                    tables.add(table);
                }
            } while(cursor.moveToNext());
        }
        return tables;
    }
    public Cursor getCursorById(String table, long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table + " WHERE id=?", new String[]{Long.toString(id)});
        return cursor;
    }
    public Cursor getCursorByParent(String table, String parentTable, long parentId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table + " WHERE "
                                            + AbstractItem.COL_PARENT_TABLE + "=? and "
                                            + AbstractItem.COL_PARENT_ID + "=?",
                                    new String[] {parentTable, Long.toString(parentId)});
        return cursor;
    }
    public long saveRecord(String table, ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();
        long id = getMaxId(table) + 1;
        values.put(AbstractItem.COL_ID, id);
        db.insert(table, null, values);
        return id;
    }
    public void updateRecord(String table, ContentValues values) {
        long id = (long) values.get("id");
        if(!getCursorById(table, id).moveToFirst()) {
            saveRecord(table, values);
        }
        SQLiteDatabase db = getWritableDatabase();
        db.update(table, values, "id=?", new String[] {Long.toString(id)});
    }
    public void delete(String table, long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(table, "id=?", new String[] {Long.toString(id)});
    }
    public void deleteChild(String table, long id) {
        for(AbstractItem item : Item.getChildren(table, id)) {
            deleteChild(item.getTable(), item.getId());
        }
        delete(table, id);
    }
    long getMaxId(String table) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table + " ORDER BY id DESC", null);
        if(cursor.moveToFirst()) {
            return cursor.getLong(cursor.getColumnIndex(AbstractItem.COL_ID));
        } else {
            return 1;
        }
    }
    public void export() {
        //TODO export the database history to a .sql file
    }
    public void load() {
        //TODO load database history from .sql file
    }
}
