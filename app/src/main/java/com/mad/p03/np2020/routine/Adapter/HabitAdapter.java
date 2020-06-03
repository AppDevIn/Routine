package com.mad.p03.np2020.routine.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.gson.Gson;
import com.mad.p03.np2020.routine.Class.Habit;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.ViewHolder.HabitHolder;
import com.mad.p03.np2020.routine.background.HabitWorker;
import com.mad.p03.np2020.routine.database.HabitDBHelper;

/**
 *
 * Model used to manage the section
 *
 * @author Hou Man
 * @since 02-06-2020
 */

public class HabitAdapter extends RecyclerView.Adapter<HabitHolder> {


    final static String TAG = "HabitAdapter";
    private Context c;
    private OnItemClickListener mListener;
    private static View view;
    private HabitDBHelper dbHandler;
    private String UID;

    /**Used as the adapter habitList*/
    public Habit.HabitList _habitList;

    /**This method is a constructor for habitAdapter*/
    @RequiresApi(api = Build.VERSION_CODES.N)
    public HabitAdapter(Context c, Habit.HabitList habitList, String UID) {
        this.c = c;
        this._habitList = habitList;
        this.UID = UID;
        dbHandler = new HabitDBHelper(c);
    }

    /**
     *
     * This method is used to bind the listener
     *
     * @param listener This parameter reference the local Listener
     * */
    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    /**
     *
     * This method is used to
     *  create new views (invoked by the layout manager)
     *
     * @param parent This parameter is used to get the viewGroup.
     *
     * @param viewType This parameter is used to get the viewType.
     *
     * @return HabitHolder This returns the habitHolder with view created
     * */
    @NonNull
    @Override
    public HabitHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_row,null);

        return new HabitHolder(view, mListener);
    }


    /**
     *
     * This method is used to
     *  replace the contents of a view (invoked by the layout manager)
     *
     * @param holder This parameter is used to get the holder
     *
     * @param position This parameter is used to get the position
     *
     * */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull HabitHolder holder, final int position) {
        // retrieve the habit object
        final Habit habit = _habitList.getItemAt(position);

        // set the background color of the holder based on its holder color value
        switch (habit.getHolder_color()){
            case ("cyangreen"):
                holder.habit_row.setBackgroundResource(R.drawable.habit_holder_cyangreen);
                break;

            case ("lightcoral"):
                holder.habit_row.setBackgroundResource(R.drawable.habit_holder_lightcoral);
                break;

            case ("fadepurple"):
                holder.habit_row.setBackgroundResource(R.drawable.habit_holder_fadepurple);
                break;

            case ("slightdesblue"):
                holder.habit_row.setBackgroundResource(R.drawable.habit_holder_slightdesblue);
                break;
        }

        // set text on TextView based on the object
        holder.mTitle.setText(habit.getTitle());
        holder.mCount.setText(String.valueOf(habit.getCount()));
        holder.mCount2.setText(String.valueOf(habit.getCount()));
        holder.mOccurrence.setText(String.valueOf(habit.getOccurrence()));
        holder.addBtn.setBackgroundColor(Color.TRANSPARENT);
        // set onClickListener on add button
        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // this will trigger the habit class add count method
                habit.addCount(); // add the count by 1
                notifyDataSetChanged(); // notify the data set has changed
                dbHandler.updateCount(habit); // update the habit count in the SQLiteDatabase
                writeHabit_Firebase(habit, UID, false); // write the habit to the firebase

            }
        });

        // set the period text based on its period attribute value
        switch (habit.getPeriod()){
            case 1:
                holder.mPeriod.setText("TODAY:");
                break;
            case 7:
                holder.mPeriod.setText("THIS WEEK:");
                break;
            case 30:
                holder.mPeriod.setText("THIS MONTH:");
                break;
            case 365:
                holder.mPeriod.setText("THIS YEAR:");
                break;
        }

        if (habit.getCount() >= habit.getOccurrence()){ // if habit count > habit occurrence
            holder.addBtn.setImageResource(R.drawable.habit_tick); // replace the add button as a tick button
        }else{ // if habit count < habit occurrence
            holder.addBtn.setImageResource(R.drawable.habit_add); // set the add button
        }

    }

    /**@return int This return the size of the data set, habitList*/
    @Override
    public int getItemCount() {
        return _habitList.size();
    }

    /**
     *
     * This method is used to send the work request
     *  to the habitWorker(WorkManager) to do the writing firebase action
     *  when the network is connected.
     *  (This will be invoked when the count increases on the page of HabitTracker)
     *
     * @param habit This parameter is used to get the habit object
     *
     * @param UID This parameter is used to get the userID
     *
     * @param isDeletion This parameter is used to indicate whether it is deletion of habit to firebase
     *
     * */
    public void writeHabit_Firebase(Habit habit, String UID, boolean isDeletion){
        Log.i(TAG, "Uploading to Firebase");

        // set constraint that the network must be connected
        Constraints myConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // put data in a data builder
        Data firebaseUserData = new Data.Builder()
                .putString("ID", UID)
                .putString("habitData", habit_serializeToJson(habit))
                .putBoolean("deletion", isDeletion)
                .build();

        // wrap the work request
        OneTimeWorkRequest mywork =
                new OneTimeWorkRequest.Builder(HabitWorker.class)
                        .setConstraints(myConstraints)
                        .setInputData(firebaseUserData)
                        .build();

        // send the work request to the work manager
        WorkManager.getInstance(c).enqueue(mywork);
    }


    /**
     *
     * This method is used to serialize a single object. (into Json String)
     *
     * @param habit This parameter is used to get the habit object
     *
     * @return String This returns the serialized object.
     *
     * */
    public String habit_serializeToJson(Habit habit) {
        Gson gson = new Gson();
        Log.i(TAG,"Object serialize");
        return gson.toJson(habit);
    }

}

