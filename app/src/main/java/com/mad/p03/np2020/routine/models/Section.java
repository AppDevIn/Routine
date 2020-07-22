package com.mad.p03.np2020.routine.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mad.p03.np2020.routine.DAL.CheckDBHelper;
import com.google.firebase.database.DataSnapshot;
import com.mad.p03.np2020.routine.background.DeleteSectionWorker;
import com.mad.p03.np2020.routine.Home.models.UploadSectionWorker;
import com.mad.p03.np2020.routine.DAL.SectionDBHelper;
import com.mad.p03.np2020.routine.DAL.TaskDBHelper;
import com.mad.p03.np2020.routine.helpers.HomeIcon;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 *
 * Model used to manage the section
 *
 * @author Jeyavishnu
 * @since 02-06-2020
 */
public class Section implements Serializable {


    /**The table name of this class in SQL*/
    public static final String TABLE_NAME = "section";

    /**Used as the primary key for this table*/
    public static final String COLUMN_SECTION_ID = "sectionID";
    /**Used to identify the order the sections are in*/
    public static final String COLUMN_POSITION = "position";
    /**Column name for table,  to identify the name of the section*/
    public static final String COLUMN_NAME = "name";
    /**Column name for table, to identify the background color of the section */
    public static final String COLUMN_COLOR = "color";
    /**Column name for table, the icon that will represent the visual of the section  */
    public static final String COLUMN_IMAGE = "image";
    /**Column name for table, a foreign key to link to the user*/
    public static final String COLUMN_USERID = "userId";


    /**
     * The query needed to create a sql database
     * for the section
     */
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_SECTION_ID + " TEXT PRIMARY KEY,"
                    + COLUMN_POSITION + " INTEGER,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_COLOR + " INTEGER,"
                    + COLUMN_IMAGE + " INTEGER,"
                    + COLUMN_USERID + " INTEGER,"
                    + "FOREIGN KEY (" + COLUMN_USERID + ") REFERENCES  " + User.TABLE_NAME + "(" + User.COLUMN_NAME_ID + "));";

    /**
     * The query needed to delete SQL table section from the database
     */
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


    //Declaring member variable
    private String mName;
    private int mBackgroundColor;
    private int bmiIcon;
    private int position;
    private String ID;
    private String mUID;
    Map<String, String> teamList = new HashMap<>();




    public Section(String name, int color, int iconResID, String UID) {
        this.mName = name;
        this.mBackgroundColor = color;
        this.bmiIcon = iconResID;
        this.ID = UUID.randomUUID().toString();
        this.mUID = UID;
    }

    public Section(String name, int color, int iconResID, String ID, int position, String UID) {
        this.mName = name;
        this.mBackgroundColor = color;
        this.bmiIcon = iconResID;
        this.ID = ID;
        this.position = position;
        this.mUID = UID;
    }

                    /*Not Used anymore*/
//    /**
//     * Create the section object from a json object
//     * @param json The json you want to convert to object
//     * @return This will return the section object
//     */
//    public static Section fromJSON(String json){
//
//        int color = 0;
//        String name = "";
//        int image = 0;
//        String id = "";
//        try {
//            //Make the string to object
//            JSONObject jsonObject = new JSONObject(json);
//
//            //Get the values from the object
//            color = Integer.parseInt(jsonObject.getString("backgroundColor"));
//            name = jsonObject.getString("name");
//            image = Integer.parseInt(jsonObject.getString("bmiIcon"));
//            id = jsonObject.getString("id");
//
//            //Return back the object
//            //Needs to change
//            return new Section(name, color, image, id,5,"jdfshkjfgnds");
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.e(TAG, "fromJSON: ", e);
//        }
//
//
//
//        return null;
//
//
//    }

    /**
     * Create a object from the DatasnapShot from
     * firebase
     * @param snapshot The snap you want to convert to object
     * @return This will return the section object
     */
    public static Section fromDataSnapShot(DataSnapshot snapshot){

        Map<String, String> teamList = new HashMap<>();
        
        String UID = snapshot.child("uid").getValue(String.class);
        String name = snapshot.child("name").getValue(String.class);
        int icon = snapshot.child("bmiIcon").getValue(Integer.class) == null ? 0 : snapshot.child("bmiIcon").getValue(Integer.class);
        int color = snapshot.child("backgroundColor").getValue(Integer.class) == null ? 0 : snapshot.child("backgroundColor").getValue(Integer.class);
        String id = snapshot.child("id").getValue(String.class);



        return new Section(name, color, icon, id, 0, UID);



    }

    /**
     * Create object from cursor from SQLite
     * @param cursor The cursor you want to convert to object
     * @return This will return the section
     */
    public static Section fromCursor(Cursor cursor){

        return new Section(
                cursor.getString(cursor.getColumnIndex(Section.COLUMN_NAME)),
                cursor.getInt(cursor.getColumnIndex(Section.COLUMN_COLOR)),
                cursor.getInt(cursor.getColumnIndexOrThrow(Section.COLUMN_IMAGE)),
                cursor.getString(cursor.getColumnIndexOrThrow(Section.COLUMN_SECTION_ID)),
                cursor.getInt(cursor.getColumnIndexOrThrow(Section.COLUMN_POSITION)),
                cursor.getString(cursor.getColumnIndexOrThrow(Section.COLUMN_USERID))
        );
    }

    /**@return int This return the int value associated with the image*/
    public int getBmiIcon() {
        return HomeIcon.getBackground(bmiIcon);
    }

    public int getIconValue() {
        return HomeIcon.getBackground(bmiIcon);
    }

    /**@return String This return the name of this section*/
    public String getName() {
        return mName;
    }

    /**@return int This returns the background color of the section*/
    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    /**@return  String this returns SectionId which is unique to each section*/
    public String getID() {
        return ID;
    }

    /**@return int This return the position the section arranged in the recycler view*/
    public int getPosition() {
        return position;
    }
    /**@return String This return the UID of the user */
    public String getUID() {
        return mUID;
    }

    /**
     *
     * This method is used to set
     * the position of this section
     *
     * @param position This parameter is used to set the position
     *                 of this section
     * */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     *
     * This method is used to set
     * the name of the section
     *
     * @param name This parameter is used to set
     *             the name of the section
     */
    public void setName(String name) {
        mName = name;
    }

    /**
     *
     * This method is used to delete
     * the section data from SQL
     * using the SectionDBHelper
     *
     * @param context To know from which state of the object the code is called from
     */
    public void deleteSection(Context context){

        SectionDBHelper sectionDBHelper = new SectionDBHelper(context);
        sectionDBHelper.delete(getID());

        
    }

    /**
     *
     * This method is used to add
     * the section data from SQL
     * using the SectionDBHelper
     *
     * @param context To know from which state of the object the code is called from
     */
    public void addSection(Context context){

        SectionDBHelper sectionDBHelper = new SectionDBHelper(context);

        sectionDBHelper.insertSection(this, mUID);
    }


    public boolean isAdmin(){

        return this.getUID().equals(FirebaseAuth.getInstance().getUid());
    }

    /**
     *
     * This method is used the get the
     * tasks associated with the section
     * in the database using the SectionDBHelper and
     * store it in this model and return the the tasks
     *
     * @param context To know from which state of the object the code is called from
     * @return List<Task> This return a list of tasks associated with this
     * section id
     */
    public List<Task> getTaskList(Context context) {

        TaskDBHelper taskDBHelper = new TaskDBHelper(context);

        return taskDBHelper.getAllTask(getID());
    }

    /**
     * Upload the section info to firebase
     * when internet connectivity is present
     *
     * It will be done in the background
     *
     * @param UID User ID used to associate the section with the user
     * @param ID To get the used in database as the key for firebase
     * @param owner to be used to observe my upload
     */
    public void executeFirebaseSectionUpload(String UID,String ID, LifecycleOwner owner){

        Log.d(TAG, "executeFirebaseSectionUpload(): Preparing the upload");

        //Setting condition
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();




        //Adding data which will be received from the worker
        @SuppressLint("RestrictedApi") Data firebaseSectionData = new Data.Builder()
                .putString(COLUMN_SECTION_ID, ID)
                .putString(COLUMN_USERID, UID)
                .putString(COLUMN_NAME, getName())
                .putInt(COLUMN_COLOR, getBackgroundColor())
                .putInt(COLUMN_IMAGE, getBmiIcon())
                .putInt(COLUMN_POSITION, getPosition())
                .build();

        //Create the request
        OneTimeWorkRequest uploadTask = new OneTimeWorkRequest.
                Builder(UploadSectionWorker.class)
                .setConstraints(constraints)
                .setInputData(firebaseSectionData)
                .build();

        //Enqueue the request
        WorkManager.getInstance().enqueue(uploadTask);


        Log.d(TAG, "executeFirebaseSectionUpload(): Put in queue");

        WorkManager.getInstance().getWorkInfoByIdLiveData(uploadTask.getId())
                .observe(owner, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        Log.d(TAG, "Section upload state: " + workInfo.getState());
                    }
                });

    }


    /**
     * Delete the section from firebase
     * when internet connectivity is present
     *
     * It will be done in the background
     *
     * @param owner to be used to observe my upload
     */
    public void executeFirebaseSectionDelete(LifecycleOwner owner){

        Log.d(TAG, "executeFirebaseSectionDelete(): Preparing to delete, on ID: " + ID);

        //Setting condition
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        //Adding data which will be received from the worker
        @SuppressLint("RestrictedApi") Data firebaseSectionData = new Data.Builder()
                .putString("ID", ID)
                .putString("UID", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .putBoolean("Admin", this.isAdmin())
                .build();

        //Create the request
        OneTimeWorkRequest deleteTask = new OneTimeWorkRequest.
                Builder(DeleteSectionWorker.class)
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
                        Log.d(TAG, "Section Delete state: " + workInfo.getState());
                    }
                });


    }

    public List<Check> getCheckList(Context context){

        CheckDBHelper checkDBHelper = new CheckDBHelper(context);

        return checkDBHelper.getAllCheck(ID);


    }

    @NonNull
    @Override
    public String toString() {
        return "Name: " + getName() + ",\tColor: " + getBackgroundColor() + ",\tImage: " + getBmiIcon() + " id: " + getID();
    }




}
