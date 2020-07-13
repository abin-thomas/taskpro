package com.atmcorp.taskpro;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.HashMap;

public class PrescriptionActivity extends AppCompatActivity {

    SQLiteDatabase db = null;
    private static String DB_PATH = "/data/data/com.atmcorp.taskpro/databases/";
    final String DB_NAME = "TaskProDB";

    @Override
    protected void onResume() {
        super.onResume();
        showTodaysMedications();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription);

        Toolbar toolbar = findViewById(R.id.customtoolbar);
        setSupportActionBar(toolbar);

        showTodaysMedications();

    }

    private void showTodaysMedications() {
        db = this.openOrCreateDatabase(DB_NAME,MODE_PRIVATE,null);
        Cursor c = db.rawQuery("SELECT drugID,name,amount,frequency,date(startDate),CAST(julianday(endDate)-julianday('now')as INTEGER)" +
                " FROM tblPrescriptions " +
                "WHERE date('now') BETWEEN date(startDate) and date(endDate)",null);

        TableLayout todaysMedicineTable = (TableLayout) findViewById(R.id.tablePrescriptionList);

        todaysMedicineTable.removeAllViews();
        todaysMedicineTable.setColumnStretchable(0,true);

        if(c != null) {
            if(c.moveToFirst()) {
                do {
                    final int drugID;
                    CardView medCard = new CardView(this);
                    medCard.setCardBackgroundColor(getResources().getColor(R.color.appBackground));
                    medCard.setRadius(10);
                    medCard.setPadding(5,10,5,10);
                    TextView tv = new TextView(this);

                    String fullDetails = c.getString(1) +"\n"
                            +c.getString(2) + "\n"
                            +c.getString(3) + "\n"
                            +"Started: "+c.getString(4) +"  |   "+c.getString(5)+" Days remaining";

                    drugID = c.getInt(0);
                    tv.setTextColor(Color.BLACK);
                    tv.setTextSize(14);
                    tv.setText(fullDetails);

                    medCard.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            Intent newActivity = new Intent(PrescriptionActivity.this,PrescriptionEditActivity.class);
                            newActivity.putExtra("drugID",drugID);
                            startActivity(newActivity);
                        }
                    });

                    medCard.addView(tv);


                    //todaysMedicineTable.removeAllViews(); // removing all the previous rows if there is any
                    TableRow tbrow0 = new TableRow(this);

                    tbrow0.setPadding(2,5,2,2);

                    tbrow0.addView(medCard);

                    Button btnDelete = new Button(this);
                    btnDelete.setText("Delete");
                    btnDelete.setId(c.getInt(0));
                    btnDelete.setBackgroundColor(getResources().getColor(R.color.deleteRed));
                    tbrow0.addView(btnDelete);

                    btnDelete.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
                            db.execSQL("DELETE FROM tblPrescriptions WHERE drugID=" + v.getId());
                            db.close();

                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());
                            overridePendingTransition(0, 0);
                        }
                    });


                    todaysMedicineTable.addView(tbrow0);



                }while(c.moveToNext());
            }
        }
        db.close();
    }

    public void AddNewMedicine(View view) {
        Intent newActivity = new Intent(PrescriptionActivity.this,PrescriptionEditActivity.class);
        newActivity.putExtra("DrugID",0);
        this.startActivity(newActivity);
    }
}