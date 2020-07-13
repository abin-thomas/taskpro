package com.atmcorp.taskpro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class BillPayHistoryActivity extends AppCompatActivity {

    private ArrayList<String> billHistoryTitles = new ArrayList<>();
    private ArrayList<ArrayList<String>> billHistoryDetails = new ArrayList<ArrayList<String>>();

    long billID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_pay_history);
        Toolbar toolbar = findViewById(R.id.customtoolbar);
        setSupportActionBar(toolbar);

        billID = getIntent().getExtras().getLong("billID");

        billHistoryTitles.add(" Sl No ");
        billHistoryTitles.add(" Amount ");
        billHistoryTitles.add(" Due Date ");
        billHistoryTitles.add(" Paid Date ");

        GetPaymentDetails();

        showPaymentHistoryTable();
    }

    private void GetPaymentDetails() {
        SQLiteDatabase db = null;
        final String DB_NAME = "TaskProDB";
        int slNo = 1; //The variable to just show the slno while printing the payment history
        db = this.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);

        Cursor c = db.rawQuery("SELECT amount,dueDate,paidDate FROM tblBillHistory WHERE billID =" +billID, null);

        if(c != null) {
            if(c.moveToFirst()) {
                do {
                    ArrayList<String> paymentDetails = new ArrayList<>();
                    paymentDetails.add(String.valueOf(slNo));
                    paymentDetails.add(c.getString(0));
                    paymentDetails.add(c.getString(1));
                    paymentDetails.add(c.getString(2));

                    billHistoryDetails.add(paymentDetails);

                    slNo++;
                }while(c.moveToNext());
            }
        }
        db.close();
    } //GetPaymentDetails()

    public void showPaymentHistoryTable() {
        TableLayout paymentHistoryTable = (TableLayout) findViewById(R.id.tablePaymentHistory);
        TableRow tbrow0 = new TableRow(this);

        if(billHistoryDetails.size()<1)
        {
            TextView mess = new TextView(this);
            mess.setText("No payment history as of now.");
            mess.setTextColor(Color.BLACK);
            tbrow0.addView(mess);
            paymentHistoryTable.addView(tbrow0);
        }
        else
        {
            for (String title: billHistoryTitles)
            {
                TextView t1v = new TextView(this);
                t1v.setText(" "+ title +" ");
                t1v.setTextColor(Color.BLACK);
                t1v.setGravity(Gravity.CENTER);
                t1v.setBackground(ContextCompat.getDrawable(this,R.drawable.tableborder));
                tbrow0.addView(t1v);

            }
            paymentHistoryTable.addView(tbrow0);

            for (ArrayList<String> tempDetails: billHistoryDetails)
            {
                TableRow tbrow = new TableRow(this);
                for (String value: tempDetails)
                {
                   TextView t1v = new TextView(this);
                    t1v.setText(" "+ value +" ");
                    t1v.setTextColor(Color.BLACK);
                    t1v.setGravity(Gravity.CENTER);
                    t1v.setBackground(ContextCompat.getDrawable(this,R.drawable.tableborder));
                    tbrow.addView(t1v);

                }
                paymentHistoryTable.addView(tbrow);
            }
        }
    }
}