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

import com.mad.p03.np2020.routine.Focus.Fragment.AchievementFragment;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.Focus.ViewHolder.AchievementViewHolder;
import com.mad.p03.np2020.routine.helpers.DividerItemDecoration;
import com.mad.p03.np2020.routine.Focus.Model.Achievement;
import com.mad.p03.np2020.routine.models.User;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * Achievement Adapter to handle the recyclerView Achievement Activity
 * The achievementAdapter will inflate the the items for the sections to which will give us
 * the view from will be passed to the view holder AchievementViewHolder
 *
 * In here you will be able to see the 3 category of the achievements that is retrieve from google firebase
 *
 * @author Lee Quan Sheng
 * @since 01-08-2020
 */
public class AchievementAdapter extends RecyclerView.Adapter<AchievementViewHolder> {

    private Context context; //Current context

    private User user;
    private String TAG = "AchievementAdapter";
    private AchievementFragment achievementFragment;
    private int highestCount;
    HashMap<Integer, ArrayList<Achievement>> typeOfAchievements;
    int badges_achieved = 0;

    /***
     *
     * @param user                  This parameter is used to pass the user object to retrieve the data for conditional check
     * @param context               This parameter is to represent the current context of the activity
     * @param achievementFragment   This parameter is to represent the achievement fragment of the activity
     */
    public AchievementAdapter(User user, Context context, AchievementFragment achievementFragment) {
        this.context = context;
        this.user = user;
        this.achievementFragment = achievementFragment;

        typeOfAchievements = user.getAchievementArrayList(); //This is used to get the achievement list
        Log.v(TAG, "Setting up achievement adapter");


    }

    /***
     *
     * @param parent    The ViewGroup is the parent view that will hold your cell that you are about to create. So, the ViewGroup parent is the RecyclerView here (it will hold your cell). The parent is used during the layout inflation process so you can see it passed in to the inflate call.
     * @param viewType The viewType is useful if you have different types of cells in your list. For example, if you have a header cell and a detail cell. You can use the viewType to make sure that you inflate the correct layout file for each of those two types of cells.
     * @return return the Viewholder that is created within the onCreateViewHolder method
     */
    @NonNull
    @Override
    public AchievementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View achievementView = LayoutInflater.from(context).inflate(R.layout.layout_gridview_achievements, parent, false);
        Log.v(TAG, "Setting up achievement view holder");

        return new AchievementViewHolder(achievementView, this, parent);
    }

    /***
     *
     * @param holder The viewholder of the achievement recyclerview
     * @param position The position of the achievement recyclerview
     */
    @Override
    public void onBindViewHolder(@NonNull AchievementViewHolder holder, int position) {
        Log.v(TAG, "Achieved at " + Achievement.getAchievementName(position));


        holder.achievementTitle.setText(Achievement.getAchievementName(position) + " Badges"); //Get the type of badges
        User.achievementView achievementArrayList;
        achievementArrayList = user.getAchievementListofPartiularType(position+1, context); //Get the badges of achievements

        //Setting the configurations of the adapter
        GridViewAdapterAchievements achievementAdapter = new GridViewAdapterAchievements(context, achievementArrayList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 3); //Declare layoutManager
        holder.badgesGridView.setItemViewCacheSize(20);
        holder.badgesGridView.setDrawingCacheEnabled(true);
        holder.badgesGridView.addItemDecoration(new DividerItemDecoration(5)); //Add Custom Spacing
        holder.badgesGridView.setLayoutManager(layoutManager);
        holder.badgesGridView.setHasFixedSize(true);
        holder.badgesGridView.setItemAnimator(new DefaultItemAnimator());
        holder.badgesGridView.setAdapter(achievementAdapter);

        //Setting the number of badges that the user unlocked
        holder.highest_title.setText("You Spent : " + achievementArrayList.getValueOutput() +" " +Achievement.getAchievementName(position));

        //Setting the progress bar based on the badges unlocked
        holder.progressBar.setMax(achievementArrayList.getArrayList().size());
        holder.progressBar.setProgress(achievementArrayList.getBadges());


        //if the badges collection has reached to its limit, the progress bar will be green
        if(achievementArrayList.getBadges() == achievementArrayList.getArrayList().size()){
            holder.progressBar.getProgressDrawable().setColorFilter(
                    Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);
        }else{
            //if the badges collection has not reached to its limit, the progress bar will be Blue
            holder.progressBar.getProgressDrawable().setColorFilter(
                    Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);
        }

        //Setting the text of the number of the achievement unlocked
        holder.badge_achieved.setText(achievementArrayList.getBadges() + " / " + achievementArrayList.getArrayList().size());
        badges_achieved = badges_achieved + achievementArrayList.getBadges();
        achievementFragment.badgeIndicator.setText(String.valueOf(badges_achieved));

    }

    //Get the achievement size
    @Override
    public int getItemCount() {
        Log.v(TAG, "Item count: " + typeOfAchievements.size());
        return typeOfAchievements.size();
    }

}
