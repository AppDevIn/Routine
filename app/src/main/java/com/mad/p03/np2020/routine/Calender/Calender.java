package com.mad.p03.np2020.routine.Calender;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewSwitcher;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mad.p03.np2020.routine.Calender.CustomCalender.CustomCalenderView;
import com.mad.p03.np2020.routine.Calender.CustomCalender.DateChangeListener;
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

/**
 *
 * This is the controller for the calender activity
 *
 *
 * @author Jeyavishnu
 * @since 02-08-2020
 *
 */
public class Calender extends AppCompatActivity implements DateChangeListener, MyDatabaseListener {


    private final String TAG = "Calender";

    RecyclerView mRecyclerView;
    private Date currentDate;
    private TaskAdapter mTaskAdapter;
    List<Task> mTaskList;
    ViewSwitcher viewSwitcher;
    Boolean isZero = false;
    CustomCalenderView calendarView;


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        overridePendingTransition(0, 0);
    }

    /**
     *
     * This is the set listener for the database and find the id for the views
     * and add animation to the view switcher
     *
     * @param savedInstanceState will be null at first as
     *                           the orientation changes it will get
     *                           in use
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        //Set the listener
        TaskDBHelper.setMyDatabaseListener(this);


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

    /**
     * Init the customcalender view and set listener
     */
    @Override
    protected void onResume() {
        super.onResume();


        calendarView = findViewById(R.id.calendar);
        currentDate = calendarView.getDate();

        calendarView.setDateListener(this);

        calendarView.notifyData();

        initRecyclerView(currentDate);



        //Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavViewBar);
        bottomNavInit(bottomNavigationView);
    }

    /**
     * To set the bottom nav to listen to item changes
     * and chose the item that have been selected
     *
     * @param bottomNavigationView The botomNav that needs to be set to listen
     */
    private void bottomNavInit(BottomNavigationView bottomNavigationView) {

        //To have the highlight
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);

        //To set setOnNavigationItemSelectedListener
        NavBarHelper navBarHelper = new NavBarHelper(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(navBarHelper);
    }


    /**
     *
     * When the date changes in the customcalender this will
     * be notified and trigger the code to change the task view
     *
     * @param date
     */
    @Override
    public void onDateChange(Date date) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


        if(!dateFormat.format(date).equals(dateFormat.format(currentDate))){
            currentDate = date;

            mTaskList = new TaskDBHelper(this).getAllTask(date);

            mTaskAdapter.setTaskList(mTaskList);
            mTaskAdapter.notifyDataSetChanged();

        }

        viewSwitch();



    }

    /**
     * This is another interface that that will
     * get triggered when the DbHelper add data into
     * task
     * @param object The task object that is being added
     */
    @Override
    public void onDataAdd(Object object) {


        Task task = (Task) object;

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Log.d(TAG, "onDataAdd(): A new data added into SQL updating local list with: " + task );

        if( task.getRemindDate() != null && (dateFormat.format(task.getRemindDate()).equals(dateFormat.format(currentDate)))){
            //Adding into the local list
            mTaskList.add(task);

            //Informing the adapter and view of the new item
            mTaskAdapter.notifyItemInserted(mTaskList.size());
        }

        calendarView.notifyData();


        viewSwitch();

    }

    /**
     * This is interface that that will
     * get triggered when the DbHelper updates data into
     * task table
     * @param object The task object that is being updated
     */
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

        calendarView.notifyData();

    }

    /**
     * This is interface that that will
     * get triggered when the DbHelper deletes data from
     * task table
     * @param ID The Task ID of the task object being deleted
     */
    @Override
    public void onDataDelete(String ID) {



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

        calendarView.notifyData();

        viewSwitch();
    }

    /**
     * This is to init the recycler for the task
     * list
     * @param date the data the task will filter
     */
    public void initRecyclerView(Date date){


        Log.i(TAG, "onCreate: Date that needs to be retrieved: " + date.toString() );

        mTaskList = new TaskDBHelper(this).getAllTask(date);

        //Switch the view
        viewSwitch();

        mRecyclerView = findViewById(R.id.rcTask);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mTaskAdapter = new TaskAdapter(mTaskList, this, true);
        mRecyclerView.setAdapter(mTaskAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //Setting up touchhelper
        ItemTouchHelper.Callback callback = new MyTaskTouchHelper(mTaskAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        mTaskAdapter.setMyTaskTouchHelper(itemTouchHelper);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

    }

    /**
     * This is to change the viewswitcher based
     * between the different views
     */
    private void viewSwitch(){
        View zero = findViewById(R.id.view1);
        View list = findViewById(R.id.view2);
        if(mTaskList.size() == 0 && viewSwitcher.getNextView() == zero){
            viewSwitcher.showNext();
        }else if(mTaskList.size() != 0 && viewSwitcher.getNextView() == list){
            viewSwitcher.showNext();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }


}