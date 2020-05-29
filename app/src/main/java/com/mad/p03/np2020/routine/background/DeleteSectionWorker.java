package com.mad.p03.np2020.routine.background;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class DeleteSectionWorker extends Worker {

    public DeleteSectionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        final long ID = getInputData().getLong("ID", 0);
        String UID = getInputData().getString("UID") ;

        Log.d("HomeDelete", "doWork: " + UID);

        FirebaseDatabase.getInstance().getReference().child("users").child(UID).child("section").child(String.valueOf(ID)).removeValue();


        return Result.success();
    }
}
