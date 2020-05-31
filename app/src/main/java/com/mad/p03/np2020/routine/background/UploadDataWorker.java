package com.mad.p03.np2020.routine.background;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.p03.np2020.routine.Class.User;

import java.util.ArrayList;

import androidx.annotation.NonNull;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class UploadDataWorker extends Worker {

    public UploadDataWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        //Get the input and create the user object
        String UID = getInputData().getString("ID");
        String Name =  getInputData().getString("Name");
        String Email = getInputData().getString("Email");
        String DOB = getInputData().getString("DOB") ;

        //Getting a database reference to Users
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(UID);

        //Setting data into the user portion
        mDatabase.child("Name").setValue(Name); //Setting the name
        mDatabase.child("Email").setValue(Email); //Setting the Email
        mDatabase.child("DOB").setValue(DOB); //Setting the DOB
        mDatabase.child("messagingToken").setValue(UID); //Adding message token

        Log.d("Register", "doInBackground(): Name, Email and DOB are uploaded");

        return Result.success();
    }


}
