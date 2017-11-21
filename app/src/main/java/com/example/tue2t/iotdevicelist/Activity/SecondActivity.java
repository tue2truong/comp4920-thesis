package com.example.tue2t.iotdevicelist.Activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.tue2t.iotdevicelist.Models.Device;
import com.example.tue2t.iotdevicelist.MySingleton;
import com.example.tue2t.iotdevicelist.R;
import com.example.tue2t.iotdevicelist.Models.Transaction;

import java.util.ArrayList;

/*
    Light bulb Activity
 */

public class SecondActivity extends AppCompatActivity {

    // front view
    Toolbar tb;
    ToggleButton toggle, modeButton;
    Button transButton;
    SeekBar sbRed, sbGreen, sbBlue;
    String red_val, green_val, blue_val;
    final static String MAX_LIGHT = "255";
    final static String MIN_LIGHT = "0";


    // device info
    String ipAdr;
    String port;
    String deviceID;
    Device device;

    // volley
    int sendMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // init
        tb = (Toolbar) findViewById(R.id.toolbar);


        // get info from previous activity
        device = (Device) getIntent().getSerializableExtra("DeviceObject");
        if(device != null) {
            tb.setTitle(device.getDeviceName());
            deviceID = device.getDeviceID().toString();
            ipAdr = device.getDeviceIP();
            port = device.getDevicePort();
        }

        // activity
        init();
        toggleButtonController();
        modeButtonController();
        seekBarController();
        transactionButtonController();
    }

    public void init() {
        toggle = (ToggleButton) findViewById(R.id.toggleButton);
        sbRed = (SeekBar) findViewById(R.id.seekBar_R);
        sbGreen = (SeekBar) findViewById(R.id.seekBar_G);
        sbBlue = (SeekBar) findViewById(R.id.seekBar_B);
        sbRed.setMax(255);
        sbGreen.setMax(255);
        sbBlue.setMax(255);
        sbRed.setProgress(255);
        sbGreen.setProgress(255);
        sbBlue.setProgress(255);
        transButton = (Button) findViewById(R.id.button_transaction);
        modeButton = (ToggleButton) findViewById(R.id.mode);

    }

    // send volley request to rest server
    public void sendRequest(String input_url) {
        // check connection mode
        if(ipAdr == MainActivity.ipAdr) {
            sendMethod = Request.Method.GET;
            Log.d("Test", "sendMethod = GET");
        } else {
            sendMethod= Request.Method.POST;
            Log.d("Test", "sendMethod=POST");
        }

        StringRequest stringRequest = new StringRequest(sendMethod, input_url,
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
                Toast.makeText(SecondActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
        MySingleton.getInstance(SecondActivity.this).addToRequestQueue(stringRequest);
    }


    // set seekbar progress according to ON/OFF
    public void setSeekBar(String url) {
        if(url.contains("on")) {
            red_val = MAX_LIGHT;
            green_val = MAX_LIGHT;
            blue_val = MAX_LIGHT;
        }
        else if(url.contains("off")) {
            red_val = MIN_LIGHT;
            green_val = MIN_LIGHT;
            blue_val = MIN_LIGHT;
        }
    }


    /*
        Color Seek bar Volley
     */
    public void seekBarController() {
        sbRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress_value;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress_value = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // construct url and send it to server if toggle button is on
                if(toggle.isChecked()) {
                    red_val = Integer.toString(progress_value);
                    String sbRed_url = "http://" + ipAdr + ":" + port + "/api/v1/device/" + deviceID + "/transaction?color-r=" + red_val +"&color-g=" +green_val +"&color-b=" +blue_val + "&intensity=100";
                    sendRequest(sbRed_url);
                    Log.d("Test", "RED_VAL: " + red_val);
                    Log.d("Test", "RED: " + sbRed_url);
                }
            }
        });

        sbGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress_value;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress_value = progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(toggle.isChecked()) {
                    green_val = Integer.toString(progress_value);
                    String sbGreen_url = "http://" + ipAdr + ":" + port + "/api/v1/device/" + deviceID + "/transaction?color-r=" + red_val +"&color-g=" +green_val +"&color-b=" +blue_val + "&intensity=100";
                    sendRequest(sbGreen_url);
                    Log.d("Test", "GREEN_VAL: " + green_val);
                    Log.d("Test", "GREEN: " + sbGreen_url);

                }
            }
        });

        sbBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress_value;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress_value = progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                blue_val = Integer.toString(progress_value);
                if(toggle.isChecked()) {
                    String sbBlue_url = "http://" + ipAdr + ":" + port + "/api/v1/device/" + deviceID + "/transaction?color-r=" + red_val +"&color-g=" +green_val +"&color-b=" +blue_val + "&intensity=100";
                    sendRequest(sbBlue_url);
                    Log.d("Test", "BLUE_VAL: " + blue_val);
                    Log.d("Test", "BLUE " + sbBlue_url);

                }
            }
        });
    }

    /*
        Transaction Button
     */
    public void transactionButtonController() {
        transButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecondActivity.this, TransactionActivity.class);
                intent.putExtra("TransactionObject", (ArrayList<Transaction>) device.getTransactionList());
                startActivity(intent);
            }
        });
    }

    /*
        Mode Button
     */
    public void modeButtonController() {
        modeButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    ipAdr = MainActivity.ipAdr;
                    port = MainActivity.port;
                    Log.d("Test", "HM ON: ");
                }
                else {
                    ipAdr = device.getDeviceIP();
                    port = device.getDevicePort();
                    Log.d("Test", "HM OFF");
                }
            }
        });
    }

    /*
       Toggle Button
    */
    public void toggleButtonController() {
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // construct url
                    String toggleReq = "http://"+ ipAdr + ":" + port + "/api/v1/device/" + deviceID + "/transaction?light=on";
                    sendRequest(toggleReq);
                    setSeekBar(toggleReq);
                    Log.d("Test", toggleReq);

                } else {
                    String toggleReq = "http://"+ ipAdr + ":" + port + "/api/v1/device/" + deviceID + "/transaction?light=off";
                    sendRequest(toggleReq);
                    setSeekBar(toggleReq);
                    Log.d("Test", toggleReq);
                }
            }
        });
    }

}

