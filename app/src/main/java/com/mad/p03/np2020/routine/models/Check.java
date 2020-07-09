package com.mad.p03.np2020.routine.models;

import android.content.Context;
import android.database.Cursor;

import com.mad.p03.np2020.routine.DAL.CheckDBHelper;
import com.mad.p03.np2020.routine.DAL.TaskDBHelper;

import java.util.UUID;

public class Check {

    /**The table name of this class in SQL*/
    public static final String TABLE_NAME = "CheckList";

    /**Used as the primary key for this table*/
    public static final String COLUMN_Check_ID = "CheckID";
    /**Used to identify the Checklist is check*/
    public static final String COLUMN_CHECKED = "Checked";
    /**Column name for table,  to identify the name of the Check*/
    public static final String COLUMN_NAME = "Name";
    /**Foreign Key ScheduleID*/
    public static final String COLUMN_SectionID = "SectionID";


    /**
     * The query needed to create a sql database
     * for the check
     */
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_Check_ID+ " TEXT PRIMARY KEY,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_CHECKED + " TEXT,"
                    + COLUMN_SectionID + " TEXT ,"
                    + "FOREIGN KEY (" + COLUMN_SectionID + ") REFERENCES  " + Section.TABLE_NAME + "(" + Section.COLUMN_SECTION_ID + "));";

    /**
     * The query needed to delete SQL table check from the database
     */
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    //Member variable
    private String mName, mID;
    private Boolean mChecked;


    public static Check fromCursor(Cursor cursor){

        return new Check(
                cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_Check_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHECKED))
        );

    }

    public Check(String name){
        mName = name;
        mChecked = false;
        mID = UUID.randomUUID().toString();
    }

    private Check(String name, String id, String checked){
        mName = name;
        mID = id;
        mChecked = Boolean.parseBoolean(checked);
    }


    public String getName() {
        return mName;
    }

    public String getID() {
        return mID;
    }

    public Boolean isChecked() {
        return mChecked;
    }

    public void setChecked(Boolean checked) {
        mChecked = checked;
    }

    public void setName(String name) {
        mName = name;
    }

    /**
     *
     * This method is used to add
     * the task data from SQL
     * using the TaskDBHelper
     *
     * @param context To know from which state of the object the code is called from
     */
    public void addCheck(Context context, String sectionID){
        CheckDBHelper checkDBHelper = new CheckDBHelper(context);

        checkDBHelper.insertCheck(this, sectionID);
    }

    /**
     *
     * This method is used to delete
     * the task data from SQL
     * using the TaskDBHelper
     *
     * @param context To know from which state of the object the code is called from
     */
    public void deleteTask(Context context){

        CheckDBHelper checkDBHelper = new CheckDBHelper(context);
        checkDBHelper.delete(getID());
    }
}
