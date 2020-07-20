package com.mad.p03.np2020.routine.DAL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import com.mad.p03.np2020.routine.models.Habit;
import com.mad.p03.np2020.routine.models.HabitGroup;
import com.mad.p03.np2020.routine.models.HabitReminder;
import com.mad.p03.np2020.routine.models.HabitRepetition;

import java.util.Calendar;

/**
 *
 * This created to handle the habit Data in SQLiteDatabase
 *
 * @author Hou Man
 * @since 02-06-2020
 */

public class HabitDBHelper extends DBHelper{

    private final String TAG = "HabitDatabase";

    /**
     *
     * This method is a constructor of HabitDBHelper.
     *
     * @param context This parameter is to get the application context.
     * */
    public HabitDBHelper(@Nullable Context context) {
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
     * This method is used to insert the habit to the habit column in the SQLiteDatabase.
     *
     * @param habit This parameter is to get the habit object.
     *
     * @param UID This parameter is the get the UID to refer which habit column is going to be inserted.
     *
     * @return long This will return the id for the habit after the habit is inserted to the habit column.
     * */
    public long insertHabit(Habit habit, String UID) {

        Log.d(TAG, "insertHabit: "+UID);

        // insert the values
        ContentValues values = new ContentValues();
        values.put(Habit.COLUMN_HABIT_TITLE,habit.getTitle());
        values.put(Habit.COLUMN_USERID,UID);
        values.put(Habit.COLUMN_HABIT_OCCURRENCE,habit.getOccurrence());
        values.put(Habit.COLUMN_HABIT_PERIOD,habit.getPeriod());
        values.put(Habit.COLUMN_HABIT_TIMECREATED,habit.getTime_created());
        values.put(Habit.COLUMN_HABIT_HOLDERCOLOR,habit.getHolder_color());

        HabitReminder reminder = habit.getHabitReminder();

        if(reminder !=  null){ // if reminder object is not null
            values.put(Habit.COLUMN_HABIT_REMINDER_ID,reminder.getId());
            values.put(Habit.COLUMN_HABIT_REMINDER_MESSAGES,reminder.getMessage());
            values.put(Habit.COLUMN_HABIT_REMINDER_MINUTES,reminder.getMinutes());
            values.put(Habit.COLUMN_HABIT_REMINDER_HOURS,reminder.getHours());
            values.put(Habit.COLUMN_HABIT_REMINDER_CUSTOMTEXT,reminder.getCustom_text());
        }else{ // if reminder object is null, set the null value for the reminder rows
            values.putNull(Habit.COLUMN_HABIT_REMINDER_ID);
            values.putNull(Habit.COLUMN_HABIT_REMINDER_MESSAGES);
            values.putNull(Habit.COLUMN_HABIT_REMINDER_MINUTES);
            values.putNull(Habit.COLUMN_HABIT_REMINDER_HOURS);
            values.putNull(Habit.COLUMN_HABIT_REMINDER_CUSTOMTEXT);
        }

        HabitGroup group = habit.getGroup();
        if (group != null){ // if group object is not null
            values.put(Habit.COLUMN_HABIT_GROUP_ID, group.getGrp_id());
            values.put(Habit.COLUMN_HABIT_GROUP_NAME, group.getGrp_name());
        }else{  // if group object is null, set the null value for the group row
            values.putNull(Habit.COLUMN_HABIT_GROUP_ID);
            values.putNull(Habit.COLUMN_HABIT_GROUP_NAME);
        }

        // get the writable database
        SQLiteDatabase db = this.getWritableDatabase();

        // insert the habit
        long id =  db.insert(Habit.TABLE_NAME, null, values);
        if (id == -1){ // if id is equal to 1, there is error inserting the habit
            Log.d(TAG, "Habit: insertHabit: " + "Error");
        }else{ // if id is not equal to 1, there is no error inserting the habit
            Log.d(TAG, "Habit: insertHabit: " + "Successful");

        }
        // close the database
        db.close();

        return id;
    }

    /**
     *
     * This method is used to retrieve all the habits based on the UID in the SQLiteDatabase.
     *
     * @return ArrayList<Habit> This will return the habitList.
     * */
    public Habit.HabitList getAllHabits() {
        Log.d(TAG, "getAllHabits: ");

        // initialise the habitList
        Habit.HabitList habitList = new Habit.HabitList();

        // get the readable database
        SQLiteDatabase db = this.getReadableDatabase();

        // run the query
        Cursor res =  db.rawQuery( "select * from " + Habit.TABLE_NAME, null );
        res.moveToFirst(); // move to the first result found

        // loop through the result found
        while(!res.isAfterLast()){
            long id = res.getLong(res.getColumnIndex(Habit.COLUMN_ID));
            String title = res.getString(res.getColumnIndex(Habit.COLUMN_HABIT_TITLE));
            int occurrence = res.getInt(res.getColumnIndex(Habit.COLUMN_HABIT_OCCURRENCE));
            int count = getHabitCount(id);
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

            long group_id = res.getLong(res.getColumnIndex(Habit.COLUMN_HABIT_GROUP_ID));
            String group_name = res.getString(res.getColumnIndex(Habit.COLUMN_HABIT_GROUP_NAME));
            HabitGroup group = null;
            if (group_name != null) {// check if habit group is null, if not set the object
                group = new HabitGroup(group_id, group_name);
            }

            // add the habit object
            Habit habit = new Habit(id,title, occurrence, count, period, time_created, holder_color, reminder, group);
            habitList.addItem(habit);
            res.moveToNext(); // move to the next result
        }
        db.close();

        return habitList;
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
                "UPDATE " + Habit.TABLE_NAME +
                        " SET " + Habit.COLUMN_HABIT_COUNT +"=" + habit.getCount() +
                        " WHERE " + Habit.COLUMN_ID + "=" + habit.getHabitID();

        db.execSQL(query); // execute the query
        db.close(); // close the db connection
    }

    /**
     *
     * This method is used to update the habit object in the SQLiteDatabase.
     *
     * @param habit This parameter is to get the habit object.
     *
     * */
    public void updateHabit(Habit habit){
        Log.d(TAG, "Habit: updateHabit: ");

        // get the readable database
        SQLiteDatabase db = this.getReadableDatabase();

        String id_filter = Habit.COLUMN_ID + " = " +habit.getHabitID();

        // put the values
        ContentValues values = new ContentValues();
        values.put(Habit.COLUMN_HABIT_TITLE, habit.getTitle());
        values.put(Habit.COLUMN_HABIT_OCCURRENCE, habit.getOccurrence());
        values.put(Habit.COLUMN_HABIT_COUNT, habit.getCount());
        values.put(Habit.COLUMN_HABIT_PERIOD, habit.getPeriod());
        values.put(Habit.COLUMN_HABIT_HOLDERCOLOR, habit.getHolder_color());

        HabitReminder reminder = habit.getHabitReminder();
        if (reminder != null){ // put the values if reminder object is not null
            values.put(Habit.COLUMN_HABIT_REMINDER_ID, reminder.getId());
            values.put(Habit.COLUMN_HABIT_REMINDER_MESSAGES, reminder.getMessage());
            values.put(Habit.COLUMN_HABIT_REMINDER_HOURS, reminder.getHours());
            values.put(Habit.COLUMN_HABIT_REMINDER_MINUTES, reminder.getMinutes());
            values.put(Habit.COLUMN_HABIT_REMINDER_CUSTOMTEXT, reminder.getCustom_text());
        }else{ // put the values null if reminder object is null
            values.putNull(Habit.COLUMN_HABIT_REMINDER_ID);
            values.putNull(Habit.COLUMN_HABIT_REMINDER_MESSAGES);
            values.putNull(Habit.COLUMN_HABIT_REMINDER_HOURS);
            values.putNull(Habit.COLUMN_HABIT_REMINDER_MINUTES);
            values.putNull(Habit.COLUMN_HABIT_REMINDER_CUSTOMTEXT);
        }

        HabitGroup group = habit.getGroup();
        if (group != null){ // put the values if group object is not null
            values.put(Habit.COLUMN_HABIT_GROUP_NAME, group.getGrp_name());
        }else{ // put the values null if group object is not null
            values.putNull(Habit.COLUMN_HABIT_GROUP_NAME);
        }

        // update the habit column
        db.update(Habit.TABLE_NAME, values, id_filter, null);
        db.close(); // close the db connection
    }

    /**
     *
     * This method is used to delete the habit object in the SQLiteDatabase.
     *
     * @param habit This parameter is to get the habit object.
     *
     * */
    public void deleteHabit(Habit habit){
        Log.d(TAG, "Habit: deleteHabit: ");

        // get the writeable database
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = Habit.COLUMN_ID + "=?"; // specify to delete based on the column id

        // put the column id
        String[] whereArgs = new String[] { String.valueOf(habit.getHabitID()) };

        // delete the habit column
        db.delete(Habit.TABLE_NAME, whereClause, whereArgs);
        
        db.close(); // close the db connection
    }

    /**This method is used to delete the all habit object in the SQLiteDatabase.* */
    public void deleteAllHabit(){
        Log.d(TAG, "Habit: deleteAllHabit: ");

        // get the writeable database
        SQLiteDatabase db = this.getWritableDatabase();

        // delete the habit table
        db.delete(Habit.TABLE_NAME,null,null);

        db.close(); //close the db connection
    }

    /**
     *
     * This method is used to check whether a specific reminder is existed in the SQLiteDatabase.
     *
     * @param habit This parameter is to get the habit object.
     *
     * @return boolean This will return true if the reminder is existed and false if it is not.
     * */
    public boolean isReminderExisted(Habit habit){

        SQLiteDatabase db = this.getReadableDatabase();

        Log.d(TAG, "isReminderExisted: ");

        Cursor cursor =  db.rawQuery( "select * from " + Habit.TABLE_NAME + " WHERE " + Habit.COLUMN_ID + " = " + habit.getHabitID(), null );
        if (cursor == null){
            return false;
        }

        cursor.moveToFirst(); //Only getting the first value

        if (cursor.getString(cursor.getColumnIndex(Habit.COLUMN_HABIT_REMINDER_MESSAGES)) != null ){
            return true;
        }

        db.close();

        return false;

    }

    /**
     *
     * This method is used to get a specific reminder existed in the SQLiteDatabase.
     *
     * @param habit This parameter is to get the habit object.
     *
     * @return boolean This will return the habitReminder object, and return null if no result found.
     * */
    public HabitReminder getReminder(Habit habit){
        Log.d(TAG, "getReminder: ");

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select * from " + Habit.TABLE_NAME + " WHERE " + Habit.COLUMN_ID + " = " + habit.getHabitID(), null );
        if (res != null){
            res.moveToFirst(); //Only getting the first value
        }

        int reminder_id = res.getInt(res.getColumnIndex(Habit.COLUMN_HABIT_REMINDER_ID));
        int reminder_hours = res.getInt(res.getColumnIndex(Habit.COLUMN_HABIT_REMINDER_HOURS));
        int reminder_minutes = res.getInt(res.getColumnIndex(Habit.COLUMN_HABIT_REMINDER_MINUTES));
        String reminder_message = res.getString(res.getColumnIndex(Habit.COLUMN_HABIT_REMINDER_MESSAGES));
        String reminder_customText = res.getString(res.getColumnIndex(Habit.COLUMN_HABIT_REMINDER_CUSTOMTEXT));

        HabitReminder reminder = null;
        if (reminder_message != null){ //check if habit reminder is null, if not set the object
            reminder = new HabitReminder(reminder_message, reminder_id, reminder_minutes, reminder_hours, reminder_customText);
        }

        db.close();

        return reminder;

    }

    /**
     *
     * This method is used to get a specific reminder existed in the SQLiteDatabase.
     *
     * @param habit This parameter is to get the habit object.
     *
     * @return boolean This will return the habitReminder object, and return null if no result found.
     * */
    public Habit getHabit(Habit habit){
        Log.d(TAG, "getHabit: ");

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select * from " + Habit.TABLE_NAME + " WHERE " + Habit.COLUMN_ID + " = " + habit.getHabitID(), null );
        if (res != null){
            res.moveToFirst(); //Only getting the first value
        }

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

        long group_id = res.getLong(res.getColumnIndex(Habit.COLUMN_HABIT_GROUP_ID));
        String group_name = res.getString(res.getColumnIndex(Habit.COLUMN_HABIT_GROUP_NAME));
        HabitGroup group = null;
        if (group_name != null) {// check if habit group is null, if not set the object
            group = new HabitGroup(group_id, group_name);
        }

        db.close();

        return new Habit(id,title, occurrence, count, period, time_created, holder_color, reminder, group);
    }

    /**
     *
     * This method is used to insert the habit to the habit column in the SQLiteDatabase from firebase.
     *
     * @param habit This parameter is to get the habit object.
     *
     * @param UID This parameter is the get the UID to refer which habit column is going to be inserted.
     *
     * */
    public void insertHabitFromFirebase(Habit habit, String UID) {

        Log.d(TAG, "insertHabitFromFirebase: "+UID);

        // insert the values
        ContentValues values = new ContentValues();
        values.put(Habit.COLUMN_ID, habit.getHabitID());
        values.put(Habit.COLUMN_HABIT_TITLE,habit.getTitle());
        values.put(Habit.COLUMN_USERID,UID);
        values.put(Habit.COLUMN_HABIT_OCCURRENCE,habit.getOccurrence());
        values.put(Habit.COLUMN_HABIT_COUNT,habit.getCount());
        values.put(Habit.COLUMN_HABIT_PERIOD,habit.getPeriod());
        values.put(Habit.COLUMN_HABIT_TIMECREATED,habit.getTime_created());
        values.put(Habit.COLUMN_HABIT_HOLDERCOLOR,habit.getHolder_color());

        HabitReminder reminder = habit.getHabitReminder();

        if(reminder !=  null){ // if reminder object is not null
            values.put(Habit.COLUMN_HABIT_REMINDER_ID,reminder.getId());
            values.put(Habit.COLUMN_HABIT_REMINDER_MESSAGES,reminder.getMessage());
            values.put(Habit.COLUMN_HABIT_REMINDER_MINUTES,reminder.getMinutes());
            values.put(Habit.COLUMN_HABIT_REMINDER_HOURS,reminder.getHours());
            values.put(Habit.COLUMN_HABIT_REMINDER_CUSTOMTEXT,reminder.getCustom_text());
        }else{ // if reminder object is null, set the null value for the reminder rows
            values.putNull(Habit.COLUMN_HABIT_REMINDER_ID);
            values.putNull(Habit.COLUMN_HABIT_REMINDER_MESSAGES);
            values.putNull(Habit.COLUMN_HABIT_REMINDER_MINUTES);
            values.putNull(Habit.COLUMN_HABIT_REMINDER_HOURS);
            values.putNull(Habit.COLUMN_HABIT_REMINDER_CUSTOMTEXT);
        }

        HabitGroup group = habit.getGroup();
        if (group != null){ // if group object is not null
            values.put(Habit.COLUMN_HABIT_GROUP_ID, group.getGrp_id());
            values.put(Habit.COLUMN_HABIT_GROUP_NAME, group.getGrp_name());
        }else{  // if group object is null, set the null value for the group row
            values.putNull(Habit.COLUMN_HABIT_GROUP_ID);
            values.putNull(Habit.COLUMN_HABIT_GROUP_NAME);
        }

        // get the writable database
        SQLiteDatabase db = this.getWritableDatabase();

        // insert the habit
        long id =  db.insert(Habit.TABLE_NAME, null, values);
        if (id == -1){ // if id is equal to 1, there is error inserting the habit
            Log.d(TAG, "Habit: insertHabit: " + "Error");
        }else{ // if id is not equal to 1, there is no error inserting the habit
            Log.d(TAG, "Habit: insertHabit: " + "Successful");

        }
        // close the database
        db.close();

    }

    public int getHabitCount(long habitID){
        Log.d(TAG, "getHabitRepetition: " + habitID);

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "select * from " + HabitRepetition.TABLE_NAME + " WHERE " + HabitRepetition.COLUMN_HABIT_ID + " = " + habitID + " AND " + HabitRepetition.COLUMN_HABIT_TIMESTAMP + " = " + getTodayTimestamp();

        Cursor res =  db.rawQuery( query  , null );
        if (res != null){
            res.moveToFirst(); //Only getting the first value
        }

        int count = res.getInt(res.getColumnIndex(HabitRepetition.COLUMN_HABIT_COUNT));

        return count;

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
