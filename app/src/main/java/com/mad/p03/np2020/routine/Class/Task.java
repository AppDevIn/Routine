package com.mad.p03.np2020.routine.Class;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.CheckBox;

import com.google.firebase.auth.FirebaseAuth;
import com.mad.p03.np2020.routine.Class.Label;
import com.mad.p03.np2020.routine.background.DeleteSectionWorker;
import com.mad.p03.np2020.routine.background.DeleteTaskWorker;
import com.mad.p03.np2020.routine.background.UploadSectionWorker;
import com.mad.p03.np2020.routine.background.UploadTaskWorker;
import com.mad.p03.np2020.routine.database.SectionDBHelper;
import com.mad.p03.np2020.routine.database.TaskDBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;



public class Task {

    private String mName;
    private String mTaskID;
    private String mSectionID;
    private boolean checked;
    private Date remindDate;
    private Date dueDate;
    private String mNotes;
    private String mLabels;
    private List<Steps> mSteps;
    private List<Label> mLabelList;

    private final static String TAG = "Task Model";

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
                    + "FOREIGN KEY (" + COLUMN_SECTION_ID + ") REFERENCES  " + Section.TABLE_NAME + "(" + Section.COLUMN_SECTION_ID + "));";

    //Query to delete the table
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


    public Task() {
    }

    public Task(String name, String sectionID) {

        this.mName = name;
        this.mSectionID = sectionID;

        setTaskID(UUID.randomUUID().toString());
    }

    public Task(String name,String sectionID, String taskID) {

        this.mName = name;
        this.mSectionID = sectionID;
        this.mTaskID = taskID;
    }


    public static Task fromCursor(Cursor cursor){

        return new Task(
                cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_SECTION_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_TASK_ID))

        );
    }

    public static Task fromJSON(String json){

        String name = "";
        String id = "";
        String sectionID = "";

        try {
            //Make the string to object
            JSONObject jsonObject = new JSONObject(json);

            //Get the values from the object

            name = jsonObject.getString("name");
            id = jsonObject.getString("id");
            sectionID = jsonObject.getString("id");

            //Return back the object
            return new Task(name, sectionID, id);


        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "fromJSON: ", e);
        }



        return null;


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

    public String getSectionID() {
        return mSectionID;
    }

    public void setSectionID(String sectionID) {
        mSectionID = sectionID;
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

    public void addTask(Context context){
        TaskDBHelper taskDBHelper = new TaskDBHelper(context);

        taskDBHelper.insertTask(this);
    }

    public void deleteTask(Context context){

        TaskDBHelper taskDBHelper = new TaskDBHelper(context);
        taskDBHelper.delete(getTaskID());
    }

    public void executeFirebaseUpload(LifecycleOwner owner){

        Log.d(TAG, "executeFirebaseUpload(): Preparing the upload");

        //Setting condition
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();


        //Adding data which will be received from the worker
        @SuppressLint("RestrictedApi") Data firebaseSectionData = new Data.Builder()
                .putString(Task.COLUMN_NAME, getName())
                .putString(Section.COLUMN_SECTION_ID, getSectionID())
                .putString(Task.COLUMN_TASK_ID, getTaskID())
                .build();

        //Create the request
        OneTimeWorkRequest uploadTask = new OneTimeWorkRequest.
                Builder(UploadTaskWorker.class)
                .setConstraints(constraints)
                .setInputData(firebaseSectionData)
                .build();

        //Enqueue the request
        WorkManager.getInstance().enqueue(uploadTask);


        Log.d(TAG, "executeFirebaseUpload(): Put in queue");

        WorkManager.getInstance().getWorkInfoByIdLiveData(uploadTask.getId())
                .observe(owner, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        Log.d(TAG, "Task upload state: " + workInfo.getState());
                    }
                });

    }

    public void executeFirebaseDelete(LifecycleOwner owner){

        Log.d(TAG, "executeFirebaseDelete(): Preparing to delete, on ID: " + getTaskID());

        //Setting condition
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        //Adding data which will be received from the worker
        @SuppressLint("RestrictedApi") Data firebaseSectionData = new Data.Builder()
                .putString("ID", getTaskID())
                .putString(Section.COLUMN_SECTION_ID, getSectionID())
                .build();

        //Create the request
        OneTimeWorkRequest deleteTask = new OneTimeWorkRequest.
                Builder(DeleteTaskWorker.class)
                .setConstraints(constraints)
                .setInputData(firebaseSectionData)
                .build();

        //Enqueue the request
        WorkManager.getInstance().enqueue(deleteTask);

        Log.d(TAG, "executeFirebaseSectionUpload(): Put in queue");


        WorkManager.getInstance().getWorkInfoByIdLiveData(deleteTask.getId())
                .observe(owner, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        Log.d(TAG, "Task Delete state: " + workInfo.getState());
                    }
                });


    }


    @NonNull
    @Override
    public String toString() {
        return "Task name: " + getName();
    }
}
