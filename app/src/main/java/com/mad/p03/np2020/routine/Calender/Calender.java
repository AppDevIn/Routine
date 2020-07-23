package com.mad.p03.np2020.routine.Calender;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CalendarView;
import android.widget.ViewSwitcher;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mad.p03.np2020.routine.DAL.TaskDBHelper;
import com.mad.p03.np2020.routine.NavBarHelper;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.Task.adapter.TaskAdapter;
import com.mad.p03.np2020.routine.Task.model.MyTaskTouchHelper;
import com.mad.p03.np2020.routine.helpers.MyDatabaseListener;
import com.mad.p03.np2020.routine.models.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Calender extends AppCompatActivity implements DateChangeListener, MyDatabaseListener {


    private final String TAG = "Calender";

    RecyclerView mRecyclerView;
    private Date currentDate;
    private TaskAdapter mTaskAdapter;
    List<Task> mTaskList;
    ViewSwitcher viewSwitcher;
    Boolean isZero = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);






        //Set the listener
        TaskDBHelper.setMyDatabaseListener(this);

        //Make full screen

        //To set to Full screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);





        //*************For View Switcher********************
        // Declare in and out animations and load them using AnimationUtils class
        viewSwitcher = findViewById(R.id.switcher);
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        // set the animation type to ViewSwitcher
        viewSwitcher.setInAnimation(in);
        viewSwitcher.setOutAnimation(out);




    }

    @Override
    protected void onResume() {
        super.onResume();

        viewSwitcher.reset();

        CustomCalenderView calendarView = findViewById(R.id.calendar);
        currentDate = calendarView.getDate();

        calendarView.setDateListener(this);

        initRecyclerView(currentDate);



        //Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavViewBar);
        bottomNavInit(bottomNavigationView);
    }

    private void bottomNavInit(BottomNavigationView bottomNavigationView) {

        //To have the highlight
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);

        //To set setOnNavigationItemSelectedListener
        NavBarHelper navBarHelper = new NavBarHelper(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(navBarHelper);
    }


    @Override
    public void onDateChange(Date date) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


        if(!dateFormat.format(date).equals(dateFormat.format(currentDate))){
            currentDate = date;

            mTaskList = new TaskDBHelper(this).getAllTask(date);

            mTaskAdapter = new TaskAdapter(mTaskList, this);
            mRecyclerView.setAdapter(mTaskAdapter);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());

            if(mTaskList.size() > 0 && isZero){
                viewSwitcher.reset();
                viewSwitcher.showNext();
                isZero = false;
            }else if (mTaskList.size() == 0 && !isZero){
                viewSwitcher.showNext();
                isZero = true;
            }
        }



    }

    @Override
    public void onDataAdd(Object object) {

        if(mTaskList.size() == 0){
            viewSwitcher.reset();
            viewSwitcher.showNext();
            isZero = false;
        }


        Task task = (Task) object;

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Log.d(TAG, "onDataAdd(): A new data added into SQL updating local list with: " + task );

        if( task.getRemindDate() != null && (dateFormat.format(task.getRemindDate()).equals(dateFormat.format(currentDate)))){
            //Adding into the local list
            mTaskList.add(task);

            //Informing the adapter and view of the new item
            mTaskAdapter.notifyItemInserted(mTaskList.size());
        }

    }

    @Override
    public void onDataUpdate(Object object) {
        Task task = (Task) object;

        for (int position = 0; position < mTaskList.size(); position++) {


            if(mTaskList.get(position).getTaskID().equals(task.getTaskID())){

                mTaskList.remove(position);
                mTaskList.add(position, task);

                mTaskAdapter.notifyItemChanged(position);
                break;
            }
        }

    }

    @Override
    public void onDataDelete(String ID) {

        if(mTaskList.size() == 1){
            viewSwitcher.showNext();
        }

        Log.d(TAG, "onDataDelete(): Checking if " + ID + " exists");

        for (int position = 0; position < mTaskList.size(); position++) {

            if(mTaskList.get(position).getTaskID().equals(ID)){

                //Remove the list
                mTaskList.remove(position);

                //Informing the adapter and view after removing
                mTaskAdapter.notifyItemRemoved(position);
                mTaskAdapter.notifyItemRangeChanged(position, mTaskList.size());
                break;
            }
        }
    }

    //Initialize the recycler view
    public void initRecyclerView(Date date){




        Log.i(TAG, "onCreate: Date that needs to be retrieved: " + date.toString() );

        mTaskList = new TaskDBHelper(this).getAllTask(date);

        mRecyclerView = findViewById(R.id.rcTask);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mTaskAdapter = new TaskAdapter(mTaskList, this);
        mRecyclerView.setAdapter(mTaskAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //Setting up touchhelper
        ItemTouchHelper.Callback callback = new MyTaskTouchHelper(mTaskAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        mTaskAdapter.setMyTaskTouchHelper(itemTouchHelper);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        //if empty display the image if not the recyclerview
        if(mTaskList.size() == 0){
            viewSwitcher.showNext();
            isZero = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(isZero){
            viewSwitcher.showNext();
        }
    }


}