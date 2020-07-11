package com.mad.p03.np2020.routine;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.mad.p03.np2020.routine.Adapter.HabitAdapter;
import com.mad.p03.np2020.routine.Adapter.HabitCheckAdapter;
import com.mad.p03.np2020.routine.helpers.HabitCheckItemClickListener;
import com.mad.p03.np2020.routine.helpers.HabitHorizontalDivider;
import com.mad.p03.np2020.routine.background.HabitWorker;
import com.mad.p03.np2020.routine.helpers.HabitItemClickListener;
import com.mad.p03.np2020.routine.models.Habit;
import com.mad.p03.np2020.routine.models.User;
import com.mad.p03.np2020.routine.DAL.HabitDBHelper;

/**
 *
 * Habit activity used to manage the habit's layout section
 *
 * @author Hou Man
 * @since 02-06-2020
 */


public class HabitActivity extends AppCompatActivity implements View.OnClickListener, HabitItemClickListener, HabitCheckItemClickListener {

    private static final String TAG = "HabitTracker";
    private static final String SHARED_PREFS = "sharedPrefs"; // initialise sharedPrefs
    private static final String channelId = "001"; // notification channel ID for habitTracker

    // initialise recyclerview and adapter
    private RecyclerView habitRecyclerView, habitCheckRecyclerView;
    private HabitAdapter habitAdapter;
    private HabitCheckAdapter habitCheckAdapter;

    // initialise the handler
    private HabitDBHelper habit_dbHandler;

    //User
    private User user;

    //FAB
    private FloatingActionButton add_habit;

    private ImageView prev_indicator, next_indicator;

    private TextView indicator_num, remind_text;


    /**
     *
     * This method will be called when the start of the HabitActivity.
     * This will initialise the recyclerview,widgets and set onClickListener on them.
     *
     * @param savedInstanceState This parameter refers to the saved state of the bundle object.
     *
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.habit_activity);

        // set the layout in full screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // set the HabitDBHelper
        habit_dbHandler = new HabitDBHelper(this);

        // set User
        user = new User();

        // initialise the shared preferences
        initSharedPreferences();

        // initialise the database
        initFirebase();

        // initialise the notification channel
        initialiseHabitNotificationChannel();


        habitRecyclerView = findViewById(R.id.habit_recycler_view);
        GridLayoutManager manager = new GridLayoutManager(HabitActivity.this,2, GridLayoutManager.HORIZONTAL, false){
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };
        habitRecyclerView.setLayoutManager(manager);
        habitRecyclerView.addItemDecoration(new HabitHorizontalDivider(8));

        prev_indicator = findViewById(R.id.habit_indicator_prev);
        next_indicator = findViewById(R.id.habit_indicator_next);
        indicator_num = findViewById(R.id.habit_indicator_number);
        remind_text = findViewById(R.id.habit_remind_text);

        prev_indicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = indicator_num.getText().toString();
                if (!num.equals("1")){
                    int n = Integer.parseInt(num)-1;
                    indicator_num.setText(String.valueOf(n));
                    n--;

                    int position = n*4;
                    habitRecyclerView.scrollToPosition(position);
                }
            }
        });

//        next_indicator.setRotation(180);
        next_indicator.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {

                  String num = indicator_num.getText().toString();
                  int n = Integer.parseInt(num);
                  int position = (n) *4;
                  int arr_size = habitAdapter._habitList.size();

                  if (position+1 <= arr_size){
                      n++;
                      indicator_num.setText(String.valueOf(n));

                      habitRecyclerView.scrollToPosition(position+3);
                  }
              }
          });


        add_habit = findViewById(R.id.add_habit);
        add_habit.setOnClickListener(this);

        habitCheckRecyclerView = findViewById(R.id.habit_check_rv);
        habitCheckRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Bottom Navigation
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        bottomNavInit(bottomNavigationView);

    }

    @Override
    protected void onStart() {
        super.onStart();

        // initialise the habitAdapter
        Habit.HabitList habitArrayList = initDummyList(habit_dbHandler.getAllHabits());

        habitCheckAdapter = new HabitCheckAdapter(this, habitArrayList);
        habitCheckRecyclerView.setAdapter(habitCheckAdapter);
        habitCheckAdapter.setOnItemClickListener(this);

        habitAdapter = new HabitAdapter(this, habitArrayList, user.getUID());
        // set adapter to the recyclerview
        habitRecyclerView.setAdapter(habitAdapter);

        // set onItemClickListener on the habitAdapter
        habitAdapter.setOnItemClickListener(this);

        int n = checkIncompleteHabits(habitAdapter._habitList);

        if (n == 0){
            remind_text.setText("You have completed all habits today!");
        }else if (n == 1){
            remind_text.setText("You still have 1 habit to do today");
        }else{
            remind_text.setText(String.format("You still have %d habits to do today",n));
        }

        indicator_num.setText("1");
    }

    /** This method is used to initialise the habit notification channel. */
    public void initialiseHabitNotificationChannel(){
        // if api > 28, create a notification channel named "HabitTracker"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "HabitTracker";
            int importance = NotificationManager.IMPORTANCE_HIGH; // set as high importance
            createNotificationChannel(channelId, channelName, importance);
        }
    }

    /**
     *
     * This method is used to create a notification channel.
     *
     * @param channelId This parameter refers to the ID of the notification channel.
     *
     * @param channelName This parameter refers to name of the notification channel.
     *
     * @param importance  This parameter refers to the importance of the notification manager
     *
     * */
    @TargetApi(Build.VERSION_CODES.O)
    public void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    /** This method is used to initialise the sharedPreferences if it is not done so. */
    public void initSharedPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // initialise if "alarm_id" never exists
        if (sharedPreferences.getInt("alarm_id",-1) <= 0){
            editor.putInt("alarm_id",0);
            editor.apply();
        }

    }

    /** This method is used to initialise the firebase */
    private void initFirebase() {
        user.setUID(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Log.i(TAG, "Getting firebase for User ID " + user.getUID());
    }

    /**
     *
     * This method is used to serialize a single object. (into Json String)
     *
     * @param habit This parameter is used to get the habit object
     *
     * @return String This returns the serialized object.
     *
     * */
    public String habit_serializeToJson(Habit habit) {
        Gson gson = new Gson();
        Log.i(TAG,"Object serialize");
        return gson.toJson(habit);
    }

    /**
     *
     * This method is an override method of onClick.
     *
     * @param v The parameter is to pass the view.
     *
     * */
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.add_habit:
                Intent activityName = new Intent(HabitActivity.this, HabitAddActivity.class);
                startActivity(activityName);
                break;
        }
    }

    /**
     *
     * This method is an override method of onItemClick on habitAdapter.
     *
     * @param position The parameter is to pass the position of the holder of the recycleView.
     *
     * */
    @Override
    public void onHabitItemClick(final int position) {
        // This will be triggered when the recycler view holder is clicked

        // Editing habit
        final Habit habit = habitAdapter._habitList.getItemAt(position); // retrieve the habit object by its position in adapter list
        Log.d(TAG, "Editing habit " + habit.getTitle());

        Intent activityName = new Intent(HabitActivity.this, HabitViewActivity.class);
        Bundle extras = new Bundle();
        extras.putString("recorded_habit", habit_serializeToJson(habit));
        activityName.putExtras(extras);
        startActivity(activityName);

    }

    @Override
    public void onHabitCheckItemClick(int position) {
        final Habit habit = habitAdapter._habitList.getItemAt(position); // retrieve the habit object by its position in adapter list
        habit.addCount(); // add the count by 1
        habitAdapter.notifyDataSetChanged(); // notify the data set has changed
        habitCheckAdapter.notifyDataSetChanged();
        habit_dbHandler.updateCount(habit); // update the habit count in the SQLiteDatabase
        writeHabit_Firebase(habit, user.getUID(), false); // write the habit to the firebase

        int n = checkIncompleteHabits(habitAdapter._habitList);

        if (n == 0){
            remind_text.setText("You have completed all habits today!");
        }else if (n == 1){
            remind_text.setText("You still have 1 habit to do today");
        }else{
            remind_text.setText(String.format("You still have %d habits to do today",n));
        }

    }

    /**
     *
     * To set the bottom nav to listen to item changes
     * and chose the item that have been selected
     *
     * @param bottomNavigationView The botomNav that needs to be set to listen
     */
    private void bottomNavInit( BottomNavigationView bottomNavigationView){

        //To have the highlight
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        //To set setOnNavigationItemSelectedListener
        NavBarHelper  navBarHelper = new NavBarHelper(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(navBarHelper);
    }

    private Habit.HabitList initDummyList (Habit.HabitList habitList){

        int size = habitList.size();

        int dummy_size = 4-(size % 4);
        if (dummy_size == 4) {return habitList;}

        for (int i = 0; i<dummy_size; i++){
            habitList.addItem(new Habit("dummy",0,0,"cyangreen"));
        }

        return habitList;
    }

    /**
     *
     * This method is used to send the work request
     *  to the habitWorker(WorkManager) to do the writing firebase action
     *  when the network is connected.
     *  (This will be invoked when the count increases on the page of HabitTracker)
     *
     * @param habit This parameter is used to get the habit object
     *
     * @param UID This parameter is used to get the userID
     *
     * @param isDeletion This parameter is used to indicate whether it is deletion of habit to firebase
     *
     * */
    public void writeHabit_Firebase(Habit habit, String UID, boolean isDeletion){
        Log.i(TAG, "Uploading to Firebase");

        // set constraint that the network must be connected
        Constraints myConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // put data in a data builder
        Data firebaseUserData = new Data.Builder()
                .putString("ID", UID)
                .putString("habitData", habit_serializeToJson(habit))
                .putBoolean("deletion", isDeletion)
                .build();

        // wrap the work request
        OneTimeWorkRequest mywork =
                new OneTimeWorkRequest.Builder(HabitWorker.class)
                        .setConstraints(myConstraints)
                        .setInputData(firebaseUserData)
                        .build();

        // send the work request to the work manager
        WorkManager.getInstance(this).enqueue(mywork);
    }

    public int checkIncompleteHabits(Habit.HabitList habitList){
        int n = 0;
        for (int i = 0; i < habitList.size(); i++){
            Habit habit = habitList.getItemAt(i);
            if (!habit.getTitle().toLowerCase().equals("dummy") && habit.getOccurrence() > habit.getCount() ){
                n++;
            }
        }
        return n;
    }




}
