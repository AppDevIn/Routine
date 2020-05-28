package com.mad.p03.np2020.routine.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mad.p03.np2020.routine.Class.Section;
import com.mad.p03.np2020.routine.Class.User;

import java.util.ArrayList;
import java.util.List;

public class SectionDBHelper extends SQLiteOpenHelper {

    //Listener
    private static MyDatabaseListener mMyDatabaseListener;

    static final String DATABASE_NAME = "MyRoutine.db";
    static final int DATABASE_VERSION = 3;
    private final String TAG = "SectionDatebase";

    public SectionDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "User database is being created");

        //Create user database
        sqLiteDatabase.execSQL(Section.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.d(TAG, "User database is being upgraded");

        sqLiteDatabase.execSQL(Section.SQL_DELETE_ENTRIES); // Delete existing user dat
        onCreate(sqLiteDatabase);
    }

    //If current version is newer than the requested one
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "User database is downgraded");

        onUpgrade(db,oldVersion,newVersion);
    }


    // Assign the listener implementing events interface that will receive the events
    public static void  setMyDatabaseListener(MyDatabaseListener myDatabaseListener){
        mMyDatabaseListener = myDatabaseListener;
    }

    /**
     * To insert the section which is color, image and the name of the section
     * in the sqlite
     *
     * @param section passed to acces the name, color and image
     * @param UID passed to be put it as the foreign key
     * @return the id in this case the row in belongs
     */
    public long insertSection(Section section, String UID){

        Log.d(TAG, "insertUser(): Preparing to insert the new user ");

        //Add values into the database
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Section.COLUMN_NAME, section.getName());
        values.put(Section.COLUMN_COLOR, section.getBackgroundColor());
        values.put(Section.COLUMN_USERID, UID);
        values.put(Section.COLUMN_IMAGE, section.getBmiIcon());//TODO: The image

        // Insert the new row, returning the primary key value of the new row
        //If -1 means there is an error
        long id = db.insert(Section.TABLE_NAME, null, values);

        if(id == -1){
            Log.e(TAG, "insertSection(): There has been error inserting the data" );
        } else{
            Log.d(TAG, "insertSection(): Data inserted");
        }
        if (mMyDatabaseListener != null)
            mMyDatabaseListener.onSectionAdd(section);

        return id;
    }

    /**
     * Get the first user in the SQL database
     *
     * @return section back
     */
    public Section getSection(long id){
        SQLiteDatabase db = this.getReadableDatabase();

        Log.d(TAG, "getUser: Querying data");


        //Get the data from sqlite
        Cursor cursor =  db.rawQuery( "select * from " + Section.TABLE_NAME+ " where id="+id+"", null );

        if (cursor != null)
            cursor.moveToFirst(); //Only getting the first value

        //Prepare a section object
        assert cursor != null;
        Section section = Section.fromCursor(cursor);
        Log.d(TAG, "getAllSections(): Reading data" + section.toString() );


        //Close the DB connection
        db.close();

        return section;

    }


    /**
     *
     * This function will query all the Section associated with
     * UID and retrieve it. With the raw data it will be
     * converted to sections.
     *
     * @param UID The user Unique Identification
     * @return A list of section data from SQL
     */

    public List<Section> getAllSections(String UID){

        List<Section> sections = new ArrayList<>();

        // Select All Query

        String selectQuery = "SELECT  * FROM " + Section.TABLE_NAME + " WHERE " + Section.COLUMN_USERID + "='" + UID +"' ORDER BY " +
                Section.COLUMN_ID + " DESC;";

        SQLiteDatabase db = this.getWritableDatabase();


        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Section section = Section.fromCursor(cursor);

                Log.d(TAG, "getAllSections(): Reading data" + section.toString() );

                sections.add(section);
            } while (cursor.moveToNext());
        }


        // close db connection
        db.close();


        // return notes list
        return sections;
    }


    public Boolean hasID(long id){
        SQLiteDatabase db = this.getReadableDatabase();

        //Get the data from sqlite
        Cursor cursor =  db.rawQuery( "select * from " + Section.TABLE_NAME+ " where id="+id+"", null );

        return cursor.moveToFirst();
    }


}


