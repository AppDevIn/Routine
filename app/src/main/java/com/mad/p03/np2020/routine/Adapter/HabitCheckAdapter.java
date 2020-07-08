package com.mad.p03.np2020.routine.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.p03.np2020.routine.Class.Habit;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.ViewHolder.HabitCheckHolder;

import static java.lang.String.format;

public class HabitCheckAdapter extends RecyclerView.Adapter<HabitCheckHolder> {

    final static String TAG = "ItemAdapter";
    public Habit.HabitList habitList;
    Context c;
    private OnItemClickListener mListener;
    static View view;

    public interface OnItemClickListener{
        //implement an interface then i can retrieve the position from the parameter
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    public HabitCheckAdapter(Context c, Habit.HabitList habitList) {
        this.c = c;
        this.habitList = habitList;
    }


    @NonNull
    @Override
    public HabitCheckHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_check_row,null);
        return new HabitCheckHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitCheckHolder holder, int position) {
        final Habit habit = habitList.getItemAt(position);
        holder.title.setText(habit.getTitle().trim());
        holder.item_checkbox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
            }
        });
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }
}
