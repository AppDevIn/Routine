package com.mad.p03.np2020.routine.Task.model;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class DeleteTeamWorker extends Worker {
    public DeleteTeamWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        String emailDel = getInputData().getString("email");
        String sectionID = getInputData().getString("sectionID");

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Section").child(sectionID).child("Team");

        mDatabase.orderByValue().equalTo(emailDel).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot ds:
                        dataSnapshot.getChildren()) {
                    if(ds.getValue().equals(emailDel)){
                        mDatabase.child(ds.getKey()).removeValue();
                        mDatabase.removeEventListener(this);
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
