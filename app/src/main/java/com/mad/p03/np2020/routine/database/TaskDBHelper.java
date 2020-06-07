package com.mad.p03.np2020.routine.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mad.p03.np2020.routine.Class.Task;
import com.mad.p03.np2020.routine.Interface.MyDatabaseListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 *
 * The database that is used to create the Task table
 * in the database
 *
 * @author Jeyavishnu
 * @since 07-06-2020
 */
public class TaskDBHelper extends DBHelper {

    private final String TAG = "Task Database";

    //Listener
    private static MyDatabaseListener mMyDatabaseListener;

    public TaskDBHelper(@Nullable Context context) {
        super(context);
    }

    /**
     * Assign the listener implementing events interface
     * that will receive the events
     * @param myDatabaseListener setting the listener where the owner will listen to the message
     */
    public static void setMyDatabaseListener(MyDatabaseListener myDatabaseListener){
        mMyDatabaseListener = myDatabaseListener;
    }

    /**
     * Create the table for section
     * @param sqLiteDatabase The database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        super.onCreate(sqLiteDatabase);
    }

    /**
     *
     * Called when the database needs to be upgraded. This will drop the
     * database and create a new one. The data from the previous one will
     * move forward into the new db
     *
     * @param sqLiteDatabase The database.
     * @param i The old database version.
     * @param i1 The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        super.onUpgrade(sqLiteDatabase, i, i1);
    }

    /**
     *
     * If current version is newer than the requested one. This will drop the
     * database and create a new one.
     *
     * @param db The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    /**
     *
     * To insert the task
     * into the sqlite
     *
     * @param task to access the variables to be
     *             put into the database
     * @return the id in this case the row in belongs
     */
    public String insertTask(Task task){

        Log.d(TAG, "insertTask(): Preparing to insert the new user ");

        //Add values into the database
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Task.COLUMN_TASK_ID, task.getTaskID());
        values.put(Task.COLUMN_SECTION_ID, task.getSectionID());
        values.put(Task.COLUMN_NAME, task.getName());
        values.put(Task.COLUMN_CHECKED, task.isChecked());
        values.put(Task.COLUMN_REMIND_DATE, task.getRemindDate() != null ? task.getRemindDate().toString() : null);
        values.put(Task.COLUMN_DUE_DATE, task.getDueDate() != null ? task.getDueDate().toString() : null);
        values.put(Task.COLUMN_DUE_DATE, task.getNotes());

        // Insert the new row, returning the primary key value of the new row
        //If -1 means there is an error
        long id = db.insert(Task.TABLE_NAME, null, values);

        if(id == -1){
            Log.e(TAG, "insertTask(): There has been error inserting the data: ");
        } else{
            Log.d(TAG, "insertTask(): Data inserted");
        }

        if (mMyDatabaseListener != null)
            mMyDatabaseListener.onDataAdd(task);

        return String.valueOf(id);
    }

    public Task getTask(String id){
        SQLiteDatabase db = this.getReadableDatabase();

        Log.d(TAG, "getUser: Querying data");


        //Get the data from sqlite
        Cursor cursor =  db.rawQuery( "select * from " + Task.TABLE_NAME+ " where "+Task.COLUMN_TASK_ID+"='"+id+"'", null );

        if (cursor != null)
            cursor.moveToFirst(); //Only getting the first value

        //Prepare a section object
        assert cursor != null;
        Task task = Task.fromCursor(cursor);
        Log.d(TAG, "getAllSections(): Reading data" + task.toString() );


        //Close the DB connection
        db.close();

        return task;

    }


    /**
     *
     * This will get all the task from the database
     * based on the sectionID and store in to the object
     * and return it
     *
     * @param sectionID The ID that the task belongs to
     * @return List<Task> This will return a list of tasks
     */
    public List<Task> getAllTask(String sectionID){

        List<Task> taskList = new ArrayList<>();

        // Select All Query

        String selectQuery = "SELECT  * FROM " + Task.TABLE_NAME + " WHERE " + Task.COLUMN_SECTION_ID + "='" + sectionID +"' ORDER BY " +
                Task.COLUMN_POSITION + " ASC;";

        SQLiteDatabase db = this.getWritableDatabase();


        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Task task = Task.fromCursor(cursor);

                Log.d(TAG, "getAllTask(): Reading data " + task.toString());

                taskList.add(task);
            } while (cursor.moveToNext());
        }


        // close db connection
        db.close();

        Log.d(TAG, "getAllTask(): The number of task are " + taskList.size());
        
        // return notes list
        return taskList;
    }

    /**
     * This will delete the data from task table with
     * TaskID
     *
     * @param ID ID that will be used find the task and delete it
     */
    public void delete(String ID){

        Log.d(TAG, "delete(): Will be deleting ID " + ID );

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(
                Task.TABLE_NAME,  // The table to delete from
                Task.COLUMN_TASK_ID + " = ?", //The condition
                new String[]{ID} // The args will be replaced by ?
        );

        Log.d(TAG, "delete(): Removed from database");

        if (mMyDatabaseListener != null)
            mMyDatabaseListener.onDataDelete(ID);

        db.close();
    }


    /**
     *
     * This is to update the name and notes in the task with
     * ID from the first parameter
     *
     * @param ID The task with this ID that will be updated
     * @param name String The name you want to update too
     * @param notes String The notes you update
     */
    public void update(String ID, String name, String notes){

        SQLiteDatabase db = this.getWritableDatabase();

        Log.d(TAG, "update(): Name or notes will be updated");

        ContentValues updateValues = new ContentValues();
        if(name != null)
            updateValues.put(Task.COLUMN_NAME, name);
        if(notes != null)
            updateValues.put(Task.COLUMN_NOTES, notes);
        db.update(
                Task.TABLE_NAME,
                updateValues,
                Task.COLUMN_TASK_ID + " = ?",
                new String[]{ID}
        );

        db.close();
    }


    /**
     * This method will update the position of the given
     * row based on the ID
     * @param ID The task with this ID that will be updated
     * @param position The position that need to be updated
     */
    public void update(String ID, int position){

        Log.d(TAG, "update(): Updating position " + position);

        SQLiteDatabase db = this.getWritableDatabase();



        ContentValues updateValues = new ContentValues();
        updateValues.put(Task.COLUMN_POSITION, position);

        db.update(
                Task.TABLE_NAME,
                updateValues,
                Task.COLUMN_TASK_ID + " = ?",
                new String[]{ID}
        );

        db.close();
    }


    /**
     *
     * This to update the checked in SQL
     *
     * @param ID String The ID of the task you want to change
     * @param checked bool if you want the task to be checked
     */
    public void update(String ID,boolean checked){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues updateValues = new ContentValues();
        updateValues.put(Task.COLUMN_CHECKED, checked);
        db.update(
                Task.TABLE_NAME,
                updateValues,
                Task.COLUMN_TASK_ID + " = ?",
                new String[]{ID}
        );

        db.close();
    }

    /**
     *
     * This is to update the date of the ID
     * associated with the task in SQL
     *
     * @param ID String The ID of the task you want to change
     * @param date String The date you want to change to please
     *             put it in string with the date formatted
     */
    public void update(String ID, String date){

        Log.d(TAG, "update: Date updated: " + date);

        Log.d(TAG, "update: Updating using object task");

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues updateValues = new ContentValues();
        updateValues.put(Task.COLUMN_REMIND_DATE, String.valueOf(date));

        db.update(
                Task.TABLE_NAME,
                updateValues,
                Task.COLUMN_TASK_ID + " = ?",
                new String[]{ID}
        );

        db.close();
    }


    /**
     * This method will update the position of the given
     * row based on the ID will update the name, checked, notes
     * and remindDate
     * @param task The task you want to update
     */
    public void update(Task task){

        Log.d(TAG, "update: Updating using object task");
        
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues updateValues = new ContentValues();
        updateValues.put(Task.COLUMN_NAME, task.getName());
        updateValues.put(Task.COLUMN_CHECKED, task.isChecked());
        updateValues.put(Task.COLUMN_NOTES, task.getNotes());
        updateValues.put(Task.COLUMN_REMIND_DATE, task.getRemindDate());
        db.update(
                Task.TABLE_NAME,
                updateValues,
                Task.COLUMN_TASK_ID + " = ?",
                new String[]{task.getTaskID()}
        );

        if (mMyDatabaseListener != null)
            mMyDatabaseListener.onDataUpdate(task);

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
        Cursor cursor =  db.rawQuery( "select * from " + Task.TABLE_NAME+ " where "+ Task.COLUMN_TASK_ID +"='"+id+"'", null );

        return cursor.moveToFirst();
    }

}
