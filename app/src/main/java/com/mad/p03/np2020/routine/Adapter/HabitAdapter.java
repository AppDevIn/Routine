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
import com.mad.p03.np2020.routine.HabitActivity;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.ViewHolder.HabitHolder;
import com.mad.p03.np2020.routine.background.HabitWorker;
import com.mad.p03.np2020.routine.database.HabitDBHelper;

public class HabitAdapter extends RecyclerView.Adapter<HabitHolder> {


    final static String TAG = "HabitAdapter";
    Context c;
    public Habit.HabitList _habitList;
    private OnItemClickListener mListener;
    static View view;
    HabitDBHelper dbHandler;
    HabitActivity act;
    String UID;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public HabitAdapter(Context c, Habit.HabitList habitList, String UID) {
        this.c = c;
        this._habitList = habitList;
        this.UID = UID;
        dbHandler = new HabitDBHelper(c);
        act =  new HabitActivity();
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }
    @NonNull
    @Override
    public HabitHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_row,null);

        return new HabitHolder(view, mListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull HabitHolder holder, final int position) {
        final Habit habit = _habitList.getItemAt(position);
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


        holder.mTitle.setText(habit.getTitle());
        holder.mCount.setText(String.valueOf(habit.getCount()));
        holder.mCount2.setText(String.valueOf(habit.getCount()));
        holder.mOccurrence.setText(String.valueOf(habit.getOccurrence()));
        holder.addBtn.setBackgroundColor(Color.TRANSPARENT);
        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                habit.addCount();
                notifyDataSetChanged();
                dbHandler.updateCount(habit);
                writeHabit_Firebase(habit, UID, false);

            }
        });

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

        if (habit.getCount() >= habit.getOccurrence()){
            holder.addBtn.setImageResource(R.drawable.habit_tick);
        }else{
            holder.addBtn.setImageResource(R.drawable.habit_add);
        }

    }

    @Override
    public int getItemCount() {
        return _habitList.size();
    }

    public void writeHabit_Firebase(Habit habit, String UID, boolean isDeletion){
        Log.i(TAG, "Uploading to Firebase");

        Constraints myConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        Data firebaseUserData = new Data.Builder()
                .putString("ID", UID)
                .putString("habitData", habit_serializeToJson(habit))
                .putBoolean("deletion", isDeletion)
                .build();

        OneTimeWorkRequest mywork =
                new OneTimeWorkRequest.Builder(HabitWorker.class)
                        .setConstraints(myConstraints)
                        .setInputData(firebaseUserData)
                        .build();

        WorkManager.getInstance(c).enqueue(mywork);
    }

    // Serialize a single object.
    public String habit_serializeToJson(Habit habit) {
        Gson gson = new Gson();
        Log.i(TAG,"Object serialize");
        return gson.toJson(habit);
    }

}

