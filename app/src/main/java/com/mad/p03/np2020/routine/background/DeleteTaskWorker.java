package com.mad.p03.np2020.routine.background;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.p03.np2020.routine.Class.Section;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class DeleteTaskWorker extends Worker {

    public DeleteTaskWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        final String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String ID = getInputData().getString("ID") ;
        String sectionID =  getInputData().getString(Section.COLUMN_SECTION_ID);

        Log.d("TaskDelete", "doWork: " + ID);

        FirebaseDatabase.getInstance().getReference().child("task").child(UID).child(sectionID).child(ID).removeValue();


        return Result.success();
    }
}
