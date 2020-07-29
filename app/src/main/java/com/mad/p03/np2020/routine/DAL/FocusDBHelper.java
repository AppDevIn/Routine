package com.mad.p03.np2020.routine.DAL;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.Nullable;

import com.mad.p03.np2020.routine.background.DatabaseObservable;
import com.mad.p03.np2020.routine.background.DatabaseObserver;
import com.mad.p03.np2020.routine.models.Check;
import com.mad.p03.np2020.routine.models.Focus;

import java.util.ArrayList;


/**
 * This created to handle the Focus Data in SQLiteDatabase
 *
 * @author Lee Quan Sheng
 * @since 02-06-2020
 */

@SuppressLint("ParcelCreator")
public class FocusDBHelper extends DBHelper implements Parcelable, DatabaseObservable {

    String TAG = "FocusDBHelper";

    /**
     * Show custom delete item AlertDialog
     *
     * @param context set context to this content
     */
    public FocusDBHelper(@Nullable Context context) {
        super(context);
    }
    ArrayList<DatabaseObserver> observerArrayList = new ArrayList<>();

    /**
     * this is called the first time a database is accessed. Creation a new database will involve here
     *
     * @param db set SQLiteDatabase Parameter for this content
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        super.onCreate(db);
        observerArrayList = new ArrayList<>();
    }

    /**
     * this is called if database version number changes. It prevents previous users apps from breaker when you change the database design
     *
     * @param db         set SQLiteDatabase Parameter for this content
     * @param oldVersion set the older version parameter for this content sqlite version
     * @param newVersion set the newer version paramter for this content of sqlite version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    /**
     * This is called to add Data to existing Database
     *
     * @param focus set focus parameter for this content
     */
    public void addData(Focus focus) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(Focus.COLUMN_TASK_fbID, focus.getFbID());
        cv.put(Focus.COLUMN_TASK_NAME, focus.getmTask());
        cv.put(Focus.COLUMN_TASK_COMPLETE, focus.getmCompletion());
        cv.put(Focus.COLUMN_TASK_DURATION, focus.getmDuration());
        cv.put(Focus.COLUMN_TASK_DATE, focus.getmDateTime());
        cv.put(Focus.COLUMN_TASK_TIME_TAKEN, focus.getmTimeTaken());
        Log.d(TAG, "Adding data in db helper: " + focus);

        long insert = db.insert(Focus.FOCUS_TABLE, null, cv); //if insert is -1 means fail
        db.close();
        notifyDbChanged();

    }

    /**
     * This is called to add Data to existing Database
     *
     * @param focus set focus parameter for this content
     * @return boolean returns success or fail on the insert of data
     */
    public boolean addArchiveData(Focus focus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(Focus.COLUMN_TASK_fbID, focus.getFbID());
        cv.put(Focus.COLUMN_TASK_NAME, focus.getmTask());
        cv.put(Focus.COLUMN_TASK_COMPLETE, focus.getmCompletion());
        cv.put(Focus.COLUMN_TASK_DURATION, focus.getmDuration());
        cv.put(Focus.COLUMN_TASK_DATE, focus.getmDateTime());
        cv.put(Focus.COLUMN_TASK_TIME_TAKEN, focus.getmTimeTaken());

        long insert = db.insert(Focus.FOCUS_Archive_TABLE, null, cv); //if insert is -1 means fail
        db.close();
        return insert != -1;
    }

    /**
     * This is called to remove Data to existing Database
     *
     * @param focus set focus parameter for this content
     */
    public void removeOneData(Focus focus) {

        // Find database that match the row data. If it found, delete and return true

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(
                Focus.FOCUS_TABLE,  // The table to delete from
                Focus.COLUMN_TASK_fbID + " = ?", //The condition
                new String[]{focus.getFbID()} // The args will be replaced by ?
        );
        Log.d(TAG, "deleted: " + focus.getFbID());

        notifyDbChanged();

        db.close();

    }

    public void removeOneAData(Focus focus) {

        // Find database that match the row data. If it found, delete and return true

        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + Focus.FOCUS_Archive_TABLE + " WHERE " + Focus.COLUMN_TASK_fbID + " = " + "'" + focus.getFbID() + "'";

        Cursor cursor = db.rawQuery(queryString, null);
        notifyDbChanged();

        db.close();

    }

    /**
     * This is called to Delete all Data to existing Database
     */
    public void deleteAllMain() {
        //This is called to destroy SQLite Database
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Focus.FOCUS_TABLE, null, null);
        db.close();
    }

    /**
     * This is called to Delete all Data to existing archive Database
     */
    public void deleteAllArchive() {
        //This is called to destroy SQLite Database
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Focus.FOCUS_Archive_TABLE, null, null);
        db.close();
    }

    /**
     * This is called to get all Row data to existing Database
     *
     * @return ArrayList of focus that has retrieve from the local database
     */
    public ArrayList<Focus> getAllMainData() {

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
                long timeTaken = cursor.getLong(5);

                Focus newFocus = new Focus(fbId, taskDate, taskDuration, taskName, taskCompletion, timeTaken);
                returnList.add(newFocus);

            } while (cursor.moveToNext());
        } else {
            Log.v("SQL", "SQL Database Fail");
        }

        cursor.close();
        db.close();

        Log.v("SQL", "Focus Data has been initialized completed with " + returnList.size());

        return returnList;
    }

    /**
     * This is called to get all Row data to existing Database
     *
     * @return ArrayList of focus that has retrieve from the archive database
     */
    public ArrayList<Focus> getAllArchiveData() {

        //This is called to get all Data existing in the firebase database
        ArrayList<Focus> returnList = new ArrayList<>();

        String queryString = "Select * FROM " + Focus.FOCUS_Archive_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                String fbId = cursor.getString(0); //fbId
                String taskName = cursor.getString(1);
                String taskDate = cursor.getString(2);
                String taskDuration = cursor.getString(3);
                String taskCompletion = cursor.getString(4);
                long timeTaken = cursor.getLong(5);

                Focus newFocus = new Focus(fbId, taskDate, taskDuration, taskName, taskCompletion, timeTaken);
                returnList.add(newFocus);

            } while (cursor.moveToNext());
        } else {
            Log.v("SQL", "SQL Database Fail");
        }

        cursor.close();
        db.close();

        Log.v("SQL", "Focus Data has been initialized completed with " + returnList.size());

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

        String query = "select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'";
        try (Cursor cursor = mDatabase.rawQuery(query, null)) {
            if (cursor != null) {
                Log.v("Database", cursor.getCount() + " number");
                if (cursor.getCount() > 0) {
                    mDatabase.close();
                    return true;

                }
            }
            mDatabase.close();
            return false;
        }
    }

    /***
     * Check if row exist
     * @return
     */

    public boolean rowExist(String fbID) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + Focus.FOCUS_TABLE + " where " + Focus.COLUMN_TASK_fbID + "='" + fbID + "'", null);

        return cursor.moveToFirst();
    }

    public boolean rowAexist(String fbID) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + Focus.FOCUS_Archive_TABLE + " where " + Focus.COLUMN_TASK_fbID + "='" + fbID + "'", null);

        return cursor.moveToFirst();
    }

    /***
     * check if row is updated based on firebase
     * @return
     */
    public Focus getOneFocusData(String fbID) {
        //This is called to get one data existing in the firebase database

        //Query to get one data from SQLite
        String queryString = "select * from " + Focus.FOCUS_TABLE + " where " + Focus.COLUMN_TASK_fbID + "='" + fbID + "'";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        assert cursor != null;

        String fbId = cursor.getString(0); //fbId
        String taskName = cursor.getString(1);
        String taskDate = cursor.getString(2);
        String taskDuration = cursor.getString(3);
        String taskCompletion = cursor.getString(4);
        long timeTaken = cursor.getLong(5);

        Focus newFocus = new Focus(fbId, taskDate, taskDuration, taskName, taskCompletion, timeTaken);

        cursor.close();
        db.close();

        Log.v("SQL", "Focus Data has been added completed with:  " + fbID);

        return newFocus;
    }

    public Focus getOneArchiveFocusData(String fbID) {
        //This is called to get one data existing in the firebase database

        //Query to get one data from SQLite
        String queryString = "select * from " + Focus.FOCUS_Archive_TABLE + " where " + Focus.COLUMN_TASK_fbID + "='" + fbID + "'";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        assert cursor != null;

        String fbId = cursor.getString(0); //fbId
        String taskName = cursor.getString(1);
        String taskDate = cursor.getString(2);
        String taskDuration = cursor.getString(3);
        String taskCompletion = cursor.getString(4);
        long timeTaken = cursor.getLong(5);

        Focus newFocus = new Focus(fbId, taskDate, taskDuration, taskName, taskCompletion, timeTaken);

        cursor.close();
        db.close();

        Log.v("SQL", "Focus Data has been added completed with:  " + fbID);

        return newFocus;
    }

    public void update(Focus focus) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues updateValues = new ContentValues();
        updateValues.put(Focus.COLUMN_TASK_fbID, focus.getFbID());
        updateValues.put(Focus.COLUMN_TASK_NAME, focus.getmTask());
        updateValues.put(Focus.COLUMN_TASK_COMPLETE, focus.getmCompletion());
        updateValues.put(Focus.COLUMN_TASK_DURATION, focus.getmDuration());
        updateValues.put(Focus.COLUMN_TASK_DATE, focus.getmDateTime());
        updateValues.put(Focus.COLUMN_TASK_TIME_TAKEN, focus.getmTimeTaken());

        db.update(
                Focus.FOCUS_TABLE,
                updateValues,
                Focus.COLUMN_TASK_fbID  + " = ?",
                new String[]{focus.getFbID()});
        db.close();
        notifyDbChanged();

    }

    public void updateAfocus(Focus focus) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues updateValues = new ContentValues();
        updateValues.put(Focus.COLUMN_TASK_fbID, focus.getFbID());
        updateValues.put(Focus.COLUMN_TASK_NAME, focus.getmTask());
        updateValues.put(Focus.COLUMN_TASK_COMPLETE, focus.getmCompletion());
        updateValues.put(Focus.COLUMN_TASK_DURATION, focus.getmDuration());
        updateValues.put(Focus.COLUMN_TASK_DATE, focus.getmDateTime());
        updateValues.put(Focus.COLUMN_TASK_TIME_TAKEN, focus.getmTimeTaken());

        db.update(
                Focus.FOCUS_Archive_TABLE,
                updateValues,
                Focus.COLUMN_TASK_fbID  + " = ?",
                new String[]{focus.getFbID()});
        db.close();
        notifyDbChanged();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public void registerDbObserver(DatabaseObserver databaseObserver) {
        if (!observerArrayList.contains(databaseObserver)){
            observerArrayList.add(databaseObserver);
        }
    }

    @Override
    public void removeDbObserver(DatabaseObserver databaseObserver) {
        observerArrayList.remove(databaseObserver);
    }

    @Override
    public void notifyDbChanged() {
        for (DatabaseObserver databaseObserver:observerArrayList){
            if (databaseObserver!= null){
                databaseObserver.onDatabaseChanged();
                Log.v(TAG,"SQLiteDatabase onChanged triggered");
            }}
    }


}
