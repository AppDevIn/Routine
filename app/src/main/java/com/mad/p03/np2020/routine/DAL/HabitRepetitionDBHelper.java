package com.mad.p03.np2020.routine.DAL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import com.mad.p03.np2020.routine.models.Habit;
import com.mad.p03.np2020.routine.models.HabitRepetition;

import java.util.Calendar;

/**
 *
 * Model used to manage the habitRepetition
 *
 * @author Hou Man
 * @since 20-07-2020
 */

public class HabitRepetitionDBHelper extends DBHelper {

    private final String TAG = "HabitRepetitionDatabase";
    private HabitDBHelper habitDBHelper;

    /**
     *
     * This method is a constructor of HabitDBHelper.
     *
     * @param context This parameter is to get the application context.
     * */
    public HabitRepetitionDBHelper(@Nullable Context context) {
        super(context);
        habitDBHelper = new HabitDBHelper(context);
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

    public long getYesterdayTimestamp(){
        Calendar cal = Calendar.getInstance();
        int year  = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int date  = cal.get(Calendar.DATE);
        cal.clear();
        cal.set(year, month, date);
        cal.add(Calendar.DATE, -1);

        return cal.getTimeInMillis();
    }

    /**
     *
     * This method is used to update the count of the habit in the SQLiteDatabase.
     *
     * @param habit This parameter is to get the habit object.
     *
     * */
    public void updateCount(Habit habit){
        Log.d(TAG, "Habit: updateCount");

        // get the readable database
        SQLiteDatabase db = this.getReadableDatabase();
        // the query of updating the row
        String query =
                "UPDATE " + HabitRepetition.TABLE_NAME +
                        " SET " + HabitRepetition.COLUMN_HABIT_COUNT +"=" + habit.getCount() +
                        " WHERE " + HabitRepetition.COLUMN_HABIT_ID + "=" + habit.getHabitID() +
                        " AND " + HabitRepetition.COLUMN_HABIT_TIMESTAMP + "=" + getTodayTimestamp();

        db.execSQL(query); // execute the query
        db.close(); // close the db connection
    }

    public void repeatingHabit(){
        Log.d(TAG, "repeatingHabit: ");
        // get the readable database
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "select * from " + HabitRepetition.TABLE_NAME + " WHERE " + HabitRepetition.COLUMN_HABIT_TIMESTAMP + "=" + getYesterdayTimestamp() + " ORDER BY " + HabitRepetition.COLUMN_HABIT_ID + " ASC ";
        // run the query
        Cursor res =  db.rawQuery( query, null );
        res.moveToFirst(); // move to the first result found

        while(!res.isAfterLast()){
            long id = res.getLong(res.getColumnIndex(HabitRepetition.COLUMN_HABIT_ID));
            int cycle = res.getInt(res.getColumnIndex(HabitRepetition.COLUMN_HABIT_CYCLE));
            int day = res.getInt(res.getColumnIndex(HabitRepetition.COLUMN_HABIT_CYCLE_DAY));
            int count = res.getInt(res.getColumnIndex(HabitRepetition.COLUMN_HABIT_COUNT));
            int conCount = res.getInt(res.getColumnIndex(HabitRepetition.COLUMN_HABIT_CONCOUNT));
            Habit habit = habitDBHelper.getHabitByID(id);
            Log.d(TAG, "repeatingHabit: " + habit.getTitle());

            switch (habit.getPeriod()){
                case 1:
                    Log.d(TAG, "repeatingHabit: DAILY");
                    if (!checkTodayRepetition(id)){
                        insertNewRepetitionHabit(id, -1, ++day, 0);
                    }
                    break;

                case 7:
                    Log.d(TAG, "repeatingHabit: WEEKLY");
                    if (day == 7){
                        if (!checkTodayRepetition(id)){
                            insertNewRepetitionHabit(id, ++cycle, 1, 0);
                        }
                    }else{
                        if (!checkTodayRepetition(id)){
                            insertNewRepetitionHabit(id, cycle, ++day, conCount+count);
                        }
                    }
                    break;

                case 30:
                    Log.d(TAG, "repeatingHabit: MONTHLY");
                    if (day == 30){
                        if (!checkTodayRepetition(id)){
                            insertNewRepetitionHabit(id, ++cycle, 1, 0);
                        }
                    }
                    else{
                        if (!checkTodayRepetition(id)){
                            insertNewRepetitionHabit(id, cycle, ++day, conCount+count);
                        }
                    }
                    break;
            }

            res.moveToNext(); // move to the next result

        }
        db.close();
    }

    public void insertNewRepetitionHabit(long habitID, int cycle, int cycle_day, int conCount){
        // insert the values
        ContentValues values = new ContentValues();
        values.put(HabitRepetition.COLUMN_HABIT_ID, habitID);
        values.put(HabitRepetition.COLUMN_HABIT_TIMESTAMP, getTodayTimestamp());
        values.put(HabitRepetition.COLUMN_HABIT_COUNT, 0);
        values.put(HabitRepetition.COLUMN_HABIT_CONCOUNT, conCount);
        if (cycle == -1){
            values.putNull(HabitRepetition.COLUMN_HABIT_CYCLE);
        }else{
            values.put(HabitRepetition.COLUMN_HABIT_CYCLE, cycle);
        }

        values.put(HabitRepetition.COLUMN_HABIT_CYCLE_DAY, cycle_day);

        // get the writable database
        SQLiteDatabase db = this.getWritableDatabase();

        // insert the habit
        long id =  db.insert(HabitRepetition.TABLE_NAME, null, values);
        if (id == -1){ // if id is equal to 1, there is error inserting the habit
            Log.d(TAG, "Habit: insertNewRepetitionHabit: " + "Error");
        }else{ // if id is not equal to 1, there is no error inserting the habit
            Log.d(TAG, "Habit: insertNewRepetitionHabit: " + "Successful");

        }
        // close the database
        db.close();
    }

    public boolean checkTodayRepetition(long habitID){
        boolean isExisted = false;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =  db.rawQuery( "select * from " + HabitRepetition.TABLE_NAME + " WHERE " + HabitRepetition.COLUMN_HABIT_ID + " = " + habitID + " AND " + HabitRepetition.COLUMN_HABIT_TIMESTAMP + "=" + getTodayTimestamp(), null );
        if (cursor.getCount() > 0){
            isExisted = true;
        }

        db.close();
        Log.d(TAG, "checkTodayRepetition: " + isExisted);
        return isExisted;
    }

    public HabitRepetition getTodayHabitRepetitionByID(long id){
        HabitRepetition hr = new HabitRepetition();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + HabitRepetition.TABLE_NAME + " WHERE " + HabitRepetition.COLUMN_HABIT_ID + " = " + id + " AND " + HabitRepetition.COLUMN_HABIT_TIMESTAMP + "=" + getTodayTimestamp();
        Log.d(TAG, "getTodayHabitRepetitionByID: "+query);
        Cursor res =  db.rawQuery( query, null );
        if (res.getCount() > 0){
            res.moveToFirst();
            hr.setRow_id(res.getLong(res.getColumnIndex(HabitRepetition.COLUMN_ID)));
            hr.setHabitID(id);
            hr.setTimestamp(res.getLong(res.getColumnIndex(HabitRepetition.COLUMN_HABIT_TIMESTAMP)));
            hr.setCycle(res.getInt(res.getColumnIndex(HabitRepetition.COLUMN_HABIT_CYCLE)));
            hr.setCycle_day(res.getInt(res.getColumnIndex(HabitRepetition.COLUMN_HABIT_CYCLE_DAY)));
            hr.setCount(res.getInt(res.getColumnIndex(HabitRepetition.COLUMN_HABIT_COUNT)));
            hr.setConCount(res.getInt(res.getColumnIndex(HabitRepetition.COLUMN_HABIT_CONCOUNT)));
        }

        db.close();

        return hr;

    }

}
