package com.atmcorp.taskpro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PrescriptionEditActivity extends AppCompatActivity {

    int drugID;
    DatePickerDialog datePicker;
    private SQLiteDatabase db = null;
    private final String DB_NAME = "TaskProDB";
    Toast message;
    EditText txtDrugName,txtDrugAmount,txtDrugFrequency,txtDrugStartDate,txtDrugEndDate;
    String valuetxtDrugName,valuetxtDrugAmount,valuetxtDrugFrequency,valuetxtDrugStartDate,valuetxtDrugEndDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription_edit);

        Toolbar toolbar = findViewById(R.id.customtoolbar);
        setSupportActionBar(toolbar);

        drugID = getIntent().getExtras().getInt("drugID");

        txtDrugName = (EditText) findViewById(R.id.txtDrugName);
        txtDrugAmount = (EditText) findViewById(R.id.txtDrugAmount);
        txtDrugFrequency = (EditText) findViewById(R.id.txtDrugFrequency);
        txtDrugStartDate = (EditText) findViewById(R.id.txtDrugStartDate);
        txtDrugEndDate = (EditText) findViewById(R.id.txtDrugEndDate);

        showCalendarPopUp(); //To show the datepicker for the dates

        if(drugID !=0)
        {
            PopulateDrugDetails(drugID);
        }

    }

    public void selectPrescriptionTask(View view) {
        switch(view.getId())
        {
            case R.id.btnPrescriptionSave:
            {
                SavePrescription();
                break;
            }
            case R.id.btnPrescriptionCancel:
            {
                finish();
                break;
            }
        }
    }

    private void PopulateDrugDetails(int drugID) {
        db = this.openOrCreateDatabase(DB_NAME,MODE_PRIVATE,null);
        Cursor c = db.rawQuery("SELECT name,amount,frequency,date(startDate),date(endDate) FROM tblPrescriptions WHERE drugID ="+drugID,null);
        if(c!=null)
        {
            if(c.moveToFirst())
            {
                do {
                    txtDrugName.setText(c.getString(0));
                    txtDrugAmount.setText(c.getString(1));
                    txtDrugFrequency.setText(c.getString(2));
                    txtDrugStartDate.setText(c.getString(3));
                    txtDrugEndDate.setText(c.getString(4));
                }while(c.moveToNext());
            }
        }
        db.close();
    }

    public void SavePrescription() {
        db = this.openOrCreateDatabase(DB_NAME,MODE_PRIVATE,null);

        valuetxtDrugName = txtDrugName.getText().toString();
        valuetxtDrugAmount = txtDrugAmount.getText().toString();
        valuetxtDrugFrequency = txtDrugFrequency.getText().toString();
        valuetxtDrugStartDate = txtDrugStartDate.getText().toString();
        valuetxtDrugEndDate = txtDrugEndDate.getText().toString();
        

        if(drugID == 0)
        {
            db.execSQL("INSERT INTO tblPrescriptions VALUES(?1,'" +
                    valuetxtDrugName +"','" +
                    valuetxtDrugAmount +"','" +
                    valuetxtDrugFrequency +"',"+
                    "julianday('"+valuetxtDrugStartDate +"')," +
                    "julianday('"+valuetxtDrugEndDate +"'))");
            message = Toast.makeText(this,"Drug Added Successfully", Toast.LENGTH_SHORT);
        }
        else
        {
            db.execSQL("UPDATE tblPrescriptions SET name ='" + valuetxtDrugName +"'," +
            "amount = '" + valuetxtDrugAmount +"'," +
            "frequency = '" + valuetxtDrugFrequency + "'," +
            "startDate = julianday('"+ valuetxtDrugStartDate +"'),"+
            "endDate = julianday('"+ valuetxtDrugEndDate +"')"+
            "WHERE drugID ="+drugID);
            message = Toast.makeText(this,"Drug Updated Successfully", Toast.LENGTH_SHORT);
        }
        message.show();

        db.close();
        finish();
    }

    public void showCalendarPopUp()
    {

        txtDrugStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                // show datePicker dialog

                datePicker = new DatePickerDialog(PrescriptionEditActivity.this,
                        new DatePickerDialog.OnDateSetListener()
                        {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                            {
                                DecimalFormat mFormat= new DecimalFormat("00");
                                String tempDate = mFormat.format(Double.valueOf(dayOfMonth));
                                String tempMonth = mFormat.format(Double.valueOf(monthOfYear+1));

                                txtDrugStartDate.setText(year + "-" + tempMonth + "-" + tempDate);
                            }
                        }, year, month, day);

                datePicker.show();
            }
        });

        txtDrugEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                // show datePicker dialog

                datePicker = new DatePickerDialog(PrescriptionEditActivity.this,
                        new DatePickerDialog.OnDateSetListener()
                        {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                            {
                                DecimalFormat mFormat= new DecimalFormat("00");
                                String tempDate = mFormat.format(Double.valueOf(dayOfMonth));
                                String tempMonth = mFormat.format(Double.valueOf(monthOfYear+1));

                                txtDrugEndDate.setText(year + "-" + tempMonth + "-" + tempDate);
                            }
                        }, year, month, day);

                datePicker.show();
            }
        });
    }//showCalendarPopUp


}