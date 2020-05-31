package com.mad.p03.np2020.routine.Class;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Parcelable;
import android.text.PrecomputedText;
import android.util.Base64;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.mad.p03.np2020.routine.Home;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.background.DeleteSectionWorker;
import com.mad.p03.np2020.routine.background.UploadSectionWorker;
import com.mad.p03.np2020.routine.database.SectionDBHelper;
import com.mad.p03.np2020.routine.database.TaskDBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
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

import static androidx.constraintlayout.widget.Constraints.TAG;

public class Section implements Serializable {

    //Declare the constants of the database
    public static final String TABLE_NAME = "section";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_SECTION_ID = "SectionID";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_USERID = "userId";
    public static final String COLUMN_COLOR = "COLOR";
    public static final String COLUMN_IMAGE = "image";

    // Create table SQL query
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_SECTION_ID + " TEXT,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_COLOR + " INTEGER,"
                    + COLUMN_IMAGE + " INTEGER,"
                    + COLUMN_USERID + " INTEGER,"
                    + "FOREIGN KEY (" + COLUMN_USERID + ") REFERENCES  " + User.TABLE_NAME + "(" + User.COLUMN_NAME_ID + "));";

    //Query to delete the table
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;



    //Declaring Constant


    //Declaring member variable
    private String mName;
    private List<Task> mTaskList = new ArrayList<>();
    private int mBackgroundColor;
    private int bmiIcon;
    private String ID;




    public Section(String name, int color, int iconResID) {
        this.mName = name;
        this.mBackgroundColor = color;
        this.bmiIcon = iconResID;
        setID(UUID.randomUUID().toString());
    }

    public Section(String name, int color, int iconResID, String ID) {
        this.mName = name;
        this.mBackgroundColor = color;
        this.bmiIcon = iconResID;
        this.ID = ID;
    }



    public static Section fromJSON(String json){

        int color = 0;
        String name = "";
        int image = 0;
        String id = "";
        try {
            //Make the string to object
            JSONObject jsonObject = new JSONObject(json);

            //Get the values from the object
            color = Integer.parseInt(jsonObject.getString("backgroundColor"));
            name = jsonObject.getString("name");
            image = Integer.parseInt(jsonObject.getString("bmiIcon"));
            id = jsonObject.getString("id");

            //Return back the object
            return new Section(name, color, image, id);


        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "fromJSON: ", e);
        }



        return null;


    }

    public static Section fromCursor(Cursor cursor){

        return new Section(
                cursor.getString(cursor.getColumnIndex(Section.COLUMN_NAME)),
                cursor.getInt(cursor.getColumnIndex(Section.COLUMN_COLOR)),
                cursor.getInt(cursor.getColumnIndexOrThrow(Section.COLUMN_IMAGE)),
                cursor.getString(cursor.getColumnIndexOrThrow(Section.COLUMN_SECTION_ID))
        );
    }

    public int getBmiIcon() {
        return bmiIcon;
    }


    public void getTaskDatabase(Context context) {

        TaskDBHelper taskDBHelper = new TaskDBHelper(context);

        this.mTaskList = taskDBHelper.getAllTask(getID());
    }

    public List<Task> getTaskList() {

        return mTaskList;
    }


    public String getName() {
        return mName;
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void deleteSection(Context context){

        SectionDBHelper sectionDBHelper = new SectionDBHelper(context);
        sectionDBHelper.delete(getID());
    }

    public String addSection(Context context, String UID){

        SectionDBHelper sectionDBHelper = new SectionDBHelper(context);

        return sectionDBHelper.insertSection(this, UID);
    }


    /**
     * Upload the section info to firebase
     * when internet connectivity is present
     *
     * It will be done in the background
     *
     * @param UID Used to associate the section with the user
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
                .putString("ID", ID)
                .putString("UID", UID)
                .putString("Name", getName())
                .putInt("Color", getBackgroundColor())
                .putInt("Image", getBmiIcon()) //TODO: Change after to image
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

    @NonNull
    @Override
    public String toString() {
        return "Name: " + getName() + ",\tColor: " + getBackgroundColor() + ",\tImage: " + getBmiIcon() + " id: " + getID(); //TODO: Add the images
    }




}
