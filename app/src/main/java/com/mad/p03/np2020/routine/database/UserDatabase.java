package com.mad.p03.np2020.routine.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mad.p03.np2020.routine.Class.Focus;
import com.mad.p03.np2020.routine.Class.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UserDatabase extends SQLiteOpenHelper{

    public static final String TABLE_NAME = "USER_TABLE";
    public static final String COLUMN_UID = "UID";
    public static final String COLUMN_UID_NAME = "NAME";
    public static final String COLUMN_EMAIL = "EMAIL";
    public static final String COLUMN_PASSWORD = "PASSWORD";
    public static final String COLUMN_DOB = "DOB";
    public String TAG = "UserSQL";

    public UserDatabase(@Nullable Context context) {
        super(context, "User.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_UID + "  TEXT PRIMARY KEY, " + COLUMN_UID_NAME + " TEXT, " + COLUMN_EMAIL + " TEXT, " + COLUMN_PASSWORD + " TEXT, " + COLUMN_DOB + " DATE)";

        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_UID, user.getUID());
        cv.put(COLUMN_UID_NAME, user.getName());
        cv.put(COLUMN_EMAIL, user.getEmailAdd());
        cv.put(COLUMN_PASSWORD, user.getPassword());
        cv.put(COLUMN_DOB, user.getDateOfBirth());

        long insert = db.insert(TABLE_NAME, null, cv); //if insert is -1 means fail
        if (insert == -1) return false;
        else return true;
    }

    public User getUserDetail() throws ParseException {
        User user = new User();

        String queryString = "Select * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                String uid = cursor.getString(0);
                String name = cursor.getString(1);
                String email = cursor.getString(2);
                String password = cursor.getString(3);
                String dob = cursor.getString(4);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");

                Date d;
                d =  dateFormat.parse(dob);

                user = new User(uid, name, email, d, password);
                Log.v(TAG, "Adding User");

            } while (cursor.moveToNext());
        } else {
            Log.v(TAG, "SQL Database Fail");
        }

        cursor.close();
        db.close();
        return user;
    }

    public void deleteAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);
        db.close();
    }

}
