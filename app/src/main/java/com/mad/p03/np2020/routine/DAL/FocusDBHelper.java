package com.mad.p03.np2020.routine.DAL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.Nullable;

import com.mad.p03.np2020.routine.Class.Focus;

import java.util.ArrayList;


/**
 *
 * Model used to manage the section
 *
 * @author Lee Quan Sheng
 * @since 02-06-2020
 */


public class FocusDBHelper extends DBHelper  implements Parcelable{



    /**
     *
     * Show custom delete item AlertDialog
     *
     * @param context set context to this content
     *
     * */
    public FocusDBHelper(@Nullable Context context) {
        super(context);
    }

    /**
     *
     * this is called the first time a database is accessed. Creation a new database will involve here
     *
     * @param db set SQLiteDatabase Parameter for this content
     * */
    @Override
    public void onCreate(SQLiteDatabase db) {
        super.onCreate(db);
    }

    /**
     *
     * this is called if database version number changes. It prevents previous users apps from breaker when you change the database design
     *
     * @param db set SQLiteDatabase Parameter for this content
     * @param oldVersion set the older version parameter for this content sqlite version
     * @param newVersion set the newer version paramter for this content of sqlite version
     * */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    /**
     *
     * This is called to add Data to existing Database
     *
     * @param focus set focus parameter for this content
     * @return boolean returns success or fail on the insert of data
     * */
    public boolean addData(Focus focus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(Focus.COLUMN_TASK_fbID, focus.getFbID());
        cv.put(Focus.COLUMN_TASK_NAME, focus.getmTask());
        cv.put(Focus.COLUMN_TASK_COMPLETE, focus.getmCompletion());
        cv.put(Focus.COLUMN_TASK_DURATION, focus.getmDuration());
        cv.put(Focus.COLUMN_TASK_DATE, focus.getmDateTime());

        long insert = db.insert(Focus.FOCUS_TABLE, null, cv); //if insert is -1 means fail
        if (insert == -1) return false;
        else return true;
    }

    /**
     *
     * This is called to remove Data to existing Database
     *
     * @param focus set focus parameter for this content
     * @return boolean returns success or fail on the insert of data
     * */
    public boolean removeOneData(Focus focus){
        // Find database that match the row data. If it found, delete and return true

        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + Focus.FOCUS_TABLE + " WHERE " + Focus.COLUMN_TASK_fbID + " = " + "'" + focus.getFbID() + "'";

        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst()){
            return true;
        }
        else{
            return false;
        }
    }

    /**
     *
     * This is called to Delete all Data to existing Database
     *
     * */
    public void deleteAll()
    {
        //This is called to destroy SQLite Database
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Focus.FOCUS_TABLE,null,null);
        db.close();
    }

    /**
     *
     * This is called to get all Row data to existing Database
     *
     * @return ArrayList of focus that has retrieve from the local database
     * */
    public ArrayList<Focus> getAllData() {

        //This is called to get all Data existing in the firebase database
        ArrayList<Focus> returnList = new ArrayList<>();

        String queryString = "Select * FROM " + Focus.FOCUS_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                String fbId = cursor.getString(0); //fbId
                String taskName = cursor.getString(1);
                String taskDate = cursor.getString(2);
                String taskDuration = cursor.getString(3);
                String taskCompletion = cursor.getString(4);

                Focus newFocus = new Focus(fbId, taskDate, taskDuration, taskName, taskCompletion);
                returnList.add(newFocus);

            } while (cursor.moveToNext());
        } else {
            Log.v("SQL", "SQL Database Fail");
        }

        cursor.close();
        db.close();
        return returnList;
    }

    /***
     *
     * Method is used to check if table exist inside the database
     *
     * @param tableName Pass in table name to the current content
     * @return boolean Return True or False;
     * True will be return if table exist
     * False will be return if table does not exist
     */
    public boolean isTableExists(String tableName) {

        SQLiteDatabase mDatabase = this.getReadableDatabase();

        String query = "select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'";
        try (Cursor cursor = mDatabase.rawQuery(query, null)) {
            if(cursor!=null) {
                if(cursor.getCount()>0) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
