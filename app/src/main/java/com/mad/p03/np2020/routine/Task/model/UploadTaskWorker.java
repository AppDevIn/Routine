package com.mad.p03.np2020.routine.Task.model;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.p03.np2020.routine.models.Section;
import com.mad.p03.np2020.routine.models.Task;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 *
 * This is to upload the data to Task data from
 * the background which implements the Worker.
 *
 * @author Jeyavishnu
 * @since 02-06-2020
 *
 */
public class UploadTaskWorker extends Worker {

    public UploadTaskWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    /**
     *
     * To do background processing, to add the task data
     * in firebase using the section object and UID, SectionID and ID as the key
     *
     * Data that need to be sent over are Name, ID and section ID
     * You can use {@code OneTimeWorkRequest.setInputData(data)} to send and
     * {@code getInputData()} to retrieve it.
     *
     *
     * @return Result This is tell what happen to the background work
     */
    @NonNull
    @Override
    public Result doWork() {

        String Name =  getInputData().getString(Task.COLUMN_NAME);
        String ID =  getInputData().getString(Task.COLUMN_TASK_ID);
        String sectionID =  getInputData().getString(Section.COLUMN_SECTION_ID);
        int position=  getInputData().getInt(Task.COLUMN_POSITION, 0);
        boolean isCheck =  getInputData().getBoolean(Task.COLUMN_CHECKED, false);

        //Getting a database reference to Users
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("task").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(sectionID).child(String.valueOf(ID));

        //Setting value using object
        mDatabase.setValue(new Task(Name,0,sectionID,ID,isCheck));

        Log.d("Register", "doInBackground(): Name, Email and DOB are uploaded");

        return Result.success();
    }
}
