package com.mad.p03.np2020.routine.ViewHolder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.p03.np2020.routine.Adapter.OnItemClickListener;
import com.mad.p03.np2020.routine.R;

/**
 *
 * Model used to manage the section
 *
 * @author Hou Man
 * @since 02-06-2020
 */

public class HabitHolder extends RecyclerView.ViewHolder {

    /** Shown as habit title on the habit holder*/
    public TextView mTitle;
    /** Shown as habit count on the left ot the habit holder*/
    public TextView mCount;
    /** Shown as habit count on the right of the habit holder*/
    public TextView mCount2;
    /** Shown as habit occurrence on the habit holder*/
    public TextView mOccurrence;
    /** Shown as habit period on the habit holder*/
    public TextView mPeriod;
    /** Shown as the add count button on the habit holder*/
    public ImageButton addBtn;
    /** The layout of the habit holder*/
    public RelativeLayout habit_row;

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
    public HabitHolder(@NonNull View itemView, final OnItemClickListener listener) {
        super(itemView);

        this.mTitle = itemView.findViewById(R.id.habitTitle);
        this.mCount = itemView.findViewById(R.id.habitCount);
        this.mCount2 = itemView.findViewById(R.id.habitCount2);
        this.mOccurrence = itemView.findViewById(R.id.habitOccurence);
        this.addBtn = itemView.findViewById(R.id.addCnt);
        this.mPeriod = itemView.findViewById(R.id.habit_period);
        this.habit_row = itemView.findViewById(R.id.habit_row);

        //set an onclick listener when the holder is clicked
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null){
                    int position = getAdapterPosition(); //this is to get the position of the holder
                    if (position != RecyclerView.NO_POSITION){
                        listener.onItemClick(position);
                        //this is to parse the position into the parameter so that we can utilise the position in other activity further on
                    }
                }
            }
        });
    }
}
