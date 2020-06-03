package com.mad.p03.np2020.routine.Class;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.mad.p03.np2020.routine.background.DeleteTaskWorker;
import com.mad.p03.np2020.routine.background.UploadTaskWorker;
import com.mad.p03.np2020.routine.database.TaskDBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    //Member variable
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

    /**Used as the primary key for this table*/
    public static final String COLUMN_TASK_ID = "TaskID";
    /**Column name for table,  to identify the name of the task*/
    public static final String COLUMN_NAME = "Name";
    /**Column name for table,  to check if this task has been checked already*/
    public static final String COLUMN_CHECKED = "Checked";
    /**Column name for table,  to know when this task is going to end*/
    public static final String COLUMN_REMIND_DATE = "RemindDate";
    /**Column name for table,  to identify the due data of this task*/
    public static final String COLUMN_DUE_DATE = "DueDate";
    /**Column name for table,  the notes to this task*/
    public static final String COLUMN_NOTES = "Notes";
    /**Column name for table,  the foreign key for the task */
    public static final String COLUMN_SECTION_ID = "SectionID";


    /**
     * The query needed to create a sql database
     * for the Task
     */
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

    /**
     * The query needed to delete SQL table task from the database
     */
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


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


    /**
     *
     * This is to convert the data received from SQL and
     * convert it into a object
     *
     * @param cursor The query that has been given buy database
     * @return Task Return back a task object
     */
    public static Task fromCursor(Cursor cursor){

        return new Task(
                cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_SECTION_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_TASK_ID))

        );
    }

    /**
     *
     * This is used to create task object using
     * a string that is in json format. This will
     * convert it into json object and extract the
     * information needed for the object
     *
     * @param json The string of data in json format
     * @return Task Return back a task object
     */
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

    /**@return String This return the name of the task*/
    public String getName() {
        return mName;
    }

    /**@return boolean Check if the task has been completed before*/
    public boolean isChecked() {
        return checked;
    }

    /**@return boolean Check if the task has been completed before*/
    public Date getRemindDate() {
        return remindDate;
    }


    public Date getDueDate() {
        return dueDate;
    }

    public String getNotes() {
        return mNotes;
    }

    public String getTaskID() {
        return mTaskID;
    }

    public String getSectionID() {
        return mSectionID;
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
