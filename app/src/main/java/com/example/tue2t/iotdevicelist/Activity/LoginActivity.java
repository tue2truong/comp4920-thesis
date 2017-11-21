package com.example.tue2t.iotdevicelist.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tue2t.iotdevicelist.SQL.DatabaseHelper;
import com.example.tue2t.iotdevicelist.R;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button loginBtn;
    EditText etUsername, etPassword;
    TextView regLink;

    DatabaseHelper dbHelper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = (EditText) findViewById(R.id.username);
        etPassword = (EditText) findViewById(R.id.password);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        regLink = (TextView) findViewById(R.id.register);


        loginBtn.setOnClickListener(this);
        regLink.setOnClickListener(this);
    }

    // handle multiple onClick
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                String uNameStr = etUsername.getText().toString();
                String passStr = etPassword.getText().toString();
                String hashStr = "";
                try {
                    byte[] passBytes = passStr.getBytes("UTF-8");
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    byte[] hashPass = md.digest(passBytes);
                    hashStr = Base64.encodeToString(hashPass, Base64.DEFAULT);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                String registeredPass = dbHelper.searchPass(uNameStr);

                if(hashStr.equals(registeredPass)) {
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    i.putExtra("UserName", uNameStr);
                    startActivity(i);
                    finish();

                } else {
                    Toast.makeText(getApplicationContext(), "Incorrect Username and Password", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.register:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
        }
    }
}
