package com.mad.p03.np2020.routine.background;

import android.content.Context;
import android.util.Log;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        final String UID = getInputData().getString("UID");
        final Boolean isAdmin = getInputData().getBoolean("Admin", false);

        Log.d("HomeDelete", "doWork: " + UID);

        if(isAdmin){
            FirebaseDatabase.getInstance().getReference().child("Section").child(String.valueOf(ID)).removeValue();
        }else {

            FirebaseDatabase.getInstance().getReference().child("users").child(UID).child("Section").child(String.valueOf(ID)).removeValue();

            FirebaseDatabase.getInstance().getReference().child("Section").child(String.valueOf(ID))
                    .child("Team")
                    .orderByValue().equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot:
                                 dataSnapshot.getChildren()) {
                                snapshot.getRef().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });




        }

        return Result.success();
    }
}
