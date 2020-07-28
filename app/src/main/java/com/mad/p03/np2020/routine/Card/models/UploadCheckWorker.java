package com.mad.p03.np2020.routine.Card.models;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.p03.np2020.routine.models.Check;
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



public class UploadCheckWorker extends Worker {

    public UploadCheckWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
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

        String Name =  getInputData().getString(Check.COLUMN_NAME);
        String ID =  getInputData().getString(Check.COLUMN_Check_ID);
        String taskID =  getInputData().getString(Check.COLUMN_TaskID);
        int position=  getInputData().getInt(Check.COLUMN_POSITION, 0);
        boolean isCheck =  getInputData().getBoolean(Check.COLUMN_CHECKED, false);

        //Getting a database reference to Users
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Check").child(String.valueOf(ID));

        //Setting data into the user portion
//        mDatabase.child(Check.COLUMN_TaskID).setValue(taskID);
//        mDatabase.child(Check.COLUMN_NAME).setValue(Name);
//        mDatabase.child(Check.COLUMN_Check_ID).setValue(ID);
//        mDatabase.child(Check.COLUMN_CHECKED).setValue(isCheck);

        CheckFirebase checkFirebase = new CheckFirebase(taskID, isCheck, Name, ID);

        mDatabase.setValue(new Check(Name, ID, isCheck ? "1":"0", 0, taskID));

        return Result.success();
    }


}


class CheckFirebase{
    String TaskID;
    Boolean Checked;
    String Name;
    String CheckID;

    public CheckFirebase() {
    }

    public CheckFirebase(String taskID, Boolean checked, String name, String checkID) {
        TaskID = taskID;
        Checked = checked;
        Name = name;
        CheckID = checkID;
    }
}