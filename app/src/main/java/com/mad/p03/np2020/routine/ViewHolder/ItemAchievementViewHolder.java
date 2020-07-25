package com.mad.p03.np2020.routine.ViewHolder;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.p03.np2020.routine.Adapter.AchievementAdapter;
import com.mad.p03.np2020.routine.Adapter.GridViewAdapterAchievements;
import com.mad.p03.np2020.routine.R;

public class ItemAchievementViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public GridViewAdapterAchievements adapter;
    public ViewGroup parent;

    public TextView requirement, achieved;
    public ImageView badgeImage;

    public ItemAchievementViewHolder(@NonNull View itemView, GridViewAdapterAchievements adapter, ViewGroup parent) {
        super(itemView);

        this.adapter = adapter;
        this.parent = parent;
        requirement = itemView.findViewById(R.id.grid_item_title);
        badgeImage = itemView.findViewById(R.id.grid_item_image);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        adapter.shareFileToInstagram(this.getAdapterPosition());
        Log.v("item", "Item on click");

    }
}
