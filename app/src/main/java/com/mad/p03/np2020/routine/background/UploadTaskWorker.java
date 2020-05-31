package com.mad.p03.np2020.routine.background;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.p03.np2020.routine.Class.Section;
import com.mad.p03.np2020.routine.Class.Task;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class UploadTaskWorker extends Worker {

    public UploadTaskWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        String Name =  getInputData().getString(Task.COLUMN_NAME);
        String ID =  getInputData().getString(Task.COLUMN_TASK_ID);

        //Getting a database reference to Users
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("task").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(String.valueOf(ID));

        //Setting value using object
        mDatabase.setValue(new Task(Name));

        Log.d("Register", "doInBackground(): Name, Email and DOB are uploaded");

        return Result.success();
    }
}
