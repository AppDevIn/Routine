package com.mad.p03.np2020.routine.Class;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.p03.np2020.routine.HabitAdapter;
import com.mad.p03.np2020.routine.R;

import java.util.ArrayList;

public class HabitGroupAdapter extends RecyclerView.Adapter<HabitGroupHolder> {

    ArrayList<HabitGroup> _habitGroupList;
    Context c;
    private HabitAdapter.OnItemClickListener mListener;
    static View view;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(HabitAdapter.OnItemClickListener listener){
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
        Log.d("debug", "onBindViewHolder: "+group.getGrp_name());

        holder.grp_name.setText(group.getGrp_name());
    }

    @Override
    public int getItemCount() {
        return _habitGroupList.size();
    }
}
