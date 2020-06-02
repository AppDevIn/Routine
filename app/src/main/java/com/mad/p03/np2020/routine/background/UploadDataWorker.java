package com.mad.p03.np2020.routine.background;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;

import androidx.work.Worker;
import androidx.work.WorkerParameters;


/**
 *
 * This is to upload the data to user data from
 * the background which implements the Worker.
 *
 * @author Jeyavishnu
 * @since 02-06-2020
 *
 */
public class UploadDataWorker extends Worker {

    public UploadDataWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    /**
     *
     * To do background processing, to add the user data
     * in firebase using the user object and UID as the key
     *
     * Data that need to be sent over are Name, Email, DOB and UID.
     * You can use {@code OneTimeWorkRequest.setInputData(data)} to send and
     * {@code getInputData()} to retrieve it.
     *
     *
     * @return Result This is tell what happen to the background work
     */

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
