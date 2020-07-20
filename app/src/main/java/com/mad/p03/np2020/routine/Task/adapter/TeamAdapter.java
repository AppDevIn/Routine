package com.mad.p03.np2020.routine.Task.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mad.p03.np2020.routine.DAL.TeamDBHelper;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.Task.ViewHolder.TaskViewHolder;
import com.mad.p03.np2020.routine.Task.ViewHolder.TeamViewHolder;
import com.mad.p03.np2020.routine.models.Team;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TeamAdapter extends RecyclerView.Adapter<TeamViewHolder>{

    Team mTeam;
    Context mContext;

    public TeamAdapter(Team team, Context context) {
        mTeam = team;
        mContext = context;
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_items, parent, false);

        return new TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        holder.txtEmail.setText(mTeam.getEmail().get(position));
    }

    @Override
    public int getItemCount() {
        return mTeam.getEmail().size();
    }

    public List<String> getEmailList() {
        return mTeam.getEmail();
    }

    public void addEmail(String email){

        //Add to the list
        mTeam.addEmail(email);
        synchronized(this){
            this.notifyItemChanged(mTeam.getEmail().size());
        }

        //Send to SQL
        TeamDBHelper teamDBHelper = new TeamDBHelper(mContext);
        teamDBHelper.insert(mTeam.getSectionID(), email);

        //Send to the firebase

    }
}
