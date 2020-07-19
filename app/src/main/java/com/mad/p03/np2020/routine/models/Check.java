package com.mad.p03.np2020.routine.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.mad.p03.np2020.routine.Card.models.DeleteCheckWorker;
import com.mad.p03.np2020.routine.Card.models.UploadCheckWorker;
import com.mad.p03.np2020.routine.DAL.CheckDBHelper;
import com.mad.p03.np2020.routine.Task.model.DeleteTaskWorker;
import com.mad.p03.np2020.routine.Task.model.UploadTaskWorker;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;



/*
 *
 * Check is object that belongs to task object
 *
 * @author Jeyavishnu
 * @since 10-07-2020
 *
 */

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
    public static final String COLUMN_TaskID = "TaskID";
    /**Used to identify the order the sections are in*/
    public static final String COLUMN_POSITION = "position";


    /**
     * The query needed to create a sql database
     * for the check
     */
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_Check_ID+ " TEXT PRIMARY KEY,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_CHECKED + " TEXT,"
                    + COLUMN_TaskID + " TEXT ,"
                    + COLUMN_POSITION + " INTEGER,"
                    + "FOREIGN KEY (" + COLUMN_TaskID + ") REFERENCES  " + Section.TABLE_NAME + "(" + Section.COLUMN_SECTION_ID + "));";

    /**
     * The query needed to delete SQL table check from the database
     */
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    private final String TAG = "Check";

    //Member variable
    private String mName, mID, mTaskID;
    private Boolean mChecked;
    private int mPosition;
    private boolean dirty = false;


    public static Check fromCursor(Cursor cursor){

        return new Check(
                cursor.getString(cursor.getColumnIndex(COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_Check_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHECKED)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_POSITION)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TaskID))

        );
    }

    public static Check fromDataSnapShot(DataSnapshot check){
        String name = check.child("name").getValue(String.class);
//        int position = check.child(COLUMN_POSITION).getValue(Integer.class) == null ? 0 : check.child("position").getValue(Integer.class);
        String ID = check.child("id").getValue(String.class);
        String taskID = check.child("taskID").getValue(String.class);

        String checked;
        if(check.child("checked").getValue() != null){
             checked = check.child("checked").getValue(Boolean.class) ? "1":"0";
        }else{
            checked = "0";
        }

        return new Check(name, ID, checked, 0, taskID);
    }

    public Check(String name){
        mName = name;
        mChecked = false;
        mID = UUID.randomUUID().toString();
    }

    public Check(String name, String id, String checked, int position, String taskID){
        mName = name;
        mID = id;
        mChecked = checked.equals("1");
        mPosition = position;
        mTaskID = taskID;
    }

    public String getName() {
        return mName;
    }

    public String getID() {
        return mID;
    }

    public int getPosition() {
        return mPosition;
    }

    public String getTaskID() {
        return mTaskID;
    }

    public Boolean isChecked() {
        return mChecked;
    }

    public void setChecked(Boolean checked) {
        mChecked = checked;
        dirty = true;
    }

    public void setName(String name) {
        mName = name;
        dirty = true;
    }

    public void setPosition(int position) {
        mPosition = position;
        dirty = true;
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

    /**
     *
     * Upload the check info to firebase
     * when internet connectivity is present
     *
     * It will be done in the background
     *
     * @param owner LifecycleOwner to be used to observe my upload
     */
    public void executeUpdateFirebase(LifecycleOwner owner, String taskID){
        if(dirty) {

            Log.d(TAG, "executeFirebaseUpload(): Preparing the upload");


            //Setting condition
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();


            //Adding data which will be received from the worker
            @SuppressLint("RestrictedApi") Data firebaseSectionData = new Data.Builder()
                    .putString(Check.COLUMN_NAME, getName())
                    .putString(Check.COLUMN_TaskID, taskID)
                    .putString(Check.COLUMN_Check_ID, getID())
                    .putInt(Check.COLUMN_POSITION, getPosition())
                    .putBoolean(Check.COLUMN_CHECKED, isChecked())
                    .build();

            //Create the request
            OneTimeWorkRequest uploadCheck = new OneTimeWorkRequest.
                    Builder(UploadCheckWorker.class)
                    .setConstraints(constraints)
                    .setInputData(firebaseSectionData)
                    .build();

            //Enqueue the request
            WorkManager.getInstance().enqueue(uploadCheck);


            Log.d(TAG, "executeFirebaseUpload(): Put in queue");

            WorkManager.getInstance().getWorkInfoByIdLiveData(uploadCheck.getId())
                    .observe(owner, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            Log.d(TAG, "Task upload state: " + workInfo.getState());
                        }
                    });
            dirty = false;
        }
    }

    /**
     *
     * Upload the task info to firebase
     * when internet connectivity is present
     *
     * It will be done in the background
     *
     * @param owner LifecycleOwner to be used to observe my upload
     */
    public void executeFirebaseUpload(LifecycleOwner owner, String taskID){

        Log.d(TAG, "executeFirebaseUpload(): Preparing the upload");

        //Setting condition
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();


        //Adding data which will be received from the worker
        @SuppressLint("RestrictedApi") Data firebaseSectionData = new Data.Builder()
                .putString(Check.COLUMN_NAME, getName())
                .putString(Check.COLUMN_TaskID, taskID)
                .putString(Check.COLUMN_Check_ID, getID())
                .putInt(Check.COLUMN_POSITION, getPosition())
                .putBoolean(Check.COLUMN_CHECKED, isChecked())
                .build();

        //Create the request
        OneTimeWorkRequest uploadCheck = new OneTimeWorkRequest.
                Builder(UploadCheckWorker.class)
                .setConstraints(constraints)
                .setInputData(firebaseSectionData)
                .build();

        //Enqueue the request
        WorkManager.getInstance().enqueue(uploadCheck);


        Log.d(TAG, "executeFirebaseUpload(): Put in queue");

        WorkManager.getInstance().getWorkInfoByIdLiveData(uploadCheck.getId())
                .observe(owner, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        Log.d(TAG, "Task upload state: " + workInfo.getState());
                    }
                });

    }


    /**
     * Delete the check from firebase
     * when internet connectivity is present
     *
     * It will be done in the background
     *
     * @param owner to be used to observe my upload
     */
    public void executeFirebaseDelete(LifecycleOwner owner, String taskID){

        Log.d(TAG, "executeFirebaseDelete(): Preparing to delete, on ID: " + getID());

        //Setting condition
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        //Adding data which will be received from the worker
        @SuppressLint("RestrictedApi") Data firebaseSectionData = new Data.Builder()
                .putString("ID", getID())
                .putString(Task.COLUMN_TASK_ID, taskID)
                .build();

        //Create the request
        OneTimeWorkRequest deleteTask = new OneTimeWorkRequest.
                Builder(DeleteCheckWorker.class)
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

    /**
     *
     * This is override to compare between 2 objects
     * this case in checks the variables of this object against
     * the one in the parameter
     *
     * @param obj The object we comparing with
     * @return return true if the variables match this object
     */
    @Override
    public boolean equals(@Nullable Object obj) {

        Check check = (Check) obj;



        if(check != null){
            boolean isSame = (check.isChecked() == this.isChecked());

            return (isSame);
        }else{
            return false;
        }
    }
}
