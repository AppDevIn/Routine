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

/**
 *
 * Model used to manage the section
 *
 * @author Hou Man
 * @since 02-06-2020
 */


public class HabitGroupAdapter extends RecyclerView.Adapter<HabitGroupHolder> {

    private Context c;
    private OnItemClickListener mListener;
    private static View view;

    /**Used as the adapter habitGroupList*/
    public ArrayList<HabitGroup> _habitGroupList;

    /**This method is a constructor for habitGroupAdapter*/
    public HabitGroupAdapter(ArrayList<HabitGroup> _habitGroupList, Context c) {
        this._habitGroupList = _habitGroupList;
        this.c = c;
    }

    /**
     *
     * This method is used to bind the listener
     *
     * @param listener This parameter reference the local Listener
     * */
    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    /**
     *
     * This method is used to
     *  create new views (invoked by the layout manager)
     *
     * @param parent This parameter is used to get the viewGroup.
     *
     * @param viewType This parameter is used to get the viewType.
     *
     * @return HabitHolder This returns the habitGroupHolder with view created
     * */
    @NonNull
    @Override
    public HabitGroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_group_row,null);

        return new HabitGroupHolder(view, mListener);
    }

    /**
     *
     * This method is used to
     *  replace the contents of a view (invoked by the layout manager)
     *
     * @param holder This parameter is used to get the holder
     *
     * @param position This parameter is used to get the position
     *
     * */
    @Override
    public void onBindViewHolder(@NonNull HabitGroupHolder holder, int position) {
        // retrieve the habitGroup object
        final HabitGroup group = _habitGroupList.get(position);

        // set the text based on its group name
        holder.grp_name.setText(group.getGrp_name());
    }

    /**@return int This return the size of the data set, habitGroupList*/
    @Override
    public int getItemCount() {
        return _habitGroupList.size();
    }
}
