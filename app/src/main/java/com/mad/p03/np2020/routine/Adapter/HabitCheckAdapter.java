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

        holder.title.setText(capitalise(habit.getTitle().trim()));
        holder.habit_count.setText(String.valueOf(habit.getCount()));
        holder.habit_occurrence.setText(String.valueOf(habit.getOccurrence()));

        int progress = habit.calculateProgress();
        holder.habit_progressBar.setProgress(progress);
        if (progress == 100){
            holder.habit_progressBar.setVisibility(View.INVISIBLE);
            holder.habit_finished.setVisibility(View.VISIBLE);
        }else{
            holder.habit_progressBar.setVisibility(View.VISIBLE);
            holder.habit_finished.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    /**
     *
     * This method is used to format text by capitalising the first text of each split text
     *
     * @param text This parameter is used to get the text
     *
     * @return String This returns the formatted text
     * */
    public String capitalise(String text){
        String txt = "";
        String[] splited = text.split("\\s+");
        for (String s: splited){
            txt += s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase() + " ";
        }
        return txt;

//        return text.substring(0,1).toUpperCase() + text.substring(1).toLowerCase();
    }
}
