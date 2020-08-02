package com.mad.p03.np2020.routine.Task.model;

import android.content.Context;
import android.os.Build;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 *
 * This is to get the data in firebase Section node from
 * the background which implements the Worker. This will retrieve
 * information on the emails that the section is shared with
 *
 * @author Jeyavishnu
 * @since 02-08-2020
 *
 */
public class GetTeamWorker extends Worker {

    public GetTeamWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    public static TeamDataListener mTeamDataListener;
    public static void setOnTeamChangeListener(TeamDataListener teamDataListener){
        mTeamDataListener = teamDataListener;
    }

    /**
     *
     * To do background processing, to get the tea, data
     * in firebase
     *
     * Data that need to be sent over is SectionID.
     * You can use {@code OneTimeWorkRequest.setInputData(data)} to send and
     * {@code getInputData()} to retrieve it.
     *
     * @return Result This is tell what happen to the background work
     */
    @NonNull
    @Override
    public Result doWork() {

        String sectionID = getInputData().getString("sectionID");

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Section").child(sectionID).child("Team");

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(mTeamDataListener != null){
                    for (DataSnapshot ds:
                    dataSnapshot.getChildren()) {
                        mTeamDataListener.onDataAdd(ds.getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return Result.success();
    }
}
