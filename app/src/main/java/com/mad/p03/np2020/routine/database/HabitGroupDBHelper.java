package com.mad.p03.np2020.routine.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import com.mad.p03.np2020.routine.Class.HabitGroup;

import java.util.ArrayList;

/**
 *
 * Model used to manage the section
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

}
