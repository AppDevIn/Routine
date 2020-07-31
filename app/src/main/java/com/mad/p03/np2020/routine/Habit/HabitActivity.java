package com.mad.p03.np2020.routine.Habit;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

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
import com.mad.p03.np2020.routine.Habit.Adapter.HabitAdapter;
import com.mad.p03.np2020.routine.Habit.Adapter.HabitCheckAdapter;
import com.mad.p03.np2020.routine.DAL.HabitDBHelper;
import com.mad.p03.np2020.routine.DAL.HabitRepetitionDBHelper;
import com.mad.p03.np2020.routine.NavBarHelper;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.background.HabitRepetitionWorker;
import com.mad.p03.np2020.routine.background.HabitWorker;
import com.mad.p03.np2020.routine.helpers.HabitCheckItemClickListener;
import com.mad.p03.np2020.routine.helpers.HabitHorizontalDivider;
import com.mad.p03.np2020.routine.helpers.HabitItemClickListener;
import com.mad.p03.np2020.routine.models.AlarmReceiver;
import com.mad.p03.np2020.routine.models.Habit;
import com.mad.p03.np2020.routine.models.HabitRepetition;
import com.mad.p03.np2020.routine.models.User;

import java.util.Calendar;
import java.util.Locale;

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
    private HabitRepetitionDBHelper habitRepetitionDBHelper;

    //User
    private User user;

    //FAB
    private static FloatingActionButton add_habit;

    private static ImageView prev_indicator, next_indicator;

    private static TextView indicator_num;
    public static TextView remind_text;
    private static ViewSwitcher viewSwitcher;

    private Button add_first_habit;

    private static RelativeLayout nothing_view;

    private static int page_x = 4;

    private GridLayoutManager manager;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        overridePendingTransition(0, 0);
    }

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

        viewSwitcher = findViewById(R.id.switcher);
        habitRecyclerView = findViewById(R.id.habit_recycler_view);
        prev_indicator = findViewById(R.id.habit_indicator_prev);
        next_indicator = findViewById(R.id.habit_indicator_next);
        indicator_num = findViewById(R.id.habit_indicator_number);
        remind_text = findViewById(R.id.habit_remind_text);
        add_habit = findViewById(R.id.add_habit);
        add_first_habit = findViewById(R.id.add_first_habit);
        habitCheckRecyclerView = findViewById(R.id.habit_check_rv);
        nothing_view = findViewById(R.id.nothing_view);

        double screenInches = getScreenInches();
        Log.d(TAG,"Screen inches : " + screenInches);
        if (screenInches <= 5.1){
            float h = getResources().getDimension(R.dimen.habitvs_height);
            ViewGroup.LayoutParams vs_param = viewSwitcher.getLayoutParams();
            vs_param.height = (int) h;
            viewSwitcher.setLayoutParams(vs_param);
            page_x = 2;

            manager = new GridLayoutManager(HabitActivity.this,1, GridLayoutManager.HORIZONTAL, false){
                @Override
                public boolean canScrollHorizontally() {
                    return false;
                }

                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };

        }else{
            manager = new GridLayoutManager(HabitActivity.this,2, GridLayoutManager.VERTICAL, false){
                @Override
                public boolean canScrollHorizontally() {
                    return false;
                }

                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
        }

        // set User
        user = new User();

        // initialise the shared preferences
        initSharedPreferences();

        // initialise the database
        initFirebase();

        // initialise the notification channel
        initialiseHabitNotificationChannel();

        prev_indicator.setOnClickListener(this);
        next_indicator.setOnClickListener(this);
        add_habit.setOnClickListener(this);
        add_first_habit.setOnClickListener(this);

        habit_dbHandler = new HabitDBHelper(this);
        habitRepetitionDBHelper = new HabitRepetitionDBHelper(this);

        habitRecyclerView.setLayoutManager(manager);
        habitRecyclerView.addItemDecoration(new HabitHorizontalDivider(8));
        habitCheckRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        user.setUID(FirebaseAuth.getInstance().getCurrentUser().getUid());

        habitRepetitionDBHelper.repeatingHabit();
        setRepeatingHabit();

        user.setHabitList(habit_dbHandler.getAllHabits());
        // initialise the habitAdapter

        Habit.HabitList habitArrayList = initDummyList(user.getHabitList());

        displayView(habitArrayList);

        habitCheckAdapter = new HabitCheckAdapter(this, habitArrayList, user);
        habitCheckRecyclerView.setAdapter(habitCheckAdapter);
        habitCheckAdapter.setOnItemClickListener(this);

        habitAdapter = new HabitAdapter(this, habitArrayList, user, habitCheckAdapter);
        // set adapter to the recyclerview
        habitRecyclerView.setAdapter(habitAdapter);

        // set onItemClickListener on the habitAdapter
        habitAdapter.setOnItemClickListener(this);

        if(habitArrayList.size() == 0){
            if (viewSwitcher.getCurrentView() != nothing_view){
                viewSwitcher.showNext();
                add_habit.setVisibility(View.VISIBLE);
                prev_indicator.setVisibility(View.INVISIBLE);
                next_indicator.setVisibility(View.INVISIBLE);
                indicator_num.setVisibility(View.INVISIBLE);
                remind_text.setVisibility(View.INVISIBLE);
            }
        }else if (habitArrayList.size() <= page_x){
            if (viewSwitcher.getCurrentView() == nothing_view){
                viewSwitcher.showPrevious();
            }
            add_habit.setVisibility(View.VISIBLE);
            remind_text.setVisibility(View.VISIBLE);
            prev_indicator.setVisibility(View.INVISIBLE);
            next_indicator.setVisibility(View.INVISIBLE);
            indicator_num.setVisibility(View.INVISIBLE);
        }else{
            if (viewSwitcher.getCurrentView() == nothing_view){
                viewSwitcher.showPrevious();
            }
            add_habit.setVisibility(View.VISIBLE);
            prev_indicator.setVisibility(View.INVISIBLE);
            next_indicator.setVisibility(View.VISIBLE);
            indicator_num.setVisibility(View.VISIBLE);
            remind_text.setVisibility(View.VISIBLE);
        }

        indicator_num.setText("1");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Bottom Navigation
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        bottomNavInit(bottomNavigationView);

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

            case R.id.add_first_habit:
                Intent activityName = new Intent(HabitActivity.this, HabitAddActivity.class);
                startActivity(activityName);
                break;

            case R.id.habit_indicator_prev:
                String p_num = indicator_num.getText().toString();
                if (!p_num.equals("1")){
                    int n = Integer.parseInt(p_num)-1;
                    indicator_num.setText(String.valueOf(n));
                    n--;
                    if (n*page_x+1 <= habitAdapter._habitList.size()){
                        next_indicator.setVisibility(View.VISIBLE);
                    }

                    int position = n*page_x;
                    habitRecyclerView.scrollToPosition(position);
                }

                if (indicator_num.getText().toString().equals("1")){
                    prev_indicator.setVisibility(View.INVISIBLE);
                }
                break;

            case R.id.habit_indicator_next:
                String n_num = indicator_num.getText().toString();
                int n = Integer.parseInt(n_num);
                int position = (n) * page_x;
                int arr_size = habitAdapter._habitList.size();
                if (position+1 <= arr_size){
                    n++;
                    indicator_num.setText(String.valueOf(n));
                    if (n*page_x+1 > arr_size) {
                        next_indicator.setVisibility(View.INVISIBLE);
                    }
                    prev_indicator.setVisibility(View.VISIBLE);
                    // 2--> 1 4-->3
                    int i = 3;
                    if (page_x == 2){
                        i = 1;
                    }
                    habitRecyclerView.scrollToPosition(position+i);
                }
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
        habitRepetitionDBHelper.updateCount(habit); //update the habit count in the SQLiteDatabase

        HabitRepetition habitRepetition = habitRepetitionDBHelper.getTodayHabitRepetitionByID(habit.getHabitID());
        writeHabitRepetition_Firebase(habitRepetition, user.getUID(), false);

        int n = checkIncompleteHabits(habitAdapter._habitList);

        if (n == 0){
            remind_text.setText("You have completed all habits today!");
        }else if (n == 1){
            remind_text.setText("You still have 1 habit to do");
        }else{
            remind_text.setText(String.format("You still have %d habits to do",n));
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


        //To set setOnNavigationItemSelectedListener
        NavBarHelper navBarHelper = new NavBarHelper(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(navBarHelper);

        //To have the highlight
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

    }

    private Habit.HabitList initDummyList (Habit.HabitList habitList){

        if (habitList.size() == 0) {return habitList;}
        int size = habitList.size();

        if (page_x == 2){
            if (size % 2 != 0){
                habitList.addItem(new Habit("dummy",0,0,"cyangreen"));
            }
        }else{
            int dummy_size = 4-(size % 4);
            if (dummy_size == 4) {return habitList;}

            for (int i = 0; i<dummy_size; i++){
                habitList.addItem(new Habit("dummy",0,0,"cyangreen"));
            }
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

    public static int checkIncompleteHabits(Habit.HabitList habitList){
        int n = 0;
        for (int i = 0; i < habitList.size(); i++){
            Habit habit = habitList.getItemAt(i);
            if (!habit.getTitle().toLowerCase().equals("dummy") && habit.getOccurrence() > habit.getCount() ){
                n++;
            }
        }
        return n;
    }

    /**
     *
     * This method is used to send the work request
     *  to the HabitWorker(WorkManager) to do the writing firebase action
     *  when the network is connected.
     *
     * @param habitRepetition This parameter is used to get the habit object
     *
     * @param UID This parameter is used to get the userID
     *
     * */
    public void writeHabitRepetition_Firebase(HabitRepetition habitRepetition, String UID, boolean isDeletion){
        Log.i(TAG, "Uploading to Firebase");

        // set constraint that the network must be connected
        Constraints myConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // put data in a data builder
        Data firebaseUserData = new Data.Builder()
                .putString("ID", UID)
                .putString("habitRepetition", habitRepetition_serializeToJson(habitRepetition))
                .putBoolean("deletion", isDeletion)
                .build();

        // send a work request
        OneTimeWorkRequest mywork =
                new OneTimeWorkRequest.Builder(HabitRepetitionWorker.class)
                        .setConstraints(myConstraints)
                        .setInputData(firebaseUserData)
                        .build();

        WorkManager.getInstance(this).enqueue(mywork);
    }

    /**
     *
     * This method is used to serialize a single object. (into Json String)
     *
     * @param habitRepetition This parameter is used to get the habitRepetition object
     *
     * @return String This returns the serialized object.
     *
     * */
    public String habitRepetition_serializeToJson(HabitRepetition habitRepetition) {
        Gson gson = new Gson();
        Log.i(TAG,"Object serialize");
        return gson.toJson(habitRepetition);
    }

    /**
     *
     * This method is used to call to reset the repeat the habit.
     *
     * */
    public void setRepeatingHabit(){
        int id = 873162723;
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.setAction("RepeatingHabit");
        intent.putExtra("id", id);
        // This initialise the pending intent which will be sent to the broadcastReceiver
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int type = AlarmManager.RTC_WAKEUP;

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        int year  = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int date  = cal.get(Calendar.DATE);
        cal.clear();
        cal.set(year, month, date);
        cal.add(Calendar.SECOND,10);

        if (System.currentTimeMillis() > cal.getTimeInMillis()){
            // increment one day to prevent setting for past alarm
            cal.add(Calendar.DATE, 1);
        }

        long time = cal.getTime().getTime();

        Log.d(TAG, "setReminder for RepeatingHabit" + " at " + cal.getTime());
        // AlarmManager set the daily repeating alarm on time chosen by the user.
        // The broadcastReceiver will receive the pending intent on the time.
        assert am != null;
        am.cancel(pi);
        am.setRepeating(type, time, AlarmManager.INTERVAL_DAY, pi);
    }

    public double getScreenInches() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(dm.widthPixels/dm.xdpi,2);
        double y = Math.pow(dm.heightPixels/dm.ydpi,2);

        return Math.sqrt(x+y);
    }

    public static void displayView(Habit.HabitList habitArrayList){

        if(habitArrayList.size() == 0){
            if (viewSwitcher.getCurrentView() != nothing_view){
                viewSwitcher.showNext();
                add_habit.setVisibility(View.VISIBLE);
                prev_indicator.setVisibility(View.INVISIBLE);
                next_indicator.setVisibility(View.INVISIBLE);
                indicator_num.setVisibility(View.INVISIBLE);
                remind_text.setVisibility(View.INVISIBLE);
            }
        }else if (habitArrayList.size() <= page_x){
            if (viewSwitcher.getCurrentView() == nothing_view){
                viewSwitcher.showPrevious();
            }
            add_habit.setVisibility(View.VISIBLE);
            remind_text.setVisibility(View.VISIBLE);
            prev_indicator.setVisibility(View.INVISIBLE);
            next_indicator.setVisibility(View.INVISIBLE);
            indicator_num.setVisibility(View.INVISIBLE);
        }else{
            if (viewSwitcher.getCurrentView() == nothing_view){
                viewSwitcher.showPrevious();
            }
            add_habit.setVisibility(View.VISIBLE);
            indicator_num.setVisibility(View.VISIBLE);
            remind_text.setVisibility(View.VISIBLE);
        }

        int n = checkIncompleteHabits(habitArrayList);

        if (n == 0){
            remind_text.setText("You have completed all habits today!");
        }else if (n == 1){
            remind_text.setText("You still have 1 habit to do");
        }else{
            remind_text.setText(String.format("You still have %d habits to do",n));
        }

    }

}
