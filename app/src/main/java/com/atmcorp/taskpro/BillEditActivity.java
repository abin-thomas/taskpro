package com.atmcorp.taskpro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Calendar;

public class BillEditActivity extends AppCompatActivity {
    long billID;
    private SQLiteDatabase db = null;
    private final String DB_NAME = "TaskProDB";
    EditText txtName, txtBillDueDate, txtAmount,txtFrequency, txtCompany, txtLastPaidDate;
    DatePickerDialog datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_edit);
        Toolbar toolbar = findViewById(R.id.customtoolbar);
        setSupportActionBar(toolbar);

        txtName = (EditText)findViewById(R.id.txtName);
        txtBillDueDate = (EditText)findViewById(R.id.txtBillDueDate);
        txtAmount = (EditText)findViewById(R.id.txtAmount);
        txtFrequency = (EditText)findViewById(R.id.txtFrequency);
        txtCompany = (EditText)findViewById(R.id.txtCompany);
        txtLastPaidDate = (EditText)findViewById(R.id.txtLastPaidDate);

        showCalendarPopUp();

        billID = getIntent().getExtras().getLong("billID");

        db = this.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);

        if(billID != 0) // billID != 0 means the call is for editing existing record.
        {
            Cursor c = db.rawQuery("SELECT name, dueDate, amount,freq, company, lastPaid" +
                    " FROM tblBill WHERE " +
                    "billID =" + billID, null);
            if(c != null) {
                if(c.moveToFirst()) {
                    do {
                        txtName.setText(c.getString(0));
                        txtBillDueDate.setText(c.getString(1));
                        txtAmount.setText(c.getString(2));
                        txtFrequency.setText(c.getString(3));
                        txtCompany.setText(c.getString(4));
                        txtLastPaidDate.setText(c.getString(5));
                    }while(c.moveToNext());
                }
            }
        }
        else // billID = 0 means we have to create a new record,hence all the edittexts should be empty.
        {

        }

        db.close();
    }

    public void selectBillTask(View view) {
        int result =0;
        Toast toast;
        db = this.openOrCreateDatabase(DB_NAME,MODE_PRIVATE,null);
        switch (view.getId())
        {
            case R.id.btnSave:
            {
                ContentValues billData = new ContentValues();
                billData.put("name", txtName.getText().toString());
                billData.put("dueDate", txtBillDueDate.getText().toString());
                billData.put("amount", txtAmount.getText().toString());
                billData.put("freq", txtFrequency.getText().toString());
                billData.put("company", txtCompany.getText().toString());
                billData.put("lastPaid", txtLastPaidDate.getText().toString());
                billData.put("status","A");

                if(billID== 0) // billID 0 means the call is from AddNewBill, so the editboxes should be empty.
                {
                    // name, dueDate, amount, freq, company, lastPayed, status
                    result = (int) db.insert("tblBill","",billData);
                }
                else
                {
                    result =db.update("tblBill",billData,"billID="+billID,null);
                }

                if(result!=0)
                {
                     toast = Toast.makeText(this.getApplicationContext(),
                            "Bill Updated Successfully ..... ",Toast.LENGTH_SHORT);
                }
                else
                {
                    toast = Toast.makeText(this.getApplicationContext(),
                            "Sorry !!! Bill NOT Updated ..... ",Toast.LENGTH_SHORT);
                }


                toast.show();

                break;
            }
            case R.id.btnCancel:
            {
                toast = Toast.makeText(this.getApplicationContext(),
                        "No chances made ..... ",Toast.LENGTH_SHORT);
                toast.show();
                break;
            }
            case R.id.btnDelete:
            {
                db.execSQL("UPDATE tblBill SET status='C' where billID =" +billID);
                toast = Toast.makeText(this.getApplicationContext(),
                        "Bill Deleted ..... ",Toast.LENGTH_SHORT);
                toast.show();
                break;
            }
        }
        finish();
        db.close();
    }

    public void showCalendarPopUp()
    {

        txtBillDueDate.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            // show datePicker dialog

            datePicker = new DatePickerDialog(BillEditActivity.this,
                    new DatePickerDialog.OnDateSetListener()
                    {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                        {
                            DecimalFormat mFormat= new DecimalFormat("00");
                            String tempDate = mFormat.format(Double.valueOf(dayOfMonth));
                            String tempMonth = mFormat.format(Double.valueOf(monthOfYear+1));

                            txtBillDueDate.setText(year + "-" + tempMonth + "-" + tempDate);
                        }
                    }, year, month, day);

            datePicker.show();
        }
    });

        txtLastPaidDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                // show datePicker dialog

                datePicker = new DatePickerDialog(BillEditActivity.this,
                        new DatePickerDialog.OnDateSetListener()
                        {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                            {
                                DecimalFormat mFormat= new DecimalFormat("00");
                                String tempDate = mFormat.format(Double.valueOf(dayOfMonth));
                                String tempMonth = mFormat.format(Double.valueOf(monthOfYear+1));

                                txtLastPaidDate.setText(year + "-" + tempMonth + "-" + tempDate);
                            }
                        }, year, month, day);

                datePicker.show();
            }
        });
    }//showCalendarPopUp


    public void showPaymentFrequency(View view) {

        txtFrequency = (EditText)findViewById(R.id.txtFrequency);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Frequency");

// add a list
        String[] expenseSubs = {"weekly", "Bi-weekly", "monthly", "yearly"};
        builder.setItems(expenseSubs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selected) {

                switch(selected)
                {
                    case 0:
                    {
                        txtFrequency.setText("weekly");
                        break;
                    }
                    case 1:
                    {
                        txtFrequency.setText("Bi-weekly");
                        break;
                    }
                    case 2:
                    {
                        txtFrequency.setText("monthly");
                        break;
                    }
                    case 3:
                    {
                        txtFrequency.setText("yearly");
                        break;
                    }
                }
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    } //showPaymentFrequency
}