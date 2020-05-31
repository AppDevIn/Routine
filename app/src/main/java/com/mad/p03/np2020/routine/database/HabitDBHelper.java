package com.mad.p03.np2020.routine.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.mad.p03.np2020.routine.Class.Habit;
import com.mad.p03.np2020.routine.Class.HabitGroup;
import com.mad.p03.np2020.routine.Class.HabitReminder;

public class HabitDBHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "MyRoutine.db";
    static final int DATABASE_VERSION = 2;
    private final String TAG = "HabitDatabase";


    public HabitDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Habit.CREATE_HABITS_TABLE);
        Log.d(TAG, "Habit Database is being created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Habit.DROP_HABITS_TABLE);
        onCreate(db);
        Log.d(TAG, "Habit Database is being upgraded");
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.setVersion(oldVersion);
    }

    public long insertHabit(Habit habit, String UID) {

        ContentValues values = new ContentValues();
        values.put(Habit.COLUMN_HABIT_TITLE,habit.getTitle());
        values.put(Habit.COLUMN_USERID,UID);
        Log.d(TAG, "insertHabit: "+UID);
        values.put(Habit.COLUMN_HABIT_OCCURRENCE,habit.getOccurrence());
        values.put(Habit.COLUMN_HABIT_COUNT,habit.getCount());
        values.put(Habit.COLUMN_HABIT_PERIOD,habit.getPeriod());
        values.put(Habit.COLUMN_HABIT_TIMECREATED,habit.getTime_created());
        values.put(Habit.COLUMN_HABIT_HOLDERCOLOR,habit.getHolder_color());

        HabitReminder reminder = habit.getHabitReminder();

        if(reminder !=  null){
            values.put(Habit.COLUMN_HABIT_REMINDER_ID,reminder.getId());
            values.put(Habit.COLUMN_HABIT_REMINDER_MESSAGES,reminder.getMessage());
            values.put(Habit.COLUMN_HABIT_REMINDER_MINUTES,reminder.getMinutes());
            values.put(Habit.COLUMN_HABIT_REMINDER_HOURS,reminder.getHours());
            values.put(Habit.COLUMN_HABIT_REMINDER_CUSTOMTEXT,reminder.getCustom_text());
        }else{
            values.putNull(Habit.COLUMN_HABIT_REMINDER_ID);
            values.putNull(Habit.COLUMN_HABIT_REMINDER_MESSAGES);
            values.putNull(Habit.COLUMN_HABIT_REMINDER_MINUTES);
            values.putNull(Habit.COLUMN_HABIT_REMINDER_HOURS);
            values.putNull(Habit.COLUMN_HABIT_REMINDER_CUSTOMTEXT);
        }

        HabitGroup group = habit.getGroup();
        if (group != null){
            values.put(Habit.COLUMN_HABIT_GROUP_NAME,habit.getGroup().getGrp_name());
        }else{
            values.putNull(Habit.COLUMN_HABIT_GROUP_NAME);
        }

        SQLiteDatabase db = this.getWritableDatabase();

        long id =  db.insert(Habit.TABLE_NAME, null, values);
        if (id == -1){
            Log.d(TAG, "Habit: insertHabit: " + "Error");
        }else{
            Log.d(TAG, "Habit: insertHabit: " + "Successful");

        }
        db.close();

        return id;
    }

    public Habit.HabitList getAllHabits(String UID) {
        Habit.HabitList habitList = new Habit.HabitList();

        SQLiteDatabase db = this.getReadableDatabase();
        Log.d(TAG, "getAllHabits: "+UID);
        Cursor res =  db.rawQuery( "select * from " +Habit.TABLE_NAME + " WHERE " + Habit.COLUMN_USERID + " =?", new String[]{UID} );
        res.moveToFirst();

        while(!res.isAfterLast()){
            long id = res.getLong(res.getColumnIndex(Habit.COLUMN_ID));
            String title = res.getString(res.getColumnIndex(Habit.COLUMN_HABIT_TITLE));
            int occurrence = res.getInt(res.getColumnIndex(Habit.COLUMN_HABIT_OCCURRENCE));
            int count = res.getInt(res.getColumnIndex(Habit.COLUMN_HABIT_COUNT));
            int period = res.getInt(res.getColumnIndex(Habit.COLUMN_HABIT_PERIOD));
            String time_created = res.getString(res.getColumnIndex(Habit.COLUMN_HABIT_TIMECREATED));
            String holder_color = res.getString(res.getColumnIndex(Habit.COLUMN_HABIT_HOLDERCOLOR));

            int reminder_id = res.getInt(res.getColumnIndex(Habit.COLUMN_HABIT_REMINDER_ID));
            int reminder_hours = res.getInt(res.getColumnIndex(Habit.COLUMN_HABIT_REMINDER_HOURS));
            int reminder_minutes = res.getInt(res.getColumnIndex(Habit.COLUMN_HABIT_REMINDER_MINUTES));
            String reminder_message = res.getString(res.getColumnIndex(Habit.COLUMN_HABIT_REMINDER_MESSAGES));
            String reminder_customText = res.getString(res.getColumnIndex(Habit.COLUMN_HABIT_REMINDER_CUSTOMTEXT));

            HabitReminder reminder = null;
            if (reminder_message != null){ //check if habit reminder is null, if not set the object
                reminder = new HabitReminder(reminder_message, reminder_id, reminder_minutes, reminder_hours, reminder_customText);
            }

            String group_name = res.getString(res.getColumnIndex(Habit.COLUMN_HABIT_GROUP_NAME));
            HabitGroup group = null;
            if (group_name != null) {// check if habit group is null, if not set the object
                group = new HabitGroup(group_name);
            }

            Habit habit = new Habit(id,title, occurrence, count, period, time_created, holder_color, reminder, group);
            habitList.addItem(habit);;
            res.moveToNext();
        }

        Log.d(TAG, "Habit: getAllHabits: ");
        return habitList;
    }

    public void updateCount(Habit habit){
        SQLiteDatabase db = this.getReadableDatabase();
        String query =
                "UPDATE " + Habit.TABLE_NAME +
                        " SET " + Habit.COLUMN_HABIT_COUNT +"=" + habit.getCount() +
                        " WHERE " + Habit.COLUMN_ID + "=" + habit.getHabitID();

        db.execSQL(query);
        db.close();

        Log.d(TAG, "Habit: updateCount");

    }

    public void updateHabit(Habit habit){
        SQLiteDatabase db = this.getReadableDatabase();

        String id_filter = Habit.COLUMN_ID + " = " +habit.getHabitID();
        ContentValues values = new ContentValues();
        values.put(Habit.COLUMN_HABIT_TITLE, habit.getTitle());
        values.put(Habit.COLUMN_HABIT_OCCURRENCE, habit.getOccurrence());
        values.put(Habit.COLUMN_HABIT_COUNT, habit.getCount());
        values.put(Habit.COLUMN_HABIT_PERIOD, habit.getPeriod());
        values.put(Habit.COLUMN_HABIT_HOLDERCOLOR, habit.getHolder_color());

        HabitReminder reminder = habit.getHabitReminder();
        if (reminder != null){
            values.put(Habit.COLUMN_HABIT_REMINDER_ID, reminder.getId());
            values.put(Habit.COLUMN_HABIT_REMINDER_MESSAGES, reminder.getMessage());
            values.put(Habit.COLUMN_HABIT_REMINDER_HOURS, reminder.getHours());
            values.put(Habit.COLUMN_HABIT_REMINDER_MINUTES, reminder.getMinutes());
            values.put(Habit.COLUMN_HABIT_REMINDER_CUSTOMTEXT, reminder.getCustom_text());
        }else{
            values.putNull(Habit.COLUMN_HABIT_REMINDER_ID);
            values.putNull(Habit.COLUMN_HABIT_REMINDER_MESSAGES);
            values.putNull(Habit.COLUMN_HABIT_REMINDER_HOURS);
            values.putNull(Habit.COLUMN_HABIT_REMINDER_MINUTES);
            values.putNull(Habit.COLUMN_HABIT_REMINDER_CUSTOMTEXT);
        }

        HabitGroup group = habit.getGroup();
        if (group != null){
            values.put(Habit.COLUMN_HABIT_GROUP_NAME, group.getGrp_name());
        }else{
            values.putNull(Habit.COLUMN_HABIT_GROUP_NAME);
        }

        db.update(Habit.TABLE_NAME, values, id_filter, null);
        db.close();
        Log.d(TAG, "Habit: updateHabit: ");
    }

    public void deleteHabit(Habit habit){
        SQLiteDatabase db = this.getReadableDatabase();

        String whereClause = Habit.COLUMN_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(habit.getHabitID()) };
        db.delete(Habit.TABLE_NAME, whereClause, whereArgs);
        
        db.close();
        Log.d(TAG, "Habit: deleteHabit: ");
    }
}
