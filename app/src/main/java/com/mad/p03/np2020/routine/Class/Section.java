package com.mad.p03.np2020.routine.Class;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.PrecomputedText;
import android.util.Base64;
import android.util.Log;

import com.mad.p03.np2020.routine.Home;
import com.mad.p03.np2020.routine.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.List;

import androidx.annotation.NonNull;

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
        try {
            //Make the string to object
            JSONObject jsonObject = new JSONObject(json);

            //Get the values from the object
            color = Integer.parseInt(jsonObject.getString("backgroundColor"));
            name = jsonObject.getString("name");
            image = Integer.parseInt(jsonObject.getString("bmiIcon"));

            //Return back the object
            return new Section(name, color, image);


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




    @NonNull
    @Override
    public String toString() {
        return "Name: " + getName() + ",\tColor: " + getBackgroundColor() + ",\tImage: " + getBmiIcon(); //TODO: Add the images
    }


}
