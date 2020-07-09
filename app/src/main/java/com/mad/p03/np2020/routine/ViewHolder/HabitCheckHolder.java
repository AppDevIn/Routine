package com.mad.p03.np2020.routine.ViewHolder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.p03.np2020.routine.R;

public class HabitCheckHolder extends RecyclerView.ViewHolder {
    public TextView title;
    /** The progress bar of the habit */
    public ProgressBar habit_progressBar;

    public ImageView habit_finished;

    public TextView habit_count;

    public TextView habit_occurrence;

    public HabitCheckHolder(@NonNull View itemView) {
        super(itemView);

        this.title = itemView.findViewById(R.id.to_do_title);
        this.habit_progressBar = itemView.findViewById(R.id.habit_check_progressBar);
        this.habit_finished = itemView.findViewById(R.id.habit_check_finished);
        this.habit_count = itemView.findViewById(R.id.habit_check_Count);
        this.habit_occurrence = itemView.findViewById(R.id.habit_check_Occurrence);

        //set an onclick listener when the holder is clicked

    }
}
