package com.mad.p03.np2020.routine.Focus.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.p03.np2020.routine.Focus.DAL.AchievementDBHelper;
import com.mad.p03.np2020.routine.Focus.DAL.FocusDBHelper;
import com.mad.p03.np2020.routine.Focus.Fragment.AchievementFragment;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.Focus.ViewHolder.AchievementViewHolder;
import com.mad.p03.np2020.routine.helpers.DividerItemDecoration;
import com.mad.p03.np2020.routine.Focus.Model.Achievement;
import com.mad.p03.np2020.routine.models.User;

import java.util.ArrayList;
import java.util.HashMap;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementViewHolder> {

    private Context context; //Current context
    private FocusDBHelper focusDBHelper;
    private AchievementDBHelper achievementDBHelper;

    private User user;
    private String TAG = "AchievementAdapter";
    private AchievementFragment achievementFragment;
    private int highestCount;
    HashMap<Integer, ArrayList<Achievement>> typeOfAchievements;
    int badges_achieved = 0;

    public AchievementAdapter(User user, Context context, FocusDBHelper focusDBHelper, AchievementFragment achievementFragment, AchievementDBHelper achievementDBHelper) {
        this.context = context;
        this.focusDBHelper = focusDBHelper;
        this.user = user;
        this.achievementFragment = achievementFragment;
        this.achievementDBHelper = achievementDBHelper;

        typeOfAchievements = user.getAchievementArrayList();
        Log.v(TAG, "Setting up achievement adapter");


    }

    @NonNull
    @Override
    public AchievementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View historyView = LayoutInflater.from(context).inflate(R.layout.layout_gridview_achievements, parent, false);
        Log.v(TAG, "Setting up achievement view holder");

        return new AchievementViewHolder(historyView, this, parent);
    }

    //Set text
    @Override
    public void onBindViewHolder(@NonNull AchievementViewHolder holder, int position) {
        Log.v(TAG, "Achieved at " + Achievement.getAchievementName(position));
        holder.achievementTitle.setText(Achievement.getAchievementName(position) + " Badges");
        User.achievementView achievementArrayList;
        achievementArrayList = user.getAchievementListofPartiularType(position+1, context);
        GridViewAdapterAchievements achievementAdapter = new GridViewAdapterAchievements(context, achievementArrayList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 3); //Declare layoutManager
        holder.gridViewItem.setItemViewCacheSize(20);
        holder.gridViewItem.setDrawingCacheEnabled(true);
        holder.gridViewItem.addItemDecoration(new DividerItemDecoration(5)); //Add Custom Spacing
        holder.gridViewItem.setLayoutManager(layoutManager);
        holder.gridViewItem.setHasFixedSize(true);
        holder.gridViewItem.setItemAnimator(new DefaultItemAnimator());

        holder.gridViewItem.setAdapter(achievementAdapter);
        holder.highest_title.setText("You Spent : " + achievementArrayList.getValueOutput() +" " +Achievement.getAchievementName(position));
        holder.progressBar.setMax(achievementArrayList.getArrayList().size());
        holder.progressBar.setProgress(achievementArrayList.getBadges());

        if(achievementArrayList.getBadges() == achievementArrayList.getArrayList().size()){
            holder.progressBar.getProgressDrawable().setColorFilter(
                    Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);
        }else{
            holder.progressBar.getProgressDrawable().setColorFilter(
                    Color.YELLOW, android.graphics.PorterDuff.Mode.SRC_IN);
        }

        holder.badge_achieved.setText(achievementArrayList.getBadges() + " / " + achievementArrayList.getArrayList().size());
        badges_achieved = badges_achieved + achievementArrayList.getBadges();
        achievementFragment.badgeIndicator.setText(String.valueOf(badges_achieved));

    }

    @Override
    public int getItemCount() {
        Log.v(TAG, "Item count: " + typeOfAchievements.size());
        return typeOfAchievements.size();
    }

}
