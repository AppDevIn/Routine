package com.mad.p03.np2020.routine.background;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.p03.np2020.routine.Class.Section;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class UploadSectionWorker extends Worker {

    public UploadSectionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        //Get the input and create the user object
        String Name =  getInputData().getString("Name");
        int Color = getInputData().getInt("Color", 0);
        String Image = getInputData().getString("Image") ;
        String UID = getInputData().getString("UID") ;
        long id = getInputData().getLong("ID", 0) ;

        //Getting a database reference to Users
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(UID).child("section").child(String.valueOf(id));

        //Setting value using objeect
        mDatabase.setValue(new Section(Name, Color));

        Log.d("Register", "doInBackground(): Name, Email and DOB are uploaded");

        return Result.success();
    }
}
