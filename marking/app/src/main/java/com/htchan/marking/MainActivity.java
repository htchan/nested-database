package com.htchan.marking;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.htchan.marking.model.AbstractItem;
import com.htchan.marking.model.DatabaseHelper;
import com.htchan.marking.model.Item;
import com.htchan.marking.model.Task;
import com.htchan.marking.ui.bottomsheet.BottomSheet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import static android.util.Log.i;

public class MainActivity extends AppCompatActivity {
    public static MainActivity mainActivity;

    private DatabaseHelper db;
    private TextView heading, headingTable;
    private View headingDiv;
    private LinearLayout detailsPanel;
    private RecyclerView itemsPanel;
    private RecyclerView.Adapter itemAdapter;
    private LinearLayout searchBar;
    private boolean backPressed;
    public AbstractItem parentItem;
    public BottomSheet bottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = DatabaseHelper.DatabaseHelper(this);
        mainActivity = this;
        heading = findViewById(R.id.heading);
        headingTable = findViewById(R.id.headingTable);
        headingDiv = findViewById(R.id.headingDiv);
        itemsPanel = findViewById(R.id.itemsPanel);
        detailsPanel = findViewById(R.id.detailsPanel);
        searchBar = findViewById(R.id.searchBar);
        bottomSheet = new BottomSheet(this, findViewById(R.id.bottomSheet));
        buildBottomSheet();
        buildSearchBar();
        show(Item.MAIN_ITEM);
        i("log", Integer.toString(itemAdapter.getItemCount()));
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
                    hideKeyBoard();
                    bottomSheet.behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });
    }
    private void buildSearchBar() {
        final TextView queryTextView = searchBar.findViewById(R.id.queryTextView);
        queryTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                //TODO search all item by text
                Toast.makeText(MainActivity.this, editable.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        ImageView close = searchBar.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryTextView.setText("");
            }
        });
        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchBar.getVisibility() == View.VISIBLE) {
                    if (searchBar.hasFocus()) {
                        hideKeyBoard();
                    }
                    searchBar.setVisibility(View.GONE);
                } else {
                    searchBar.setVisibility(View.VISIBLE);
                    searchBar.requestFocus();
                }
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
            detailsPanelAddRow(Task.COL_DETAILS, t.details);
        } else if (! parent.getTable().equals(Item.TABLE_NAME)) {
            // TODO show the extra fields ot customer item
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
    public void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
