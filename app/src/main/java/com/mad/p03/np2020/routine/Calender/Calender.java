package com.mad.p03.np2020.routine.Calender;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mad.p03.np2020.routine.NavBarHelper;
import com.mad.p03.np2020.routine.R;

public class Calender extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);


    }

    @Override
    protected void onResume() {
        super.onResume();
        //Bottom Navigation
//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavViewBar);
//        bottomNavInit(bottomNavigationView);
    }

//    private void bottomNavInit(BottomNavigationView bottomNavigationView) {
//
//        //To have the highlight
//        Menu menu = bottomNavigationView.getMenu();
//        MenuItem menuItem = menu.getItem(1);
//        menuItem.setChecked(true);
//
//        //To set setOnNavigationItemSelectedListener
//        NavBarHelper navBarHelper = new NavBarHelper(this);
//        bottomNavigationView.setOnNavigationItemSelectedListener(navBarHelper);
//    }

}