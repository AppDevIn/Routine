package com.mad.p03.np2020.routine.background;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mad.p03.np2020.routine.Class.Section;
import com.mad.p03.np2020.routine.Class.Task;
import com.mad.p03.np2020.routine.database.SectionDBHelper;
import com.mad.p03.np2020.routine.database.TaskDBHelper;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class GetTaskSectionWorker extends Worker {
    TaskDBHelper mTaskDBHelper;
    SectionDBHelper mSectionDBHelper;
    public GetTaskSectionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
         mTaskDBHelper = new TaskDBHelper(context);
         mSectionDBHelper = new SectionDBHelper(context);
    }

    @NonNull
    @Override
    public Result doWork() {

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("users").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("section");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    String name = singleSnapshot.child("name").getValue(String.class);
                    String id = singleSnapshot.child("id").getValue(String.class);
                    int icon = singleSnapshot.child("bmiIcon").getValue(Integer.class) == null ? 0 : singleSnapshot.child("bmiIcon").getValue(Integer.class);
                    int color = singleSnapshot.child("backgroundColor").getValue(Integer.class) == null ? 0 : singleSnapshot.child("backgroundColor").getValue(Integer.class);

                    mSectionDBHelper.insertSection(new Section(name,color, icon,id,0,UID), UID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference().child("task").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        taskRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot sectionKey : dataSnapshot.getChildren()) {

                    for (DataSnapshot task : sectionKey.getChildren()){

                        String name = task.child("name").getValue(String.class);
                        int position = task.child("position").getValue(Integer.class);
                        String sectionID = task.child("sectionID").getValue(String.class);
                        String ID = task.child("taskID").getValue(String.class);
                        boolean checked = task.child("checked").getValue(Boolean.class);
                        String notes = task.child("notes").getValue(String.class);;
                        String remindDate = task.child("remindDate").getValue(String.class);;

                        mTaskDBHelper.insertTask(new Task(name, position, sectionID, ID, checked, notes, remindDate));

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
