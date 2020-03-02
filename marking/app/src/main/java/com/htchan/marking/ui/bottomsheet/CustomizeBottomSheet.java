package com.htchan.marking.ui.bottomsheet;

import android.content.ContentValues;
import android.content.Context;
import android.widget.LinearLayout;

import com.htchan.marking.R;
import com.htchan.marking.model.CustomizeItem;

public class CustomizeBottomSheet extends AbstractBottomSheetLayout<CustomizeItem> {
    public CustomizeBottomSheet(Context context, String table) {
        super(context);
        inflate(context, R.layout.customize_bottom_sheet, this);
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
    public void setContent(CustomizeItem item) {

    }
    @Override
    public CustomizeItem toItem() {
        return null;
    }
    @Override
    public boolean valid() {
        return false;
    }
}
