package com.mad.p03.np2020.routine.DAL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import com.mad.p03.np2020.routine.models.Habit;
import com.mad.p03.np2020.routine.models.HabitRepetition;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * Model used to manage the habitRepetition
 *
 * @author Hou Man
 * @since 20-07-2020
 */

public class HabitRepetitionDBHelper extends DBHelper {

    private final String TAG = "HabitRepetitionDatabase";

    /**
     *
     * This method is a constructor of HabitDBHelper.
     *
     * @param context This parameter is to get the application context.
     * */
    public HabitRepetitionDBHelper(@Nullable Context context) {
        super(context);
    }

    /**
     *
     * This method is used to initialise the database.
     *
     * @param db This parameter is to get the SQLiteDatabase.
     * */
    @Override
    public void onCreate(SQLiteDatabase db) {
        super.onCreate(db);
    }

    /**
     *
     * This method is used to upgrade the database.
     *
     * @param db This parameter is to get the SQLiteDatabase.
     *
     * @param oldVersion This parameter is the old version.
     *
     * @param newVersion This parameter is the new version.
     * */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }

    /**
     *
     * This method is used to downgrade the database.
     *
     * @param db This parameter is to get the SQLiteDatabase.
     *
     * @param oldVersion This parameter is the old version.
     *
     * @param newVersion This parameter is the new version.
     * */
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    /**
     *
     * This method is used to insert the habitRepetition to the habitRepetition column in the SQLiteDatabase.
     *
     * @param habit This parameter is to get the habit object.
     *
     * @return long This will return the id for the habitRepetition after the habitRepetition is inserted to the habitRepetition column.
     * */
    public long insertHabitRepetition(Habit habit, int habitCount) {

        Log.d(TAG, "insertHabitRepetitions: "+ habit.getTitle());

        // insert the values
        ContentValues values = new ContentValues();
        values.put(HabitRepetition.COLUMN_HABIT_ID, habit.getHabitID());
        values.put(HabitRepetition.COLUMN_HABIT_TIMESTAMP, getTodayTimestamp());
        values.put(HabitRepetition.COLUMN_HABIT_COUNT, habitCount);
        values.put(HabitRepetition.COLUMN_HABIT_CONCOUNT, 0);
        switch (habit.getPeriod()){
            case 1:
                values.putNull(HabitRepetition.COLUMN_HABIT_CYCLE);
                values.put(HabitRepetition.COLUMN_HABIT_CYCLE_DAY, 1);
                break;

            case 7:

            case 30:
                values.put(HabitRepetition.COLUMN_HABIT_CYCLE, 1);
                values.put(HabitRepetition.COLUMN_HABIT_CYCLE_DAY, 1);
                break;

        }

        // get the writable database
        SQLiteDatabase db = this.getWritableDatabase();

        // insert the habit
        long id =  db.insert(HabitRepetition.TABLE_NAME, null, values);
        if (id == -1){ // if id is equal to 1, there is error inserting the habit
            Log.d(TAG, "Habit: insertHabitRepetitions: " + "Error");
        }else{ // if id is not equal to 1, there is no error inserting the habit
            Log.d(TAG, "Habit: insertHabitRepetitions: " + "Successful");

        }
        // close the database
        db.close();

        return id;
    }

    public long getTodayTimestamp(){
        Calendar cal = Calendar.getInstance();
        int year  = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int date  = cal.get(Calendar.DATE);
        cal.clear();
        cal.set(year, month, date);

        return cal.getTimeInMillis();
    }

}
