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
import com.mad.p03.np2020.routine.Habit.models.Habit;

/**
 *
 * This is to upload the data to Habit data from
 * the background which implements the Worker.
 *
 * @author Hou Man
 * @since 02-06-2020
 */


public class HabitWorker extends Worker {

    private static final String TAG = "HabitWorker" ;
    private DatabaseReference mDatabase;
    private Habit habitData;

    /**This method is a constructor for habitWorker*/
    public HabitWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
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

        // return the work result
        return ListenableWorker.Result.success();
    }

    /**
     *
     * This method is used to
     *  write the habit object to the firebase.
     *
     * */
    private void writeToFirebase() {
        // Write Data to firebase
        Log.d(TAG, "writeToFirebase: Habit Data being uploaded ");

        mDatabase.child(String.valueOf(habitData.getHabitID())).setValue(habitData);

    }

    /**
     *
     * This method is used to
     *  delete the habit object from the firebase.
     *
     * */
    private void deleteToFirebase() {
        // Delete data from firebase
        Log.d(TAG, "writeToFirebase: Habit Data being deleted with the uid of " + habitData.getHabitID());

        mDatabase.child(String.valueOf(habitData.getHabitID())).removeValue();

    }

    /**
     *
     * This method is used to deserialize to single object. (from Json)
     *
     * @param jsonString This parameter is used to get json string
     *
     * @return String This returns the deserialized Habit object.
     *
     * */
    private Habit deserializeFromJson(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, Habit.class);
    }
}
