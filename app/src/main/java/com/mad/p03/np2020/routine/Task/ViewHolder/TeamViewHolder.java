package com.mad.p03.np2020.routine.Task.ViewHolder;

import android.view.View;
import android.widget.TextView;

import com.mad.p03.np2020.routine.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TeamViewHolder extends RecyclerView.ViewHolder {

    public TextView txtEmail;
    public TeamViewHolder(@NonNull View itemView) {
        super(itemView);

        txtEmail = itemView.findViewById(R.id.txtListName);

    }
}
