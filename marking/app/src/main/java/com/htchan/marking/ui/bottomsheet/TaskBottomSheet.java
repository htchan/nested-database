package com.htchan.marking.ui.bottomsheet;

import android.content.ContentValues;
import android.content.Context;

import com.htchan.marking.R;
import com.htchan.marking.model.Task;

public class TaskBottomSheet extends AbstractBottomSheetLayout<Task> {
    public TaskBottomSheet(Context context) {
        super(context);
        inflate(context, R.layout.task_bottom_sheet, this);
    }
    public ContentValues toValues() {
        //TODO return the values from the layout
        ContentValues values = new ContentValues();
        return values;
    }
    @Override
    public void clear() {

    }
    @Override
    public void setContent(Task item) {

    }
    @Override
    public Task toItem() {
        return null;
    }
    @Override
    public boolean valid() {
        return false;
    }
}
