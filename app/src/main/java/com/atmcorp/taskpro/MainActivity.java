package com.atmcorp.taskpro;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase db = null;
    private final String DB_NAME = "TaskProDB";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.customtoolbar);
        setSupportActionBar(toolbar);

        dbSetup();
    }

    public void selectTask(View v) {
        switch(v.getId()){
            case R.id.bills_button: {
                Intent billsActivity =
                        new Intent(MainActivity.this,
                                BillsActivity.class);
                startActivity(billsActivity);
                break;
            }
            case R.id.purchaselist_button: {
                Intent cartActivity =
                        new Intent(MainActivity.this,
                                PurchaseListActivity.class);
                startActivity(cartActivity);
                break;
            }
            case R.id.todo_button: {
                Intent todoActivity =
                        new Intent(MainActivity.this,
                                TodoActivity.class);
                startActivity(todoActivity);
                break;
            }
            case R.id.prescription_button: {
                Intent medicineActivity =
                        new Intent(MainActivity.this,
                                PrescriptionActivity.class);
                startActivity(medicineActivity);
                break;
            }
        } // switch
    } // selectTask

    private void dbSetup() {
        // create the db if it does not have an oreo cookies
        db = this.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);

        // create our bills table if it does not exist
        db.execSQL("CREATE TABLE IF NOT EXISTS tblBill" +
                "(billID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name VARCHAR, dueDate VARCHAR, amount NUMERIC," +
                "freq VARCHAR, company VARCHAR, lastPaid VARCHAR," +
                " status CHAR(1))");

        db.execSQL("CREATE TABLE IF NOT EXISTS tblBillHistory" +
                "(billHistoryID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "amount NUMERIC, dueDate VARCHAR, paidDate VARCHAR, billID INTEGER)");


        // create our todolist table if it does not exist
        db.execSQL("CREATE TABLE IF NOT EXISTS tblTodoList" +
                "(taskID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title VARCHAR, priority VARCHAR,startDate VARCHAR, dueDate VARCHAR,"+
                " status VARCHAR,notes VARCHAR)");


        // create our purchaseList table if it does not exist
        db.execSQL("CREATE TABLE IF NOT EXISTS tblPurchaseMaster" +
                "(purchaseID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title VARCHAR)");


        db.execSQL("CREATE TABLE IF NOT EXISTS tblPurchaseItems" +
                "(itemID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "item VARCHAR,purchaseID INTEGER,status CHAR(1))");

        db.execSQL("CREATE TABLE IF NOT EXISTS tblPrescriptions" +
                "(drugID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name VARCHAR,amount VARCHAR,frequency VARCHAR,startDate REAL,endDate REAL)");
    }
}