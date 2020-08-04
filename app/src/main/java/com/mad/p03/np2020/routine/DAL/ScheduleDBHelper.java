package com.mad.p03.np2020.routine.DAL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import com.mad.p03.np2020.routine.models.Schedule;
import com.mad.p03.np2020.routine.models.Task;
import com.mad.p03.np2020.routine.models.User;

import java.util.ArrayList;
import java.util.List;

public class ScheduleDBHelper extends DBHelper {

    private final String TAG = "ScheduleDatabase Helper";

    public ScheduleDBHelper(@Nullable Context context) {
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
     *
     * Function to insert schedule
     *
     * @param schedule to pass through a schedule object
     *
     * */
    public void insertSchedule(Schedule schedule)
    {
        Log.v(TAG, "Inserting Schedule");

        //Add values into the database
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Schedule.COLUMN_SCHEDULE_ID, schedule.getScheduleID());
        values.put(Schedule.COLUMN_UNIQUE, schedule.getUnique());

        db.insert(Schedule.TABLE_NAME, null, values);
        db.close();
    }

    /**
     * Function to delete schedule
     *
     * @param id for finding in db based on id
     *
     * */
    public void deleteSchedule(int id)
    {
        Log.v(TAG, "Deleting Schedule: ");

        //Add values into the database
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

    }

    public Schedule getSchedule(String ScheduleID)
    {
        Schedule schedule = null;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from " + Schedule.TABLE_NAME + " where " + Schedule.COLUMN_SCHEDULE_ID + "='" + ScheduleID+"';", null);

        if (cursor.moveToFirst()) {
            //Prepare a user object
            schedule = new Schedule(
                    cursor.getString(cursor.getColumnIndex(Schedule.COLUMN_SCHEDULE_ID)),
                    cursor.getInt(cursor.getColumnIndex(Schedule.COLUMN_UNIQUE))
            );
        }

        //Close the DB connection
        db.close();

        return schedule;
    }

    /**
     *
     * Function to get all schedule from sqlLite db
     *
     * */
    public List<Schedule> getAllSchedule()
    {
        List<Schedule> scheduleList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =  db.rawQuery( "select * from " + Schedule.TABLE_NAME, null );

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Schedule schedule = Schedule.fromCursor(cursor);

                Log.d(TAG, "getAllSchedule(): Reading data " + schedule.toString());

                scheduleList.add(schedule);
            } while (cursor.moveToNext());
        }

        db.close();
        return scheduleList;
    }
}
