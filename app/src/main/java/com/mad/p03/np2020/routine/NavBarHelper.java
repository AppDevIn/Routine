package com.mad.p03.np2020.routine;


import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mad.p03.np2020.routine.Home.Home;

import androidx.annotation.NonNull;


/**
 *
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
     *
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


        switch (item.getItemId()){
            case R.id.habit:
                Intent intent = new Intent(mContext, HabitActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                mContext.startActivity(intent, options.toBundle());

                break;
            case R.id.focus:
                Intent intentFocus = new Intent(mContext, FocusActivity.class);
                intentFocus.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                mContext.startActivity(intentFocus, options.toBundle());
                break;
            case R.id.home:
                Intent intentHome = new Intent(mContext, Home.class);
                intentHome.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                mContext.startActivity(intentHome, options.toBundle());
                break;
            case R.id.calender:
                Toast.makeText(mContext, "Calender will be implemented in stage 2", Toast.LENGTH_SHORT).show();
                //TODO: Stage 2 implement
//                Intent intent2 = new Intent(mContext, Main4Activity.class);
//                mContext.startActivity(intent2, options.toBundle());
                break;
            case R.id.profile:
                Toast.makeText(mContext, "Profile will be implemented in stage 2", Toast.LENGTH_SHORT).show();
                //TODO: Stage 2 implement
//                Intent intent5 = new Intent(mContext, Main5Activity.class);
//                mContext.startActivity(intent5, options.toBundle());
                break;
        }

        return true;
    }
}
