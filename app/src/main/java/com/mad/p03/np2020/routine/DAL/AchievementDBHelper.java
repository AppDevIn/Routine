package com.mad.p03.np2020.routine.DAL;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.Nullable;

import com.mad.p03.np2020.routine.models.Achievement;
import com.mad.p03.np2020.routine.models.Section;
import com.mad.p03.np2020.routine.models.Task;

import java.util.ArrayList;


/**
 * This created to handle the Focus Data in SQLiteDatabase
 *
 * @author Lee Quan Sheng
 * @since 22-07-2020
 */

public class AchievementDBHelper extends DBHelper  implements Parcelable {

    private final String TAG = "Achievement Database";


    public AchievementDBHelper(@Nullable Context context) {
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

    public String insertAchievements(Achievement achievement) {

        Log.d(TAG, "Hour tasks Adding");

        //Add values into the database
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Achievement.COLUMN_TASK_ID, achievement.getStageNo());
        values.put(Achievement.COLUMN_HOURS, achievement.getRequirement());
        values.put(Achievement.COLUMN_FILENAME, achievement.getFilename());
        values.put(Achievement.COLUMN_TYPE, achievement.getTypeAchievement()); //Type of achievements
        values.put(Achievement.COLUMN_URL, achievement.getBadgeUrl());


        // Insert the new row, returning the primary key value of the new row
        //If -1 means there is an error
        long id = db.insert(Achievement.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(TAG, "Error insert data to SQL Database");
        } else {
            Log.d(TAG, "Data inserted to database");
        }


        return String.valueOf(id);
    }

    /**
     * This is called to get all Row data to existing Database
     *
     * @return ArrayList of hour achievement that has retrieve from the local database
     */
    public ArrayList<Achievement> getAchievementData() {

        //This is called to get all Data existing in the firebase database
        ArrayList<Achievement> returnList = new ArrayList<>();

        String queryString = "Select * FROM " + Achievement.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                String stageNo = cursor.getString(0); //fbId
                int requirement = cursor.getInt(1);
                String fileName = cursor.getString(2);
                int type = cursor.getInt(3);
                String badgeUrl = cursor.getString(4);

                Achievement newFocus = new Achievement(stageNo, requirement, fileName, type,badgeUrl);
                returnList.add(newFocus);

            } while (cursor.moveToNext());
        } else {
            Log.v("SQL", "SQL Database Fail");
        }

        cursor.close();
        db.close();

        Log.v("SQL", "Focus Data has been initialized completed with " + returnList.size());

        return returnList;
    }

    /**
     * This is called to Delete all Data to existing Database
     */
    public void deleteAllMain() {
        //This is called to destroy SQLite Database
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Achievement.TABLE_NAME, null, null);
        db.close();
    }

    public Boolean Exist(String id, int typeAch) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + Achievement.TABLE_NAME + " where " + Achievement.COLUMN_TASK_ID + "='" + id + "'" + " AND " + Achievement.COLUMN_TYPE + "='" + typeAch + "'", null);

        return cursor.moveToFirst();
    }

    public Achievement getAchievementItem(String id, int typeAch) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor;
        cursor = db.rawQuery("select * from " + Achievement.TABLE_NAME + " where " + Achievement.COLUMN_TASK_ID + "='" + id + "'" + " AND " + Achievement.COLUMN_TYPE + "='" + typeAch + "'", null);

        if (cursor != null) {
            cursor.moveToFirst(); //Only getting the first value
        }

        assert cursor != null;


        String stageNo = cursor.getString(0); //fbId
        int requirement = cursor.getInt(1);
        String fileName = cursor.getString(2);
        int type = cursor.getInt(3);
        String badgeURL = cursor.getString(4);
        Achievement newFocus = new Achievement(stageNo, requirement, fileName, type, badgeURL);

        //Close the DB connection
        db.close();

        return newFocus;

    }

    public void update(Achievement achievement) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues updateValues = new ContentValues();
        updateValues.put(Achievement.COLUMN_TASK_ID, achievement.getStageNo());
        updateValues.put(Achievement.COLUMN_HOURS, achievement.getRequirement());
        updateValues.put(Achievement.COLUMN_FILENAME, achievement.getFilename());
        updateValues.put(Achievement.COLUMN_TYPE, achievement.getTypeAchievement());
        updateValues.put(Achievement.COLUMN_URL, achievement.getBadgeUrl());

        db.update(
                Achievement.TABLE_NAME,
                updateValues,
                Achievement.COLUMN_TASK_ID + " = ? AND " + Achievement.COLUMN_TYPE + " = ?",
                new String[]{achievement.getStageNo(), String.valueOf(achievement.getTypeAchievement())}
        );

        db.close();
    }

    public void delete(String task, int type) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(
                Achievement.TABLE_NAME,  // The table to delete from
                Achievement.COLUMN_TASK_ID + " = ? AND " + Achievement.COLUMN_TYPE + " = ?",
                new String[]{task, String.valueOf(type)}
        );

        db.close();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
