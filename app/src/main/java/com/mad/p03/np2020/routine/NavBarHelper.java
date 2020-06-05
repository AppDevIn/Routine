package com.mad.p03.np2020.routine;


import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mad.p03.np2020.routine.Class.Focus;
import com.mad.p03.np2020.routine.Class.Habit;

import androidx.annotation.NonNull;

public class NavBarHelper implements BottomNavigationView.OnNavigationItemSelectedListener {


    private Context mContext;
    NavBarHelper(Context context) {
        mContext = context;
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        ActivityOptions options =
                ActivityOptions.makeCustomAnimation(mContext, 0, 0);

        switch (item.getItemId()){
            case R.id.habit:
                mContext.startActivity(new Intent(mContext, HabitActivity.class), options.toBundle());
                break;
            case R.id.focus:
                mContext.startActivity(new Intent(mContext, FocusActivity.class), options.toBundle());
                break;
            case R.id.home:
                Intent intent3 = new Intent(mContext, Home.class);
                mContext.startActivity(intent3, options.toBundle());
                break;
            case R.id.calender:
                //TODO: Stage 2 implement
//                Intent intent2 = new Intent(mContext, Main4Activity.class);
//                mContext.startActivity(intent2, options.toBundle());
                break;
            case R.id.profile:
                //TODO: Stage 2 implement
//                Intent intent5 = new Intent(mContext, Main5Activity.class);
//                mContext.startActivity(intent5, options.toBundle());
                break;
        }

        return false;
    }
}
