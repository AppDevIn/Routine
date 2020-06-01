package com.mad.p03.np2020.routine.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.Nullable;

import com.mad.p03.np2020.routine.Class.Focus;

import java.util.ArrayList;

public class FocusDatabase extends SQLiteOpenHelper implements Parcelable {

    public static final String FOCUS_TABLE = "FOCUS_TABLE";
    public static final String COLUMN_TASK_NAME = "TASK_NAME";
    public static final String COLUMN_TASK_DATE = "TASK_DATE";
    public static final String COLUMN_TASK_DURATION = "TASK_DURATION";
    public static final String COLUMN_TASK_COMPLETE = "TASK_COMPLETE";
    public static final String COLUMN_TASK_fbID = "fbID";
    public static final String COLUMN_TASK_ID = "ID";

    public FocusDatabase(@Nullable Context context) {
        super(context, "focus.db", null, 1);
    }

    // this is called the first time a database is accessed. Creation a new database will involve here
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + FOCUS_TABLE + " (" + COLUMN_TASK_ID + "  INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_TASK_fbID + " TEXT, " + COLUMN_TASK_NAME + " TEXT, " + COLUMN_TASK_DATE + " TEXT, " + COLUMN_TASK_DURATION + " TEXT, " + COLUMN_TASK_COMPLETE + " BOOL)";

        db.execSQL(createTableStatement);
    }

    //this is called if database version number changes. It prevents previous users apps from breaker when you change the database design
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addData(Focus focus) {
        //This is called to add Data to existing Database
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TASK_fbID, focus.getFbID());
        cv.put(COLUMN_TASK_NAME, focus.getmTask());
        cv.put(COLUMN_TASK_COMPLETE, focus.getmCompletion());
        cv.put(COLUMN_TASK_DURATION, focus.getmDuration());
        cv.put(COLUMN_TASK_DATE, focus.getmDateTime());

        long insert = db.insert(FOCUS_TABLE, null, cv); //if insert is -1 means fail
        if (insert == -1) return false;
        else return true;
    }

    public boolean removeOneData(Focus focus){
        // Find database that match the row data. If it found, delete and return true

        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + FOCUS_TABLE + " WHERE " + COLUMN_TASK_ID + " = " + focus.getSqlID();

        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst()){
            return true;
        }
        else{
            return false;
        }
    }

    public void deleteAll()
    {
        //This is called to destroy SQLite Database
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(FOCUS_TABLE,null,null);
        db.close();
    }


    public ArrayList<Focus> getAllData() {

        //This is called to get all Data existing in the firebase database
        ArrayList<Focus> returnList = new ArrayList<>();

        String queryString = "Select * FROM " + FOCUS_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                String uid = cursor.getString(0);
                String fbId = cursor.getString(1); //fbId
                String taskName = cursor.getString(2);
                String taskDate = cursor.getString(3);
                String taskDuration = cursor.getString(4);
                String taskCompletion = cursor.getString(5);

                Focus newFocus = new Focus(fbId, uid, taskDate, taskDuration, taskName, taskCompletion);
                returnList.add(newFocus);

            } while (cursor.moveToNext());
        } else {
            Log.v("SQL", "SQL Database Fail");
        }

        cursor.close();
        db.close();
        return returnList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
