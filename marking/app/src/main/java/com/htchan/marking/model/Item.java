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
    public Item(long id) {
        fromCursor(DatabaseHelper.DatabaseHelper().getCursorById(TABLE_NAME, id));
        this.table = TABLE_NAME;
    }
    public Item(String title, String parentTable, long parentId) {
        super(TABLE_NAME, -1, title, parentTable, parentId);
    }
    @Override
    public void save() {
        id = DatabaseHelper.DatabaseHelper().saveRecord(Item.TABLE_NAME, toValues());
    }
    @Override
    public void update() {
        DatabaseHelper.DatabaseHelper().updateRecord(Item.TABLE_NAME, toValues());
    }
    @Override
    public void delete() {
        for(AbstractItem child : getChildren(table, id)) {
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
        return "Table: " + Item.TABLE_NAME + "\nID: " + id + "\nTitle: " + title;
    }
    @Override
    public boolean equals(Object obj) {
        if(! (obj instanceof Item)) {
            return false;
        } else {
            Item item = (Item) obj;
            return this.id == item.id;
        }
    }
}
