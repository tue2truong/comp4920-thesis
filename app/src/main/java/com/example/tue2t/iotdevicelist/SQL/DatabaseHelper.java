package com.example.tue2t.iotdevicelist.SQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.volley.toolbox.StringRequest;
import com.example.tue2t.iotdevicelist.Models.UserInfo;

/**
 * Created by tue2t on 10/10/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION  = 1;
    private static final String DATABASE_NAME  = "users.db";
    private static final String TABLE_NAME  = "users";
    private static final String COLUMN_UID  = "userID";
    private static final String COLUMN_NAME  = "name";
    private static final String COLUMN_DOB  = "dob";
    private static final String COLUMN_EMAIL  = "email";
    private static final String COLUMN_USERNAME  = "username";
    private static final String COLUMN_PASS  = "pass";

    // DB
    SQLiteDatabase db;
    private static final String TABLE_CREATE = "create table " + TABLE_NAME + " (userID integer primary key not null , " +
            "name text not null , dob text not null , email text not null , username text not null , pass text not null);";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        this.db = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(query);
        this.onCreate(db);
    }

    // insert new entry to db
    public void addNewUser(UserInfo userInfo) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String query = "select * from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        int counter = cursor.getCount();
        counter++;

        values.put(COLUMN_UID, counter);
        values.put(COLUMN_NAME, userInfo.getName());
        values.put(COLUMN_DOB, userInfo.getDob());
        values.put(COLUMN_EMAIL, userInfo.getEmail());
        values.put(COLUMN_USERNAME, userInfo.getUserName());
        values.put(COLUMN_PASS, userInfo.getPass());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // update db given userName
    public void updateUserInfo (String uName, String newName, String newDOB, String newEmail) {
        // find userID
        db = this.getReadableDatabase();
        String query = "select username, userID from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        String currUserName;
        int id = -1;

        if(cursor.moveToFirst())
        {
            do {
                currUserName = cursor.getString(0);
                if (currUserName.equals(uName)) {
                    id = cursor.getInt(1);
                    break;
                }
            } while (cursor.moveToNext());
        }

        // update db for user
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name", newName);
        values.put("dob", newDOB);
        values.put("email", newEmail);


        db.update(TABLE_NAME, values, "userID="+id, null);
    }

    // search pass given uName
    public String searchPass(String uName) {
        db = this.getReadableDatabase();
        String query = "select username, pass from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        String currUserName;
        String pass = "NOT FOUND";

        if(cursor.moveToFirst())
        {
            do {
                currUserName = cursor.getString(0);
                if (currUserName.equals(uName)) {
                    pass = cursor.getString(1);
                    break;
                }
            } while (cursor.moveToNext());
        }
        return pass;
    }

    //search DOB given uName
    public String searchName(String uName) {
        db = this.getReadableDatabase();
        String query = "select username, name from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        String currUserName;
        String name = "NOT FOUND";

        if(cursor.moveToFirst())
        {
            do {
                currUserName = cursor.getString(0);
                if (currUserName.equals(uName)) {
                    name = cursor.getString(1);
                    break;
                }
            } while (cursor.moveToNext());
        }
        return name;
    }

    //search DOB given uName
    public String searchDOB(String uName) {
        db = this.getReadableDatabase();
        String query = "select username, dob from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        String currUserName;
        String dob = "NOT FOUND";

        if(cursor.moveToFirst())
        {
            do {
                currUserName = cursor.getString(0);
                if (currUserName.equals(uName)) {
                    dob = cursor.getString(1);
                    break;
                }
            } while (cursor.moveToNext());
        }
        return dob;
    }

    // search email given uName
    public String searchEmail(String uName) {
        db = this.getReadableDatabase();
        String query = "select username, email from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        String currUserName;
        String email = "NOT FOUND";

        if(cursor.moveToFirst())
        {
            do {
                currUserName = cursor.getString(0);
                if (currUserName.equals(uName)) {
                    email = cursor.getString(1);
                    break;
                }
            } while (cursor.moveToNext());
        }
        return email;
    }
}
