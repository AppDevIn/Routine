package com.mad.p03.np2020.routine.background;

import android.content.Context;
import android.util.Log;


import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 *
 * This is to delete the data in firebase Section node from
 * the background which implements the Worker.
 *
 * @author Jeyavishnu
 * @since 02-06-2020
 *
 */
public class DeleteSectionWorker extends Worker {

    public DeleteSectionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }


    /**
     *
     * To do background processing, to delete this data in
     * firebase using the ID of the section and removing in
     * firebase.
     *
     * Data that need to be sent over are ID and UID.
     * You can use {@code OneTimeWorkRequest.setInputData(data)} to send and
     * {@code getInputData()} to retrieve it.
     *
     *
     * @return Result This is tell what happen to the background work
     */
    @NonNull
    @Override
    public Result doWork() {

        final String ID = getInputData().getString("ID");
        final String UID = getInputData().getString("UID") ;

        Log.d("HomeDelete", "doWork: " + UID);

        FirebaseDatabase.getInstance().getReference().child("users").child(UID).child("section").child(String.valueOf(ID)).removeValue();


        return Result.success();
    }
}
