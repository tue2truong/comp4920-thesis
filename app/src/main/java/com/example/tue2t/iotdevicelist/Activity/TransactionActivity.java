package com.example.tue2t.iotdevicelist.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tue2t.iotdevicelist.R;
import com.example.tue2t.iotdevicelist.Models.Transaction;

import java.util.ArrayList;

public class TransactionActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        ArrayList<Transaction> transactions =  (ArrayList<Transaction>) getIntent().getSerializableExtra("TransactionObject");

        if (transactions == null) {
            Toast.makeText(getApplicationContext(), "No Transaction Available", Toast.LENGTH_LONG).show();
        }
        else {
            ArrayList<String> displayList = new ArrayList<String>();
            displayList = transactionToString(transactions);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, displayList);
            ListView lv = (ListView) findViewById(R.id.transaction_list);
            lv.setAdapter(adapter);
        }
    }

    public ArrayList<String> transactionToString(ArrayList<Transaction> transactions) {
        ArrayList<String> retList = new ArrayList<String>();
        for (Transaction curr : transactions) {
            retList.add(curr.toString());
        }
        return retList;
    }


}
