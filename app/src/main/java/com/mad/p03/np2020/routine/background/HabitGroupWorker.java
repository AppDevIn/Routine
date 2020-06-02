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
import com.mad.p03.np2020.routine.Class.HabitGroup;

public class HabitGroupWorker extends Worker {

    private static final String TAG = "HabitGroupWorker";
    DatabaseReference mDatabase;
    HabitGroup habitGroupData;

    public HabitGroupWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String UID = getInputData().getString("ID");

        //Deserialization from jsonObject
        habitGroupData = deserializeFromJson(getInputData().getString("habitData" + ""));

        //Referencing Data
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(UID).child("habitGroup");

        writeToFirebase();

        return ListenableWorker.Result.success();
    }

    private void writeToFirebase() {
        // Write Data to firebase
        Log.d(TAG, "writeToFirebase: HabitGroup Data being uploaded ");

        mDatabase.child(String.valueOf(habitGroupData.getGrp_id())).setValue(habitGroupData);

    }

    // Deserialize to single object.
    private HabitGroup deserializeFromJson(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, HabitGroup.class);
    }
}
