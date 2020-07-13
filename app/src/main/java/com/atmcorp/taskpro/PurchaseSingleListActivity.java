package com.atmcorp.taskpro;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class PurchaseSingleListActivity extends AppCompatActivity {

    int purchaseID;
    String purchaseTitle;
    String newItemValue = "";
    private SQLiteDatabase db = null;
    private static String DB_PATH = "/data/data/com.atmcorp.taskpro/databases/";
    private final String DB_NAME = "TaskProDB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_single_list);

        Toolbar toolbar = findViewById(R.id.customtoolbar);
        setSupportActionBar(toolbar);

        purchaseID = getIntent().getExtras().getInt("purchaseID");
        purchaseTitle = getIntent().getExtras().getString("purchaseTitle");

        ShowPurchaseListItems();
    }

    private void ShowPurchaseListItems() {
        EditText txtTitle;

        TableLayout tablePurchaseList = (TableLayout) findViewById(R.id.tablePurchaseItems);
        tablePurchaseList.removeAllViews(); // removing all the previous rows if there is any
        tablePurchaseList.setColumnStretchable(0,true); // using this to stretch the first column of each row to the maximum available space.
        TableRow tbrow = new TableRow(this);

        EditText listTitle = new EditText(this);
        listTitle.setText(purchaseTitle);
        listTitle.setFocusable(false);
        listTitle.setTextColor(Color.BLACK);
        tbrow.addView(listTitle);
        tablePurchaseList.addView(tbrow);

        db = this.openOrCreateDatabase(DB_NAME,MODE_PRIVATE,null);
        Cursor c = db.rawQuery("SELECT itemID,item,status FROM tblPurchaseItems WHERE purchaseID=" + purchaseID,null);

        if(c!=null) {
            if (c.moveToFirst())
            {
                do{
                    TableRow tbrowItem = new TableRow(this);

                    CheckBox cb = new CheckBox(this);
                    cb.setText(c.getString(1));
                    cb.setTextColor(Color.BLACK);
                    cb.setTextSize(18);
                    cb.setId(c.getInt(0));
                    char status = c.getString(2).charAt(0);
                    if(status=='D')
                    {
                        cb.setChecked(true);
                        cb.setPaintFlags(cb.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                    tbrowItem.addView(cb);
                    cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                    {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                        {
                            db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
                            if ( isChecked )
                            {
                                buttonView.setPaintFlags(buttonView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                db.execSQL("UPDATE tblPurchaseItems SET status='D' WHERE itemID=" + buttonView.getId());
                            }
                            else
                            {
                                buttonView.setPaintFlags(0); //To remove the strikethru
                                db.execSQL("UPDATE tblPurchaseItems SET status='A' WHERE itemID=" + buttonView.getId() + " AND status = 'D'");
                            }
                            db.close();
                        }
                    });

                    Button btn = new Button(this);
                    btn.setText("Delete");

                    btn.setBackgroundColor(getResources().getColor(R.color.deleteRed));
                    btn.setId(c.getInt(0));
                    tbrowItem.addView(btn);
                    btn.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
                            db.execSQL("DELETE FROM tblPurchaseItems WHERE itemID=" + v.getId());
                            db.close();

                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());
                            overridePendingTransition(0, 0);
                        }
                    });


                    tablePurchaseList.addView(tbrowItem);


                    }while (c.moveToNext());

            }

        }

        db.close();
    }

    public void AddNewPurchaseItem(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Value");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        //input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newItemValue = input.getText().toString();
                db=SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
                db.execSQL("INSERT INTO tblPurchaseItems VALUES(?1,'" +newItemValue +"'," + purchaseID + ",'A')");
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