package com.mad.p03.np2020.routine.Class;

import android.graphics.Color;
import android.text.PrecomputedText;
import android.util.Log;

import com.mad.p03.np2020.routine.R;

import org.json.JSONException;
import org.json.JSONObject;

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
                    + COLUMN_IMAGE + " TEXT,"
                    + COLUMN_USERID + " INTEGER,"
                    + "FOREIGN KEY (" + COLUMN_USERID + ") REFERENCES  " + User.TABLE_NAME + "(" + User.COLUMN_NAME_ID + "));";

    //Query to delete the table
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;



    //Declaring Constant


    //Declaring memebr variable
    private String mName;
    private List<Task> mTaskList;
    private int mBackgroundColor;




    public Section(String name, int color) {
        this.mName = name;
        this.mBackgroundColor = color;

    }


    public static Section fromJSON(String json){


        int color = 0;
        String name = "";
        String image = "";
        try {
            //Make the string to object
            JSONObject jsonObject = new JSONObject(json);

            //Get the values from the object
            color = Integer.parseInt(jsonObject.getString("backgroundColor"));
            name = jsonObject.getString("name");
//            image = jsonObject.getString("Image");

            //Return back the object
            return new Section(name, color);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "onChildAdded: " + color);


        return null;


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
        return "Name: " + getName() + ",\tColor: " + getBackgroundColor(); //TODO: Add the images
    }
}
