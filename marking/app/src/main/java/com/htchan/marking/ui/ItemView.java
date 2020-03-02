package com.htchan.marking.ui;

import android.Manifest;
import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.htchan.marking.MainActivity;
import com.htchan.marking.R;
import com.htchan.marking.model.AbstractItem;
import com.htchan.marking.model.DatabaseHelper;
import com.htchan.marking.model.Item;
import com.htchan.marking.ui.bottomsheet.BottomSheet;

public class ItemView extends LinearLayout {
    private TextView titleView;
    private AbstractItem item;

    public ItemView(final Context context) {
        super(context);
        View.inflate(context, R.layout.item_view, this);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO allow main activity go inside when clicked
                MainActivity.mainActivity.show(item);
            }
        });
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PopupMenu popup = new PopupMenu(context, ItemView.this);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.modify:
                                //TODO open bottom sheet for user to change item content
                                BottomSheet bottomSheet = MainActivity.mainActivity.bottomSheet;
                                bottomSheet.setMode(BottomSheet.Mode.SAVE);
                                bottomSheet.setLayout(item);
                                bottomSheet.behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                return true;
                            case R.id.move:
                                //TODO allow user to move item
                                return true;
                            case R.id.delete:
                                //TODO make a popup to confirm delete the item or not
                                item.delete();
                                MainActivity.mainActivity.reloadItemsPanel();
                                Toast.makeText(context, "Delete " + item.title, Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.deleteChild:
                                //TODO make a popup to confirm delete the item or not
                                item.deleteChild();
                                MainActivity.mainActivity.reloadItemsPanel();
                                Toast.makeText(context, "Delete " + item.title, Toast.LENGTH_SHORT).show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
                return true;
            }
        });
        titleView = findViewById(R.id.title);
    }
    public void setTitle(String title) {
        titleView.setText(title);
    }
    public void setItem(AbstractItem item) {
        this.item = item;
    }
}
