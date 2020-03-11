package com.htchan.marking.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.htchan.marking.MainActivity;
import com.htchan.marking.R;
import com.htchan.marking.model.AbstractItem;
import com.htchan.marking.ui.bottomsheet.AbstractBottomSheetLayout;
import com.htchan.marking.ui.bottomsheet.BottomSheet;

public class AbstractItemView extends LinearLayout {
    private Context context;
    private AbstractItem abstractItem;
    public AbstractItemView(final Context context) {
        super(context);
        this.context = context;
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO allow main activity go inside when clicked
                MainActivity.mainActivity.show(abstractItem);
            }
        });
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PopupMenu popup = new PopupMenu(context, AbstractItemView.this, Gravity.RIGHT);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.modify:
                                BottomSheet bottomSheet = MainActivity.mainActivity.bottomSheet;
                                bottomSheet.setMode(BottomSheet.Mode.SAVE);
                                bottomSheet.setLayout(abstractItem);
                                bottomSheet.behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                return true;
                            case R.id.move:
                                //TODO allow user to move item
                                return true;
                            case R.id.delete:
                                //TODO make a popup to confirm delete the item or not
                                abstractItem.delete();
                                MainActivity.mainActivity.reloadItemsPanel();
                                Toast.makeText(context, "Delete " + abstractItem.title, Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.deleteChild:
                                //TODO make a popup to confirm delete the item or not
                                abstractItem.deleteChild();
                                MainActivity.mainActivity.reloadItemsPanel();
                                Toast.makeText(context, "Delete " + abstractItem.title + "with children", Toast.LENGTH_SHORT).show();
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
    }
    public void setItem(AbstractItem abstractItem) {
        this.abstractItem = abstractItem;
    }
    public AbstractItem getItem() {
        return abstractItem;
    }
    public void setLayout(int layout) {
        View.inflate(context, layout, this);
    }
}
