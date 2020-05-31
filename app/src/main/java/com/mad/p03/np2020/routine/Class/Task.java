package com.mad.p03.np2020.routine.Class;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.CheckBox;

import com.mad.p03.np2020.routine.Class.Label;
import com.mad.p03.np2020.routine.database.SectionDBHelper;
import com.mad.p03.np2020.routine.database.TaskDBHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;

public class Task {

    private String mName;
    private String mTaskID;
    private int mUID;
    private boolean checked;
    private Date remindDate;
    private Date dueDate;
    private String mNotes;
    private String mLabels;
    private List<Steps> mSteps;
    private List<Label> mLabelList;

    //Declare the constants of the database
    public static final String TABLE_NAME = "task";

    public static final String COLUMN_TASK_ID = "TaskID";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_CHECKED = "Checked";
    public static final String COLUMN_REMIND_DATE = "RemindDate";
    public static final String COLUMN_DUE_DATE = "DueDate";
    public static final String COLUMN_NOTES = "Notes";
    public static final String COLUMN_SECTION_ID = "SectionID";

    // Create table SQL query
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_TASK_ID + " TEXT PRIMARY KEY,"
                    + COLUMN_SECTION_ID + " TEXT,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_CHECKED + " BOOLEAN,"
                    + COLUMN_REMIND_DATE + " TEXT,"
                    + COLUMN_DUE_DATE + " TEXT,"
                    + COLUMN_NOTES + " TEXT,"
                    + "FOREIGN KEY (" + COLUMN_SECTION_ID + ") REFERENCES  " + Section.TABLE_NAME + "(" + Section.COLUMN_ID + "));";

    //Query to delete the table
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public Task(String name) {

        this.mName = name;
        setTaskID(UUID.randomUUID().toString());
    }

    public Task(String name, String taskID) {

        this.mName = name;
        this.mTaskID = taskID;
    }


    public static Task fromCursor(Cursor cursor){

        return new Task(
                cursor.getString(cursor.getColumnIndex(Task.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(Task.COLUMN_TASK_ID))
        );
    }




    public String getName() {
        return mName;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Date getRemindDate() {
        return remindDate;
    }

    public void setRemindDate(Date remindDate) {
        this.remindDate = remindDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getNotes() {
        return mNotes;
    }

    public void setNotes(String notes) {
        mNotes = notes;
    }

    public String getTaskID() {
        return mTaskID;
    }

    public void setTaskID(String taskID) {
        mTaskID = taskID;
    }

    public Date stringToDate(String date){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyyy");
        try {
            return sdf.parse(date);
        } catch (ParseException ex) {
            Log.e("Exception", "Date unable to change reason: "+ ex.getLocalizedMessage());
            return null;
        }
    }

    public void addTask(Context context, String sectionID){
        TaskDBHelper taskDBHelper = new TaskDBHelper(context);

        taskDBHelper.insertTask(this, sectionID);
    }

    public void deleteTask(Context context){

        TaskDBHelper taskDBHelper = new TaskDBHelper(context);
        taskDBHelper.delete(getTaskID());
    }

    @NonNull
    @Override
    public String toString() {
        return "Task name: " + getName();
    }
}
