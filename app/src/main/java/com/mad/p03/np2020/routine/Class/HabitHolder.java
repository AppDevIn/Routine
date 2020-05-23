package com.mad.p03.np2020.routine.Class;

import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.p03.np2020.routine.HabitAdapter;
import com.mad.p03.np2020.routine.R;

public class HabitHolder extends RecyclerView.ViewHolder {

    public TextView mTitle,mCount,mCount2,mOccurrence, mPeriod;
    public ImageButton addBtn;
    public RelativeLayout habit_row;

    public HabitHolder(@NonNull View itemView, final HabitAdapter.OnItemClickListener listener) {
        super(itemView);

        this.mTitle = itemView.findViewById(R.id.habitTitle);
        this.mCount = itemView.findViewById(R.id.habitCount);
        this.mCount2 = itemView.findViewById(R.id.habitCount2);
        this.mOccurrence = itemView.findViewById(R.id.habitOccurence);
        this.addBtn = itemView.findViewById(R.id.addCnt);
        this.mPeriod = itemView.findViewById(R.id.habit_period);
        this.habit_row = itemView.findViewById(R.id.habit_row);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null){
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        listener.onItemClick(position);
                    }
                }
            }
        });
    }
}
