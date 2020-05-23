package com.mad.p03.np2020.routine.Class;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.p03.np2020.routine.HabitAdapter;
import com.mad.p03.np2020.routine.R;

public class HabitGroupHolder extends RecyclerView.ViewHolder {

    public TextView grp_name;

    public HabitGroupHolder(@NonNull View itemView, final HabitGroupAdapter.OnItemClickListener listener) {
        super(itemView);

        this.grp_name = itemView.findViewById(R.id.habit_group_name);

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
