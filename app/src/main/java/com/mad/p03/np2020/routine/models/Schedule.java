package com.mad.p03.np2020.routine.models;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.mad.p03.np2020.routine.DAL.DBHelper;

public class Schedule {

    private final String TAG = "Schedule Database";

    public static final String TABLE_NAME = "ScheduleDB";

    /**Used as the primary key for this table*/
    public static final String COLUMN_SCHEDULE_ID = "ScheduleID";
    /**Column for schedule uniqueID*/
    public static final String COLUMN_UNIQUE = "UniqueID";

    /**
     * The query needed to create a sql database
     * for the Task
     */
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_SCHEDULE_ID + " TEXT PRIMARY KEY, "
                    + COLUMN_UNIQUE + " INTEGER )";

    /**
     * The query needed to delete SQL table task from the database
     */
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    private String ScheduleID;
    private int Unique;

    public Schedule(String scheduleId, int unique)
    {
        this.ScheduleID = scheduleId;
        this.Unique = unique;
    }

    /**
     *
     * This is to convert the data received from SQL and
     * convert it into a object
     *
     * @param cursor The query that has been given buy database
     * @return Task Return back a task object
     */
    public static Schedule fromCursor(Cursor cursor)
    {
        return new Schedule(
                cursor.getString(cursor.getColumnIndex(COLUMN_SCHEDULE_ID)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_UNIQUE))
        );
    }

    public String getScheduleID()
    {
        return ScheduleID;
    }

    public int getUnique()
    {
        return Unique;
    }


}

