package com.mad.p03.np2020.routine.background;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.mad.p03.np2020.routine.Class.FocusHolder;

public class FocusWorker extends Worker {

    DatabaseReference mDatabase;
    FocusHolder focusData;

    public FocusWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        boolean REFERENCE_STATUS = getInputData().getBoolean("deletion", true);
        //Result in deletion and creation of data
        //Getting the focus object
        String UID = getInputData().getString("ID");

        //Deserialization from jsonObject
        focusData = deserializeFromJson(getInputData().getString("focusData" + ""));

        //Referencing Data
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(UID);

        String bool = (REFERENCE_STATUS) ? deleteToFirebase() : writeToFirebase();

        Log.i("Firebase status: ", bool);
        return ListenableWorker.Result.success();
    }

    private String writeToFirebase() {
        //Write Data to firebase
        Log.i("Focus", "Focus Data being uploaded");
        Task<Void> result = mDatabase.child("FocusData").child(focusData.getFbID()).setValue(focusData);
        return result.getResult().toString();
    }

    private String deleteToFirebase() {
        Log.i("Focus", "Focus Data being deleted with the uid of " + focusData.getFbID());
        Task<Void> result = mDatabase.child("FocusData").child(focusData.getFbID()).removeValue();
        return result.getResult().toString();
    }

    // Deserialize to single object.
    private FocusHolder deserializeFromJson(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, FocusHolder.class);
    }
}
