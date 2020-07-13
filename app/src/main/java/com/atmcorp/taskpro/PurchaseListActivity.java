package com.atmcorp.taskpro;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class PurchaseListActivity extends AppCompatActivity {

    private HashMap<Integer,String> purchaseTitles = new HashMap<>();
    String newPurchaseList = "";
    private SQLiteDatabase db = null;
    private static String DB_PATH = "/data/data/com.atmcorp.taskpro/databases/";
    private final String DB_NAME = "TaskProDB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_list);

        Toolbar toolbar = findViewById(R.id.customtoolbar);
        setSupportActionBar(toolbar);

        GetPurchaseTitles();
        showPurchaseTitles();
        
    }

    private void GetPurchaseTitles() {
        SQLiteDatabase db = null;
        final String DB_NAME = "TaskProDB";

        purchaseTitles.clear();

        db = this.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
        Cursor c = db.rawQuery("SELECT * FROM tblPurchaseMaster", null);

        if(c != null) {
            if(c.moveToFirst()) {
                do {
                    purchaseTitles.put(c.getInt(0),c.getString(1));

                }while(c.moveToNext());
            }
        }
        db.close();
    } //GetPurchaseTitles()

    private void showPurchaseTitles() {
        TableLayout purchaseTitleTable = (TableLayout) findViewById(R.id.tablePurchaseList);

        purchaseTitleTable.removeAllViews(); // removing all the previous rows if there is any
        TableRow tbrow0 = new TableRow(this);

        if(purchaseTitles.size()<1)
        {
            TextView mess = new TextView(this);
            mess.setText("No purchase lists as of now.");
            mess.setTextColor(Color.BLACK);
            tbrow0.addView(mess);
            purchaseTitleTable.addView(tbrow0);
        }
        else
        {
            for (final int titleID: purchaseTitles.keySet())
            {
                final TableRow tbrow = new TableRow(this);

                Button btnTitle = new Button(this);
                btnTitle.setText(" "+ purchaseTitles.get(titleID) +" "); //Title
                btnTitle.setTextColor(Color.BLACK);
                btnTitle.setGravity(Gravity.START);
                btnTitle.setTextSize(24);
                btnTitle.setPadding(10,10,10,10);
                btnTitle.setBackground(ContextCompat.getDrawable(this,R.drawable.tableborder));
                tbrow.addView(btnTitle);


                btnTitle.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        //do something

                        Intent newActivity = new Intent(v.getContext(),PurchaseSingleListActivity.class);
                        newActivity.putExtra("purchaseID",titleID);
                        newActivity.putExtra("purchaseTitle",purchaseTitles.get(titleID));
                        v.getContext().startActivity(newActivity);
                    }
                });
                purchaseTitleTable.addView(tbrow);

            }
        }

    }//showPurchaseTitles()

    public void AddNewPurchaseList(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter List Name");

// Set up the input
        final EditText input = new EditText(this);

        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newPurchaseList = input.getText().toString();
                db=SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
                db.execSQL("INSERT INTO tblPurchaseMaster VALUES(?1,'" +newPurchaseList +"')");
                db.close();

                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();


    }
}