package com.htchan.marking.ui.bottomsheet;

import android.content.ContentValues;
import android.content.Context;
import android.widget.EditText;

import com.htchan.marking.MainActivity;
import com.htchan.marking.R;
import com.htchan.marking.baseModel.AbstractItem;
import com.htchan.marking.baseModel.Task;

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
    @Override
    public void clear() {
        id = -1;
        title.setText("");
        details.setText("");
    }
    @Override
    public void setContent(Task item) {
        id = item.getId();
        title.setText(item.getTitle());
        details.setText(item.getDetails());
    }
    @Override
    public Task toItem() {
        if (id < 0) {
            AbstractItem parent = MainActivity.mainActivity.parentItem;
            Task t = new Task(title.getText().toString().trim().replace("\n", "\\n"), parent.getTable(), parent.getId(), false, details.getText().toString().trim().replace("\n", "\\n"));
            return t;
        } else {
            Task t = new Task(id);
            t.title = title.getText().toString().trim().replace("\n", "\\n");
            t.details = details.getText().toString().trim().replace("\n", "\\n");
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
