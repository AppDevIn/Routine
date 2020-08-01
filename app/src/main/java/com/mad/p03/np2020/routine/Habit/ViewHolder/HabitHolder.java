package com.mad.p03.np2020.routine.Habit.ViewHolder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.Habit.Interface.HabitItemClickListener;

/**
 *
 * To be used with the adapter HabitGroupAdapter. This
 * holds reference to the id of the view resource
 *
 * @author Hou Man
 * @since 02-06-2020
 */

public class HabitHolder extends RecyclerView.ViewHolder {

    /** Shown as habit title on the habit holder*/
    public TextView mTitle;
    /** Shown as habit count on the left ot the habit holder*/
    public TextView mCount;
    /** Shown as habit occurrence on the habit holder*/
    public TextView mOccurrence;
    /** Shown as habit period on the habit holder*/
    public TextView mPeriod;
    /** Shown as the add count button on the habit holder*/
    public ImageButton addBtn;
    /** The background of the habit holder*/
    public CardView habit_card;
    /** The progress of the habit */
    public TextView habit_progress;
    /** The progress bar of the habit */
    public ProgressBar habit_progressBar;

    public ImageView habit_finished;

    /**
     *
     * This method is used to
     *  bind the widgets to the holder and set the onClickListener interface to each holder by parsing its holder position.
     *
     * @param itemView This parameter is used to get the view of the holder.
     *
     * @param listener This parameter is used to get the listener interface.
     *
     * */
    public HabitHolder(@NonNull View itemView, final HabitItemClickListener listener) {
        super(itemView);

        this.mTitle = itemView.findViewById(R.id.habitTitle);
        this.habit_card = itemView.findViewById(R.id.habit_card);
        this.mCount = itemView.findViewById(R.id.habitCount);
        this.mOccurrence = itemView.findViewById(R.id.habitOccurrence);
        this.habit_progress = itemView.findViewById(R.id.habit_progress);
        this.habit_progressBar = itemView.findViewById(R.id.habit_progressBar);
        this.habit_finished = itemView.findViewById(R.id.habit_finished);

//        this.addBtn = itemView.findViewById(R.id.addCnt);
//        this.mPeriod = itemView.findViewById(R.id.habit_period);

        //set an onclick listener when the holder is clicked
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null){
                    int position = getAdapterPosition(); //this is to get the position of the holder
                    if (position != RecyclerView.NO_POSITION){
                        listener.onHabitItemClick(position);
                        //this is to parse the position into the parameter so that we can utilise the position in other activity further on
                    }
                }
            }
        });
    }
}
