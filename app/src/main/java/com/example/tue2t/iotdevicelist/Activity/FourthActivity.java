package com.example.tue2t.iotdevicelist.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.tue2t.iotdevicelist.Models.Device;
import com.example.tue2t.iotdevicelist.Models.Transaction;
import com.example.tue2t.iotdevicelist.MySingleton;
import com.example.tue2t.iotdevicelist.R;

import java.util.ArrayList;

public class FourthActivity extends AppCompatActivity {

    // interface
    ToggleButton btn;
    Button transButton;
    Toolbar tb;

    // device info
    String ipAdr;
    String port;
    String deviceID;
    Device device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);

        //init
        btn = (ToggleButton) findViewById(R.id.toggleButton);
        transButton = (Button) findViewById(R.id.button_transaction);
        tb = (Toolbar) findViewById(R.id.toolbar);

        // get intent
        // get info from previous activity
        device = (Device) getIntent().getSerializableExtra("DeviceObject");
        if(device != null) {
            tb.setTitle(device.getDeviceName());
            deviceID = device.getDeviceID().toString();
            ipAdr = device.getDeviceIP();
            port = device.getDevicePort();
        }

        toggleButtonController();
        transactionButtonController();
    }

    public void toggleButtonController() {
        btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // construct url
                    String toggleReq = "http://"+ ipAdr + ":" + port + "/api/v1/device/" + deviceID + "/transaction?plug=on";
                    sendRequest(toggleReq);
                    Log.d("Test", toggleReq);

                } else {
                    String toggleReq = "http://"+ ipAdr + ":" + port + "/api/v1/device/" + deviceID + "/transaction?plug=off";
                    sendRequest(toggleReq);
                    Log.d("Test", toggleReq);
                }
            }
        });
    }

    public void transactionButtonController() {
        transButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FourthActivity.this, TransactionActivity.class);
                intent.putExtra("TransactionObject", (ArrayList<Transaction>) device.getTransactionList());
                startActivity(intent);
            }
        });
    }

    // send volley request to rest server
    public void sendRequest(String input_url) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, input_url,
                new Response.Listener<String>() {
                    // response from server
                    @Override
                    public void onResponse(String response) {


                    }
                }, new Response.ErrorListener() {
            // error message from server
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(FourthActivity.this, "Error received from server", Toast.LENGTH_SHORT).show();
            }
        });
        MySingleton.getInstance(FourthActivity.this).addToRequestQueue(stringRequest);
    }


}
