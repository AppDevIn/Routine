package com.mad.p03.np2020.routine.Class;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.mad.p03.np2020.routine.background.DeleteTaskWorker;
import com.mad.p03.np2020.routine.background.UploadTaskWorker;
import com.mad.p03.np2020.routine.database.TaskDBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;


/**
 *
 * Model used to manage the Task
 *
 * @author Jeyavishnu
 * @since 04-06-2020
 */
public class Task implements Serializable {

    //Member variable
    private String mName="";
    private String mTaskID="";
    private String mSectionID="";
    private int mPosition=0;
    private boolean checked;
    private String remindDate = "";
    private String mNotes = "";
    private boolean dirty = false;
    private Date dueDate;
    private final static String TAG = "Task Model";

    /**The table name for this model*/
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
    /**Used to identify the order the sections are in*/
    public static final String COLUMN_POSITION = "position";
    /**Column name for table,  the foreign key for the task */
    public static final String COLUMN_SECTION_ID = "SectionID";


    /**
     * The query needed to create a sql database
     * for the Task
     */
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_TASK_ID + " TEXT PRIMARY KEY,"
                    + COLUMN_POSITION + " INTEGER,"
                    + COLUMN_SECTION_ID + " TEXT,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_CHECKED + " INTEGER,"
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

        this.mTaskID = UUID.randomUUID().toString();
    }

    public Task(String name, int position, String sectionID, String taskID, boolean checked, String notes, String remindDate )  {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.mName = name;
        this.mSectionID = sectionID;
        this.mTaskID = taskID;
        this.mPosition = position;
        this.checked = checked;
        this.mNotes = notes;
        this.remindDate = remindDate;

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
                cursor.getInt(cursor.getColumnIndex(COLUMN_POSITION)),
                cursor.getString(cursor.getColumnIndex(COLUMN_SECTION_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_TASK_ID)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_CHECKED)) == 1,
                cursor.getString(cursor.getColumnIndex(COLUMN_NOTES)),
                cursor.getString(cursor.getColumnIndex(COLUMN_REMIND_DATE))
        );
    }

    public static Task fromDataSnapShot(DataSnapshot task){
        String name = task.child("name").getValue(String.class);
        int position = task.child("position").getValue(Integer.class) == null ? 0 : task.child("position").getValue(Integer.class);
        String sectionID = task.child("sectionID").getValue(String.class);
        String ID = task.child("taskID").getValue(String.class);
        boolean checked = task.child("checked").getValue(Boolean.class) == null ? false : task.child("checked").getValue(Boolean.class);
        String notes = task.child("notes").getValue(String.class);
        String remindDate = task.child("remindDate").getValue(String.class);;

        return new Task(name, position, sectionID, ID, checked, notes, remindDate);
    }

    /**
     *
     * This is used to create task object using
     * a string that is in json format. This will
     * convert it into json object and extract the
     * information needed for the object
     *
     * @param data The string of data in json format
     * @return Task Return back a task object
     */
    public static Task fromMap(Map<String, String> data){

        String name = "";
        String id = "";
        String sectionID = "";
        boolean checked = false;
        String notes = "";
        String remindDate = "Sun Jun 07 13:15:51 GMT+08:00 2020";


        try {


            //Get the values from the object

            name = data.get("name");
            id = data.get("id");
            sectionID = data.get("sectionID");
//            notes = jsonObject.getString("notes");
            checked = Boolean.parseBoolean(data.get("checked"));



            //Return back the object
            return new Task(name, 0, sectionID, id,checked, notes, remindDate);


        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "fromMap: ", e);
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

    /**@return Date This return the data that I need to remind about the task*/
    public String getRemindDate() {
        return remindDate;
    }

    /**@return Date This return the data that I need to remind about the task*/
    public Date getDateRemindDate() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return dateFormat.parse(remindDate);
        }catch (Exception e){
            return null;
        }
    }


    /**@return String This return a string of notes for this task*/
    public String getNotes() {
        return mNotes;
    }

    /**@return String This return the unique task ID for each task*/
    public String getTaskID() {
        return mTaskID;
    }

    /**
     * This method is used to set the Section ID this
     * task belongs too
     *
     * @return This parameter is used to set the section ID
     * this task belongs too
     */
    public String getSectionID() {
        return mSectionID;
    }

    /**@return int This return the current order of the task*/
    public int getPosition() {
        return mPosition;
    }

    /**@return Date This return the date the task is going to be due*/
    public Date getDueDate() {
        return dueDate;
    }

    /**
     *
     * This method is used to set
     * the taskID of this task
     *
     * @param taskID This parameter is used to set the taskID
     *                 of this task
     */
    public void setTaskID(String taskID) {
        dirty = true;
        mTaskID = taskID;
    }

    /**
     *
     * This method is used to set
     * the taskID of this task
     *
     * @param position int This is used to set the order
     *                 where the list is at now
     */
    public void setPosition(int position) {
//        dirty = true;
        mPosition = position;
    }

    public void setChecked(boolean checked) {
        dirty = true;
        this.checked = checked;
    }

    public void setName(String name) {
        dirty = true;
        mName = name;
    }

    public void setNotes(String notes) {
        dirty = true;
        mNotes = notes;
    }

    public void setRemindDate(String remindDate) {
        dirty = true;
        this.remindDate = remindDate;
    }

    /**
     *
     * @param date String This parameter gives me the data and time in
     *             this format dd/MM/yyyy
     * @return Date Return the date object
     */
    public Date stringToDate(String date){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return sdf.parse(date);
        } catch (ParseException ex) {
            Log.e("Exception", "Date unable to change reason: "+ ex.getLocalizedMessage());
            return null;
        }
    }

    /**
     *
     * This method is used to add
     * the task data from SQL
     * using the TaskDBHelper
     *
     * @param context To know from which state of the object the code is called from
     */
    public void addTask(Context context){
        TaskDBHelper taskDBHelper = new TaskDBHelper(context);

        taskDBHelper.insertTask(this);
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

        TaskDBHelper taskDBHelper = new TaskDBHelper(context);
        taskDBHelper.delete(getTaskID());
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
    public void executeFirebaseUpload(LifecycleOwner owner){

        Log.d(TAG, "executeFirebaseUpload(): Preparing the upload");

        //Setting condition
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();


        //Adding data which will be received from the worker
        @SuppressLint("RestrictedApi") Data firebaseSectionData = new Data.Builder()
                .putString(Task.COLUMN_NAME, getName())
                .putString(Task.COLUMN_SECTION_ID, getSectionID())
                .putString(Task.COLUMN_TASK_ID, getTaskID())
                .putInt(Task.COLUMN_POSITION, getPosition())
                .putBoolean(Task.COLUMN_CHECKED, isChecked())
                .putString(Task.COLUMN_NOTES, getNotes())
                .putString(Task.COLUMN_REMIND_DATE, getRemindDate().toString())
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


    /**
     *
     * Upload the task info to firebase
     * when internet connectivity is present
     *
     * It will be done in the background
     *
     * @param owner LifecycleOwner to be used to observe my upload
     */
    public void executeUpdateFirebase(LifecycleOwner owner){
        if(dirty) {
            Log.d(TAG, "executeFirebaseUpload(): Preparing the upload");

            //Setting condition
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();


            //Adding data which will be received from the worker
            @SuppressLint("RestrictedApi") Data firebaseSectionData = new Data.Builder()
                    .putString(Task.COLUMN_NAME, getName())
                    .putString(Task.COLUMN_SECTION_ID, getSectionID())
                    .putString(Task.COLUMN_TASK_ID, getTaskID())
                    .putInt(Task.COLUMN_POSITION, getPosition())
                    .putBoolean(Task.COLUMN_CHECKED, isChecked())
                    .putString(Task.COLUMN_NOTES, getNotes())
                    .putString(Task.COLUMN_REMIND_DATE, getRemindDate().toString())
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

            if(owner != null) {


                WorkManager.getInstance().getWorkInfoByIdLiveData(uploadTask.getId())
                        .observe(owner, new Observer<WorkInfo>() {
                            @Override
                            public void onChanged(WorkInfo workInfo) {
                                Log.d(TAG, "Task Delete state: " + workInfo.getState());
                            }
                        });
            }
        }
    }


    /**
     * Delete the task from firebase
     * when internet connectivity is present
     *
     * It will be done in the background
     *
     * @param owner to be used to observe my upload
     */
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

        if(owner != null) {


            WorkManager.getInstance().getWorkInfoByIdLiveData(deleteTask.getId())
                    .observe(owner, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            Log.d(TAG, "Task Delete state: " + workInfo.getState());
                        }
                    });
        }


    }

    @Override
    public boolean equals(@Nullable Object obj) {

        Task task = (Task) obj;

        //Test
//        boolean isTask = this.mTaskID.equals(task.getTaskID());
//        boolean isNotes =  (this.mNotes.equals(task.getNotes()) || task.mNotes == null);
//        boolean hasChecked = this.isChecked() == task.isChecked();
//        boolean isName = this.getName().equals(task.getName());
//        boolean isRemindDate =  this.getRemindDate().equals(task.getRemindDate());


        if(task != null){
            boolean isSame = this.mTaskID.equals(task.getTaskID()) &&
                    (this.mNotes.equals(task.getNotes()) || task.mNotes == null)&&
                    this.isChecked() == task.isChecked() &&
                    this.getName().equals(task.getName()) &&
                    this.getRemindDate().equals(task.getRemindDate());
            return (isSame);
        }else{
            return false;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "Task name: " + getName();
    }
}
