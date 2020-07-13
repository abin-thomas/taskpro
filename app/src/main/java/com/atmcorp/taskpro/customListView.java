package com.atmcorp.taskpro;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class customListView extends BaseAdapter implements ListAdapter {
    private SQLiteDatabase db = null;
    private static String DB_PATH = "/data/data/com.atmcorp.taskpro/databases/";

    private static String DB_NAME = "TaskProDB";

    private ArrayList<String> list;
    private Context context;

    public customListView()
    {
        // Required... A empty constructor
    }

    public customListView(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {


        return 0;
        //just return 0 if your list items do not have an Id variable.
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_list_view, null);
        }

        //Handle TextView and display string from your list
        final TextView listItemText = (TextView)view.findViewById(R.id.customlist_text);

        //Below 2 lines of code is to avoid showing the billID in the list
        String tempCurrentBill = list.get(position);
        tempCurrentBill=tempCurrentBill.substring(tempCurrentBill.indexOf("| ")+1);

        listItemText.setText(tempCurrentBill);

        //Handle buttons and add onClickListeners
        Button paidBtn = (Button)view.findViewById(R.id.BtnPaid);
        Button editBtn = (Button)view.findViewById(R.id.BtnEdit);
        Button historyBtn = (Button)view.findViewById(R.id.BtnPayHistory);

        paidBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                listItemText.setPaintFlags(listItemText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                markAsPaid(position);

                //list.remove(position); //or some other task
                notifyDataSetChanged();

            }
        });
        editBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                long id;
                id = Integer.parseInt(list.get(position).substring(0,list.get(position).indexOf(" | ")));

                Intent newActivity = new Intent(v.getContext(),BillEditActivity.class);
                newActivity.putExtra("billID",id);
                context.startActivity(newActivity);
            }
        });

        historyBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                long id;
                id = Integer.parseInt(list.get(position).substring(0,list.get(position).indexOf(" | ")));

                Intent newActivity = new Intent(v.getContext(),BillPayHistoryActivity.class);
                newActivity.putExtra("billID",id);
                context.startActivity(newActivity);
            }
        });

        return view;
    }

    private void markAsPaid(int arrayIndex) {
        String billFrequency="";
        String payFrequencyDays ="+0 days";
        db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);

        BillsActivity objBills = new BillsActivity();


        Cursor c = db.rawQuery("SELECT freq FROM tblBill WHERE billID =" + objBills.itemIDs.get(arrayIndex),null);
        if(c != null) {
            if (c.moveToFirst()) {
                do {
                    billFrequency = c.getString(0);
                } while (c.moveToNext());
            }
        }
        switch (billFrequency)
        {
            case "weekly":
            {
                payFrequencyDays = "+7 days";
                break;
            }
            case "Bi-weekly":
            {
                payFrequencyDays = "+14 days";
                break;
            }
            case "monthly":
            {
                payFrequencyDays = "+1 month";
                break;
            }
            case "yearly":
            {
                payFrequencyDays = "+1 year";
                break;
            }
        }
        // insert record into tblBillHistory - id, amount, payedDate, billID
        db.execSQL("INSERT INTO tblBillHistory (amount,dueDate,paidDate,billID) SELECT amount,dueDate,date('now'), billID FROM tblBill WHERE billID= " + objBills.itemIDs.get(arrayIndex));

        db.execSQL("UPDATE tblBill SET dueDate = date(dueDate, '" + payFrequencyDays +"'), lastPaid = date('now') WHERE billID = " + objBills.itemIDs.get(arrayIndex));


        db.close();



    }
}