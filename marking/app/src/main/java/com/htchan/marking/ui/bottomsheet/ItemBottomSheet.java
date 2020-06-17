package com.htchan.marking.ui.bottomsheet;

import android.content.Context;
import android.widget.EditText;

import com.htchan.marking.MainActivity;
import com.htchan.marking.R;
import com.htchan.marking.baseModel.AbstractItem;
import com.htchan.marking.baseModel.Item;

public class ItemBottomSheet extends AbstractBottomSheetLayout<Item> {
    private long id;
    private EditText title;
    public ItemBottomSheet(Context context) {
        super(context);
        inflate(context, R.layout.item_bottom_sheet, this);
        id = -1;
        title = findViewById(R.id.title);
    }
    @Override
    public void clear() {
        id = -1;
        title.setText("");
    }
    @Override
    public void setContent(Item item) {
        id = item.getId();
        title.setText(item.getTitle());
    }
    @Override
    public Item toItem() {
        // return the values from this layout
        if(id < 0) {
            AbstractItem parent = MainActivity.mainActivity.parentItem;
            Item i = new Item(title.getText().toString().trim().replace("\n", "\\n"), parent.getTable(), parent.getId());
            return i;
        } else {
            Item i = new Item(id);
            i.title = title.getText().toString().trim().replace("\n", "\\n");
            return i;
        }
    }
    @Override
    public boolean valid() {
        if(title.getText().toString().trim().length() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
