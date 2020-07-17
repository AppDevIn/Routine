package com.mad.p03.np2020.routine.Calender;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mad.p03.np2020.routine.NavBarHelper;
import com.mad.p03.np2020.routine.R;

import java.util.Date;

public class Calender extends AppCompatActivity implements DateChangeListener {


    private final String TAG = "Calender";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);


        CustomCalenderView calendarView = findViewById(R.id.calendar);
        Date date = calendarView.getDate();

        calendarView.setDateListener(this);

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


    @Override
    public void onDateChange(Date date) {

        Log.i(TAG, "onCreate: Date that needs to be retrieved: " + date.toString() );
    }

    //TODO: Initialize the recycler view
    public void initRecyclerView(){
        
    }

}