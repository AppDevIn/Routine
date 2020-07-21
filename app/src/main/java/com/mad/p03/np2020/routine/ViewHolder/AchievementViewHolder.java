package com.mad.p03.np2020.routine.ViewHolder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.p03.np2020.routine.Adapter.AchievementAdapter;
import com.mad.p03.np2020.routine.Adapter.FocusAdapter;
import com.mad.p03.np2020.routine.R;

public class AchievementViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public AchievementAdapter adapter;
    public ViewGroup parent;

    private TextView achievementTitle;
    /**
     *
     * FocusViewHolder for custom RecyclerView
     * an item view and metadata about its place within the RecyclerView
     *
     * @param itemView set the positon to this section
     * @param adapter set the adapter to this section
     * @param parent set the parent to this section
     *
     * */
    public AchievementViewHolder(@NonNull View itemView, AchievementAdapter adapter, ViewGroup parent) {
        super(itemView);

        this.adapter = adapter;
        this.parent = parent;
        achievementTitle = itemView.findViewById(R.id.achievement_title);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

    }
}
