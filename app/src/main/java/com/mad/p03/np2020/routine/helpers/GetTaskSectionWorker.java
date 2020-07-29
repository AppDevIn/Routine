package com.mad.p03.np2020.routine.helpers;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mad.p03.np2020.routine.DAL.CheckDBHelper;
import com.mad.p03.np2020.routine.models.Check;
import com.mad.p03.np2020.routine.models.Section;
import com.mad.p03.np2020.routine.models.Task;
import com.mad.p03.np2020.routine.DAL.SectionDBHelper;
import com.mad.p03.np2020.routine.DAL.TaskDBHelper;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

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

    Dictionary taskList = new Hashtable();

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
        CheckSectionToDelete();
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
                .child("Section");

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Section");

        /*
            Listen to the data change in firebase
            This also does reads all the data again
        */
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildAdded(): " + dataSnapshot.getKey());

                String id = dataSnapshot.getKey();


                //Check if it exist in the database
                mDatabase.orderByKey().equalTo(id).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if(!mSectionDBHelper.hasID(id)) {
                            Section section = Section.fromDataSnapShot(dataSnapshot);
                            mSectionDBHelper.insertSection(section, section.getUID());
                            addTask(section.getID());

                        }else if(mSectionDBHelper.hasID(id)) {

                            Section section = Section.fromDataSnapShot(dataSnapshot);
                            Section sectionDataBase = mSectionDBHelper.getSection(id);
                            if (!section.equals(sectionDataBase)) {
                                Log.d(TAG, "onChildAdded(): This has been changed so updating......");
                                mSectionDBHelper.updateSection(section);
                            }
                        }

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if(mSectionDBHelper.hasID(id)){
                            Section section = Section.fromDataSnapShot(dataSnapshot);
                            Section sectionDataBase = mSectionDBHelper.getSection(id);
                            if (!section.equals(sectionDataBase)) {
                                Log.d(TAG, "onChildChanged(): This has been changed so updating......");
                                mSectionDBHelper.updateSection(section);
                            }
                        }

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


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
        DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference().child("Task");


        /*
            Listen to the data change in firebase
            This also does reads all the data again
        */
        taskRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildAdded(): " + dataSnapshot.getKey());

                String id = dataSnapshot.child("taskID").getValue(String.class);
                String sectionID = dataSnapshot.child("sectionID").getValue(String.class);



                //Check if the task exist in the database
                if(!mTaskDBHelper.hasID(id) && mSectionDBHelper.hasID(sectionID)) {
                    Task task = Task.fromDataSnapShot(dataSnapshot);
                    mTaskDBHelper.insertTask(task);
                }
                else if (mSectionDBHelper.hasID(sectionID)){ //If doesn't exist it means it needs to be updated
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
                String sectionID = dataSnapshot.child("sectionID").getValue(String.class);



                if(mSectionDBHelper.hasID(sectionID)) {
                    Task task = Task.fromDataSnapShot(dataSnapshot);
                    Task taskDataBase = mTaskDBHelper.getTask(id);
                    //If exist in the database than update
                    if (mTaskDBHelper.hasID(id) && !task.equals(taskDataBase)) {
                        mTaskDBHelper.update(task);
                    }
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
        DatabaseReference checkRef = FirebaseDatabase.getInstance().getReference().child("Check");


        /*
            Listen to the data change in firebase
            This also does reads all the data again
        */
        checkRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                String id = dataSnapshot.getKey();
                String taskID = dataSnapshot.child("taskID").getValue(String.class);
                Log.d(TAG, "onChildAdded(), startListenCheck(): Check ID to be " + id);

                //Check if the task exist in the database
                if(!mCheckDBHelper.hasID(id) && mTaskDBHelper.hasID(taskID)) {
                    Check check = Check.fromDataSnapShot(dataSnapshot);
                    mCheckDBHelper.insertCheck(check);
                }
                else if(mTaskDBHelper.hasID(taskID)){ //If doesn't exist it means it needs to be updated
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
                String taskID = dataSnapshot.child("taskID").getValue(String.class);

                Check check = Check.fromDataSnapShot(dataSnapshot);
                Check checkDataBase = mCheckDBHelper.getCheck(id);
                //If exist in the database than update
                if((checkDataBase != null)&&(mCheckDBHelper.hasID(id) && !check.equals(checkDataBase)) && mTaskDBHelper.hasID(taskID)) {
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

    private void addTask(String sectionID){
        DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference().child("Task");

        taskRef.orderByChild("sectionID").equalTo(sectionID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot:
                        dataSnapshot.getChildren()) {
                    String id = snapshot.child("taskID").getValue(String.class);

                    //Check if the task exist in the database
                    if(!mTaskDBHelper.hasID(id) && mSectionDBHelper.hasID(sectionID)) {
                        Task task = Task.fromDataSnapShot(snapshot);
                        mTaskDBHelper.insertTask(task);
                        //Get all the checks under this task
                        addCheck(task.getTaskID());
                    }
                    else if (mSectionDBHelper.hasID(sectionID) && mTaskDBHelper.hasID(id)){ //If doesn't exist it means it needs to be updated
                        Task task = Task.fromDataSnapShot(snapshot);
                        Task taskDataBase = mTaskDBHelper.getTask(id);
                        if(!task.equals(taskDataBase)){
                            Log.d(TAG, "onChildAdded(): This has been changed so updating......");
                            mTaskDBHelper.update(task);
                        }
                    }
                }


                //Remove value listener
                taskRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addCheck(String TaskID){
        DatabaseReference checkRef = FirebaseDatabase.getInstance().getReference().child("Check");

        checkRef.orderByChild("taskID").equalTo(TaskID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Loop trough the children
                for (DataSnapshot snapshot:
                        dataSnapshot.getChildren()) {

                    String id = snapshot.getKey();

                    //Check if the task exist in the database
                    if(!mCheckDBHelper.hasID(id) && mTaskDBHelper.hasID(TaskID)) {
                        Check check = Check.fromDataSnapShot(snapshot);
                        mCheckDBHelper.insertCheck(check);
                    }
                    else if(mTaskDBHelper.hasID(TaskID)){ //If doesn't exist it means it needs to be updated
                        Check check = Check.fromDataSnapShot(snapshot);
                        Check checkDataBase = mCheckDBHelper.getCheck(id);
                        if(!check.equals(checkDataBase)){
                            Log.d(TAG, "onChildAdded(): This has been changed so updating......");
                            mCheckDBHelper.update(check);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void CheckSectionToDelete(){

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("Section");
        List<Section> sectionList = mSectionDBHelper.getAllSections("");

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                //Check which has a child
                for (int i = 0; i < sectionList.size(); i++) {
                    //Remove the ones with the child
                    if (!dataSnapshot.child(sectionList.get(i).getID()).exists()) {
                        Log.d(TAG, "onDataChange: Deleting section " + sectionList.get(i).getName());
                        mSectionDBHelper.delete(sectionList.get(i).getID());
                    }else{
                        CheckTaskToDelete(sectionList.get(i).getID());
                    }
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void CheckTaskToDelete(String sectionID){

        DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference().child("Task");
        List<Task> taskList = mTaskDBHelper.getAllTask(sectionID);

        taskRef.orderByChild("sectionID").equalTo(sectionID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                //Check which has a child
                for (int i = 0; i < taskList.size(); i++) {
                    //Remove the ones with the child

                    if (!dataSnapshot.child(taskList.get(i).getTaskID()).exists()) {
                        Log.d(TAG, "onDataChange: Deleting task " + taskList.get(i).getName());
                        mTaskDBHelper.delete(taskList.get(i).getTaskID());
                    }else{
                        CheckCheckToDelete(taskList.get(i).getTaskID());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void CheckCheckToDelete(String taskID){

        DatabaseReference checkRef = FirebaseDatabase.getInstance().getReference().child("Check");
        List<Check> checkList = mCheckDBHelper.getAllCheck(taskID);

        checkRef.orderByChild("taskID").equalTo(taskID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Check which has a child
                for (int i = 0; i < checkList.size(); i++) {
                    //Remove the ones with the child

                    if (!dataSnapshot.child(checkList.get(i).getID()).exists()) {
                        Log.d(TAG, "onDataChange: Deleting task " + checkList.get(i).getName());
                        mCheckDBHelper.delete(checkList.get(i).getID());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
