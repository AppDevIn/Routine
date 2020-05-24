package com.mad.p03.np2020.routine.Class;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class LocalDatabase extends SQLiteOpenHelper {

    public static final String FOCUS_TABLE = "FOCUS_TABLE";
    public static final String COLUMN_TASK_NAME = "TASK_NAME";
    public static final String COLUMN_TASK_DATE = "TASK_DATE";
    public static final String COLUMN_TASK_DURATION = "TASK_DURATION";
    public static final String COLUMN_TASK_COMPLETE = "TASK_COMPLETE";
    public static final String COLUMN_TASK_ID = "ID";

    public LocalDatabase(@Nullable Context context) {
        super(context, "focus.db", null, 1);
    }

    // this is called the first time a database is accessed. Creation a new database will involve here
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + FOCUS_TABLE + " (" + COLUMN_TASK_ID + "  INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_TASK_NAME + " TEXT, " + COLUMN_TASK_DATE + " TEXT, " + COLUMN_TASK_DURATION + " TEXT, " + COLUMN_TASK_COMPLETE + " BOOL)";

        db.execSQL(createTableStatement);
    }

    //this is called if database version number changes. It prevents previous users apps from breaker when you change the database design
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addData(FocusHolder focus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TASK_NAME, focus.getmTask());
        cv.put(COLUMN_TASK_COMPLETE, focus.getmCompletion());
        cv.put(COLUMN_TASK_DURATION, focus.getmDuration());
        cv.put(COLUMN_TASK_DATE, focus.getmDateTime());

        long insert = db.insert(FOCUS_TABLE, null, cv); //if insert is -1 means fail
        if (insert == -1) return false;
        else return true;
    }

    //TODO Delete focus history record


    public ArrayList<FocusHolder> getAllData() {
        ArrayList<FocusHolder> returnList = new ArrayList<>();

        String queryString = "Select * FROM " + FOCUS_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                String taskName = cursor.getString(1);
                String taskDate = cursor.getString(2);
                String taskDuration = cursor.getString(3);
                String taskCompletion = cursor.getInt(4) == 1 ? "True" : "False";

                FocusHolder newFocus = new FocusHolder(taskDate, taskDuration, taskName, taskCompletion);
                returnList.add(newFocus);

            } while (cursor.moveToNext());
        } else {
            Log.v("SQL", "SQL Database Fail");
        }

        cursor.close();
        db.close();
        return returnList;
    }

}
