package com.mad.p03.np2020.routine.Card.ViewHolder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mad.p03.np2020.routine.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyCheckViewHolder  extends RecyclerView.ViewHolder {


    public TextView mListName;
    public CheckBox mCheckBox;

    public MyCheckViewHolder(@NonNull View itemView) {
        super(itemView);

        //Find the id
        mListName = itemView.findViewById(R.id.txtListName);
        mCheckBox = itemView.findViewById(R.id.checkbox);

    }
}