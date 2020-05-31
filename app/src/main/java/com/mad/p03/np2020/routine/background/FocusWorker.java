package com.mad.p03.np2020.routine.background;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.mad.p03.np2020.routine.Class.Focus;

public class FocusWorker extends Worker {

    private DatabaseReference mDatabase;
    private Focus focusData;
    private Task<Void> result;

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

        if ((REFERENCE_STATUS)) {
            deleteToFirebase();
        } else {
            writeToFirebase();
        }

        return ListenableWorker.Result.success();
    }

    private void writeToFirebase() {
        //Write Data to firebase
        Log.i("Focus", "Focus Data being uploaded");
        mDatabase.child("FocusData").child(focusData.getFbID()).setValue(focusData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                result = mDatabase.child("FocusData").child(focusData.getFbID()).setValue(focusData);
            }
        });;
    }

    private void deleteToFirebase() {
        Log.i("Focus", "Focus Data being deleted with the uid of " + focusData.getFbID());
        mDatabase.child("FocusData").child(focusData.getFbID()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                result = mDatabase.child("FocusData").child(focusData.getFbID()).removeValue();
            }
        });
    }

    // Deserialize to single object.
    private Focus deserializeFromJson(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, Focus.class);
    }
}
