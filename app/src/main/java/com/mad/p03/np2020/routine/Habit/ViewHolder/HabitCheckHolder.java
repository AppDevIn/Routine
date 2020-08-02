package com.mad.p03.np2020.routine.Habit.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.p03.np2020.routine.Habit.Interface.HabitCheckItemClickListener;
import com.mad.p03.np2020.routine.R;

/**
 *
 * To be used with the adapter HabitCheckAdapter. This
 * holds reference to the id of the view resource
 *
 * @author Hou Man
 * @since 02-08-2020
 */

public class HabitCheckHolder extends RecyclerView.ViewHolder {
    public TextView title;
    /** The progress bar of the habit */
    public ProgressBar habit_progressBar;

    public ImageView habit_finished;

    public TextView habit_count;

    public TextView habit_occurrence;

    public HabitCheckHolder(@NonNull View itemView, final HabitCheckItemClickListener listener) {
        super(itemView);

        this.title = itemView.findViewById(R.id.to_do_title);
        this.habit_progressBar = itemView.findViewById(R.id.habit_check_progressBar);
        this.habit_finished = itemView.findViewById(R.id.habit_check_finished);
        this.habit_count = itemView.findViewById(R.id.habit_check_Count);
        this.habit_occurrence = itemView.findViewById(R.id.habit_check_Occurrence);

        //set an onclick listener when the holder is clicked
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null){
                    int position = getAdapterPosition(); //this is to get the position of the holder
                    if (position != RecyclerView.NO_POSITION){
                        listener.onHabitCheckItemClick(position);
                        //this is to parse the position into the parameter so that we can utilise the position in other activity further on
                    }
                }
            }
        });
    }
}
