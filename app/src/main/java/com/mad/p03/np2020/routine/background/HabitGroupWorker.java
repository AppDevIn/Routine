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

/**
 *
 * Model used to manage the section
 *
 * @author Hou Man
 * @since 02-06-2020
 */


public class HabitGroupWorker extends Worker {

    private static final String TAG = "HabitGroupWorker";
    private DatabaseReference mDatabase;
    private HabitGroup habitGroupData;

    /**This method is a constructor for habitGroupWorker*/
    public HabitGroupWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
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
        habitGroupData = deserializeFromJson(getInputData().getString("habitData" + ""));

        //Referencing Data
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(UID).child("habitGroup");

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
        Log.d(TAG, "writeToFirebase: HabitGroup Data being uploaded ");

        mDatabase.child(String.valueOf(habitGroupData.getGrp_id())).setValue(habitGroupData);

    }

    /**
     *
     * This method is used to deserialize to single object. (from Json)
     *
     * @param jsonString This parameter is used to get json string
     *
     * @return String This returns the deserialized habitGroup object.
     *
     * */
    private HabitGroup deserializeFromJson(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, HabitGroup.class);
    }
}
