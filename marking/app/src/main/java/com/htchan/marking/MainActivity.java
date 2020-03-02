package com.htchan.marking;

import android.content.Context;
import android.os.Bundle;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.htchan.marking.model.AbstractItem;
import com.htchan.marking.model.DatabaseHelper;
import com.htchan.marking.model.Item;
import com.htchan.marking.ui.bottomsheet.BottomSheet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static MainActivity mainActivity;

    private DatabaseHelper db;
    private TextView heading;
    private RecyclerView itemsPanel;
    private RecyclerView.Adapter itemAdapter;
    private boolean backPressed;
    public AbstractItem parentItem;
    public BottomSheet bottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = DatabaseHelper.DatabaseHelper(this);
        mainActivity = this;
        itemsPanel = findViewById(R.id.itemsPanel);
        heading = findViewById(R.id.heading);
        show(Item.MAIN_ITEM);
        bottomSheet = new BottomSheet(this, findViewById(R.id.bottomSheet));
        buildBottomSheet();
        Log.i("log", Integer.toString(itemAdapter.getItemCount()));

    }
    @Override
    protected void onStart() {
        super.onStart();
        this.backPressed = false;
    }
    @Override
    public void onBackPressed() {
        if (bottomSheet.behavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            bottomSheet.behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else if (!parentItem.equals(Item.MAIN_ITEM)) {
            switch (parentItem.parentTable) {
                case Item.TABLE_NAME:
                    Log.d("show", Long.toString(parentItem.parentId));
                    show(new Item(parentItem.parentId));
                    break;
            }
        } else {
            if (backPressed) {
                super.onBackPressed();
            } else {
                backPressed = true;
                Toast.makeText(this, "Please BACK again to exit", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        backPressed = false;
                    }
                }, 1000);
            }
        }
    }

    private void setShareBotton() {
        View share = findViewById(R.id.share);
        //TODO allow user to eport or import database by .sql file
    }
    private void updateHeading() {
        heading.setText(parentItem.title);
    }
    private void buildBottomSheet() {
        bottomSheet.behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open bottom sheet
                if(bottomSheet.behavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheet.clear();
                    bottomSheet.setMode(BottomSheet.Mode.ADD);
                    bottomSheet.behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    bottomSheet.behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });
    }
    public void show(AbstractItem parent) {
        parentItem = parent;
        updateHeading();
        reloadItemsPanel();
    }
    public void reloadItemsPanel() {
        Log.i("info", Item.getChildren(parentItem.getTable(), parentItem.getId()).toString());
        itemAdapter = new ItemAdapter(Item.getChildren(parentItem.getTable(), parentItem.getId()));
        itemsPanel.setAdapter(itemAdapter);
        itemsPanel.setLayoutManager(new LinearLayoutManager(this));
    }
    public void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
