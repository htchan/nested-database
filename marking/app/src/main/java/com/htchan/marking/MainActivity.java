package com.htchan.marking;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.htchan.marking.baseModel.AbstractItem;
import com.htchan.marking.baseModel.CustomizeItem;
import com.htchan.marking.baseModel.DatabaseHelper;
import com.htchan.marking.baseModel.Item;
import com.htchan.marking.baseModel.Task;
import com.htchan.marking.ui.bottomsheet.BottomSheet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.util.Log.i;

public class MainActivity extends AppCompatActivity {
    public static MainActivity mainActivity;
    public static Item MAIN_ITEM;

    private DatabaseHelper db;
    private FloatingActionButton floatButton;
    private TextView heading, headingTable;
    private View headingDiv;
    private LinearLayout detailsPanel;
    private RecyclerView itemsPanel;
    private RecyclerView.Adapter itemAdapter;
    private LinearLayout searchBar;
    private LinearLayout movingRow;
    private boolean backPressed;
    private boolean movingConfirm;
    public AbstractItem parentItem;
    public BottomSheet bottomSheet;
    public AbstractItem movingItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = DatabaseHelper.DatabaseHelper(this);
        mainActivity = this;
        MAIN_ITEM = new Item(0);
        floatButton = findViewById(R.id.fab);
        heading = findViewById(R.id.heading);
        heading.setMovementMethod(new ScrollingMovementMethod());
        heading.requestFocus();
        headingTable = findViewById(R.id.headingTable);
        headingDiv = findViewById(R.id.headingDiv);
        itemsPanel = findViewById(R.id.itemsPanel);
        detailsPanel = findViewById(R.id.detailsPanel);
        searchBar = findViewById(R.id.searchBar);
        movingRow = findViewById(R.id.movingRow);
        bottomSheet = new BottomSheet(this, findViewById(R.id.bottomSheet));
        buildBottomSheet();
        setShareBotton();
        buildSearchBar();
        buildMovingRow();
        show(MAIN_ITEM);
        movingItem = null;
        movingConfirm = false;
        Log.i("info", CustomizeItem.TABLES.toString());
    }
    @Override
    protected void onStart() {
        super.onStart();
        this.backPressed = false;
    }
    @Override
    public void onBackPressed() {
        if (bottomSheet.behavior.getState() != BottomSheetBehavior.STATE_HIDDEN) { // collapse bottom sheet
            bottomSheet.behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else if (!parentItem.equals(MAIN_ITEM)) { // show parent
            switch (parentItem.parentTable) {
                case Item.TABLE_NAME:
                    Log.d("show item", Long.toString(parentItem.parentId));
                    show(new Item(parentItem.parentId));
                    break;
                case Task.TABLE_NAME:
                    Log.d("show task", Long.toString(parentItem.parentId));
                    show(new Task(parentItem.parentId));
                default:
                    Log.d("show custom item", parentItem.parentTable + "\t" + Long.toString(parentItem.parentId));
                    show(new CustomizeItem(parentItem.parentTable, parentItem.parentId));
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
        final View share = findViewById(R.id.share);
        //TODO allow user to eport or import database by .sql file
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(MainActivity.this, share, Gravity.RIGHT);
                popup.getMenuInflater().inflate(R.menu.share_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.exportSql:
                                DatabaseHelper.DatabaseHelper().exportSQL(MainActivity.this.getPackageName());
                                return true;
                            case R.id.importSql:
                                DatabaseHelper.DatabaseHelper().loadSQL(MainActivity.this.getPackageName());
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });
    }
    private void updateHeading() {
        if (parentItem.getTable().equals(Item.TABLE_NAME)) {
            headingTable.setVisibility(View.GONE);
            headingDiv.setVisibility(View.GONE);
            heading.setText(parentItem.title);
        } else {
            headingTable.setVisibility(View.VISIBLE);
            headingDiv.setVisibility(View.VISIBLE);
            headingTable.setText(parentItem.getTable());
            heading.setText(parentItem.title);
        }
    }
    private void buildBottomSheet() {
        bottomSheet.behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open bottom sheet
                if(bottomSheet.behavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheet.clear();
                    bottomSheet.setMode(BottomSheet.Mode.ADD);
                    bottomSheet.behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    hideKeyBoard();
                    bottomSheet.behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });
        hideKeyBoard();
    }
    private void buildSearchBar() {
        final TextView queryTextView = searchBar.findViewById(R.id.queryTextView);
        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchBar.getVisibility() == View.VISIBLE) {
                    if (searchBar.hasFocus()) {
                        hideKeyBoard();
                    }
                    searchBar.setVisibility(View.GONE);
                    updateHeading();
                    show(parentItem);
                } else {
                    searchBar.setVisibility(View.VISIBLE);
                    searchBar.requestFocus();
                }
            }
        });
        findViewById(R.id.confirmSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO start search by the content when button click
                headingTable.setVisibility(View.GONE);
                headingDiv.setVisibility(View.GONE);
                heading.setText("Search Result");
                if (queryTextView.getText().toString().equals("")) {
                    reloadItemsPanel(new ArrayList<AbstractItem>());
                    return;
                } else {
                    reloadItemsPanel(db.search(queryTextView.getText().toString()));
                    hideKeyBoard();
                }
            }
        });
    }
    private void buildMovingRow() {
        findViewById(R.id.confirmMove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movingConfirm = true;
                moveMode(null);
            }
        });
        findViewById(R.id.cancelMove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                movingConfirm = false;
                moveMode(null);
            }
        });
    }
    public void show(AbstractItem parent) {
        parentItem = parent;
        if (bottomSheet.getMode() == BottomSheet.Mode.SAVE) {
            bottomSheet.behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
        updateHeading();
        reloadItemsPanel();
        detailsPanel.removeAllViews();
        if(parent.getTable().equals(Task.TABLE_NAME)) {
            Task t = (Task) parentItem;
            detailsPanelAddRow(Task.COL_DETAILS, t.getDetails());
        } else if (! parent.getTable().equals(Item.TABLE_NAME)) {
            // TODO show the extra fields ot customer item
            CustomizeItem c = (CustomizeItem) parentItem;
            for (String key : CustomizeItem.TABLES.get(c.getTable())) {
                detailsPanelAddRow(key, c.getValues(key));
            }
        }
    }
    private void detailsPanelAddRow(String fieldStr, String contentStr) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        TextView field = new TextView(this);
        field.setText(fieldStr);
        field.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        field.setTextColor(Color.WHITE);
        field.setPadding(8, 0, 5, 0);
        View divider = new View(this);
        divider.setLayoutParams(new LinearLayout.LayoutParams(2, ViewGroup.LayoutParams.MATCH_PARENT));
        divider.setBackgroundColor(Color.WHITE);
        TextView content = new TextView(this);
        content.setText(contentStr);
        content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        content.setTextColor(Color.WHITE);
        content.setPadding(5, 0, 8, 0);
        row.addView(field);
        row.addView(divider);
        row.addView(content);
        divider = new View(this);
        divider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        divider.setBackgroundColor(Color.WHITE);
        detailsPanel.addView(row);
        detailsPanel.addView(divider);
    }
    public void reloadItemsPanel() {
        itemAdapter = new AbstraceItemAdapter(AbstractItem.getChildren(parentItem.getTable(), parentItem.getId()));
        itemsPanel.setAdapter(itemAdapter);
        itemsPanel.setLayoutManager(new LinearLayoutManager(this));
    }
    public void reloadItemsPanel(ArrayList<AbstractItem> items) {
        itemAdapter = new AbstraceItemAdapter(items);
        itemsPanel.setAdapter(itemAdapter);
        itemsPanel.setLayoutManager(new LinearLayoutManager(this));
    }
    public void moveMode(AbstractItem item) {
        if (movingItem == null) {
            //TODO start moving mode
            movingItem = item;
            // set the moving row to be visible
            movingRow.setVisibility(View.VISIBLE);
            // disable the floating button
            floatButton.setVisibility(View.GONE);
            // add a bottom row for tick and cross to cancel the moving and confirm moving (like Moving | tick | cross)
        } else {
            //TODO end moving mode
            Log.i("moving confirm", Boolean.toString(movingConfirm));
            if (movingConfirm) {
                // move item to current place
                movingItem.parentTable = parentItem.getTable();
                movingItem.parentId = parentItem.getId();
                movingItem.update();
                // update item panel
                reloadItemsPanel();
            } else {
                // if cross is selected, jump to moving item parent
                Log.i("info", "cancel");
                switch (movingItem.parentTable) {
                    case Item.TABLE_NAME:
                        Log.d("show item", Long.toString(parentItem.parentId));
                        show(new Item(parentItem.parentId));
                        break;
                    case Task.TABLE_NAME:
                        Log.d("show task", Long.toString(parentItem.parentId));
                        show(new Task(parentItem.parentId));
                    default:
                        Log.d("show custom item", parentItem.parentTable + "\t" + Long.toString(parentItem.parentId));
                        show(new CustomizeItem(parentItem.parentTable, parentItem.parentId));
                }
            }
            // enable floating button
            movingRow.setVisibility(View.GONE);
            floatButton.setVisibility(View.VISIBLE);
            movingItem = null;
        }
        Log.i("info", Boolean.toString(movingItem == null));
    }
    public void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
