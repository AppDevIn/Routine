package com.mad.p03.np2020.routine.Task.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.Task.ViewHolder.TaskViewHolder;
import com.mad.p03.np2020.routine.Task.ViewHolder.TeamViewHolder;
import com.mad.p03.np2020.routine.Task.model.TeamDataListener;
import com.mad.p03.np2020.routine.models.Team;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TeamAdapter extends RecyclerView.Adapter<TeamViewHolder> implements TeamDataListener{

    Team mTeam;
    Context mContext;

    final static String TAG = "TeamAdapter";

    public TeamAdapter(Team team, Context context) {
        mTeam = team;
        mTeam.setEmail(new ArrayList<>());
        mTeam.getTeamFirebase(this);
        mContext = context;
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.activity_list_item, parent, false);

        return new TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        holder.txtEmail.setText(mTeam.getEmail().get(position));
        holder.txtEmail.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d(TAG, "onLongClick: " + mTeam.getEmail().get(position) + " is being removed from " + mTeam.getSectionID());

                deleteEmail(position);

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTeam.getEmail().size();
    }


    @Override
    public void onDataAdd(String email) {
        mTeam.addEmail(email);

        notifyItemInserted(mTeam.getEmail().size()-1);
    }

    public List<String> getEmailList() {
        return mTeam.getEmail();
    }



    public void addEmail(String email){

        //Add to the list
        mTeam.addEmail(email);
        synchronized(this){
            this.notify();
        }

        //Send to the firebase
        mTeam.excuteFirebaseUpload(email);

    }

    private void deleteEmail(int position){

        //Delete from the firebase
        mTeam.excuteEmailDeleteFirebase(position);

        //Delete from the list
        mTeam.getEmail().remove(position);


        notifyItemRemoved(position);

    }
}
