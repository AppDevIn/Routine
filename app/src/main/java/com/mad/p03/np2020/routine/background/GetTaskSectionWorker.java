package com.mad.p03.np2020.routine.background;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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
import androidx.annotation.Nullable;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class GetTaskSectionWorker extends Worker {
    TaskDBHelper mTaskDBHelper;
    SectionDBHelper mSectionDBHelper;

    final private static String TAG = "TaskSectionListener";

    public GetTaskSectionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
         mTaskDBHelper = new TaskDBHelper(context);
         mSectionDBHelper = new SectionDBHelper(context);
    }
    /**
     *
     * To do background processing, to listen to section and task data
     * in firebase
     *
     * @return Result This is tell what happen to the background work
     */

    @NonNull
    @Override
    public Result doWork() {

        listenOneTime();
        startListenSection();
        startListenTask();

        return Result.success();
    }



    private void listenOneTime(){



    }

    /**
     * This will add all update, delete and see the is new data if there is it will
     * be updated
     */
    private void startListenSection(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("users").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("section");

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildAdded(): " + dataSnapshot.getKey());

                String id = dataSnapshot.child("id").getValue(String.class);

                if(!mSectionDBHelper.hasID(id)) {
                    Section section = Section.fromDataSnapShot(dataSnapshot);
                    mSectionDBHelper.insertSection(section, section.getUID());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildChanged(): " + dataSnapshot.getKey());
                Log.d(TAG, "onChildChanged(): " + dataSnapshot.getValue());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                Log.d(TAG, "onChildRemoved(): " + dataSnapshot.getKey());
                Log.d(TAG, "onChildRemoved(): " + dataSnapshot.getValue());

                String id = dataSnapshot.getKey();

                //If data exist than delete
                if(mSectionDBHelper.hasID(id)){
                    mSectionDBHelper.delete(id);
                    Log.d(TAG, "delete: " + id + " Has been deleted");
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * This will add all update, delete and see the is new data if there is it will
     * be updated
     */
    private void startListenTask(){

        DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference().child("task").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        taskRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildAdded(): " + dataSnapshot.getKey());

                String id = dataSnapshot.child("taskID").getValue(String.class);

                if(!mTaskDBHelper.hasID(id)) {
                    Task task = Task.fromDataSnapShot(dataSnapshot);
                    mTaskDBHelper.insertTask(task);
                } else{
                    Task task = Task.fromDataSnapShot(dataSnapshot);
                    Task taskDataBase = mTaskDBHelper.getTask(id);
                    if(!task.equals(taskDataBase)){
                        Log.d(TAG, "onChildAdded(): This has been changed so updating......");
                        mTaskDBHelper.update(task);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildChanged(): " + dataSnapshot.getKey());
                Log.d(TAG, "onChildChanged(): " + dataSnapshot.getValue());

                String id = dataSnapshot.child("taskID").getValue(String.class);

                if(mTaskDBHelper.hasID(id)) {
                    Task task = Task.fromDataSnapShot(dataSnapshot);
                    mTaskDBHelper.update(task);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                Log.d(TAG, "onChildRemoved(): " + dataSnapshot.getKey());
                Log.d(TAG, "onChildRemoved(): " + dataSnapshot.getValue());

                String id = dataSnapshot.getKey();

                //If data exist than delete
                if(mTaskDBHelper.hasID(id)){
                    mTaskDBHelper.delete(id);
                    Log.d(TAG, "delete: " + id + " Has been deleted");
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
