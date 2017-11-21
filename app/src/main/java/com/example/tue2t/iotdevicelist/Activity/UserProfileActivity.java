package com.example.tue2t.iotdevicelist.Activity;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tue2t.iotdevicelist.Models.Device;
import com.example.tue2t.iotdevicelist.R;
import com.example.tue2t.iotdevicelist.SQL.DatabaseHelper;

import org.w3c.dom.Text;

public class UserProfileActivity extends AppCompatActivity {

    DatabaseHelper dbHelper = new DatabaseHelper(this);
    String uName, name, dob, email;
    TextView tvName, tvDOB, tvEmail;
    Button editBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        tvName = (TextView) findViewById(R.id.tvName);
        tvDOB = (TextView) findViewById(R.id.tvDOB);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        editBtn = (Button) findViewById(R.id.button_edit);

        Bundle bundle = getIntent().getExtras();
        uName = bundle.getString("UserName");

        // this could have done better
        // dbHelper could return an entire user profile object -- no time to change now lol !!!
        name = dbHelper.searchName(uName);
        tvName.setText(name);
        dob = dbHelper.searchDOB(uName);
        tvDOB.setText(dob);
        email = dbHelper.searchEmail(uName);
        tvEmail.setText(email);


        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
                builder.setTitle("Edit profile ");
                LinearLayout layout = new LinearLayout(UserProfileActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText nameBox = new EditText(UserProfileActivity.this);
                nameBox.setHint("Enter Name");
                layout.addView(nameBox);

                final EditText dobBox = new EditText(UserProfileActivity.this);
                dobBox.setHint("Enter DOB");
                layout.addView(dobBox);

                final EditText emailBox = new EditText(UserProfileActivity.this);
                emailBox.setHint("Enter email");
                layout.addView(emailBox);

                builder.setView(layout);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!nameBox.getText().toString().equals("")) {
                            tvName.setText(nameBox.getText());
                        }
                        if (!dobBox.getText().toString().equals("")) {
                            tvDOB.setText(dobBox.getText());
                        }
                        if (!emailBox.getText().toString().equals("")) {
                            tvEmail.setText(emailBox.getText());
                        }
                        dbHelper.updateUserInfo(uName, tvName.getText().toString(), tvDOB.getText().toString(), tvEmail.getText().toString());

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
        });
    }
}

