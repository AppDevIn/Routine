package com.mad.p03.np2020.routine.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.ViewHolder.CardViewHolder;

import java.util.ArrayList;

/**
 *
 * CardActivity class used to manage card activities
 *
 * @author Pritheev
 * @since 02-06-2020
 *
 */
public class CardAdapter extends RecyclerView.Adapter<CardViewHolder> {

    private final String TAG = "CardAdapter";

    private ArrayList<String> data = new ArrayList<>();

    public CardAdapter(ArrayList<String> input) {

        data.add("lol");
        data.add("lol2");
        this.data = input;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_layout, parent, false);
        final CardViewHolder holder = new CardViewHolder(item);
        Log.v(TAG, "ViewHolder created");
        return holder;
    }

    public void onBindViewHolder(CardViewHolder holder, int position){
        holder.stepItem.setText(data.get(position));
        Log.v(TAG, data.get(position));
        Log.v(TAG, "View bound to holder");
    }

    public int getItemCount(){
        Log.v(TAG, "List item count is : " + data.size());
        return data.size();
    }
}
