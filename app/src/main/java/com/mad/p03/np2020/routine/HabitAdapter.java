package com.mad.p03.np2020.routine;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.p03.np2020.routine.Class.Habit;
import com.mad.p03.np2020.routine.Class.HabitHolder;
import com.mad.p03.np2020.routine.database.HabitDBHelper;

public class HabitAdapter extends RecyclerView.Adapter<HabitHolder> {


    final static String TAG = "HabitAdapter";
    Context c;
    Habit.HabitList _habitList;
    private OnItemClickListener mListener;
    static View view;
    HabitDBHelper dbHandler;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public HabitAdapter(Context c, Habit.HabitList habitList) {
        this.c = c;
        this._habitList = habitList;
        dbHandler = new HabitDBHelper(c);
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

}

