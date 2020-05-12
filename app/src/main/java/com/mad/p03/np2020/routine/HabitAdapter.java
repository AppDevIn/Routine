package com.mad.p03.np2020.routine;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.p03.np2020.routine.Class.Habit;

import java.util.ArrayList;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.MyHolder> {

    final static String TAG = "HabitAdapter";
    Context c;
    ArrayList<Habit> habitList;
    private OnItemClickListener listener;
    static View view;

    public HabitAdapter(Context c, ArrayList<Habit> habitList, OnItemClickListener listener) {
        this.c = c;
        this.habitList = habitList;
        this.listener = listener;
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        public TextView mTitle,mCount,mCount2,mOccurrence;
        public ImageButton addBtn;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            this.mTitle = itemView.findViewById(R.id.habitTitle);
            this.mCount = itemView.findViewById(R.id.habitCount);
            this.mCount2 = itemView.findViewById(R.id.habitCount2);
            this.mOccurrence = itemView.findViewById(R.id.habitOccurence);
            this.addBtn = itemView.findViewById(R.id.addCnt);
        }

        public void bind(final Habit habit, final OnItemClickListener listener) {

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(habit);
                }
            });
        }
    }

    @NonNull
    @Override
    public HabitAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_row,null);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        final Habit habit = habitList.get(position);

        holder.mTitle.setText(habit.getTitle().toUpperCase().trim());
        holder.mCount.setText(String.valueOf(habit.getCount()));
        holder.mCount2.setText(String.valueOf(habit.getCount()));
        holder.mOccurrence.setText(String.valueOf(habit.getOccurrence()));
        holder.addBtn.setBackgroundColor(Color.TRANSPARENT);
        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                habit.addCount();
                notifyDataSetChanged();
            }
        });

        if (habit.getCount() >= habit.getOccurrence()){
            holder.addBtn.setImageResource(R.drawable.habit_tick);
        }

        holder.bind(habit, listener);

    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Habit habit);

    }

}

