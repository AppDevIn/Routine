package com.mad.p03.np2020.routine.Task.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.Task.ViewHolder.TaskViewHolder;
import com.mad.p03.np2020.routine.Task.ViewHolder.TeamViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TeamAdapter extends RecyclerView.Adapter<TeamViewHolder>{

    List<String> emailList;

    public TeamAdapter(List<String> emailList) {
        this.emailList = emailList;
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_items, parent, false);
        return new TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        holder.txtEmail.setText(emailList.get(position));
    }

    @Override
    public int getItemCount() {
        return emailList.size();
    }

    public List<String> getEmailList() {
        return emailList;
    }

    public void setEmailList(List<String> emailList) {
        this.emailList = emailList;
    }

    public void addEmail(String email){

        //Add to the list

        //Send to SQL

        //Send to the firebase

    }
}
