package com.mad.p03.np2020.routine.ViewHolder;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.signature.ObjectKey;
import com.mad.p03.np2020.routine.Adapter.AchievementAdapter;
import com.mad.p03.np2020.routine.Adapter.GridViewAdapterAchievements;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.models.Achievement;
import com.mad.p03.np2020.routine.models.Focus;

import java.io.File;
import java.text.ParseException;
import java.util.HashMap;

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
