package com.mad.p03.np2020.routine.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mad.p03.np2020.routine.Class.Section;
import com.mad.p03.np2020.routine.Class.Task;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class TaskDBHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "MyRoutine.db";
    static final int DATABASE_VERSION = 5;
    private final String TAG = "Task Database";

    public TaskDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "Task database is being created");

        //Create user database
        sqLiteDatabase.execSQL(Task.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.d(TAG, "Task database is being upgraded");

        sqLiteDatabase.execSQL(Task.SQL_DELETE_ENTRIES); // Delete existing task
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Task database is downgraded");

        onUpgrade(db,oldVersion,newVersion);
    }


    public String insertTask(Task task, String sectionID){

        Log.d(TAG, "insertUser(): Preparing to insert the new user ");

        //Add values into the database
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Task.COLUMN_TASK_ID, task.getTaskID());
        values.put(Task.COLUMN_SECTION_ID, sectionID);
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


        return String.valueOf(id);
    }

    public List<Task> getAllTask(String sectionID){

        List<Task> taskList = new ArrayList<>();

        // Select All Query

        String selectQuery = "SELECT  * FROM " + Task.TABLE_NAME + " WHERE " + Task.COLUMN_SECTION_ID + "='" + sectionID +"' ORDER BY " +
                Task.COLUMN_TASK_ID + " DESC;";

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

}
