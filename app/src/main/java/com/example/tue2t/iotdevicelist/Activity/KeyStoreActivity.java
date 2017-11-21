package com.example.tue2t.iotdevicelist.Activity;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tue2t.iotdevicelist.R;
import com.example.tue2t.iotdevicelist.Security.DHKeyExchange;

import java.security.PublicKey;


public class KeyStoreActivity extends AppCompatActivity implements View.OnClickListener {

    DHKeyExchange ksHelper;

    Button createBtn, encryptBtn, decryptBtn, listBtn;
    EditText editText;
    TextView textView;

    String currAlias;
    String encryptStr, decryptStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_store);

        createBtn = (Button) findViewById(R.id.create);
        encryptBtn = (Button) findViewById(R.id.encrypt);
        decryptBtn = (Button) findViewById(R.id.decrypt);
        listBtn = (Button) findViewById(R.id.list_key);
        editText = (EditText) findViewById(R.id.inputString);
        textView = (TextView) findViewById(R.id.outputText);
        ksHelper = new DHKeyExchange();
        ksHelper.generateKeys();

        createBtn.setOnClickListener(this);
        encryptBtn.setOnClickListener(this);
        decryptBtn.setOnClickListener(this);
        listBtn.setOnClickListener(this);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.encrypt:
                //PublicKey pub = ksHelper.getPublicKey(currAlias);
                //String pubStr = Base64.encodeToString(pub.getEncoded(), Base64.DEFAULT);

                String inputStr = editText.getText().toString();
                encryptStr = ksHelper.encrypt(currAlias, inputStr);

                textView.setText("CurrAlias: " + currAlias + "\n -------- \n" + "Encrypt " + inputStr + ": \n" + encryptStr);

                Log.d("Test", "Input String: " + inputStr);

                break;

            case R.id.create:
                // generate new key pair
                String newAlias = ksHelper.generateKeys();
                currAlias = newAlias;

                textView.setText("CurrAlias: " + currAlias + "\n -------- \n\n");


                Log.d("Test", "CREATE HELLO");
                break;

            case R.id.decrypt:
                decryptStr = ksHelper.decrypt(currAlias, encryptStr);

                textView.setText("CurrAlias: " + currAlias + "\n -------- \n\n" + "Decrypt: " + decryptStr );

                Log.d("Test", "DECRYPT HELLO");
                break;

            case R.id.list_key:

                startActivity(new Intent(KeyStoreActivity.this, KeyListActivity.class));
                Log.d("Test", "LIST HELLO");
                break;
        }
    }
}
