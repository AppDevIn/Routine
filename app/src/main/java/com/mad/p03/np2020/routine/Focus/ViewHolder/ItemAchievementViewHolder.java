package com.mad.p03.np2020.routine.Focus.ViewHolder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.p03.np2020.routine.Focus.Adapter.GridViewAdapterAchievements;
import com.mad.p03.np2020.routine.R;

/**
 *
 * ViewHolder used to manage the GridViewAdapterAchievements
 *
 * @author Lee Quan Sheng
 * @since 02-06-2020
 */
public class ItemAchievementViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public GridViewAdapterAchievements adapter;
    public ViewGroup parent;

    public TextView requirement;
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
        adapter.showBadgeAchieved(getAdapterPosition());
    }
}
