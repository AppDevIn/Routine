package com.mad.p03.np2020.routine.Habit.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mad.p03.np2020.routine.Habit.DAL.HabitGroupDBHelper;
import com.mad.p03.np2020.routine.Habit.Interface.HabitItemClickListener;
import com.mad.p03.np2020.routine.Habit.models.HabitGroup;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.Habit.ViewHolder.HabitGroupHolder;
import com.mad.p03.np2020.routine.models.User;

import java.util.ArrayList;

/**
 *
 * This will be the controller glue between the viewHolder and the model.
 * This will inflate the the items for the habitGroups to which will give us
 * the view from will be passed to the view holder HabitGroupViewHolder
 *
 * @author Hou Man
 * @since 02-06-2020
 */


public class HabitGroupAdapter extends RecyclerView.Adapter<HabitGroupHolder> {

    private Context c;
    private HabitItemClickListener mListener;
    private static View view;
    private User user;
    private HabitGroupDBHelper habitGroupDBHelper;
    private static final String TAG = "HabitGroupAdapter";

    /**Used as the adapter habitGroupList*/
    public ArrayList<HabitGroup> _habitGroupList;

    /**This method is a constructor for habitGroupAdapter*/
    public HabitGroupAdapter(ArrayList<HabitGroup> _habitGroupList, Context c, User user) {
        this._habitGroupList = _habitGroupList;
        this.c = c;
        this.user = user;

        habitGroupDBHelper = new HabitGroupDBHelper(c);
        eventListener();
    }

    /**
     *
     * This method is used to bind the listener
     *
     * @param listener This parameter reference the local Listener
     * */
    public void setOnItemClickListener(HabitItemClickListener listener){
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

    /**
     * Listen to firebase data change to update views on the recyclerView
     */
    private void eventListener() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUID());
        myRef.child("habitGroup").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notifyItemChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    /**
     * Notify Item changed if user delete or add data
     */
    public void notifyItemChanged() {
        _habitGroupList = habitGroupDBHelper.getAllGroups();
        this.notifyDataSetChanged();
    }
}
