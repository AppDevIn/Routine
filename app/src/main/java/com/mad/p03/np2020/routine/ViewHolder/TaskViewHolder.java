package com.mad.p03.np2020.routine.ViewHolder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mad.p03.np2020.routine.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TaskViewHolder extends RecyclerView.ViewHolder {

    public TextView mListName;
    public CheckBox mCheckBox;

    public TaskViewHolder(@NonNull View itemView) {
        super(itemView);

        mListName = itemView.findViewById(R.id.txtListName);
        mCheckBox = itemView.findViewById(R.id.checkbox);
    }
}
