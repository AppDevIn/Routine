package com.mad.p03.np2020.routine.ViewHolder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.p03.np2020.routine.R;

public class HabitCheckHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public CheckBox item_checkbox;

    public HabitCheckHolder(@NonNull View itemView) {
        super(itemView);

        this.title = itemView.findViewById(R.id.to_do_title);
        this.item_checkbox = itemView.findViewById(R.id.checkBox);
        //set an onclick listener when the holder is clicked

    }
}
