package com.mad.p03.np2020.routine.ViewHolder;

import android.view.View;
import android.widget.CheckedTextView;

import androidx.recyclerview.widget.RecyclerView;
/**
 *
 * CardActivity class used to manage card activities
 *
 * @author Pritheev
 * @since 02-06-2020
 *
 */
public class CardViewHolder extends RecyclerView.ViewHolder {
    public CheckedTextView stepItem;

    public CardViewHolder(View itemView){
        super(itemView);
        stepItem = itemView.findViewById(android.R.id.text1);
    }
}
