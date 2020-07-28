package com.mad.p03.np2020.routine.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.mad.p03.np2020.routine.Task.model.DeleteTeamWorker;
import com.mad.p03.np2020.routine.Task.model.GetTeamWorker;
import com.mad.p03.np2020.routine.Task.model.TeamDataListener;
import com.mad.p03.np2020.routine.Task.model.UploadTaskWorker;
import com.mad.p03.np2020.routine.Task.model.UploadTeamWorker;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class Team  {

    final static private String TAG = "Team";

    /**The table name of this class in SQL*/
    public static final String TABLE_NAME = "Team";

    /**Used as the foreign key for this table*/
    public static final String COLUMN_SectionID = "SectionID";
    /**The email*/
    public static final String COLUMN_EMAIL = "Email";


    /**
     * The query needed to create a sql database
     * for the check
     */
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + "("
                    + "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_EMAIL + " TEXT,"
                    + COLUMN_SectionID + " TEXT ,"
                    + "FOREIGN KEY (" + COLUMN_SectionID + ") REFERENCES  " + Section.TABLE_NAME + "(" + Section.COLUMN_SECTION_ID + "));";

    /**
     * The query needed to delete SQL table check from the database
     */
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    String sectionID;
    List<String> email = new ArrayList<>();

    public Team() {
        email = new ArrayList<>();
    }


    public String getSectionID() {
        return sectionID;
    }

    public void setSectionID(String sectionID) {
        this.sectionID = sectionID;
    }

    public List<String> getEmail() {
        return email;
    }

    public void setEmail(List<String> email) {
        this.email = email;
    }

    public void addEmail(String email){
        this.email.add(email);
    }

    public void excuteFirebaseUpload(String email){

        //Setting condition
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();



        //Adding data which will be received from the worker
        @SuppressLint("RestrictedApi") Data firebaseTeamData = new Data.Builder()
                .putString("sectionID",this.sectionID)
                .putString("email", email)
                .build();


        //Create the request
        OneTimeWorkRequest uploadTask = new OneTimeWorkRequest.
                Builder(UploadTeamWorker.class)
                .setConstraints(constraints)
                .setInputData(firebaseTeamData)
                .build();

        //Enqueue the request
        WorkManager.getInstance().enqueue(uploadTask);


        Log.d(TAG, "executeFirebaseUpload(): Put in queue");

    }

    public void excuteEmailDeleteFirebase(int position){

        //Setting condition
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();



        //Adding data which will be received from the worker
        @SuppressLint("RestrictedApi") Data firebaseTeamData = new Data.Builder()
                .putString("sectionID",this.sectionID)
                .putString("email", this.getEmail().get(position))
                .build();


        //Create the request
        OneTimeWorkRequest uploadTask = new OneTimeWorkRequest.
                Builder(DeleteTeamWorker.class)
                .setConstraints(constraints)
                .setInputData(firebaseTeamData)
                .build();

        //Enqueue the request
        WorkManager.getInstance().enqueue(uploadTask);


        Log.d(TAG, "executeFirebaseUpload(): Put in queue");

    }

    public void getTeamFirebase(TeamDataListener teamDataListener){

        //Setting condition
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        GetTeamWorker.setOnTeamChangeListener(teamDataListener);

        //Adding data which will be received from the worker
        @SuppressLint("RestrictedApi") Data firebaseTeamData = new Data.Builder()
                .putString("sectionID",this.sectionID)
                .build();


        //Create the request
        OneTimeWorkRequest uploadTask = new OneTimeWorkRequest.
                Builder(GetTeamWorker.class)
                .setConstraints(constraints)
                .setInputData(firebaseTeamData)
                .build();

        //Enqueue the request
        WorkManager.getInstance().enqueue(uploadTask);


        Log.d(TAG, "executeFirebaseUpload(): Put in queue");



    }

}
