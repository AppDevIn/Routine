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
import com.mad.p03.np2020.routine.models.HabitRepetition;

public class HabitRepetitionWorker extends Worker {

    private static final String TAG = "HabitRepetitionWorker";
    private DatabaseReference mDatabase;
    private HabitRepetition habitRepetition;

    /**This method is a constructor for habitGroupWorker*/
    public HabitRepetitionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    /**
     *
     * This method is used to
     *  do the work request sent by the activity when meet the constraints.
     *
     * @return Result This returns the result of the work.
     * */
    @NonNull
    @Override
    public Result doWork() {
        String UID = getInputData().getString("ID");

        //Deserialization from jsonObject
        habitRepetition = deserializeHRFromJson(getInputData().getString("habitRepetition" + ""));

        //Referencing Data
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(UID).child("habitRepetition");

        writeToFirebase();

        // return the work result
        return ListenableWorker.Result.success();
    }

    /**
     *
     * This method is used to
     *  write the habitGroup object to the firebase.
     *
     * */
    private void writeToFirebase() {
        // Write Data to firebase
        Log.d(TAG, "writeToFirebase: HabitRepetition Data being uploaded ");

        mDatabase.child(String.valueOf(habitRepetition.getRow_id())).setValue(habitRepetition);

    }

    /**
     *
     * This method is used to deserialize to single object. (from Json)
     *
     * @param jsonString This parameter is used to get json string
     *
     * @return String This returns the deserialized HabitRepetition object.
     *
     * */
    private HabitRepetition deserializeHRFromJson(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, HabitRepetition.class);
    }
}
