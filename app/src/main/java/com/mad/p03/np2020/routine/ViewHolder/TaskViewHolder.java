package com.mad.p03.np2020.routine.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.mad.p03.np2020.routine.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TaskViewHolder extends RecyclerView.ViewHolder {

    public TextView mListName;
    public CheckBox mCheckBox;
    public ViewSwitcher mViewSwitcher, mViewSwitcherTaskName;
    public Button mBtnAdd;

    public TaskViewHolder(@NonNull View itemView) {
        super(itemView);

        mListName = itemView.findViewById(R.id.txtListName);
        mCheckBox = itemView.findViewById(R.id.checkbox);
        mViewSwitcher = itemView.findViewById(R.id.viewswitcher);
        mBtnAdd = itemView.findViewById(R.id.btnAdd);
        mViewSwitcherTaskName = itemView.findViewById(R.id.viewswitcherTaskName);
    }
}
