package com.example.tue2t.iotdevicelist.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tue2t.iotdevicelist.SQL.DatabaseHelper;
import com.example.tue2t.iotdevicelist.R;
import com.example.tue2t.iotdevicelist.Models.UserInfo;

import java.security.MessageDigest;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    Button regBtn;
    EditText etName, etDOB, etEmail, etUsername, etPassword, etConfirmPassword;
    private String nameStr, dobStr, emailStr, uNameStr, passStr, confirmPassStr;

    DatabaseHelper dbHelper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = (EditText) findViewById(R.id.reg_name);
        etDOB = (EditText) findViewById(R.id.reg_DOB);
        etEmail = (EditText) findViewById(R.id.reg_email);
        etUsername = (EditText) findViewById(R.id.reg_username);
        etPassword = (EditText) findViewById(R.id.reg_password);
        etConfirmPassword = (EditText) findViewById(R.id.reg_confirmpassword);
        regBtn = (Button) findViewById(R.id.regBtn);

        regBtn.setOnClickListener(this);
    }

    // handle multiple onClick
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.regBtn:
                nameStr = etName.getText().toString();
                dobStr = etDOB.getText().toString();
                emailStr = etEmail.getText().toString();
                uNameStr = etUsername.getText().toString();
                passStr = etPassword.getText().toString();
                confirmPassStr = etConfirmPassword.getText().toString();

                if(!passStr.equals(confirmPassStr)) {
                    Toast.makeText(getApplicationContext(), "Password doesn't match", Toast.LENGTH_SHORT).show();
                }
                else {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setName(nameStr);
                    userInfo.setDob(dobStr);
                    userInfo.setEmail(emailStr);
                    userInfo.setUserName(uNameStr);
                    //userInfro.setPass(passStr);
                    try {
                        byte[] passBytes = passStr.getBytes("UTF-8");
                        MessageDigest md = MessageDigest.getInstance("MD5");
                        byte[] hashPass = md.digest(passBytes);
                        String hashStr = Base64.encodeToString(hashPass, Base64.DEFAULT);
                        userInfo.setPass(hashStr);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }


                    // insert to db
                    dbHelper.addNewUser(userInfo);
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                }
                break;
        }
    }
}
