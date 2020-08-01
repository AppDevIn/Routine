package com.mad.p03.np2020.routine.Focus.ViewHolder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.p03.np2020.routine.Focus.Adapter.AchievementAdapter;
import com.mad.p03.np2020.routine.R;

/***
 * This is the layout for the achievement view holder.
 * This display the type of achievements, not the badges (Do not get confused(
 *
 *  @author Lee Quan Sheng
 *  @since 01-08-2020
 */
public class AchievementViewHolder extends RecyclerView.ViewHolder {
    public AchievementAdapter adapter;
    public ViewGroup parent;

    public TextView achievementTitle, highest_title, badge_achieved; //TextView to display the title of the achievement, highest amount, and the number of badge achieved
    public RecyclerView badgesGridView; //RecyclerView to display the badges
    public ProgressBar progressBar; //Progress bar to display the progress of the badges

    /**
     * FocusViewHolder for custom RecyclerView
     * an item view and metadata about its place within the RecyclerView
     *
     * @param itemView set the positon to this section
     * @param adapter  set the adapter to this section
     * @param parent   set the parent to this section
     */
    public AchievementViewHolder(@NonNull View itemView, AchievementAdapter adapter, ViewGroup parent) {
        super(itemView);

        this.adapter = adapter;
        this.parent = parent;
        achievementTitle = itemView.findViewById(R.id.achievement_title);
        highest_title = itemView.findViewById(R.id.highest_title);

        //GridView
        badgesGridView = itemView.findViewById(R.id.achievement_grid);

        progressBar = itemView.findViewById(R.id.simpleProgressBar);
        badge_achieved = itemView.findViewById(R.id.badge_achieved);

    }

}
