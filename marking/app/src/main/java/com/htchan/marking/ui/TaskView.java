package com.htchan.marking.ui;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htchan.marking.R;

public class TaskView extends LinearLayout {
    private CheckBox checkbox;
    private TextView title;

    public TaskView(Context context) {
        super(context);
        View.inflate(context, R.layout.task_view, this);
        checkbox = findViewById(R.id.finished);
        title = findViewById(R.id.title);
    }
}
