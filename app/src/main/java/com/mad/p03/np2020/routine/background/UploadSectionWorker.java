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
        String name =  getInputData().getString(Section.COLUMN_NAME);
        String UID = getInputData().getString(Section.COLUMN_USERID) ;
        String id = getInputData().getString(Section.COLUMN_SECTION_ID) ;
        int image = getInputData().getInt(Section.COLUMN_IMAGE, 0) ;
        int color = getInputData().getInt(Section.COLUMN_COLOR, 0);
        int position = getInputData().getInt(Section.COLUMN_POSITION, 0);


        //Getting a database reference to Users
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(UID).child("section").child(String.valueOf(id));

        //Setting value using object
        mDatabase.setValue(new Section(name, color, image, id, position, UID));

        Log.d("Register", "doInBackground(): Name, Email and DOB are uploaded");

        return Result.success();
    }
}
