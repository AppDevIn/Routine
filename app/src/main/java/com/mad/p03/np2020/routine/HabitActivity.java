package com.mad.p03.np2020.routine;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.mad.p03.np2020.routine.Adapter.HabitAdapter;
import com.mad.p03.np2020.routine.Adapter.OnItemClickListener;
import com.mad.p03.np2020.routine.Class.Habit;
import com.mad.p03.np2020.routine.Class.HabitGroup;
import com.mad.p03.np2020.routine.Class.User;
import com.mad.p03.np2020.routine.database.HabitDBHelper;

/**
 *
 * Habit activity used to manage the habit's layout section
 *
 * @author Hou Man
 * @since 02-06-2020
 */


public class HabitActivity extends AppCompatActivity implements View.OnClickListener, OnItemClickListener {

    private static final String TAG = "HabitTracker";
    private static final String SHARED_PREFS = "sharedPrefs"; // initialise sharedPrefs
    private static final String channelId = "001"; // notification channel ID for habitTracker

    // initialise recyclerview and adapter
    private RecyclerView habitRecyclerView;
    private HabitAdapter habitAdapter;

    // initialise the handler
    private HabitDBHelper habit_dbHandler;


    //User
    private User user;

    /**
     *
     * This method will be called when the start of the HabitActivity
     *
     * @param savedInstanceState This parameter refers to the saved state of the bundle object.
     *
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG,"onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit);

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

        ImageView add_habit = findViewById(R.id.add_habit);
        // set onClickListener on add_habit button
        add_habit.setOnClickListener(this);

        habitRecyclerView = findViewById(R.id.my_recycler_view);
        habitRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // initialise the habitAdapter
        habitAdapter = new HabitAdapter(this, habit_dbHandler.getAllHabits(),user.getUID());

        // set adapter to the recyclerview
        habitRecyclerView.setAdapter(habitAdapter);

        // set onItemClickListener on the habitAdapter
        habitAdapter.setOnItemClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
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
     * This method is used to serialize a single object. (into Json String)
     *
     * @param habitGroup This parameter is used to get the habitGroup object
     *
     * @return String This returns the serialized object.
     *
     * */
    public String habitGroup_serializeToJson(HabitGroup habitGroup) {
        Gson gson = new Gson();
        Log.i(TAG,"Object serialize");
        return gson.toJson(habitGroup);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.add_habit:
                Intent activityName = new Intent(HabitActivity.this, HabitAddActivity.class);
                startActivity(activityName);
                break;

        }
    }

    @Override
    public void onItemClick(final int position) {
        // Editing habit
        final Habit habit = habitAdapter._habitList.getItemAt(position); // retrieve the habit object by its position in adapter list
        if (habit.getHabitReminder()!=null){
            Log.d(TAG, "onItemClick: "+habit.getHabitReminder().getMessage());
        }
        Log.d(TAG, "Editing habit " + habit.getTitle());

        Intent activityName = new Intent(HabitActivity.this, HabitViewActivity.class);
        Bundle extras = new Bundle();
        extras.putString("recorded_habit", habit_serializeToJson(habit));
        activityName.putExtras(extras);
        startActivity(activityName);

    }
}
