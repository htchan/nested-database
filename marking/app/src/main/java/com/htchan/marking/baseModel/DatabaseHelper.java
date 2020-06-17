package com.htchan.marking.baseModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.htchan.marking.MainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "storage.db";
    private static DatabaseHelper database = null;

    private ArrayList<String> tables = new ArrayList<>();

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        List<String> existTables = Arrays.asList(DatabaseLog.TABLE_NAME, Item.TABLE_NAME, Task.TABLE_NAME);
        for (String table : getTables()) {
            Log.i("info", table);
            if (! existTables.contains(table)) {
                CustomizeItem.TABLES.put(table, getFields(table));
            }
        }
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
    public String createTableStatement(String table, List<String> values, List<String> types) {
        if (values.size() != types.size()) {
            return null;
        }
        String sql = "CREATE TABLE IF NOT EXISTS " + table + " (";
        for (int i = 0; i < values.size(); ++i) {
            sql += values.get(i) + " " + types.get(i);
            if(i < values.size() - 1) {
                sql += ", ";
            }
        }
        sql += ")";
        return sql;
    }
    public void saveTable(String sql) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        saveLog("create", sql);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = createTableStatement(DatabaseLog.TABLE_NAME,
                Arrays.asList(DatabaseLog.COL_TIMESTAMP, DatabaseLog.COL_OPERATION, DatabaseLog.COL_OBJ),
                Arrays.asList("LONG", "TEXT", "TEXT"));
        db.execSQL(sql);

        DatabaseLog log = new DatabaseLog(0, "create", sql);
        db.insert(DatabaseLog.TABLE_NAME, null, log.toValues());
        sql = createTableStatement(Item.TABLE_NAME,
                Arrays.asList(Item.COL_ID, Item.COL_TITLE, Item.COL_PARENT_TABLE, Item.COL_PARENT_ID),
                Arrays.asList("LONG", "TEXT", "TEXT", "LONG"));
        db.execSQL(sql);
        log = new DatabaseLog(1, "create", sql);
        db.insert(DatabaseLog.TABLE_NAME, null, log.toValues());
        AbstractItem item = new Item("Main", Item.TABLE_NAME, -1);
        item.id = 0;
        ContentValues values = item.toValues();
        db.insert(Item.TABLE_NAME, null, values);
        log = new DatabaseLog(2, "insert", item.toString());
        db.insert(DatabaseLog.TABLE_NAME, null, log.toValues());
        sql = createTableStatement(Task.TABLE_NAME,
                Arrays.asList(Task.COL_ID, Task.COL_TITLE, Task.COL_PARENT_TABLE, Task.COL_PARENT_ID, Task.COL_CHECKED, Task.COL_DETAILS),
                Arrays.asList("LONG", "TEXT", "TEXT", "LONG", "INTEGER", "TEXT"));
        db.execSQL(sql);
        log = new DatabaseLog(3, "create", sql);
        db.insert(DatabaseLog.TABLE_NAME, null, log.toValues());
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    public ArrayList<String> getTables() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        if(cursor.moveToFirst()) {
            do {
                String table = cursor.getString(cursor.getColumnIndex("name"));
                if(!tables.contains(table) && !table.equals("android_metadata") && (!table.startsWith("__"))) {
                    tables.add(table);
                }
            } while(cursor.moveToNext());
        }
        return tables;
    }
    public ArrayList<String> getFields(String table) {
        // get the fields and types in string pair and put into arraylist
        String sql = "SELECT * FROM " + table;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<String> fields = new ArrayList<String>(Arrays.asList(cursor.getColumnNames()));
        fields.remove(AbstractItem.COL_ID);
        fields.remove(AbstractItem.COL_TITLE);
        fields.remove(AbstractItem.COL_PARENT_TABLE);
        fields.remove(AbstractItem.COL_PARENT_ID);
        return fields;
    }
    public Cursor getCursorByTable(String table) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table, new String[]{});
        return cursor;
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
    public long saveRecord(String table, AbstractItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = item.toValues();
        long id = getMaxId(table) + 1;
        values.put(AbstractItem.COL_ID, id);
        db.insert(table, null, values);
        saveLog("insert",  item.toString().replace("&id=-1&", "&id="+id+"&"));

        return id;
    }
    public void updateRecord(String table, AbstractItem item) {
        ContentValues values = item.toValues();
        long id = (long) values.get("id");
        if ((!getCursorById(table, id).moveToFirst())&&(id > 0)) {
            saveRecord(table, item);
        }
        SQLiteDatabase db = getWritableDatabase();
        db.update(table, values, "id=?", new String[] {Long.toString(id)});
        saveLog("update", item.toString());
    }
    public void delete(String table, AbstractItem item) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(table, "id=?", new String[] {Long.toString(item.getId())});
        saveLog("delete", item.toString());
    }
    public void deleteChild(String table, AbstractItem item) {
        for(AbstractItem childItem : Item.getChildren(table, item.id)) {
            deleteChild(childItem.getTable(), childItem);
        }
        delete(table, item);
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
    public ArrayList<AbstractItem> search(String target) {
        // loop all tables
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<AbstractItem> results = new ArrayList<>();
        for (String table : getTables()) {
            String query = "(title like \"%to%\")";
            for (String field : getFields(table)) {
                query += " or (" + field + " like \"%" + target + "%\"" + ")";
            }
            Log.i("statement", query);
            Cursor cursor = db.rawQuery("SELECT * FROM " + table + " WHERE " + query, new String[]{});
            Log.i("count", Integer.toString(cursor.getCount()));
            if (cursor.moveToFirst()) {
                do {
                    Item temp = new Item("", "", -1);
                    temp.fromCursor(cursor);
                    results.add(temp);
                } while (cursor.moveToNext());
            }
        }
        Log.i("info", Integer.toString(results.size()));
        return results;
    }

    private void saveLog(String operation, String str) {
        long timestamp = new Date().getTime();
        DatabaseLog log = new DatabaseLog(timestamp, operation, str);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(DatabaseLog.TABLE_NAME, null, log.toValues());
    }

    public void exportSQL(String packageName) {
        // set output path
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + packageName + "/files/backup.db";
        File f = new File(path);
        Log.i("create dir", Boolean.toString(f.mkdirs()));
        if (f.exists()) {
            f.delete();
        }
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(f, null);
        // create log table
        db.execSQL(createTableStatement(DatabaseLog.TABLE_NAME,
                Arrays.asList(DatabaseLog.COL_TIMESTAMP, DatabaseLog.COL_OPERATION, DatabaseLog.COL_OBJ),
                Arrays.asList("LONG", "TEXT", "TEXT")));
        // create item table
        db.execSQL(createTableStatement(Item.TABLE_NAME,
                Arrays.asList(Item.COL_ID, Item.COL_TITLE, Item.COL_PARENT_TABLE, Item.COL_PARENT_ID),
                Arrays.asList("LONG", "TEXT", "TEXT", "LONG")));
        // create task table
        db.execSQL(createTableStatement(Task.TABLE_NAME,
                Arrays.asList(Task.COL_ID, Task.COL_TITLE, Task.COL_PARENT_TABLE, Task.COL_PARENT_ID, Task.COL_CHECKED, Task.COL_DETAILS),
                Arrays.asList("LONG", "TEXT", "TEXT", "LONG", "INTEGER", "TEXT")));
        // save log record
        Cursor cursor = getCursorByTable(DatabaseLog.TABLE_NAME);
        if (cursor.moveToFirst()) {
            do {
                db.insert(DatabaseLog.TABLE_NAME, null, DatabaseLog.fromCursor(cursor).toValues());
            } while (cursor.moveToNext());
        }
        // save all other tables
        for (String table : getTables()) {
            if (! Arrays.asList(DatabaseLog.TABLE_NAME, Item.TABLE_NAME, Task.TABLE_NAME).contains(table)) {
                List<String> fields = new ArrayList<String>(Arrays.asList(AbstractItem.COL_ID, AbstractItem.COL_TITLE, AbstractItem.COL_PARENT_TABLE, AbstractItem.COL_PARENT_ID));
                fields.addAll(getFields(table));
                Log.i("table : ", fields.toString());
                List<String> types = new ArrayList<String>(Arrays.asList("LONG", "TEXT", "TEXT", "LONG"));
                for (int i = types.size(); i < fields.size(); ++i) {
                    types.add("TEXT");
                }
                db.execSQL(createTableStatement(table, fields, types));
            }
            cursor = getCursorByTable(table);
            if (cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndex(AbstractItem.COL_ID));
                    switch (table) {
                        case Item.TABLE_NAME:
                            db.insert(table, null, new Item(id).toValues());
                            break;
                        case Task.TABLE_NAME:
                            db.insert(table, null, new Task(id).toValues());
                            break;
                        default:
                            db.insert(table, null, new CustomizeItem(table, id).toValues());
                    }
                } while (cursor.moveToNext());
            }
        }
        db.close();
    }
    public boolean loadSQL(String packageName) {
        //TODO load database from .sql file
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + packageName + "/files/backup.db";
        File f = new File(path);
        if (! f.exists()) {
            return false;
        }
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(f, null);
        // TODO read record from database
        // TODO read tables
        ArrayList<String> tables = new ArrayList();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        if (cursor.moveToFirst()) {
            do {
               //TODO put table into arraylist
                tables.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        //TODO delete old record and customerize table
        SQLiteDatabase localDB = getWritableDatabase();
        localDB.execSQL("DELETE FROM " + Item.TABLE_NAME);
        localDB.execSQL("DELETE FROM " + Task.TABLE_NAME);
        localDB.execSQL("DELETE FROM " + DatabaseLog.TABLE_NAME);
        for (String table : CustomizeItem.TABLES.keySet()) {
            localDB.execSQL("DROP TABLE " +table);
        }
        tables.remove("sqlite_sequence");
        tables.remove("android_metadata");
        for (String table : tables) {
            cursor = db.rawQuery("SELECT * FROM " + table, null);
            switch (table) {
                case DatabaseLog.TABLE_NAME:
                    if (cursor.moveToFirst()) {
                        do {
                            ContentValues values = new ContentValues();
                            values.put(DatabaseLog.COL_TIMESTAMP, cursor.getLong(cursor.getColumnIndex(DatabaseLog.COL_TIMESTAMP)));
                            values.put(DatabaseLog.COL_OPERATION, cursor.getString(cursor.getColumnIndex(DatabaseLog.COL_OPERATION)));
                            values.put(DatabaseLog.COL_OBJ, cursor.getString(cursor.getColumnIndex(DatabaseLog.COL_OBJ)));
                            localDB.insert(table,null,  values);
                        } while (cursor.moveToNext());
                    }
                    break;
                case Item.TABLE_NAME:
                    if (cursor.moveToFirst()) {
                        do {
                            ContentValues values = new ContentValues();
                            values.put(AbstractItem.COL_ID, cursor.getLong(cursor.getColumnIndex(AbstractItem.COL_ID)));
                            values.put(AbstractItem.COL_TITLE, cursor.getString(cursor.getColumnIndex(AbstractItem.COL_TITLE)));
                            values.put(AbstractItem.COL_PARENT_TABLE, cursor.getString(cursor.getColumnIndex(AbstractItem.COL_PARENT_TABLE)));
                            values.put(AbstractItem.COL_PARENT_ID, cursor.getLong(cursor.getColumnIndex(AbstractItem.COL_PARENT_ID)));
                            localDB.insert(table,null,  values);
                        } while (cursor.moveToNext());
                    }
                    break;
                case Task.TABLE_NAME:
                    if (cursor.moveToFirst()) {
                        do {
                            ContentValues values = new ContentValues();
                            values.put(AbstractItem.COL_ID, cursor.getLong(cursor.getColumnIndex(AbstractItem.COL_ID)));
                            values.put(AbstractItem.COL_TITLE, cursor.getString(cursor.getColumnIndex(AbstractItem.COL_TITLE)));
                            values.put(AbstractItem.COL_PARENT_TABLE, cursor.getString(cursor.getColumnIndex(AbstractItem.COL_PARENT_TABLE)));
                            values.put(AbstractItem.COL_PARENT_ID, cursor.getLong(cursor.getColumnIndex(AbstractItem.COL_PARENT_ID)));
                            values.put(Task.COL_CHECKED, cursor.getInt(cursor.getColumnIndex(Task.COL_CHECKED)));
                            values.put(Task.COL_DETAILS, cursor.getString(cursor.getColumnIndex(Task.COL_DETAILS)));
                            localDB.insert(table,null,  values);
                        } while (cursor.moveToNext());
                    }
                    break;
                default:
                    ArrayList<String> fields = new ArrayList<>();
                    fields.addAll(Arrays.asList(cursor.getColumnNames()));
                    fields.remove(AbstractItem.COL_ID);
                    fields.remove(AbstractItem.COL_TITLE);
                    fields.remove(AbstractItem.COL_PARENT_TABLE);
                    fields.remove(AbstractItem.COL_PARENT_ID);
                    String sql = "CREATE TABLE IF NOT EXISTS " + table + " (" + AbstractItem.COL_ID + " LONG, " +
                                    AbstractItem.COL_TITLE + " TEXT, " + AbstractItem.COL_PARENT_TABLE + " TEXT, " +
                                    AbstractItem.COL_PARENT_ID + " LONG";
                    for (String field : fields) {
                        sql += ", " + field + " TEXT";
                    }
                    sql += ")";
                    localDB.execSQL(sql);
                    if (cursor.moveToFirst()) {
                        do {
                            ContentValues values = new ContentValues();
                            values.put(AbstractItem.COL_ID, cursor.getLong(cursor.getColumnIndex(AbstractItem.COL_ID)));
                            values.put(AbstractItem.COL_TITLE, cursor.getString(cursor.getColumnIndex(AbstractItem.COL_TITLE)));
                            values.put(AbstractItem.COL_PARENT_TABLE, cursor.getString(cursor.getColumnIndex(AbstractItem.COL_PARENT_TABLE)));
                            values.put(AbstractItem.COL_PARENT_ID, cursor.getLong(cursor.getColumnIndex(AbstractItem.COL_PARENT_ID)));
                            for (String field : fields) {
                                values.put(field, cursor.getString(cursor.getColumnIndex(field)));
                            }
                            localDB.insert(table,null,  values);
                        } while (cursor.moveToNext());
                    }
            }
        }
        MainActivity.mainActivity.reloadItemsPanel();
        return true;
    }
}
