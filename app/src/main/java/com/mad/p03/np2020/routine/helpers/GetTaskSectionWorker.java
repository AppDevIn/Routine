package com.mad.p03.np2020.routine.helpers;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.p03.np2020.routine.DAL.CheckDBHelper;
import com.mad.p03.np2020.routine.models.Check;
import com.mad.p03.np2020.routine.models.Section;
import com.mad.p03.np2020.routine.models.Task;
import com.mad.p03.np2020.routine.DAL.SectionDBHelper;
import com.mad.p03.np2020.routine.DAL.TaskDBHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 *
 * This is to listen to changes and addition task and section
 *
 *
 * @author Jeyavishnu
 * @since 08-06-2020
 */
public class GetTaskSectionWorker extends Worker {
    TaskDBHelper mTaskDBHelper;
    SectionDBHelper mSectionDBHelper;
    CheckDBHelper mCheckDBHelper;

    final private static String TAG = "TaskSectionListener";

    public GetTaskSectionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
         mTaskDBHelper = new TaskDBHelper(context);
         mSectionDBHelper = new SectionDBHelper(context);
         mCheckDBHelper = new CheckDBHelper(context);
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
        startListenCheck();

        return Result.success();
    }



    private void listenOneTime(){

    }

    /**
     * This will add all update, delete and see the is new data if there is it will
     * be updated
     */
    private void startListenSection(){

        //Get the database reference got section
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("users").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("section");

        /*
            Listen to the data change in firebase
            This also does reads all the data again
        */
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildAdded(): " + dataSnapshot.getKey());

                String id = dataSnapshot.child("id").getValue(String.class);

                //Check if it exist in the database
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

        //Get the database reference for task
        DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference().child("task").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        /*
            Listen to the data change in firebase
            This also does reads all the data again
        */
        taskRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildAdded(): " + dataSnapshot.getKey());

                String id = dataSnapshot.child("taskID").getValue(String.class);

                //Check if the task exist in the database
                if(!mTaskDBHelper.hasID(id)) {
                    Task task = Task.fromDataSnapShot(dataSnapshot);
                    mTaskDBHelper.insertTask(task);
                }
                else{ //If doesn't exist it means it needs to be updated
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

                //If exist in the database than update
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



    /**
     * This will add all update, delete and see the is new data if there is it will
     * be updated
     */
    private void startListenCheck(){

        //Get the database reference for task
        DatabaseReference checkRef = FirebaseDatabase.getInstance().getReference().child("check").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        /*
            Listen to the data change in firebase
            This also does reads all the data again
        */
        checkRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String id = dataSnapshot.getKey();

                Log.d(TAG, "onChildAdded(), startListenCheck(): Check ID to be " + id);

                //Check if the task exist in the database
                if(!mCheckDBHelper.hasID(id)) {
                    Check check = Check.fromDataSnapShot(dataSnapshot);
                    mCheckDBHelper.insertCheck(check);
                }
                else{ //If doesn't exist it means it needs to be updated
                    Check check = Check.fromDataSnapShot(dataSnapshot);
                    Check checkDataBase = mCheckDBHelper.getCheck(id);
                    if(!check.equals(checkDataBase)){
                        Log.d(TAG, "onChildAdded(): This has been changed so updating......");
                        mCheckDBHelper.update(check);
                    }
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildChanged(): " + dataSnapshot.getKey());
                Log.d(TAG, "onChildChanged(): " + dataSnapshot.getValue());

                String id = dataSnapshot.getKey();

                //If exist in the database than update
                if(mCheckDBHelper.hasID(id)) {
                    Check check = Check.fromDataSnapShot(dataSnapshot);
                    mCheckDBHelper.update(check);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                Log.d(TAG, "onChildRemoved(): " + dataSnapshot.getKey());
                Log.d(TAG, "onChildRemoved(): " + dataSnapshot.getValue());

                String id = dataSnapshot.getKey();

                //If data exist than delete
                if(mCheckDBHelper.hasID(id)){
                    mCheckDBHelper.delete(id);
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
