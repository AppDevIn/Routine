package com.mad.p03.np2020.routine.DAL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import com.mad.p03.np2020.routine.models.Habit;
import com.mad.p03.np2020.routine.models.HabitGroup;
import com.mad.p03.np2020.routine.models.HabitRepetition;

import java.util.ArrayList;

/**
 *
 * This created to handle the habitGroup Data in SQLiteDatabase
 *
 * @author Hou Man
 * @since 02-06-2020
 */

public class HabitGroupDBHelper extends DBHelper {

    private final String TAG = "HabitGroupDatabase";

    /**
     *
     * This method is a constructor of HabitGroupDBHelper.
     *
     * @param context This parameter is to get the application context.
     * */
    public HabitGroupDBHelper(@Nullable Context context) {
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
     * This method is used to retrieve all the habitGroups in the SQLiteDatabase.
     *
     * @return ArrayList<HabitGroup> This will return the habitGroupList.
     * */
    public ArrayList<HabitGroup> getAllGroups(){
        Log.d(TAG, "getAllGroups: ");

        // get the readable database
        SQLiteDatabase db = this.getReadableDatabase();

        // initialise the habitGroups list
        ArrayList<HabitGroup> habitGroups = new ArrayList<>();

        // run the query
        Cursor res =  db.rawQuery( "select * from " + HabitGroup.TABLE_NAME, null );
        res.moveToFirst(); // move to the first result found

        // loop through the result found
        while(!res.isAfterLast()) {
            long id = res.getLong(res.getColumnIndex(HabitGroup.COLUMN_ID));
            String name = res.getString(res.getColumnIndex(HabitGroup.COLUMN_GROUP_NAME));

            // add the group to the list
            habitGroups.add(new HabitGroup(id, name));
            res.moveToNext(); // move to the next result found
        }

        db.close(); // close the db connection

        return habitGroups;
    }

    /**
     *
     * This method is used to insert the group to the group column in the SQLiteDatabase.
     *
     * @return long This will return the id for the habitGroup after the habitGroup is inserted to the habitGroup column.
     * */
    public long insertGroup(HabitGroup habitGroup){
        Log.d(TAG, "insertGroup: " + habitGroup.getGrp_name());

        // put the values
        ContentValues values = new ContentValues();
        values.put(HabitGroup.COLUMN_GROUP_NAME, habitGroup.getGrp_name());

        // get the writeable database
        SQLiteDatabase db = this.getWritableDatabase();

        // insert the habitGroup
        long id =  db.insert(HabitGroup.TABLE_NAME, null, values);
        if (id == -1){ // if id is equal to 1, there is error inserting the habitGroup
            Log.d(TAG, "insertGroup: " + "Error");
        }else{ // if id is not equal to 1, there is no error inserting the habitGroup
            Log.d(TAG, "insertGroup: " + "Successful");
        }

        db.close(); // close the db connection

        return id;
    }

    /**This method is used to delete the all habitGroups object in the SQLiteDatabase.* */
    public void deleteAllHabitGroups(){
        Log.d(TAG, "Habit: deleteAllHabit: ");

        // get the writeable database
        SQLiteDatabase db = this.getWritableDatabase();

        // delete the habit table
        db.delete(HabitGroup.TABLE_NAME,null,null);

        db.close(); //close the db connection
    }

    /**
     *
     * This method is used to insert the group to the group column in the SQLiteDatabase from firebase.
     *
     * @return long This will return the id for the habitGroup after the habitGroup is inserted to the habitGroup column.
     * */
    public void insertGroupFromFirebase(HabitGroup habitGroup){
        Log.d(TAG, "insertGroup: " + habitGroup.getGrp_name());

        // put the values
        ContentValues values = new ContentValues();
        values.put(HabitGroup.COLUMN_ID, habitGroup.getGrp_id());
        values.put(HabitGroup.COLUMN_GROUP_NAME, habitGroup.getGrp_name());

        // get the writeable database
        SQLiteDatabase db = this.getWritableDatabase();

        // insert the habitGroup
        long id =  db.insert(HabitGroup.TABLE_NAME, null, values);
        if (id == -1){ // if id is equal to 1, there is error inserting the habitGroup
            Log.d(TAG, "insertGroup: " + "Error");
        }else{ // if id is not equal to 1, there is no error inserting the habitGroup
            Log.d(TAG, "insertGroup: " + "Successful");
        }

        db.close(); // close the db connection

    }

    public boolean isHabitGroupExisted(long rowID){
        boolean isExisted = false;

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "select * from " + HabitGroup.TABLE_NAME + " WHERE " + HabitGroup.COLUMN_ID + " = " + rowID;

        Cursor res =  db.rawQuery( query  , null );
        if (res.getCount() > 0){
            isExisted = true;
        }

        db.close();

        return isExisted;
    }

    public HabitGroup getHabitGroupByRowID(long id){
        HabitGroup hg = new HabitGroup();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + HabitGroup.TABLE_NAME + " WHERE " + HabitGroup.COLUMN_ID + " = " + id;
        Cursor res =  db.rawQuery( query, null );
        if (res.getCount() > 0){
            res.moveToFirst();
            hg.setGrp_id(res.getLong(res.getColumnIndex(HabitGroup.COLUMN_ID)));
            hg.setGrp_name(res.getString(res.getColumnIndex(HabitGroup.COLUMN_GROUP_NAME)));
        }

        db.close();

        return hg;
    }

    public void update(HabitGroup hg){
        String id_filter = HabitGroup.COLUMN_ID + " = " + hg.getGrp_id();

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //update row id
        values.put(HabitGroup.COLUMN_ID, hg.getGrp_id());
        values.put(HabitGroup.COLUMN_GROUP_NAME, hg.getGrp_name());

        // update the habit column
        db.update(HabitGroup.TABLE_NAME, values, id_filter, null);
        db.close(); // close the db connection
    }

    public void removeOneData(HabitGroup hg) {

        // Find database that match the row data. If it found, delete and return true

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(
                HabitGroup.TABLE_NAME,  // The table to delete from
                HabitGroup.COLUMN_ID + " = ?", //The condition
                new String[]{String.valueOf(hg.getGrp_id())} // The args will be replaced by ?
        );

        Log.d(TAG, "removeOneData: "+hg.getGrp_id());
        db.close();

    }

}
