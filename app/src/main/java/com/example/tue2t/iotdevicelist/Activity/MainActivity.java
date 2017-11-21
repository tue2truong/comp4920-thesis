package com.example.tue2t.iotdevicelist.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.os.Handler;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.tue2t.iotdevicelist.Security.DHKeyExchange;
import com.example.tue2t.iotdevicelist.Models.Device;
import com.example.tue2t.iotdevicelist.DeviceAdapter;
import com.example.tue2t.iotdevicelist.MySingleton;
import com.example.tue2t.iotdevicelist.R;
import com.example.tue2t.iotdevicelist.Models.Transaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.GregorianCalendar;


public class MainActivity extends AppCompatActivity {

    // user information
    String uName = "TestUser";

    int REQUEST_TIMEOUT = 10;

    // change volley time out to 10s
    final static int MY_SOCKET_TIME_OUT = 10000;


    // user interface
    private Handler handler;
    ArrayList<Device> deviceList = null;
    DeviceAdapter adapter = null;
    ListView lv = null;

    // network
    public static final String ipAdr = "192.168.1.105";
    public static final String port= "8002";

    // performance measurement
    long startTime;
    long endTime;
    int transCounter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);


        // get user detail from login page
        Bundle bundle = getIntent().getExtras();
        uName = bundle.getString("UserName");

        // check for internet connectivity
        if (!isConnected(MainActivity.this)) {
            Toast.makeText(MainActivity.this, "ERROR: No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        else {
            // HM or overlay connection
            if(isHMConnected(MainActivity.this)) {
                Toast.makeText(MainActivity.this, "SUCCESS: HM Connected", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(MainActivity.this, "SUCCESS: Overlay Connected", Toast.LENGTH_SHORT).show();
                // perform AsyncTask doInBackGround() for overlay routing
            }
        }

        displayDeviceList();

        /*
        // unit test
        testDeviceListDisplay();
        testKeyExchange();
         */
    }

    /*
    MENU
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // set login user detail
        MenuItem accountItem = menu.findItem(R.id.action_account);
        accountItem.setTitle(uName);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // KeyStore - for demo purposes
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, KeyStoreActivity.class);
            startActivity(intent);
            return true;
        }

        if(id == R.id.action_add_dev) {
            addDevice();
            return true;
        }

        if (id == R.id.action_account) {
            Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
            intent.putExtra("UserName", uName);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_logout) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /*
    HELPER FUNCTIONS
     */

    private void displayDeviceList() {
        // display device list
        deviceList = new ArrayList<Device>();
        adapter = new DeviceAdapter(this, deviceList);
        lv = (ListView) findViewById(R.id.deviceList);
        lv.setAdapter(adapter);
        handler = new Handler();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                requestDeviceList();
            }
        });
        t.start();

        handleListViewEvent();
    }

    private void handleListViewEvent() {
        /*
        Click event
         */
        // hold item to delete it
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Device selDevice = (Device) lv.getItemAtPosition(position);
                removeDevice(selDevice, position);
                return true;
            }
        });

        // click item
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View view, final int position, long id) {
                Device selDevice = (Device) lv.getItemAtPosition(position);

                if(selDevice.getDeviceName().contains("ight")) {
                    Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                    intent.putExtra("DeviceObject", (Device) lv.getItemAtPosition(position));
                    startActivity(intent);
                }
                else if(selDevice.getDeviceName().contains("mera")) {
                    Intent intent = new Intent(MainActivity.this, ThirdActivity.class);
                    intent.putExtra("DeviceObject", (Device) lv.getItemAtPosition(position));
                    startActivity(intent);
                }
                else if (selDevice.getDeviceName().contains("lug")) {
                    Intent intent = new Intent(MainActivity.this, FourthActivity.class);
                    intent.putExtra("DeviceObject", (Device) lv.getItemAtPosition(position));
                    startActivity(intent);
                }
            }
        });
    }

    // device list json extraction
    public void requestDeviceList() {
        /*
        String devList_url = "http://"+ ipAdr + ":" + port + "/api/v1/device";
        RequestFuture<JSONArray> future = RequestFuture.newFuture();
        JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, devList_url, future, future);
        MySingleton.getInstance(MainActivity.this).addToRequestQueue(stringRequest);

        try {
            JSONArray response = future.get(REQUEST_TIMEOUT, TimeUnit.SECONDS);
            Log.d("Test", "receive response");

            try {
                for (int i = 0; i < response.length(); i++) {
                    JSONObject jsonDeviceObj = response.getJSONObject(i);
                    // device info
                    JSONObject deviceInfo = jsonDeviceObj.getJSONObject("device");
                    String deviceName = deviceInfo.getString("deviceName");
                    String deviceIP = deviceInfo.getString("IPAddress");
                    String devicePort = deviceInfo.getString("portNumber");
                    String deviceID = Integer.toString(deviceInfo.getInt("deviceID"));
                    ArrayList<Transaction> transactions = new ArrayList<Transaction>();

                    Log.d("Test", deviceName);
                    // transaction list
                    JSONArray transList = jsonDeviceObj.getJSONArray("transactions");
                    for (int j = 0; j < transList.length(); j++) {
                        JSONObject transactionInfo = transList.getJSONObject(j);
                        String dateString = transactionInfo.getString("dateString");
                        String data = transactionInfo.getString("data");
                        String type = transactionInfo.getString("type");

                        Transaction trans = new Transaction(dateString, data, type);
                        transactions.add(trans);
                    }
                    Device newDevice = new Device(deviceName, deviceIP, devicePort, deviceID, transactions);
                    deviceList.add(newDevice);
                    Log.d("Test", String.valueOf(deviceList.size()));
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        */
        //startTime = new GregorianCalendar().getTimeInMillis();
        //Log.d("Test", "Start Time: " + String.valueOf(startTime));

        String devList_url = "http://"+ ipAdr + ":" + port + "/api/v1/device";
        JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, devList_url,
                new Response.Listener<JSONArray>() {
                    // response from server
                    @Override
                    public void onResponse(JSONArray response) {
                        endTime = new GregorianCalendar().getTimeInMillis();
                        //Log.d("Test", "End Time: " + String.valueOf(endTime));
                        long rtt = endTime - startTime;
                        Log.d("Test", "RTT: " + String.valueOf(rtt));
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonDeviceObj = response.getJSONObject(i);
                                // device info
                                JSONObject deviceInfo = jsonDeviceObj.getJSONObject("device");
                                String deviceName = deviceInfo.getString("deviceName");
                                String deviceIP = deviceInfo.getString("IPAddress");
                                String devicePort = deviceInfo.getString("portNumber");
                                String deviceID = Integer.toString(deviceInfo.getInt("deviceID"));
                                ArrayList<Transaction> transactions = new ArrayList<Transaction>();

                                //Log.d("Test", deviceName);
                                // transaction list
                                JSONArray transList = jsonDeviceObj.getJSONArray("transactions");
                                transCounter = transList.length();
                                Log.d("Test", "Transactions Number: " + String.valueOf(transCounter) + "\n\n\n");
                                for (int j = 0; j < transList.length(); j++) {
                                    JSONObject transactionInfo = transList.getJSONObject(j);
                                    String dateString = transactionInfo.getString("dateString");
                                    String data = transactionInfo.getString("data");
                                    String type = transactionInfo.getString("type");

                                    Transaction trans = new Transaction(dateString, data, type);
                                    transactions.add(trans);
                                }
                                Device newDevice = new Device(deviceName, deviceIP, devicePort, deviceID, transactions);
                                deviceList.add(newDevice);
                                //Log.d("Test", String.valueOf(deviceList.size()));

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            // error message from server
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(MainActivity.this, "Failed to retrieve device list", Toast.LENGTH_SHORT).show();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        startTime = new GregorianCalendar().getTimeInMillis();
        //  Log.d("Test", "Start Time: " + String.valueOf(startTime));
        MySingleton.getInstance(MainActivity.this).addToRequestQueue(stringRequest);

    }

    // add new device
    public void addDevice() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Device");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText nameBox = new EditText(this);
        nameBox.setHint("Device Name");
        layout.addView(nameBox);

        final EditText ipBox = new EditText(this);
        ipBox.setHint("IP Address");
        layout.addView(ipBox);

        final EditText portBox = new EditText(this);
        portBox.setHint("Port");
        layout.addView(portBox);

        builder.setView(layout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // displayList.add(preferredCase(input.getText().toString()));
                int deviceID = 0;
                deviceList.add(new Device(nameBox.getText().toString(), ipBox.getText().toString(), portBox.getText().toString(), Integer.toString(deviceID), null));
                lv.setAdapter(adapter);

                // CREATE NEW DEVICE TODO
                // send IP address of the HM to the device

                // resend device list request to update device list

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    // remove device
    public void removeDevice(final Device selectedItem, final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove " + selectedItem.getDeviceName() + "?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deviceList.remove(position);
                lv.setAdapter(adapter);
                Toast.makeText(getApplicationContext(),"REMOVE: " + selectedItem.getDeviceName(), Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    // format user input (nice interface)
    public static String preferredCase(String original)
    {
        if (original.isEmpty())
            return original;

        return original.substring(0, 1).toUpperCase() + original.substring(1).toLowerCase();
    }

    // send volley request to rest server
    public void sendRequest(String input_url) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, input_url,
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
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
        MySingleton.getInstance(MainActivity.this).addToRequestQueue(stringRequest);
    }

    // check connectivity
    public boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean bool = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return bool;
    }

    // check wifi name - pizza
    public boolean isHMConnected(Context context) {
        boolean result = false;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID();
        ssid = ssid.replace("\"", "");
        if(ssid.equals("pizza")) {
            result = true;
        }
        return result;
    }

    /*
    TESTER FUNCTION
     */

    // device list test
    public void testDeviceListDisplay() {
        Device testDevice1 = new Device("LightOne", "192.168.1.1", "8002", "1", null);
        Device testDevice2 = new Device("CameraTwo", "192.168.1.2", "8002", "2", null);

        ArrayList<Transaction> testDevice3Transactions = new ArrayList<Transaction>();
        testDevice3Transactions.add(new Transaction("09/10/2017 14:18.29", "Lights : ON", "data"));
        testDevice3Transactions.add(new Transaction("09/10/2017 14:18.30", "Lights : OFF", "data"));
        testDevice3Transactions.add(new Transaction("09/10/2017 14:28.29", "Lights : ON", "data"));
        Device testDevice3 = new Device("LightThree", "192.168.1.3", "8002", "3", testDevice3Transactions);

        Device testDevice4 = new Device("LightFour", "192.168.1.4", "8002", "4", null);
        Device testDevice5 = new Device("CameraFive", "192.168.1.5", "8002", "5", null);
        Device testDevice6 = new Device("CameraSix", "192.168.1.6", "8002", "6", null);
        deviceList.add(testDevice1);
        deviceList.add(testDevice2);
        deviceList.add(testDevice3);
        deviceList.add(testDevice4);
        deviceList.add(testDevice5);
        deviceList.add(testDevice6);
    }

    //key exchange test
    public void testKeyExchange() {
        DHKeyExchange keyExchange = new DHKeyExchange();
        keyExchange.generateKeys();
    }
}


