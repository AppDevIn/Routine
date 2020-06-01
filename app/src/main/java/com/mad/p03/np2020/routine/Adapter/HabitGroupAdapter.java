package com.mad.p03.np2020.routine.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.p03.np2020.routine.Class.HabitGroup;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.ViewHolder.HabitGroupHolder;

import java.util.ArrayList;

public class HabitGroupAdapter extends RecyclerView.Adapter<HabitGroupHolder> {

    public ArrayList<HabitGroup> _habitGroupList;
    Context c;
    private HabitGroupAdapter.OnItemClickListener mListener;
    static View view;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(HabitGroupAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }

    public HabitGroupAdapter(ArrayList<HabitGroup> _habitGroupList, Context c) {
        this._habitGroupList = _habitGroupList;
        this.c = c;
    }

    @NonNull
    @Override
    public HabitGroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_group_row,null);

        return new HabitGroupHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitGroupHolder holder, int position) {
        final HabitGroup group = _habitGroupList.get(position);

        holder.grp_name.setText(group.getGrp_name());
    }

    @Override
    public int getItemCount() {
        return _habitGroupList.size();
    }
}