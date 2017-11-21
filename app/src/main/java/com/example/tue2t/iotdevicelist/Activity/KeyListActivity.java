package com.example.tue2t.iotdevicelist.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.tue2t.iotdevicelist.R;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

public class KeyListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_list);

        // init
        ArrayList<String> keyList = new ArrayList<>();
        ListView lv = (ListView) findViewById(R.id.listView);

        try {
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            Enumeration<String> aliasList = ks.aliases();
            keyList = Collections.list(aliasList);
        }
        catch (Exception e) {
            Log.d("Test", "Failed to load keystore");
            e.printStackTrace();
        }

        // display
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, keyList);
        lv.setAdapter(adapter);
    }
}
