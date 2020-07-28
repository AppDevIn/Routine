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

public class GetTeamWorker extends Worker {

    public GetTeamWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    public static TeamDataListener mTeamDataListener;
    public static void setOnTeamChangeListener(TeamDataListener teamDataListener){
        mTeamDataListener = teamDataListener;
    }

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
