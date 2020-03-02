package com.htchan.marking.ui.bottomsheet;

import android.content.Context;
import android.widget.LinearLayout;

import com.htchan.marking.model.AbstractItem;


public abstract class AbstractBottomSheetLayout<I extends AbstractItem> extends LinearLayout {
    public AbstractBottomSheetLayout(Context context) {
        super(context);
    }
    public abstract void clear();
    public abstract void setContent(I item);
    public abstract I toItem();
    public boolean valid() {
        return false;
    }
}
