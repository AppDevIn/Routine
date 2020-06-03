package com.mad.p03.np2020.routine.Class;

import android.view.View;
import android.widget.CheckedTextView;

import androidx.recyclerview.widget.RecyclerView;

public class CardViewHolder extends RecyclerView.ViewHolder {
    CheckedTextView stepItem;

    public CardViewHolder(View itemView){
        super(itemView);
        stepItem = itemView.findViewById(android.R.id.text1);
    }
}
