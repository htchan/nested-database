package com.htchan.marking.ui.bottomsheet;

import android.content.ContentValues;
import android.content.Context;
import android.widget.EditText;

import com.htchan.marking.MainActivity;
import com.htchan.marking.R;
import com.htchan.marking.model.AbstractItem;
import com.htchan.marking.model.Task;

public class TaskBottomSheet extends AbstractBottomSheetLayout<Task> {
    private long id;
    private EditText title, details;
    public TaskBottomSheet(Context context) {
        super(context);
        inflate(context, R.layout.task_bottom_sheet, this);
        id = -1;
        title = findViewById(R.id.title);
        details = findViewById(R.id.details);
    }
    public ContentValues toValues() {
        //TODO return the values from the layout
        ContentValues values = new ContentValues();
        return values;
    }
    @Override
    public void clear() {
        id = -1;
        title.setText("");
        details.setText("");
    }
    @Override
    public void setContent(Task item) {
        id = item.getId();
        title.setText(item.title);
        details.setText(item.details);
    }
    @Override
    public Task toItem() {
        if (id < 0) {
            AbstractItem parent = MainActivity.mainActivity.parentItem;
            Task t = new Task(title.getText().toString().trim(), parent.getTable(), parent.getId(), false, details.getText().toString().trim());
            return t;
        } else {
            Task t = new Task(id);
            t.title = title.getText().toString().trim();
            t.details = details.getText().toString().trim();
            return t;
        }
    }
    @Override
    public boolean valid() {
        if (title.getText().toString().trim().length() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
