package com.mad.p03.np2020.routine.DAL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mad.p03.np2020.routine.models.Task;
import com.mad.p03.np2020.routine.models.Team;

import androidx.annotation.Nullable;

public class TeamDBHelper extends DBHelper {

    final String TAG = "TeamDBHelper";

    public TeamDBHelper(@Nullable Context context) {
        super(context);
    }

    /**
     * Create the table for section
     * @param sqLiteDatabase The database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        super.onCreate(sqLiteDatabase);
    }

    /**
     *
     * Called when the database needs to be upgraded. This will drop the
     * database and create a new one. The data from the previous one will
     * move forward into the new db
     *
     * @param sqLiteDatabase The database.
     * @param i The old database version.
     * @param i1 The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        super.onUpgrade(sqLiteDatabase, i, i1);
    }

    /**
     *
     * If current version is newer than the requested one. This will drop the
     * database and create a new one.
     *
     * @param db The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    public void insert(String sectionID, String email){
        Log.d(TAG, "insertTask(): Preparing to insert the new Section ");

        //Add values into the database
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Team.COLUMN_EMAIL, email);
        values.put(Team.COLUMN_SectionID, sectionID);

        //Insert the new row, returning the primary key value of the new row
        //If -1 means there is an error
        long id = db.insert(Team.TABLE_NAME, null, values);

        if(id == -1){
            Log.e(TAG, "insertTask(): There has been error inserting the data: ");
        } else{
            Log.d(TAG, "insertTask(): Data inserted");
        }
    }

    public Team getTeam(String sectionID){

        SQLiteDatabase db = this.getReadableDatabase();

        Log.d(TAG, "getTeam: Querying data");

        Team team = new Team();

        //Get the data from sqlite
        Cursor cursor =  db.rawQuery( "SELECT * FROM " + Team.TABLE_NAME+ " WHERE "+Team.COLUMN_SectionID+"='"+sectionID+"'", null );

        if ((cursor != null) &&  cursor.moveToFirst()){

            team.setSectionID(cursor.getString(cursor.getColumnIndex(Team.COLUMN_SectionID)));

            do {
                team.addEmail(cursor.getString(cursor.getColumnIndex(Team.COLUMN_EMAIL)));
            } while (cursor.moveToNext());

        }

        //Close the DB connection
        db.close();

        return team;

    }


    public void delete(String sectionID, String email){

        Log.d(TAG, "delete(): Will be deleting ID " + sectionID );

        SQLiteDatabase db = this.getWritableDatabase();


        db.delete(
                Team.TABLE_NAME,  // The table to delete from
                Team.COLUMN_SectionID + " = ? AND " + Team.COLUMN_EMAIL + " = ?", //The condition
                new String[]{sectionID, email} // The args will be replaced by ?
        );


        Log.d(TAG, "delete(): Removed from database");


        db.close();
    }



}
