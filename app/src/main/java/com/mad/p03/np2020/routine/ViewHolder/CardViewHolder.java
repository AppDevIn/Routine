package com.mad.p03.np2020.routine.ViewHolder;

import android.view.View;
import android.widget.CheckedTextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mad.p03.np2020.routine.R;

/**
 *
 * CardViewHolder class for viewholder of recyclerview
 *
 * @author Pritheev
 * @since 02-06-2020
 *
 */
public class CardViewHolder extends RecyclerView.ViewHolder {

    public CheckedTextView stepItem;

    public CardViewHolder(View itemView){
        super(itemView);
        stepItem = itemView.findViewById(R.id.checkedTextView);
    }
}
