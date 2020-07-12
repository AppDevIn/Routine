package com.mad.p03.np2020.routine.DAL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mad.p03.np2020.routine.helpers.CheckDataListener;
import com.mad.p03.np2020.routine.helpers.MyDatabaseListener;
import com.mad.p03.np2020.routine.models.Check;
import com.mad.p03.np2020.routine.models.Section;
import com.mad.p03.np2020.routine.models.Task;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class CheckDBHelper extends DBHelper{

    private final String TAG = "CheckDatabase";

    //Listener
    private static CheckDataListener sCheckDataListener;

    public CheckDBHelper(@Nullable Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        super.onCreate(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        super.onUpgrade(sqLiteDatabase, i, i1);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    /**
     * Assign the listener implementing events interface
     * that will receive the events
     * @param checkDataListener setting the listener where the owner will listen to the message
     */
    public static void setMyDatabaseListener(CheckDataListener checkDataListener){
        sCheckDataListener = checkDataListener;
    }


    public Check getCheck(String id){
        SQLiteDatabase db = this.getReadableDatabase();

        Log.d(TAG, "getUser: Querying data");


        //Get the data from sqlite
        Cursor cursor =  db.rawQuery( "SELECT * FROM " + Check.TABLE_NAME+ " where "+Check.COLUMN_Check_ID+"='"+id+"'", null );

        if (cursor != null)
            cursor.moveToFirst(); //Only getting the first value

        //Prepare a section object
        assert cursor != null;
        Check check = Check.fromCursor(cursor);
        Log.d(TAG, "getTask(): Reading data" + check.toString() );


        //Close the DB connection
        db.close();

        return check;

    }

    public List<Check> getAllCheck(String taskID){

        List<Check> checkList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Log.d(TAG, "getUser: Querying data");


        //Get the data from sqlite
        Cursor cursor =  db.rawQuery( "SELECT * FROM " + Check.TABLE_NAME+ " WHERE TaskID='"+taskID+"' ORDER BY " + Check.COLUMN_POSITION + " ASC;", null );

        if (cursor.moveToFirst()){
            do {
                Check check = Check.fromCursor(cursor);

                Log.d(TAG, "getAllSections(): Reading data" + check.toString() );

                checkList.add(check);
            } while (cursor.moveToNext());
        }

        Log.d(TAG, "Reading data" + checkList.toString() );


        //Close the DB connection
        db.close();

        return checkList;

    }

    public String insertCheck(Check check, String taskID) {
        Log.d(TAG, "insertCheck(): Preparing to insert the new Check ");

        //Add values into the database
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Check.COLUMN_Check_ID, check.getID());
        values.put(Check.COLUMN_CHECKED, check.isChecked());
        values.put(Check.COLUMN_NAME, check.getName());
        values.put(Check.COLUMN_TaskID, taskID);

        // Insert the new row, returning the primary key value of the new row
        //If -1 means there is an error
        long id = db.insert(Check.TABLE_NAME, null, values);

        if(id == -1){
            Log.e(TAG, "insertTask(): There has been error inserting the data: ");
        } else {
            Log.d(TAG, "insertTask(): Data inserted");
        }

        if (sCheckDataListener != null)
            sCheckDataListener.onDataAdd(check);

        return String.valueOf(id);

    }

    public String insertCheck(Check check) {
        Log.d(TAG, "insertCheck(): Preparing to insert the new Check ");

        //Add values into the database
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Check.COLUMN_Check_ID, check.getID());
        values.put(Check.COLUMN_CHECKED, check.isChecked());
        values.put(Check.COLUMN_NAME, check.getName());
        values.put(Check.COLUMN_TaskID, check.getTaskID());

        // Insert the new row, returning the primary key value of the new row
        //If -1 means there is an error
        long id = db.insert(Check.TABLE_NAME, null, values);

        if(id == -1){
            Log.e(TAG, "insertTask(): There has been error inserting the data: ");
        } else {
            Log.d(TAG, "insertTask(): Data inserted");
        }

        if (sCheckDataListener != null)
            sCheckDataListener.onDataAdd(check);

        return String.valueOf(id);

    }



    /**
     * This method will update the position of the given
     * row based on the ID
     * @param ID
     * @param name
     */
    public void update(String ID, String name){

        SQLiteDatabase db = this.getWritableDatabase();



        ContentValues updateValues = new ContentValues();
        if(name != null)
            updateValues.put(Task.COLUMN_NAME, name);
        db.update(
                Task.TABLE_NAME,
                updateValues,
                Task.COLUMN_TASK_ID + " = ?",
                new String[]{ID}
        );

        db.close();
    }


    /**
     * This will delete the data from checkList table with
     * section
     *
     * @param ID ID that will be used find the task and delete it
     */
    public void delete(String ID){

        Log.d(TAG, "delete(): Will be deleting ID " + ID );

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(
                Check.TABLE_NAME,  // The table to delete from
                Check.COLUMN_Check_ID + " = ?", //The condition
                new String[]{ID} // The args will be replaced by ?
        );

        Log.d(TAG, "delete(): Removed from database");

        if (sCheckDataListener != null)
            sCheckDataListener.onDataDelete(ID);

        db.close();
    }

    /**
     * This will delete all the data from checkList table based
     * of the taskID
     *
     * @param taskID ID that will be used find the task and delete it
     */
    public void deleteAll(String taskID){

        Log.d(TAG, "delete(): Will be deleting ID " + taskID );

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(
                Check.TABLE_NAME,  // The table to delete from
                Check.COLUMN_TaskID + " = ?", //The condition
                new String[]{taskID} // The args will be replaced by ?
        );

        Log.d(TAG, "delete(): Removed from database");

        db.close();
    }

    /**
     * This method will update the position of the given
     * row based on the ID
     * @param ID
     * @param position
     */
    public void update(String ID, int position){

        Log.d(TAG, "update(): Updating position " + position + " for " + ID);

        SQLiteDatabase db = this.getWritableDatabase();



        ContentValues updateValues = new ContentValues();
        updateValues.put(Check.COLUMN_POSITION, position);

        db.update(
                Check.TABLE_NAME,
                updateValues,
                Check.COLUMN_Check_ID + " = ?",
                new String[]{ID}
        );

        db.close();
    }

    /**
     * This method will update the position of the given
     * row based on the ID
     * @param ID
     * @param checked
     */
    public void update(String ID,boolean checked){

        Log.d(TAG, "update: Updating the check status too " + checked);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues updateValues = new ContentValues();
        updateValues.put(Check.COLUMN_CHECKED, checked);
        db.update(
                Check.TABLE_NAME,
                updateValues,
                Check.COLUMN_Check_ID + " = ?",
                new String[]{ID}
        );

        db.close();
    }


    public void update(Check check){

        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues updateValues = new ContentValues();
        updateValues.put(Check.COLUMN_CHECKED, check.isChecked());

        db.update(
                Check.TABLE_NAME,
                updateValues,
                Check.COLUMN_Check_ID + " = ?",
                new String[]{check.getID()}
        );

        if (sCheckDataListener != null)
            sCheckDataListener.onDataUpdate(check);

        db.close();
    }

    /**
     *
     * This method will query from the database
     * if it doesn't exist it return false if does true
     * based on the {@code moveToFirst()}
     *
     * @param id Section ID to check against the table
     * @return Boolean if is true or false depending on if
     * the row exits. If it exists is true
     */
    public Boolean hasID(String id){
        SQLiteDatabase db = this.getReadableDatabase();

        //Get the data from sqlite
        Cursor cursor =  db.rawQuery( "SELECT * FROM " + Check.TABLE_NAME+ " where "+ Check.COLUMN_Check_ID +"='"+id+"'", null );

        return cursor.moveToFirst();
    }


}
