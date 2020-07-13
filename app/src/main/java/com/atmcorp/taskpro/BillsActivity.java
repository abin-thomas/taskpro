package com.atmcorp.taskpro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class BillsActivity extends AppCompatActivity {

    private SQLiteDatabase db = null;
    private final String DB_NAME = "TaskProDB";
    public static ArrayList<String> itemNames = new ArrayList<>();
    public static ArrayList<String> itemIDs = new ArrayList<>();
    public static ArrayList<String> itemAmounts = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
        PopulateBills();
        showBillsOnList();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        PopulateBills();
        showBillsOnList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bills);
        Toolbar toolbar = findViewById(R.id.customtoolbar);
        setSupportActionBar(toolbar);
        PopulateBills();
        showBillsOnList();
    }

    public void showBillsOnList()
    {
        ListView items_list = (ListView) findViewById(R.id.bill_list);
        customListView adapter = new customListView(itemNames, this)
        {
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the current item from ListView
                View view = super.getView(position,convertView,parent);
                TextView item = (TextView) view.findViewById(R.id.customlist_text);

                // Set a background color for ListView regular row/item
                view.setBackgroundResource(R.color.appBackground);

                // Set the item text style to bold
                item.setTypeface(item.getTypeface(), Typeface.BOLD);
                // Change the item text size
                item.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);

                if(position %2 == 1) {
                    // Set the item text color
                    item.setTextColor(Color.parseColor("#FF3E80F1"));
                }
                else {
                    item.setTextColor(Color.parseColor("#FFD17057"));
                }
                return view;
            }

        };
        items_list.setAdapter((adapter));
    }
    public void PopulateBills() {
        itemIDs.clear();
        itemAmounts.clear();
        itemNames.clear();

        String curItem;
        db = this.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
        // query needs to show items/bills ONLY 14 days (or less) before the duedate (from today)
        Cursor c = db.rawQuery("SELECT billID,name,amount,company,CAST(julianday(dueDate) - julianday('now') AS INTEGER) || ' days'" +
                " FROM tblBill WHERE " +
                "julianday(dueDate) - julianday('now') < 15 and status ='A'" +
                "ORDER BY CAST(julianday(dueDate) - julianday('now') AS INTEGER)", null);

        if(c != null) {
            if(c.moveToFirst()) {
                do {
                    curItem = c.getString(0) + " | " +
                            c.getString(1) + " | $" +
                            c.getString(2) + " | " +
                            c.getString(3) + " | " +
                            c.getString(4);
                    itemNames.add(curItem);
                    itemIDs.add(c.getString(0));
                    itemAmounts.add(c.getString(2));
                }while(c.moveToNext());
            }
        }
        db.close();
    } // PopulateBills()

    public void showAddNewBill(View view) {
        Intent newActivity = new Intent(BillsActivity.this,BillEditActivity.class);
        newActivity.putExtra("billID",0);
        this.startActivity(newActivity);
    }
}