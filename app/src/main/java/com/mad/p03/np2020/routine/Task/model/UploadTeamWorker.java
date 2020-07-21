package com.mad.p03.np2020.routine.Task.model;

import android.content.Context;
import android.os.Build;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.p03.np2020.routine.models.Team;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class UploadTeamWorker extends Worker {



    public UploadTeamWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public Result doWork() {

        String email =  getInputData().getString("email");
        String sectionID = getInputData().getString("sectionID");

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Section").child(sectionID).child("Team");


        mDatabase.child(mDatabase.push().getKey()).setValue(email);

        return Result.success();
    }
}
