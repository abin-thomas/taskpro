package com.atmcorp.taskpro;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Calendar;

public class TodoEditActivity extends AppCompatActivity {

    EditText txttodoTitle,txttodoPriority,txttodoStartDate, txttodoDueDate,txttodoStatus,txttodoNotes;
    DatePickerDialog datePicker;
    int taskID;

    private SQLiteDatabase db = null;
    private final String DB_NAME = "TaskProDB";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_edit);

        taskID= getIntent().getExtras().getInt("taskID");

        txttodoTitle = (EditText)findViewById(R.id.txttodotitle);
        txttodoPriority = (EditText)findViewById(R.id.txttodopriority);
        txttodoStartDate = (EditText)findViewById(R.id.txttodostartdate);
        txttodoDueDate = (EditText)findViewById(R.id.txttododuedate);
        txttodoStatus = (EditText)findViewById(R.id.txttodostatus);
        txttodoNotes = (EditText)findViewById(R.id.txttodonotes);

        if(taskID != 0)
        {
            PopulateEditTexts();

            DisableAllEditTexts();
        }


        ShowCalendarPopUp();

    }

    private void PopulateEditTexts() {
        db =this.openOrCreateDatabase(DB_NAME,MODE_PRIVATE,null);
        Cursor c = db.rawQuery("SELECT * FROM tblTodoList WHERE taskID =" + taskID,null);
        if(c != null) {
            if(c.moveToFirst()) {
                do {
                    txttodoTitle.setText(c.getString(1));
                    txttodoPriority.setText(c.getString(2));
                    txttodoStartDate.setText(c.getString(3));
                    txttodoDueDate.setText(c.getString(4));
                    txttodoStatus.setText(c.getString(5));
                    txttodoNotes.setText(c.getString(6));
                }while(c.moveToNext());
            }
        }
        db.close();
    }

    private void DisableAllEditTexts() {
        ViewGroup parentView = (ViewGroup) findViewById(R.id.todoConstraintLayout);

        for (int i = 0, count = parentView.getChildCount(); i < count; ++i) {
            View view = parentView.getChildAt(i);
            if (view instanceof EditText) { //Here we check whether the child is a EditText or Not
                ((EditText)view).setEnabled(false);
            }
        }
    }

    private void EnableAllEditTexts() {
        ViewGroup parentView = (ViewGroup) findViewById(R.id.todoConstraintLayout);

        for (int i = 0, count = parentView.getChildCount(); i < count; ++i) {
            View view = parentView.getChildAt(i);
            if (view instanceof EditText) { //Here we check whether the child is a EditText or Not
                ((EditText)view).setEnabled(true);
            }
        }
    }

    public void ShowCalendarPopUp()
    {
        txttodoStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                // show datePicker dialog

                datePicker = new DatePickerDialog(TodoEditActivity.this,
                        new DatePickerDialog.OnDateSetListener()
                        {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                            {
                                DecimalFormat mFormat= new DecimalFormat("00");
                                String tempDate = mFormat.format(Double.valueOf(dayOfMonth));
                                String tempMonth = mFormat.format(Double.valueOf(monthOfYear+1));

                                txttodoStartDate.setText(year + "-" + tempMonth + "-" + tempDate);
                            }
                        }, year, month, day);

                datePicker.show();
            }
        });

        txttodoDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                // show datePicker dialog

                datePicker = new DatePickerDialog(TodoEditActivity.this,
                        new DatePickerDialog.OnDateSetListener()
                        {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                            {
                                DecimalFormat mFormat= new DecimalFormat("00");
                                String tempDate = mFormat.format(Double.valueOf(dayOfMonth));
                                String tempMonth = mFormat.format(Double.valueOf(monthOfYear+1));

                                txttodoDueDate.setText(year + "-" + tempMonth + "-" + tempDate);
                            }
                        }, year, month, day);

                datePicker.show();
            }
        });
    }//showCalendarPopUp

    public void selectToDoTask(View view) {
        int result = 0;
        Toast toast;

        db =this.openOrCreateDatabase(DB_NAME,MODE_PRIVATE,null);

        switch (view.getId())
        {
            case R.id.BtntodoEdit:
            {
                EnableAllEditTexts();
                break;
            }
            case R.id.BtntodoSave:
            {
                ContentValues taskData = new ContentValues();
                taskData.put("title", txttodoTitle.getText().toString());
                taskData.put("priority", txttodoPriority.getText().toString());
                taskData.put("startDate", txttodoStartDate.getText().toString());
                taskData.put("dueDate", txttodoDueDate.getText().toString());
                taskData.put("status", txttodoStatus.getText().toString());
                taskData.put("notes", txttodoNotes.getText().toString());

                if(taskID == 0) // taskID 0 means the call is from AddNewTask.
                {

                    result = (int) db.insert("tblTodoList","",taskData);
                }
                else
                {
                    result =db.update("tblTodoList",taskData,"taskID="+taskID,null);
                }

                if(result!=0)
                {
                    toast = Toast.makeText(this.getApplicationContext(),
                            "Task Updated Successfully ..... ",Toast.LENGTH_SHORT);
                }
                else
                {
                    toast = Toast.makeText(this.getApplicationContext(),
                            "Sorry !!! Task NOT Updated ..... ",Toast.LENGTH_SHORT);
                }


                toast.show();
                finish();
                break;
            }
            case R.id.BtntodoCancel:
            {
                finish();
                break;
            }
            case R.id.BtntodoDelete:
            {
                db.execSQL("UPDATE tblTodoList SET status='Delete' where taskID =" +taskID);
                toast = Toast.makeText(this.getApplicationContext(),
                        "Task Deleted ..... ",Toast.LENGTH_SHORT);
                toast.show();
                finish();
                break;
            }
        }
        db.close();

    }

    public void ShowTaskStatus(View view) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Task Status");

    // add a list
    String[] statusWords = {"Not Started","Started", "In Progress", "Done"};
        builder.setItems(statusWords, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int selected) {

            switch(selected)
            {
                case 0:
                {
                    txttodoStatus.setText("Not Started");
                    break;
                }
                case 1:
                {
                    txttodoStatus.setText("Started");
                    break;
                }
                case 2:
                {
                    txttodoStatus.setText("In Progress");
                    break;
                }
                case 3:
                {
                    txttodoStatus.setText("Done");
                    break;
                }
            }
        }
    });

    // create and show the alert dialog
    AlertDialog dialog = builder.create();
        dialog.show();
} //ShowTaskStatus

    public void ShowTaskPriority(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Task Priority");

// add a list
        String[] priorityWords = {"Low","Medium", "High"};
        builder.setItems(priorityWords, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selected) {

                switch(selected)
                {
                    case 0:
                    {
                        txttodoPriority.setText("Low");
                        break;
                    }
                    case 1:
                    {
                        txttodoPriority.setText("Medium");
                        break;
                    }
                    case 2: {
                        txttodoStatus.setText("High");
                        break;
                    }
                }
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    } //ShowTaskPriority
}