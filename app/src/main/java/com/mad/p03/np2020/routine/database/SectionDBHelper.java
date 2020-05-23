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

    static final String DATABASE_NAME = "MyRoutine.db";
    static final int DATABASE_VERSION = 1;
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


    /**
     * To insert the section which is color, image and the name of the section
     * in the sqlite
     *
     * @param section passed to acces the name, color and image
     * @return the id in this case the row in belongs
     */
    public long insertSection(Section section){

        Log.d(TAG, "insertUser(): Preparing to insert the new user ");

        //Add values into the database
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Section.COLUMN_NAME, section.getName());
        values.put(Section.COLUMN_COLOR, section.getBackgroundColor());
//        values.put(User.COLUMN_NAME_PASSWORD, user.getPassword());//TODO: The image

        // Insert the new row, returning the primary key value of the new row
        //If -1 means there is an error
        long id = db.insert(Section.TABLE_NAME, null, values);

        if(id == -1){
            Log.e(TAG, "insertSection(): There has been error inserting the data" );
        } else{
            Log.d(TAG, "insertSection(): Data inserted");
        }

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


        //Get the data from sqllite
        Cursor cursor =  db.rawQuery( "select * from " + Section.TABLE_NAME+ " where id="+id+"", null );

        if (cursor != null)
            cursor.moveToFirst(); //Only getting the first value

        //Prepare a section object
        Section section = new Section(
                cursor.getString(cursor.getColumnIndex(Section.COLUMN_NAME)),
                cursor.getInt(cursor.getColumnIndex(Section.COLUMN_COLOR))
        );

        //Close the DB connection
        db.close();

        return section;

    }


    public List<Section> getAllSections(){

        List<Section> sections = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Section.TABLE_NAME + " ORDER BY " +
                Section.COLUMN_NAME + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Section section = new Section();
                section.setName(cursor.getString(cursor.getColumnIndex(Section.COLUMN_NAME)));
                section.setBackgroundColor(cursor.getInt(cursor.getColumnIndex(Section.COLUMN_COLOR)));

                Log.d(TAG, "getAllSections(): Reading data" + section.toString() );

                sections.add(section);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        ;
        // return notes list
        return sections;
    }

}
