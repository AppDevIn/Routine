package com.mad.p03.np2020.routine.Class;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardViewHolder> {
    ArrayList<String> data;

    public CardAdapter(ArrayList<String> input) {
        data = input;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View item = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new CardViewHolder(item);
    }

    public void onBindViewHolder(CardViewHolder holder, int position){
        String s = data.get(position);
        holder.stepItem.setText(s);
    }

    public int getItemCount(){
        return data.size();
    }
}
