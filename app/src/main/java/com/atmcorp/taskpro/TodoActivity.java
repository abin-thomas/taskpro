package com.atmcorp.taskpro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class TodoActivity extends AppCompatActivity {


    public static ArrayList<ArrayList<String>> todoDetails = new ArrayList<ArrayList<String>>();
    private ArrayList<String> todoListTitles = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
        GetTaskListDetails();
        showTaskListTable();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        GetTaskListDetails();
        showTaskListTable();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        Toolbar toolbar = findViewById(R.id.customtoolbar);
        setSupportActionBar(toolbar);

        todoListTitles.add(" Title ");
        todoListTitles.add(" Priority ");
        todoListTitles.add(" Due Date ");
        todoListTitles.add(" Status ");


        GetTaskListDetails();

        showTaskListTable();
    }

    private void GetTaskListDetails() {
        SQLiteDatabase db = null;
        final String DB_NAME = "TaskProDB";

        todoDetails.clear();

        db = this.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
        Cursor c = db.rawQuery("SELECT * FROM tblToDoList WHERE status !='Done' AND status !='Delete' ORDER BY dueDate", null);

        if(c != null) {
            if(c.moveToFirst()) {
                do {
                    ArrayList<String> details = new ArrayList<>();
                    details.add(c.getString(0)); //ID
                    details.add(c.getString(1)); //title
                    details.add(c.getString(2)); //priority
                    details.add(c.getString(3)); //startDate
                    details.add(c.getString(4)); //dueDate
                    details.add(c.getString(5)); //status
                    details.add(c.getString(6)); //notes


                    todoDetails.add(details);


                }while(c.moveToNext());
            }
        }
        db.close();
    } //GetTaskListDetails()

    public void showTaskListTable() {
        TableLayout todoListTable = (TableLayout) findViewById(R.id.tableToDoList);

        todoListTable.removeAllViews(); // removing all the previous rows if there is any
        todoListTable.setColumnStretchable(0,true);
        todoListTable.setColumnStretchable(1,true);
        todoListTable.setColumnStretchable(2,true);
        todoListTable.setColumnStretchable(3,true);
        TableRow tbrow0 = new TableRow(this);

        if(todoDetails.size()<1)
        {
            TextView mess = new TextView(this);
            mess.setText("No active tasks as of now.");
            mess.setTextColor(Color.BLACK);
            tbrow0.addView(mess);
            todoListTable.addView(tbrow0);
        }
        else
        {
            for (String title: todoListTitles)
            {
                TextView t1v = new TextView(this);
                t1v.setText(" "+ title +" ");
                t1v.setTextColor(Color.BLACK);
                t1v.setGravity(Gravity.CENTER);
                t1v.setTextSize(20);
                t1v.setBackground(ContextCompat.getDrawable(this,R.drawable.tableborder));
                tbrow0.addView(t1v);

            }
            todoListTable.addView(tbrow0);


            for (ArrayList<String> tempTasks: todoDetails)
            {
                final TableRow tbrow = new TableRow(this);

                final Object[] values = tempTasks.toArray();

                TextView t1v = new TextView(this);
                t1v.setText(" "+ values[1] +" "); //Title
                t1v.setTextColor(Color.BLACK);
                t1v.setGravity(Gravity.CENTER);
                t1v.setTextSize(18);
                t1v.setBackground(ContextCompat.getDrawable(this,R.drawable.tableborder));
                tbrow.addView(t1v);

                TextView t2v = new TextView(this);
                t2v.setText(" "+ values[2] +" "); //Priority
                t2v.setTextColor(Color.BLACK);
                t2v.setGravity(Gravity.CENTER);
                t2v.setTextSize(18);
                t2v.setBackground(ContextCompat.getDrawable(this,R.drawable.tableborder));
                tbrow.addView(t2v);

                TextView t3v = new TextView(this);
                t3v.setText(" "+ values[4] +" "); //DueDate
                t3v.setTextColor(Color.BLACK);
                t3v.setGravity(Gravity.CENTER);
                t3v.setTextSize(18);
                t3v.setBackground(ContextCompat.getDrawable(this,R.drawable.tableborder));
                tbrow.addView(t3v);

                TextView t4v = new TextView(this);
                t4v.setText(" "+ values[5] +" "); //Status
                t4v.setTextColor(Color.BLACK);
                t4v.setGravity(Gravity.CENTER);
                t4v.setTextSize(18);
                t4v.setBackground(ContextCompat.getDrawable(this,R.drawable.tableborder));
                tbrow.addView(t4v);

                tbrow.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        //do something
                        Intent newActivity = new Intent(v.getContext(),TodoEditActivity.class);
                        newActivity.putExtra("taskID",Integer.parseInt(values[0].toString()));
                        v.getContext().startActivity(newActivity);
                    }
                });
                todoListTable.addView(tbrow);

            }
        }
    }//showTaskListTable

    public void showAddNewTask(View view) {
        Intent newActivity = new Intent(TodoActivity.this,TodoEditActivity.class);
        newActivity.putExtra("taskID",0);
        this.startActivity(newActivity);
    }
}