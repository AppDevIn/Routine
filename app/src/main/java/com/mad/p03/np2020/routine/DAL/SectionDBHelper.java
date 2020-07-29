package com.mad.p03.np2020.routine.DAL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mad.p03.np2020.routine.helpers.MyDatabaseListener;
import com.mad.p03.np2020.routine.models.Section;
import com.mad.p03.np2020.routine.models.Task;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * This is the database that is used create the section table
 * in the database
 *
 *
 * @author Jeyavishnu
 * @since 03-06-2020
 */
public class SectionDBHelper extends DBHelper{

    //Listener
    private static MyDatabaseListener mMyDatabaseListener;

    private final String TAG = "SectionDatabase";
    Context mContext;

    public SectionDBHelper(Context context) {
        super(context);
        mContext = context;
    }


    /**
     * Create the table section
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

    /**
     * Assign the listener implementing events interface
     * that will receive the events
     * @param myDatabaseListener setting the listener where the owner will listen to the message
     */
    public static void setMyDatabaseListener(MyDatabaseListener myDatabaseListener){
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
    public String insertSection(Section section, String UID){

        Log.d(TAG, "insertUser(): Preparing to insert the new user ");

        //Add values into the database
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Section.COLUMN_SECTION_ID, section.getID());
        values.put(Section.COLUMN_NAME, section.getName());
        values.put(Section.COLUMN_COLOR, section.getBackgroundColor());
        values.put(Section.COLUMN_USERID, UID);
        values.put(Section.COLUMN_IMAGE, section.getIconValue());//The image

        // Insert the new row, returning the primary key value of the new row
        //If -1 means there is an error
        long id = db.insert(Section.TABLE_NAME, null, values);

        if(id == -1){
            Log.e(TAG, "insertSection(): There has been error inserting the data" );
        } else{
            Log.d(TAG, "insertSection(): Data inserted");
        }
        if (mMyDatabaseListener != null)
            mMyDatabaseListener.onDataAdd(section);

        return String.valueOf(id);
    }

    /**
     * Get the first user in the SQL database
     *
     * @return section back
     */
    public Section getSection(String id){
        SQLiteDatabase db = this.getReadableDatabase();

        Log.d(TAG, "getUser: Querying data");


        //Get the data from sqlite
        Cursor cursor =  db.rawQuery( "select * from " + Section.TABLE_NAME+ " where sectionID='"+id+"'", null );

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

        String selectQuery = "SELECT  * FROM " + Section.TABLE_NAME +" ORDER BY " +
                Section.COLUMN_POSITION + " ASC;";

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


    /**
     * To delete the data in SQL
     * from the ID parameter
     *
     * @param ID it is from section object
     */
    public void delete(String ID){
        SQLiteDatabase db = this.getWritableDatabase();


        //Will delete the check and task list
        new TaskDBHelper(mContext).deleteAll(ID);

        //Delete the section
        db.delete(
                Section.TABLE_NAME,  // The table to delete from
                Section.COLUMN_SECTION_ID + " = ?", //The condition
                new String[]{ID} // The args will be replaced by ?
                );
        mMyDatabaseListener.onDataDelete(ID);




        db.close();
    }


    /**
     *
     * This method will update the position of the given
     * row based on the ID
     *
     * @param section The object that needs to be updates
     */
    public void updatePosition(Section section){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues updateValues = new ContentValues();
        updateValues.put(Section.COLUMN_POSITION, section.getPosition());
        db.update(
                Section.TABLE_NAME,
                updateValues,
                Section.COLUMN_SECTION_ID + " = ?",
                new String[]{section.getID()}
        );

        db.close();
    }

    /**
     *
     * This method will update the position of the given
     * row based on the ID
     *
     * @param section The object that needs to be updates
     */
    public void updateSection(Section section){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues updateValues = new ContentValues();
        updateValues.put(Section.COLUMN_COLOR, section.getBackgroundColor());
        updateValues.put(Section.COLUMN_IMAGE, section.getIconValue());
        updateValues.put(Section.COLUMN_NAME, section.getName());
        db.update(
                Section.TABLE_NAME,
                updateValues,
                Section.COLUMN_SECTION_ID + " = ?",
                new String[]{section.getID()}
        );

        if (mMyDatabaseListener != null)
            mMyDatabaseListener.onDataUpdate(section);

        db.close();
    }



    /**
     *
     * This method will query from the database
     * if it doesn't exist it return false if does true
     * based on the {@code moveToFirst()}
     *
     * @param id Section ID to check against the table
     * @return Boolean if is true or false depending on if
     * the row exits. If it exists is true
     */
    public Boolean hasID(String id){
        SQLiteDatabase db = this.getReadableDatabase();

        //Get the data from sqlite
        Cursor cursor =  db.rawQuery( "select * from " + Section.TABLE_NAME+ " where "+ Section.COLUMN_SECTION_ID +"='"+id+"'", null );

        return cursor.moveToFirst();
    }


}


