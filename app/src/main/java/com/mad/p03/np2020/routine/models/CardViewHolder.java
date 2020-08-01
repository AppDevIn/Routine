package com.mad.p03.np2020.routine.models;

import android.view.View;
import android.widget.CheckedTextView;

import androidx.recyclerview.widget.RecyclerView;
/**
 *
 * CardViewHolder Class for setting notification
 *
 * @author Pritheev
 * @since 02-06-2020
 *
 */
public class CardViewHolder extends RecyclerView.ViewHolder {
    CheckedTextView stepItem;

    public CardViewHolder(View itemView){
        super(itemView);
        stepItem = itemView.findViewById(android.R.id.text1);
    }
}
