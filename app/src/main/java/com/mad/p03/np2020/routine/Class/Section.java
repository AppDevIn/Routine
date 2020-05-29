package com.mad.p03.np2020.routine.Class;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.PrecomputedText;
import android.util.Base64;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.mad.p03.np2020.routine.Home;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.background.DeleteSectionWorker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class Section {

    //Declare the constants of the database
    public static final String TABLE_NAME = "section";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_USERID = "userId";
    public static final String COLUMN_COLOR = "COLOR";
    public static final String COLUMN_IMAGE = "image";

    // Create table SQL query
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_COLOR + " INTEGER,"
                    + COLUMN_IMAGE + " INTERGER,"
                    + COLUMN_USERID + " INTEGER,"
                    + "FOREIGN KEY (" + COLUMN_USERID + ") REFERENCES  " + User.TABLE_NAME + "(" + User.COLUMN_NAME_ID + "));";

    //Query to delete the table
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;



    //Declaring Constant


    //Declaring member variable
    private String mName;
    private List<Task> mTaskList;
    private int mBackgroundColor;
    private int bmiIcon;
    private Context mContext;
    private long ID;




    public Section(String name, int color, int iconResID) {
        this.mName = name;
        this.mBackgroundColor = color;
        this.bmiIcon = iconResID;

    }

    public Section(String name, int color, int iconResID, long ID) {
        this.mName = name;
        this.mBackgroundColor = color;
        this.bmiIcon = iconResID;
        this.ID = ID;
    }



    public static Section fromJSON(String json){

        int color = 0;
        String name = "";
        int image = 0;
        long id = 0;
        try {
            //Make the string to object
            JSONObject jsonObject = new JSONObject(json);

            //Get the values from the object
            color = Integer.parseInt(jsonObject.getString("backgroundColor"));
            name = jsonObject.getString("name");
            image = Integer.parseInt(jsonObject.getString("bmiIcon"));
            id = Integer.parseInt(jsonObject.getString("id"));

            //Return back the object
            return new Section(name, color, image, id);


        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "fromJSON: ", e);
        }
        Log.d(TAG, "onChildAdded: " + color);


        return null;


    }

    public static Section fromCursor(Cursor cursor){

        return new Section(
                cursor.getString(cursor.getColumnIndex(Section.COLUMN_NAME)),
                cursor.getInt(cursor.getColumnIndex(Section.COLUMN_COLOR)),
                cursor.getInt(cursor.getColumnIndexOrThrow(Section.COLUMN_IMAGE)),
                cursor.getLong(cursor.getColumnIndexOrThrow(Section.COLUMN_ID))
        );
    }

    public int getBmiIcon() {
        return bmiIcon;
    }

    public void setBmiIcon(int bmiIcon) {
        this.bmiIcon = bmiIcon;
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

    public void setBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public void addTask(Task task){
        // TODO: Please upload any chnages to this class to the main branch`
        mTaskList.add(task);

    }
    
    public void rmTask(Task task){
        // TODO: Please upload any chnages to this class to the main branch`
        mTaskList.remove(task);
    }



    public void executeFirebaseSectionDelete(){

        Log.d(TAG, "executeFirebaseSectionDelete(): Preparing to delete, on ID: " + ID);

        //Setting condition
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        //Adding data which will be received from the worker
        @SuppressLint("RestrictedApi") Data firebaseSectionData = new Data.Builder()
                .putLong("ID", ID)
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

        
//        WorkManager.getInstance(mContext).getWorkInfoByIdLiveData(deleteTask.getId())
//                .observe(Home.this, new Observer<WorkInfo>() {
//                    @Override
//                    public void onChanged(WorkInfo workInfo) {
//                        Log.d(TAG, "Section upload state: " + workInfo.getState());
//                    }
//                });


    }

    @NonNull
    @Override
    public String toString() {
        return "Name: " + getName() + ",\tColor: " + getBackgroundColor() + ",\tImage: " + getBmiIcon() + " id: " + getID(); //TODO: Add the images
    }




}
