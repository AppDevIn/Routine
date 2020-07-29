package com.mad.p03.np2020.routine;


import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mad.p03.np2020.routine.Calender.Calender;
import com.mad.p03.np2020.routine.Focus.FocusActivity;
import com.mad.p03.np2020.routine.Habit.HabitActivity;
import com.mad.p03.np2020.routine.Home.Home;
import com.mad.p03.np2020.routine.Profile.ProfileActivity;

import androidx.annotation.NonNull;


/**
 * This allow all the layout with the bottom nav to
 * function the same and to set the listener for item by the nav bar
 *
 * @author Jeyavishnu
 * @since 05-06-2020
 */
public class NavBarHelper implements BottomNavigationView.OnNavigationItemSelectedListener {


    private Context mContext;

    public NavBarHelper(Context context) {
        mContext = context;
    }


    /**
     * This method will navigate them to the
     * intended layout and changing the animation to none
     *
     * @param item The selected item
     * @return true to display the selected item and false if
     * the item should not be selected
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        //Remove animation
        ActivityOptions options =
                ActivityOptions.makeCustomAnimation(mContext, 0, 0);


        switch (item.getItemId()) {
            case R.id.habit:
                Intent intent = new Intent(mContext, HabitActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                mContext.startActivity(intent, options.toBundle());
                break;
            case R.id.focus:
                Intent intentFocus = new Intent(mContext, FocusActivity.class);
                intentFocus.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                mContext.startActivity(intentFocus, options.toBundle());
                break;
            case R.id.home:
                Intent intentHome = new Intent(mContext, Home.class);
                intentHome.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                mContext.startActivity(intentHome, options.toBundle());
                break;
            case R.id.calender:
                Intent intent2 = new Intent(mContext, Calender.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                mContext.startActivity(intent2, options.toBundle());

                break;
            case R.id.profile:
                Intent intentProfile = new Intent(mContext, ProfileActivity.class);
                intentProfile.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                mContext.startActivity(intentProfile, options.toBundle());
                break;
        }

        return true;
    }
}
