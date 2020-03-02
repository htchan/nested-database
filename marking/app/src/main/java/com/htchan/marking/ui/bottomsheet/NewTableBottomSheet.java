package com.htchan.marking.ui.bottomsheet;

import android.content.Context;
import android.widget.LinearLayout;

import com.htchan.marking.R;
import com.htchan.marking.model.CustomizeItem;

import java.util.ArrayList;
import java.util.List;

public class NewTableBottomSheet extends AbstractBottomSheetLayout<CustomizeItem> {
    public NewTableBottomSheet(Context context) {
        super(context);
        inflate(context, R.layout.new_table_bottom_sheet, this);
    }
    public List<String> getValues() {
        List<String> values = new ArrayList<>();
        return values;
    }
    public List<String> getTypes() {
        List<String> types = new ArrayList<>();
        return types;
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
