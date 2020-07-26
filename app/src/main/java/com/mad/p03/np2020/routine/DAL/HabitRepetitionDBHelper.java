package com.mad.p03.np2020.routine.DAL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.mad.p03.np2020.routine.background.HabitRepetitionWorker;
import com.mad.p03.np2020.routine.models.Habit;
import com.mad.p03.np2020.routine.models.HabitRepetition;

import java.util.ArrayList;
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
    private Context context;

    /**
     *
     * This method is a constructor of HabitDBHelper.
     *
     * @param context This parameter is to get the application context.
     * */
    public HabitRepetitionDBHelper(@Nullable Context context) {
        super(context);
        habitDBHelper = new HabitDBHelper(context);
        this.context = context;
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
        values.put(HabitRepetition.COLUMN_ID, getLastAssignedRowID());
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

        HabitRepetition hr = getTodayHabitRepetitionByID(habit.getHabitID());
        int count = habit.getCount() - hr.getConCount();
        // get the readable database
        SQLiteDatabase db = this.getReadableDatabase();
        // the query of updating the row
        String query =
                "UPDATE " + HabitRepetition.TABLE_NAME +
                        " SET " + HabitRepetition.COLUMN_HABIT_COUNT +"=" + count +
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

            boolean isUpdated = false;
            switch (habit.getPeriod()){
                case 1:
                    Log.d(TAG, "repeatingHabit: DAILY");
                    if (!checkTodayRepetition(id)){
                        insertNewRepetitionHabit(id, -1, ++day, 0);
                        isUpdated = true;
                    }
                    break;

                case 7:
                    Log.d(TAG, "repeatingHabit: WEEKLY");
                    if (day == 7){
                        if (!checkTodayRepetition(id)){
                            insertNewRepetitionHabit(id, ++cycle, 1, 0);
                            isUpdated = true;
                        }
                    }else{
                        if (!checkTodayRepetition(id)){
                            insertNewRepetitionHabit(id, cycle, ++day, conCount+count);
                            isUpdated = true;
                        }
                    }
                    break;

                case 30:
                    Log.d(TAG, "repeatingHabit: MONTHLY");
                    if (day == 30){
                        if (!checkTodayRepetition(id)){
                            insertNewRepetitionHabit(id, ++cycle, 1, 0);
                            isUpdated = true;
                        }
                    }
                    else{
                        if (!checkTodayRepetition(id)){
                            insertNewRepetitionHabit(id, cycle, ++day, conCount+count);
                            isUpdated = true;
                        }
                    }
                    break;
            }

            if (isUpdated){
                HabitRepetition habitRepetition = getTodayHabitRepetitionByID(habit.getHabitID());
                writeHabitRepetition_Firebase(habitRepetition, FirebaseAuth.getInstance().getCurrentUser().getUid());
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

    public void writeAllToFirebase(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + HabitRepetition.TABLE_NAME;

        Cursor res =  db.rawQuery( query, null );
        res.moveToFirst();
        while(!res.isAfterLast()){
            HabitRepetition hr = new HabitRepetition();
            hr.setRow_id(res.getLong(res.getColumnIndex(HabitRepetition.COLUMN_ID)));
            hr.setHabitID(res.getLong(res.getColumnIndex(HabitRepetition.COLUMN_HABIT_ID)));
            hr.setTimestamp(res.getLong(res.getColumnIndex(HabitRepetition.COLUMN_HABIT_TIMESTAMP)));
            hr.setCycle(res.getInt(res.getColumnIndex(HabitRepetition.COLUMN_HABIT_CYCLE)));
            hr.setCycle_day(res.getInt(res.getColumnIndex(HabitRepetition.COLUMN_HABIT_CYCLE_DAY)));
            hr.setCount(res.getInt(res.getColumnIndex(HabitRepetition.COLUMN_HABIT_COUNT)));
            hr.setConCount(res.getInt(res.getColumnIndex(HabitRepetition.COLUMN_HABIT_CONCOUNT)));

            writeHabitRepetition_Firebase(hr, FirebaseAuth.getInstance().getCurrentUser().getUid() );
            res.moveToNext();
        }

        db.close();


    }

    /**
     *
     * This method is used to send the work request
     *  to the HabitWorker(WorkManager) to do the writing firebase action
     *  when the network is connected.
     *
     * @param habitRepetition This parameter is used to get the habit object
     *
     * @param UID This parameter is used to get the userID
     *
     * */
    public void writeHabitRepetition_Firebase(HabitRepetition habitRepetition, String UID){
        Log.i(TAG, "Uploading to Firebase");

        // set constraint that the network must be connected
        Constraints myConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // put data in a data builder
        Data firebaseUserData = new Data.Builder()
                .putString("ID", UID)
                .putString("habitRepetition", habitRepetition_serializeToJson(habitRepetition))
                .build();

        // send a work request
        OneTimeWorkRequest mywork =
                new OneTimeWorkRequest.Builder(HabitRepetitionWorker.class)
                        .setConstraints(myConstraints)
                        .setInputData(firebaseUserData)
                        .build();

        WorkManager.getInstance(context).enqueue(mywork);
    }

    /**
     *
     * This method is used to serialize a single object. (into Json String)
     *
     * @param habitRepetition This parameter is used to get the habitRepetition object
     *
     * @return String This returns the serialized object.
     *
     * */
    public String habitRepetition_serializeToJson(HabitRepetition habitRepetition) {
        Gson gson = new Gson();
        Log.i(TAG,"Object serialize");
        return gson.toJson(habitRepetition);
    }

    /**This method is used to delete the all habit object in the SQLiteDatabase.* */
    public void deleteAllHabitRepetitions(){
        Log.d(TAG, "Habit: deleteAllHabitRepetitions: ");

        // get the writeable database
        SQLiteDatabase db = this.getWritableDatabase();

        // delete the habit table
        db.delete(HabitRepetition.TABLE_NAME,null,null);

        db.close(); //close the db connection
    }

    /**
     *
     * This method is used to insert the habit to the habit column in the SQLiteDatabase from firebase.
     *
     * @param hr This parameter is to get the habitRepetition object.
     *
     * @param UID This parameter is the get the UID to refer which habit column is going to be inserted.
     *
     * */
    public void insertHabitRepetitionFromFirebase(HabitRepetition hr, String UID) {

        Log.d(TAG, "insertHabitFromFirebase: "+UID);

        // insert the values
        ContentValues values = new ContentValues();
        values.put(HabitRepetition.COLUMN_ID, hr.getRow_id());
        values.put(HabitRepetition.COLUMN_HABIT_ID, hr.getHabitID());
        values.put(HabitRepetition.COLUMN_HABIT_TIMESTAMP, hr.getTimestamp());
        values.put(HabitRepetition.COLUMN_HABIT_COUNT, hr.getCount());
        values.put(HabitRepetition.COLUMN_HABIT_CONCOUNT, hr.getConCount());
        int cycle = hr.getCycle();
        if (cycle == 0){
            values.putNull(HabitRepetition.COLUMN_HABIT_CYCLE);
        }else{
            values.put(HabitRepetition.COLUMN_HABIT_CYCLE, cycle);
        }
        values.put(HabitRepetition.COLUMN_HABIT_CYCLE_DAY, hr.getCycle_day());

        // get the writable database
        SQLiteDatabase db = this.getWritableDatabase();

        // insert the habit
        long id =  db.insert(HabitRepetition.TABLE_NAME, null, values);
        if (id == -1){ // if id is equal to 1, there is error inserting the habit
            Log.d(TAG, "HabitRepetition: insertHabitRepetition: " + "Error");
        }else{ // if id is not equal to 1, there is no error inserting the habit
            Log.d(TAG, "HabitRepetition: insertHabitRepetition: " + "Successful");

        }
        // close the database
        db.close();

    }

    public long getLastAssignedRowID(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select max(_id) from " + HabitRepetition.TABLE_NAME , null );
        if (res.getCount() > 0){
            res.moveToFirst(); //Only getting the first value
            return res.getLong(res.getColumnIndex("max(_id)")) + 1;
        }else{
            return 1;
        }
    }

    /**
     *
     * This method is used to delete the habitRepetition object in the SQLiteDatabase.
     *
     * @param habit This parameter is to get the habit object.
     *
     * */
    public void deleteHabitRepetition(Habit habit){
        Log.d(TAG, "Habit: deleteHabit: ");

        // get the writeable database
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = HabitRepetition.COLUMN_HABIT_ID + "=?"; // specify to delete based on the column id

        // put the column id
        String[] whereArgs = new String[] { String.valueOf(habit.getHabitID()) };

        // delete the habit column
        db.delete(HabitRepetition.TABLE_NAME, whereClause, whereArgs);

        db.close(); // close the db connection
    }

    public ArrayList<Long> getAllHabitRepetitionsIDByHabitID(long habitID) {
        ArrayList<Long> arr = new ArrayList<>();

        // get the readable database
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "select * from " + HabitRepetition.TABLE_NAME + " WHERE " + HabitRepetition.COLUMN_HABIT_ID + " = " + habitID;
        Log.d(TAG, "getAllHabitRepetitionsIDByHabitID: "+query);
        // run the query
        Cursor res = db.rawQuery(query, null);

        if (res.getCount() > 0) {
            Log.d(TAG, "getAllHabitRepetitionsIDByHabitIDCount: "+res.getCount());
            res.moveToFirst(); // move to the first result found

            while (!res.isAfterLast()) {
                long id = res.getLong(res.getColumnIndex(HabitRepetition.COLUMN_ID));
                Log.d(TAG, "getAllHabitRepetitionsIDByHabitID: "+id);
                arr.add(id);
                res.moveToNext();
            }
        }

        db.close();
        return arr;
    }

    public ArrayList<HabitRepetition> getAllHabitRepetitionsByHabitID(long habitID) {
        ArrayList<HabitRepetition> arr = new ArrayList<>();

        // get the readable database
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "select * from " + HabitRepetition.TABLE_NAME + " WHERE " + HabitRepetition.COLUMN_HABIT_ID + " = " + habitID;
        // run the query
        Cursor res = db.rawQuery(query, null);

        if (res.getCount() > 0) {
            Log.d(TAG, "getAllHabitRepetitionsByHabitID: ");
            res.moveToFirst(); // move to the first result found

            while (!res.isAfterLast()) {
                HabitRepetition hr = new HabitRepetition();
                hr.setRow_id(res.getLong(res.getColumnIndex(HabitRepetition.COLUMN_ID)));
                hr.setHabitID(res.getLong(res.getColumnIndex(HabitRepetition.COLUMN_HABIT_ID)));
                hr.setCycle(res.getInt(res.getColumnIndex(HabitRepetition.COLUMN_HABIT_CYCLE)));
                hr.setCycle_day(res.getInt(res.getColumnIndex(HabitRepetition.COLUMN_HABIT_CYCLE_DAY)));
                hr.setTimestamp(res.getLong(res.getColumnIndex(HabitRepetition.COLUMN_HABIT_TIMESTAMP)));
                hr.setCount(res.getInt(res.getColumnIndex(HabitRepetition.COLUMN_HABIT_COUNT)));
                hr.setConCount(res.getInt(res.getColumnIndex(HabitRepetition.COLUMN_HABIT_CONCOUNT)));
                arr.add(hr);

                res.moveToNext();
            }
        }

        db.close();
        return arr;
    }

    public int getMaxCycle(long habitID){
        int cycle = 0;
        // get the readable database
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "select max(cycle) from " + HabitRepetition.TABLE_NAME + " WHERE " + HabitRepetition.COLUMN_HABIT_ID + " = " + habitID;
        // run the query
        Cursor res = db.rawQuery(query, null);

        if (res.getCount() > 0) {
            Log.d(TAG, "getAllHabitRepetitionsByHabitID: ");
            res.moveToFirst(); // move to the first result found

            cycle = res.getInt(res.getColumnIndex("max(cycle)"));

        }

        db.close();

        return cycle;
    }

    public int getMaxCountByCycle(long habitID, int cycle){
        int count = 0;
        // get the readable database
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "select max(count+conCount) from " + HabitRepetition.TABLE_NAME + " WHERE " + HabitRepetition.COLUMN_HABIT_ID + " = " + habitID + " AND " + HabitRepetition.COLUMN_HABIT_CYCLE + " = " + cycle;
        // run the query
        Cursor res = db.rawQuery(query, null);

        if (res.getCount() > 0) {
            Log.d(TAG, "getAllHabitRepetitionsByHabitID: ");
            res.moveToFirst(); // move to the first result found

            count = res.getInt(res.getColumnIndex("max(count+conCount)"));

        }

        db.close();

        return count;
    }

    public boolean isNextMonth(long habitID, long next_ms){
        boolean isNextMonth = false;
        // get the readable database
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "select count(*) from " + HabitRepetition.TABLE_NAME + " WHERE " + HabitRepetition.COLUMN_HABIT_ID + " = " + habitID + " AND " + HabitRepetition.COLUMN_HABIT_TIMESTAMP + " >= " + next_ms;
        // run the query
        Cursor res = db.rawQuery(query, null);
        if (res.getCount() > 0) {
            res.moveToFirst(); // move to the first result found

            int count = res.getInt(res.getColumnIndex("count(*)"));
            if (count > 0){
                isNextMonth = true;
            }
        }

        db.close();

        return isNextMonth;
    }

    public int getCountBetweenMonth(long habitID, long ms, long next_ms){
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "select sum(count) from " + HabitRepetition.TABLE_NAME + " WHERE " + HabitRepetition.COLUMN_HABIT_ID + " = " + habitID + " AND " + ms + " <= " + HabitRepetition.COLUMN_HABIT_TIMESTAMP + " < " + next_ms;
        // run the query
        Cursor res = db.rawQuery(query, null);

        if (res.getCount() > 0) {
            res.moveToFirst(); // move to the first result found
            count = res.getInt(res.getColumnIndex("sum(count)"));
        }

        db.close();

        return count;
    }
}
