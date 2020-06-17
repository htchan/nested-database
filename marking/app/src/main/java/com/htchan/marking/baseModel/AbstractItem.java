package com.htchan.marking.baseModel;


import android.content.ContentValues;
import android.database.Cursor;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

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

    public long getId() {
        return id;
    }
    public String getTable() {
        return table;
    }
    public String getTitle() {
        return title.replace("\\n", "\n");
    }
    public static ArrayList<AbstractItem> getChildren(String table, long id) {
        ArrayList<AbstractItem> children = new ArrayList<>();
        // extract the children by id
        DatabaseHelper db = DatabaseHelper.DatabaseHelper();
        Iterator<String> i = db.getTables().iterator();
        while (i.hasNext()) {
            String searchTable = i.next();
            Cursor cursor = db.getCursorByParent(searchTable, table, id);
            if(cursor.moveToFirst()) {
                do {
                    long childId = cursor.getLong(cursor.getColumnIndex(AbstractItem.COL_ID));
                    switch(searchTable) {
                        case Item.TABLE_NAME:
                            children.add(new Item(childId));
                            break;
                        case Task.TABLE_NAME:
                            children.add(new Task(childId));
                            break;
                        default:
                            children.add(new CustomizeItem(searchTable, childId));
                    }
                } while (cursor.moveToNext());
            }
        }
        return children;
    }
    protected String encode(String input) throws java.io.UnsupportedEncodingException {
        return URLEncoder.encode(input, "UTF-8");
    }
    protected String decode(String input) throws java.io.UnsupportedEncodingException {
        return URLDecoder.decode(input, "UTF-8");
    }
    public static ContentValues fromString(String str) {
        if (str.equals("encode error")) {
            return null;
        } else {
            //todo change the URL encoded string to content value
            ContentValues values = new ContentValues();
            return values;
        }
    }
    @Override
    public String toString() {
        try {
            return COL_ID + "=" + encode(Long.toString(id)) + "&" +
                    COL_PARENT_TABLE + "=" + encode(parentTable) + "&" +
                    COL_PARENT_ID + "=" + encode(Long.toString(parentId)) + "&" +
                    COL_TITLE + "=" + encode(title);
        } catch (Exception e) {
            return "encode error";
        }
    }
    public void fromCursor(Cursor cursor) {
        if (cursor.getPosition() < 0) {
            cursor.moveToFirst();
        }
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
