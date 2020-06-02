package com.mad.p03.np2020.routine.background;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.mad.p03.np2020.routine.Class.Habit;

public class HabitWorker extends Worker {

    private static final String TAG = "HabitWorker" ;
    DatabaseReference mDatabase;
    Habit habitData;

    public HabitWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        boolean isDeletion = getInputData().getBoolean("deletion", false);

        String UID = getInputData().getString("ID");

        //Deserialization from jsonObject
        habitData = deserializeFromJson(getInputData().getString("habitData" + ""));

        //Referencing Data
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(UID).child("habit");

        if (isDeletion) {
            deleteToFirebase();
        } else {
            writeToFirebase();
        }


        return ListenableWorker.Result.success();
    }

    private void writeToFirebase() {
        // Write Data to firebase
        Log.d(TAG, "writeToFirebase: Habit Data being uploaded ");

        mDatabase.child(String.valueOf(habitData.getHabitID())).setValue(habitData);

    }

    private void deleteToFirebase() {
        // Delete data from firebase
        Log.d(TAG, "writeToFirebase: Habit Data being deleted with the uid of " + habitData.getHabitID());

        mDatabase.child(String.valueOf(habitData.getHabitID())).removeValue();

    }

    // Deserialize to single object.
    private Habit deserializeFromJson(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, Habit.class);
    }
}
