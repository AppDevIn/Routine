package com.mad.p03.np2020.routine.background;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mad.p03.np2020.routine.Class.Section;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


public class FirebaseSectionWorker extends Worker {

    final String TAG = "FirebaseBackground";

    private DatabaseReference mDatabase;


    public FirebaseSectionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    public void onStopped() {
        super.onStopped();

        Log.d(TAG, "onStopped: Firebase listener stopped");

    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork(): Has started to run in the background");


        //Database reference
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child("V30jZctVgSPh00CVskSYiXNRezC2").child("section");


        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildAdded(): A child has been added to task: " + dataSnapshot);

                Section section = Section.fromJSON(Objects.requireNonNull(dataSnapshot.getValue()).toString());

                //Check if the section is empty
                if(section != null){
                    Log.d(TAG, "onChildAdded(): New object: " + section.toString());
                }else
                    Log.e(TAG, "onChildAdded(): Section is null");

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildChanged(): A child has been changed: " + dataSnapshot);
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

        return Result.success();
    }
}
