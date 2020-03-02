package com.htchan.marking.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Item extends AbstractItem{
    public static final String TABLE_NAME = "item";
    public static Item MAIN_ITEM = new Item(0);
    public List<AbstractItem> children = new ArrayList<>();
    public Item(long id) {
        fromCursor(DatabaseHelper.DatabaseHelper().getCursorById(TABLE_NAME, id));
        this.table = TABLE_NAME;
        children = getChildren(table, id);
    }
    public Item(String title, String parentTable, long parentId) {
        super(TABLE_NAME, -1, title, parentTable, parentId);
        //children = getChildren(table, id);
    }
    public static ArrayList<AbstractItem> getChildren(String table, long id) {
        ArrayList<AbstractItem> children = new ArrayList<>();
        //TODO extract the children by id
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
    @Override
    public void save() {
        id = DatabaseHelper.DatabaseHelper().saveRecord(Item.TABLE_NAME, this.toValues());
    }
    @Override
    public void update() {
        DatabaseHelper.DatabaseHelper().updateRecord(Item.TABLE_NAME, this.toValues());
    }
    @Override
    public void delete() {
        for(AbstractItem child : children) {
            child.parentTable = parentTable;
            child.parentId = parentId;
            child.update();
        }
        DatabaseHelper.DatabaseHelper().delete(Item.TABLE_NAME, id);
    }
    @Override
    public void deleteChild() {
        DatabaseHelper.DatabaseHelper().deleteChild(Item.TABLE_NAME, id);
    }
    @Override
    public void fromCursor(Cursor cursor) {
        super.fromCursor(cursor);
    }
    @Override
    public ContentValues toValues() {
        return super.toValues();
    }
    @Override
    public String toString() {
        return "Table: " + Item.TABLE_NAME + "\nID: " + this.id + "\nTitle: " + this.title;
    }
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Item)) {
            return false;
        } else {
            Item item = (Item) obj;
            return this.id == item.id;
        }
    }
}
