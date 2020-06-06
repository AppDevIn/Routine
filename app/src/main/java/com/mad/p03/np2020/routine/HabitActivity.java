package com.mad.p03.np2020.routine;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.mad.p03.np2020.routine.Adapter.HabitAdapter;
import com.mad.p03.np2020.routine.Adapter.HabitGroupAdapter;
import com.mad.p03.np2020.routine.Adapter.OnItemClickListener;
import com.mad.p03.np2020.routine.Class.AlarmReceiver;
import com.mad.p03.np2020.routine.Class.Habit;
import com.mad.p03.np2020.routine.Class.HabitGroup;
import com.mad.p03.np2020.routine.Class.HabitReminder;
import com.mad.p03.np2020.routine.Class.User;
import com.mad.p03.np2020.routine.background.HabitGroupWorker;
import com.mad.p03.np2020.routine.background.HabitWorker;
import com.mad.p03.np2020.routine.database.HabitDBHelper;
import com.mad.p03.np2020.routine.database.HabitGroupDBHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.lang.String.format;

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
    private RecyclerView habitGroupRecyclerView;
    private HabitGroupAdapter groupAdapter;

    // initialise period section
    private int [] period_buttonIDS;
    private String[] period_textList;
    private int[] period_countList;

    // initialise color section
    private int[] color_buttonIDS;
    private int[] color_schemeIDS;
    private String[] colorList;

    // initialise the variable used by timePicker
    private int minutes;
    private int hours;

    // initialise the handler
    private HabitDBHelper habit_dbHandler;
    private HabitGroupDBHelper group_dbhandler;

    // initialise the dateFormat
    private DateFormat dateFormat;

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

        // set values for period section
        period_buttonIDS = new int[]{R.id.daily_period, R.id.weekly_period, R.id.monthly_period, R.id.yearly_period};
        period_textList = new String[]{"DAY", "WEEK", "MONTH", "YEAR"};
        period_countList = new int[]{1, 7, 30, 365};

        // set values for color section
        color_buttonIDS = new int[]{R.id.lightcoral_btn, R.id.slightdesblue_btn, R.id.fadepurple_btn, R.id.cyangreen_btn};
        color_schemeIDS = new int[]{R.color.colorLightCoral, R.color.colorSlightDesBlue, R.color.colorFadePurple, R.color.colorCyanGreen};
        colorList = new String[]{"lightcoral", "slightdesblue", "fadepurple", "cyangreen"};

        // initialise dateFormat
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        // set the HabitDBHelper
        habit_dbHandler = new HabitDBHelper(this);
        // set the HabitGroupDBHelper
        group_dbhandler = new HabitGroupDBHelper(this);

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

    /**
     *
     * This method is used to populate the period button by setting onclickListener on them
     * and it will change the color based on the user's option in period section.
     *
     * @param dialogView This parameter refers to the view
     *
     * @param period This parameter refers to the period list which indicates the chosen period
     *
     * @param period_text This parameter refers to the period TextView in the view
     * */
    public void populatePeriodBtn(final View dialogView, final int[] period, final TextView period_text){
        // set listener on buttons to change the color based on the user's option in period section
        for (final int i :period_buttonIDS){
            final Button btn = dialogView.findViewById(i); // find button in the view
            btn.setBackgroundColor(Color.TRANSPARENT);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = btn.getId(); // retrieve the buttonID

                    for (int i = 0; i < 4; i++){
                        Button _btn = dialogView.findViewById(period_buttonIDS[i]);
                        if (id == period_buttonIDS[i]){
                            // if is selected by the user, add a grey background.
                            _btn.setBackgroundColor(Color.parseColor("#dfdfdf"));
                            period_text.setText(period_textList[i]);  // set the period text
                            period[0] = period_countList[i];  // update the chosen period
                        }else {
                            // if is not selected by the user, remove the grey background.
                            _btn.setBackgroundColor(Color.TRANSPARENT);
                        }

                    }
                }
            });
        }

    }

    /**
     *
     * This method is used to populate the color button by setting onclickListener on them
     * and it will change the color based on the user's option in color section.
     *
     * @param dialogView This parameter refers to the view
     *
     * @param color This parameter refers to the color list which indicates the chosen color
     *
     * */
    public void populateColorBtn(final View dialogView, final String[] color){
        // set listener on buttons to change the color based on the user's option in color section
        for (final int i :color_buttonIDS){
            final Button btn = dialogView.findViewById(i);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = btn.getId();

                    for (int i = 0; i < 4; i++){
                        Button _btn = dialogView.findViewById(color_buttonIDS[i]);
                        if (id == color_buttonIDS[i]){
                            // if is selected by the user, add a black border surrounding the color button.
                            GradientDrawable drawable = new GradientDrawable();
                            drawable.setShape(GradientDrawable.RECTANGLE);
                            drawable.setStroke(5, Color.BLACK);
                            drawable.setColor(getResources().getColor(color_schemeIDS[i]));
                            _btn.setBackground(drawable);
                            color[0] = colorList[i];
                        }else {
                            // if is not selected by the user, remove the black border.
                            _btn.setBackgroundResource(color_schemeIDS[i]);
                        }
                    }
                }
            });
        }

    }

    /**
     *
     * This method is used to initialise the color button at color section since nothing is chosen at first
     *
     * @param dialogView This parameter refers to the view
     *
     * @param color This parameter refers to the color list which indicates the chosen color
     *
     * */
    public void habit_add_initialise_colorSection(final View dialogView, final String[] color){
        // initialise the color button at color section since nothing is chosen at first
        // At default, lightCoral color is chosen.
        // A black border will surround the color.
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setStroke(5, Color.BLACK);
        drawable.setColor(getResources().getColor(R.color.colorLightCoral));
        dialogView.findViewById(R.id.lightcoral_btn).setBackground(drawable);
        color[0] = "lightcoral";
    }

    /**
     *
     * This method is used to initialise the color of "daily" button at period section since nothing is chosen at first
     *
     * @param dialogView This parameter refers to the view
     *
     * @param period This parameter refers to the period list which indicates the chosen period
     *
     * */
    public void habit_add_initialise_periodSection(final View dialogView, final int[] period){
        // initialise the color of "daily" button at period section since nothing is chosen at first
        // At default, "daily" period is chosen at first.
        // A grey background will surround the "daily" button.
        period[0] = 1;
        dialogView.findViewById(R.id.daily_period).setBackgroundColor(Color.parseColor("#dfdfdf"));
    }

    /**
     *
     * This method is used to initialise the holder color on the color section based on the habit holder color of the habit
     *
     * @param dialogView This parameter refers to the view
     *
     * @param habit This parameter refers to the habit object.
     *
     * @param color This parameter refers to the color list which indicates the chosen color
     *
     * */
    public void habit_edit_initialise_colorSection(View dialogView, final Habit habit, final String[] color){
        // initialise the holder color on the color section based on the habit holder color of the habit
        for(int i = 0; i < 4; i++){
            if (colorList[i].equals(habit.getHolder_color())){ // loop to find matched values
                color[0] = colorList[i]; // update the chosen holder color
                // a black border will surround the habit's holder color
                GradientDrawable drawable = new GradientDrawable();
                drawable.setShape(GradientDrawable.RECTANGLE);
                drawable.setStroke(5, Color.BLACK);
                drawable.setColor(getResources().getColor(color_schemeIDS[i]));
                dialogView.findViewById(color_buttonIDS[i]).setBackground(drawable);
                break;
            }
        }

    }

    /**
     *
     * This method is used to initialise the color of button at period section based on the habit period of the habit
     *
     * @param dialogView This parameter refers to the view
     *
     * @param habit This parameter refers to the habit object.
     *
     * @param period This parameter refers to the period list which indicates the chosen period
     *
     * @param period_text This parameter refers to the period TextView in the view
     *
     * */
    public void habit_edit_initialise_periodSection(View dialogView, final Habit habit, final int[] period, final TextView period_text){
        // initialise the color of button at period section based on the habit period of the habit
        for(int i = 0; i < 4; i++){
            if (period_countList[i] == habit.getPeriod()){ // loop to find matches value
                period[0] = period_countList[i]; // update the chosen period
                period_text.setText(period_textList[i]); // set the period text
                // set grey background on the period button
                dialogView.findViewById(period_buttonIDS[i]).setBackgroundColor(getResources().getColor(R.color.colorWhiteGrey));
                break;
            }
        }
    }


    /**
     *
     * This method is used to set the habitReminder.
     *
     * @param name This parameter refers to the title of the reminder.
     *
     * @param minutes This parameter refers to the minutes set of the reminder.
     *
     * @param hours  This parameter refers to the hours set of the reminder.
     *
     * @param custom_txt This parameter refers to the custom message of the reminder.
     *
     * */
    public void setReminder(String name, int minutes, int hours, int id, String custom_txt){
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.setAction("HabitTracker");
        intent.putExtra("Name", name);
        intent.putExtra("id", id);
        intent.putExtra("custom_txt", custom_txt);
        // This initialise the pending intent which will be sent to the broadcastReceiver
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int type = AlarmManager.RTC_WAKEUP;
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MINUTE,minutes);
        c.set(Calendar.HOUR_OF_DAY,hours);
        c.set(Calendar.SECOND,0);

        if (System.currentTimeMillis() > c.getTimeInMillis()){
            // increment one day to prevent setting for past alarm
            c.add(Calendar.DATE, 1);
        }

        long time = c.getTime().getTime();

        Log.d(TAG, "setReminder for ID "+ id + " at " + c.getTime());
        // AlarmManager set the daily repeating alarm on time chosen by the user.
        // The broadcastReceiver will receive the pending intent on the time.
        am.setRepeating(type, time, AlarmManager.INTERVAL_DAY, pi);
    }

    /**
     *
     * This method is used to cancel the habitReminder.
     *
     * @param name This parameter refers to the title of the reminder.
     *
     * @param id This parameter refers to the unique id of the alarm.
     *
     * @param custom_txt This parameter refers to the custom message of the reminder.
     *
     * */
    public void cancelReminder(String name,int id, String custom_txt){
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.setAction("HabitTracker");
        intent.putExtra("Name", name);
        intent.putExtra("id",id);
        intent.putExtra("custom_txt",custom_txt);
        // fill in the same pending intent as when setting it
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Log.d(TAG, "cancelReminder for ID "+ id);
        // Alarm manager cancel the reminder
        am.cancel(pi);
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

    /**
     *
     * This method is used to format text by capitalising the first text of each split text
     *
     * @param text This parameter is used to get the text
     *
     * @return String This returns the formatted text
     * */
    public String capitalise(String text){
        String txt = "";
        String[] splited = text.split("\\s+");
        for (String s: splited){
            txt += s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase() + " ";
        }
        return txt;
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

    /**
     *
     * This method is used to get the unique habit reminder id
     *
     * @return int It will return the unique id.
     * */
    public int getUniqueHabitReminderID(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int id = sharedPreferences.getInt("alarm_id",-1);
        int _new = ++id;
        editor.putInt("alarm_id", _new);
        Log.d(TAG, "getUniqueHabitReminderID: " + _new);
        editor.apply();
        return id;
    }

    /** This method is used to initialise the firebase */
    private void initFirebase() {
        user.setUID(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Log.i(TAG, "Getting firebase for User ID " + user.getUID());
    }

    /**
     *
     * This method is used to send the work request
     *  to the HabitWorker(WorkManager) to do the writing firebase action
     *  when the network is connected.
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

        // send a work request
        OneTimeWorkRequest mywork =
                new OneTimeWorkRequest.Builder(HabitWorker.class)
                        .setConstraints(myConstraints)
                        .setInputData(firebaseUserData)
                        .build();

        WorkManager.getInstance(this).enqueue(mywork);
    }

    /**
     *
     * This method is used to send the work request
     *  to the HabitGroupWorker(WorkManager) to do the writing firebase action
     *  when the network is connected.
     *
     * @param habitGroup This parameter is used to get the habitGroup object
     *
     * @param UID This parameter is used to get the userID
     *
     * */
    public void writeHabitGroup_Firebase(HabitGroup habitGroup, String UID){
        Log.i(TAG, "Uploading to Firebase");

        // set constraint that the network must be connected
        Constraints myConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // put data in a data builder
        Data firebaseUserData = new Data.Builder()
                .putString("ID", UID)
                .putString("habitData", habitGroup_serializeToJson(habitGroup))
                .build();

        // send a work request
        OneTimeWorkRequest mywork =
                new OneTimeWorkRequest.Builder(HabitGroupWorker.class)
                        .setConstraints(myConstraints)
                        .setInputData(firebaseUserData)
                        .build();

        WorkManager.getInstance(this).enqueue(mywork);
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
                Intent activityName = new Intent(HabitActivity.this, AddHabitActivity.class);
                startActivity(activityName);
//                Log.d(TAG, "Add Habit");
//                // Create an alert dialog (add habit)
//                final AlertDialog.Builder builder = new AlertDialog.Builder(HabitActivity.this,R.style.CustomAlertDialog); // use the custom alert dialog type
//                ViewGroup viewGroup = findViewById(android.R.id.content);
//                final View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.add_habit, viewGroup, false); // inflate the layout
//                builder.setView(dialogView); // set the view of the builder
//                final AlertDialog alertDialog = builder.create(); // build the dialog (add habit)
//
//                // initialise widgets
//                final TextView menu_count = dialogView.findViewById(R.id.menu_count);
//                final TextView habit_name = dialogView.findViewById(R.id.add_habit_name);
//                final TextView habit_occur = dialogView.findViewById(R.id.habit_occurence);
//                final TextView period_text = dialogView.findViewById(R.id.period_txt);
//                final TextView habit_reminder_indicate_text = dialogView.findViewById(R.id.reminder_indicate_text);
//                final TextView group_indicate_text = dialogView.findViewById(R.id.group_indicate_text);
//                final ImageButton add_btn = dialogView.findViewById(R.id.menu_add_count);
//                final ImageButton minus_btn = dialogView.findViewById(R.id.menu_minus_count);
//
//                // initialise period section
//                final int[] period = new int[1]; // this is used to store the chosen period
//                populatePeriodBtn(dialogView, period, period_text);
//                habit_add_initialise_periodSection(dialogView,period);
//
//                // initialise color section
//                final String[] color = new String[1]; // this is used to store the chosen color
//                populateColorBtn(dialogView,color);
//                habit_add_initialise_colorSection(dialogView,color);
//
//                // set onClickListener on the add count button
//                add_btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String cnt = menu_count.getText().toString();
//                        // add the count by 1
//                        int count = Integer.parseInt(cnt);
//                        count++;
//                        // set the count in the TextView
//                        menu_count.setText(String.valueOf(count));
//                    }
//                });
//
//                // set onClickListener on the minus count button
//                minus_btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String cnt = menu_count.getText().toString();
//                        // minus the count by 1 if count > 0
//                        int count = Integer.parseInt(cnt);
//                        if (count > 0){
//                            count--;
//                        }
//                        // set the count in the TextView
//                        menu_count.setText(String.valueOf(count));
//                    }
//                });
//
//
//                // Group section
//                // initialise group
//                final String[] _grp_name = {null}; // this is used to store the chosen group name
//                final long[] _grp_id = new long[1]; // this is used to store the chosen group id
//
//                // set onClickListener on the group indicate text
//                group_indicate_text.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        // Create an alert dialog (group view)
//                        final AlertDialog.Builder builder = new AlertDialog.Builder(HabitActivity.this,R.style.CustomAlertDialog); // use the custom alert dialog type
//                        LayoutInflater inflater = getLayoutInflater();
//                        View convertView = inflater.inflate(R.layout.habit_group, null); // inflate the layout
//
//                        // initialise widgets
//                        ImageView close = convertView.findViewById(R.id.habit_group_view_close);
//                        Button cancel = convertView.findViewById(R.id.habit_group_view_cancel);
//                        Button create_grp = convertView.findViewById(R.id.habit_group_view_create_group);
//                        final TextView curr_grp = convertView.findViewById(R.id.current_grp);
//
//                        // set the current group text based on the chosen group
//                        if (_grp_name[0] != null){
//                            // set the chosen group on TextView
//                            curr_grp.setText(_grp_name[0]);
//                        }else{
//                            // set "None" on TextView
//                            curr_grp.setText("None");
//                        }
//
//                        // inflate habitGroup RecyclerView
//                        habitGroupRecyclerView = convertView.findViewById(R.id.habit_recycler_view);
//                        habitGroupRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//                        // initialise group adapter
//                        groupAdapter= new HabitGroupAdapter(group_dbhandler.getAllGroups(),getApplicationContext());
//                        // set groupAdapter on RecyclerView
//                        habitGroupRecyclerView.setAdapter(groupAdapter);
//                        builder.setView(convertView); // set the view of the builder
//                        final AlertDialog alertDialog = builder.create(); // build the dialog (group view)
//
//                        // set OnItemClickListener on group adapter
//                        groupAdapter.setOnItemClickListener(new OnItemClickListener() {
//                            @Override
//                            public void onItemClick(int position) {
//                                // when a holder is clicked
//
//                                // retrieve the group of the holder
//                                HabitGroup grp = groupAdapter._habitGroupList.get(position);
//                                group_indicate_text.setText(grp.getGrp_name()); // update the chosen group on the group_indicate_text TextView
//                                _grp_name[0] = grp.getGrp_name(); // update the chosen group name
//                                _grp_id[0] = grp.getGrp_id(); // update the chosen group id
//                                alertDialog.dismiss(); // dismiss the dialog (group view)
//                            }
//                        });
//
//                        // set onClickListener on close button
//                        close.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                alertDialog.dismiss(); // dismiss the dialog (group view)
//                            }
//                        });
//
//                        // set onClickListener on cancel group button
//                        cancel.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                // cancel the existing group
//                                _grp_name[0] = null; // update the chosen group name to null
//                                _grp_id[0] = -1; // update the chosen group id to negative(ineffective)
//                                group_indicate_text.setText("NONE"); // set "None" on group_indicate_text
//                                alertDialog.dismiss(); // dismiss the dialog (group view)
//                            }
//                        });
//
//                        // set onClickListener on create group button
//                        create_grp.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                // create an alert dialog (create group)
//                                AlertDialog.Builder builder = new AlertDialog.Builder(HabitActivity.this);
//                                ViewGroup viewGroup = findViewById(android.R.id.content);
//                                View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.habit_group_create, viewGroup, false); // inflate the view
//                                builder.setView(dialogView); // set the view of the builder
//                                final AlertDialog alertDialog = builder.create(); // build the dialog (create group)
//
//                                // initialise widgets
//                                final Button cancelBtn = dialogView.findViewById(R.id.group_cancel);
//                                final Button saveBtn = dialogView.findViewById(R.id.group_save);
//                                final EditText name = dialogView.findViewById(R.id.creating_group_name);
//
//                                // setonClickListener on cancel button
//                                cancelBtn.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        alertDialog.dismiss(); // dismiss the dialog (create group)
//                                    }
//                                });
//
//                                // setonClickListener on save button
//                                saveBtn.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        // Retrieve the values from the input field
//                                        String grp_name = name.getText().toString();
//                                        HabitGroup grp = new HabitGroup(grp_name);
//
//                                        // Insert the group into SQLiteDatabase
//                                        long grp_id = group_dbhandler.insertGroup(grp);
//                                        // The insert process is success if group id returned is not equal to 1
//
//                                        if (grp_id != -1){ // check if group id returned is not equal to 1
//                                            grp.setGrp_id(grp_id); // set the group id to the group object
//                                            groupAdapter._habitGroupList.add(grp); // add the group to the adapter list
//                                            groupAdapter.notifyDataSetChanged(); // notify data set has changed
//                                            writeHabitGroup_Firebase(grp, user.getUID()); // write habitGroup to firebase
//                                            Toast.makeText(HabitActivity.this, "New group has been created.", Toast.LENGTH_SHORT).show();
//                                        }
//
//                                        alertDialog.dismiss(); // dismiss the dialog after the process (create group)
//                                    }
//                                });
//
//                                alertDialog.show(); // show the create group dialog
//                            }
//                        });
//
//                        alertDialog.show(); // show the group view dialog
//                    }
//                });
//
//
//                // Reminder section
//                // initialise reminder
//                final int[] chosen_hours = new int[1]; // this is used to store the chosen reminder hours
//                final int[] chosen_minutes = new int[1]; // this is used to store the chosen reminder minutes
//                final String[] custom_text = {""}; // this is used to store the chosen reminder's custom text
//                final boolean[] reminder_flag = {false}; // this is used to store the reminder flag which indicates active or inactive of the reminder
//
//                // set onClickListener on habit_reminder_indicate_text
//                habit_reminder_indicate_text.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        // Create an alert dialog (set reminder)
//                        final AlertDialog.Builder builder = new AlertDialog.Builder(HabitActivity.this,R.style.CustomAlertDialog); // use the custom alert dialog type
//                        ViewGroup viewGroup = findViewById(android.R.id.content);
//                        final View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.habit_reminder_view, viewGroup, false); // inflate the view
//                        builder.setView(dialogView); // set the view of builder
//                        final AlertDialog alertDialog = builder.create(); // build the dialog (set reminder)
//
//                        // initialise widgets
//                        final ImageView close_btn = dialogView.findViewById(R.id.habit_reminder_view_close);
//                        final Switch reminder_switch = dialogView.findViewById(R.id.habit_reminder_view_switch);
//                        final TextView reminder_displayTime = dialogView.findViewById(R.id.habit_reminder_view_displaytime);
//                        final TimePicker timePicker = dialogView.findViewById(R.id.habit_reminder_view_timepicker);
//                        final TextView _custom_text = dialogView.findViewById(R.id.habit_reminder_view_customtext);
//                        final ImageView save_btn = dialogView.findViewById(R.id.habit_reminder_view_save);
//
//                        // to determine what should be displayed on timePicker and time indicate field
//                        if (reminder_flag[0]){ // if the flag is true which indicates active reminder
//                            Calendar c = Calendar.getInstance();
//                            // display time and timePicker based on the chosen hours and minutes
//                            c.set(Calendar.HOUR_OF_DAY, chosen_hours[0]);
//                            c.set(Calendar.MINUTE, chosen_minutes[0]);
//                            timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
//                            timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
//                            reminder_switch.setChecked(true); // set the switch checked as the reminder is active
//                            reminder_displayTime.setText(format("%d:%d",chosen_hours[0],chosen_minutes[0])); // set the text based on the chosen timing
//
//                        }else{ // if the flag is false which indicates inactive reminder
//                            // set the minutes and hours based on the current time
//                            if (Build.VERSION.SDK_INT <= 23) {
//                                minutes = timePicker.getCurrentMinute(); // before api level 23
//                                hours = timePicker.getCurrentHour(); // before api level 23
//                            }else{
//                                minutes = timePicker.getMinute(); // after api level 23
//                                hours  = timePicker.getHour(); // after api level 23
//                            }
//                            reminder_switch.setChecked(false); // set the switch unchecked as the reminder is inactive
//                            reminder_displayTime.setText(format("%d:%d",hours,minutes)); // set the text based on the chosen timing
//                        }
//
//                        // leave the custom text input field as blank if nothing has been filled and recorded down
//                        // or set the custom text based on the chosen custom text
//                        if (custom_text[0] != ""){
//                            _custom_text.setText(custom_text[0]);
//                        }
//
//                        // set onTimeChangedListener on timePicker
//                        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
//                            @Override
//                            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
//
//                                if (Build.VERSION.SDK_INT <= 23) {
//                                    minutes = timePicker.getCurrentMinute(); // before api level 23
//                                    hours = timePicker.getCurrentHour(); // before api level 23
//                                }else{
//                                    minutes = timePicker.getMinute(); // after api level 23
//                                    hours  = timePicker.getHour(); // after api level 23
//                                }
//                                reminder_displayTime.setText(format("%d:%d",hours,minutes)); // update the text based on the chosen timing
//                            }
//                        });
//
//                        // set onClickListener on save button
//                        save_btn.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                if (reminder_switch.isChecked()){ // if switch is switched, turn the reminder active
//                                    // set the text on habit_reminder_indicate_text based on chosen timing
//                                    habit_reminder_indicate_text.setText((format("%d:%d",hours,minutes)));
//                                    if (_custom_text.getText().toString() != ""){
//                                        // update the chosen custom text if it is not blank
//                                        custom_text[0] = _custom_text.getText().toString();
//                                    }
//                                    reminder_flag[0] = true; // turn on the flag
//                                    chosen_hours[0] = hours; // record down the chosen hours
//                                    chosen_minutes[0] = minutes; // record down the chosen minutes
//
//                                }else{ // if switch is unchecked, turn the reminder inactive
//                                    reminder_flag[0] = false; // turn off thr flag
//                                    habit_reminder_indicate_text.setText("NONE"); // set "None" on habit_reminder_indicate_text
//                                    custom_text[0] = ""; // reset the custom text as nothing
//                                }
//
//                                alertDialog.dismiss(); // dismiss the dialog (set reminder)
//                            }
//                        });
//
//                        // set onClickListener on close button
//                        close_btn.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                alertDialog.dismiss(); // dismiss the dialog (set reminder)
//                            }
//                        });
//
//                        alertDialog.show(); // show the alert dialog (set reminder)
//                    }
//                });
//
//                // set onClickListener on close button
//                Button buttonClose = dialogView.findViewById(R.id.habit_close);
//                buttonClose.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        alertDialog.dismiss(); // dismiss the add_habit dialog
//                    }
//                });
//
//                // set onClickListener on create button
//                Button buttonOk = dialogView.findViewById(R.id.create_habit);
//                buttonOk.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String name = habit_name.getText().toString(); // retrieve the title of the habit from the input field
//                        if (name.equalsIgnoreCase("")){
//                            // set Error Message if the user never input the habit title
//                            habit_name.setError("Please enter habit name");
//                            return;
//                        }
//
//                        int occur = Integer.parseInt(habit_occur.getText().toString()); // retrieve the occurrence of the habit from the input field
//                        int cnt = Integer.parseInt(menu_count.getText().toString()); // retrieve the count of the habit from the input field
//
//                        Date date = new Date(); // call the date function
//                        HabitReminder hr = null;
//                        if (reminder_flag[0]){ // set a reminder if the reminder is in active
//                            int id = getUniqueHabitReminderID(); // retrieve the unique habitReminder ID
//                            String txt = custom_text[0]; // retrieve the custom text
//                            setReminder(name, chosen_minutes[0], chosen_hours[0], id, txt); // set the reminder
//                            hr = new HabitReminder(name, id, chosen_minutes[0], chosen_hours[0], txt); // set a new habitReminder object
//                        }
//
//                        HabitGroup hg = null;
//                        if (_grp_name[0] != null){ // set a new habitGroup object if the user chooses something for the group name
//                            hg = new HabitGroup(_grp_id[0], _grp_name[0]);
//                        }
//
//                        // create a habit object
//                        Habit habit = new Habit(name, occur, cnt, period[0], dateFormat.format(date),color[0],hr,hg);
//
//                        // insert the habit into SQLiteDatabase
//                        long habitID = habit_dbHandler.insertHabit(habit, user.getUID());
//                        // The insert process is success if habit id returned is not equal to 1
//
//                        if (habitID != -1){ // if habitID returned is legit
//                            habit.setHabitID(habitID); // set the id to the habit
//                            habitAdapter._habitList.addItem(habit); // add the habit into the adapter list
//                            habitAdapter.notifyDataSetChanged(); // notify the data set has changed
//                            writeHabit_Firebase(habit, user.getUID(), false); // write habit to firebase
//
//                            Log.d(TAG, "onClick: "+habit.getHabitID());
//                            // toast a message to alert the habit has been created
//                            Toast.makeText(HabitActivity.this, format("Habit %shas been created.",capitalise(name)), Toast.LENGTH_SHORT).show();
//                        }
//
//                        alertDialog.dismiss(); // dismiss the add_habit dialog
//                    }
//                });
//
//                alertDialog.show(); // show the add_habit dialog
//
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
//
//        // Create an alert dialog for habitView
//        final AlertDialog.Builder builder = new AlertDialog.Builder(HabitActivity.this,R.style.CustomAlertDialog); // use the custom alert dialog type
//        ViewGroup viewGroup = findViewById(android.R.id.content);
//        View dialogView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.habit_view, viewGroup, false); // inflate view
//        builder.setView(dialogView); // set view to builder
//        final AlertDialog alertDialog = builder.create(); // build the dialog (habit view)
//
//        // initialise widgets
//        final TextView title = dialogView.findViewById(R.id.habit_view_title);
//        final TextView cnt = dialogView.findViewById(R.id.habit_view_count);
//        final ImageButton reduceBtn = dialogView.findViewById(R.id.habit_view_reduce);
//        final ImageButton addBtn = dialogView.findViewById(R.id.habit_view_add);
//        final ImageButton modifyBtn = dialogView.findViewById(R.id.habit_view_modify);
//        final ImageButton closeBtn = dialogView.findViewById(R.id.habit_view_close);
//        final ImageButton editBtn = dialogView.findViewById(R.id.habit_view_edit);
//        final ImageButton deletebtn = dialogView.findViewById(R.id.habit_view_delete);
//        final TextView occurrence = dialogView.findViewById(R.id.habitOccurence);
//        final TextView cnt2 = dialogView.findViewById(R.id.habitCount);
//        final TextView period = dialogView.findViewById(R.id.habit_period);
//        final LinearLayout habit_view_upper = dialogView.findViewById(R.id.habit_view_upper);
//
//        // set text on input fields based on the habit object
//        title.setText(habit.getTitle());
//        cnt.setText(String.valueOf(habit.getCount()));
//        occurrence.setText(String.valueOf(habit.getOccurrence()));
//        cnt2.setText(String.valueOf(habit.getCount()));
//        period.setText(habit.returnPeriodText(habit.getPeriod()));
//
//        // set the transparent background of the button
//        reduceBtn.setBackgroundColor(Color.TRANSPARENT);
//        addBtn.setBackgroundColor(Color.TRANSPARENT);
//        modifyBtn.setBackgroundColor(Color.TRANSPARENT);
//        closeBtn.setBackgroundColor(Color.TRANSPARENT);
//        editBtn.setBackgroundColor(Color.TRANSPARENT);
//        deletebtn.setBackgroundColor(Color.TRANSPARENT);
//
//        // set the background color of upper habit view as the holder color
//        habit_view_upper.setBackgroundColor(getResources().getColor(habit.returnColorID(habit.getHolder_color())));
//
//        // set onClickListener on close button
//        closeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                alertDialog.dismiss(); // dismiss the habit view dialog
//            }
//        });
//
//        // set onClickListener on add count button
//        addBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "Habit: Add Count");
//                // trigger the Habit class add count method
//                habit.addCount(); // increase the count by 1
//                habitAdapter.notifyDataSetChanged(); // notify data set has changed
//                habit_dbHandler.updateCount(habit); // update the habit data in SQliteDatabase
//                writeHabit_Firebase(habit, user.getUID(), false); // write the habit data into firebase
//                cnt.setText(String.valueOf(habit.getCount())); // set text on the count TextView
//                cnt2.setText(String.valueOf(habit.getCount())); // set text on the count TextView
//            }
//        });
//
//        reduceBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "Habit: Minus Count");
//                // trigger the Habit class minus count method
//                habit.minusCount(); // minus the count by 1
//                habitAdapter.notifyDataSetChanged(); // notify data set has changed
//                habit_dbHandler.updateCount(habit); // update the habit data in SQliteDatabase
//                writeHabit_Firebase(habit, user.getUID(), false); // write the habit data into firebase
//                cnt.setText(String.valueOf(habit.getCount())); // set text on the count TextView
//                cnt2.setText(String.valueOf(habit.getCount())); // set text on the count TextView
//            }
//        });
//
//        modifyBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "Habit: Modify Count");
//                // Create an alert dialog (modify count)
//                AlertDialog.Builder builder = new AlertDialog.Builder(HabitActivity.this); // initialise the builder
//                ViewGroup viewGroup = findViewById(android.R.id.content);
//                View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.habit_view_modifycnt_dialog, viewGroup, false); // inflate the view
//                builder.setView(dialogView); //set view to the builder
//                final AlertDialog alertDialog = builder.create(); // build the alert dialog
//                alertDialog.show(); // show the alert dialog (modify count)
//
//                // initialise the widgets
//                final TextView dialog_title = dialogView.findViewById(R.id.habit_view_dialog_title);
//                final Button cancelBtn = dialogView.findViewById(R.id.cancel_dialog);
//                final Button saveBtn = dialogView.findViewById(R.id.save_dialog);
//                final EditText dialog_cnt = dialogView.findViewById(R.id.dialog_cnt);
//
//                // set text on the input fields based on the habit
//                dialog_title.setText(habit.getTitle());
//                dialog_cnt.setHint(cnt.getText().toString());
//
//                // set onClickListener on the cancel button
//                cancelBtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        alertDialog.dismiss(); // dismiss the alert dialog (modify count)
//                    }
//                });
//
//                // set onClickListener on the save button
//                saveBtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        int dialogCnt = Integer.parseInt(dialog_cnt.getText().toString()); // retrieve the count from the input field
//                        // trigger the habit class modify count method
//                        habit.modifyCount(dialogCnt); // modify the count
//                        habitAdapter.notifyDataSetChanged(); // notify the data set has changed in the adapter
//                        habit_dbHandler.updateCount(habit); // update the habit data in SQLiteDatabase
//                        writeHabit_Firebase(habit, user.getUID(), false); // write habit data to firebase
//                        cnt.setText(String.valueOf(habit.getCount()));  // set text on the count TextView
//                        cnt2.setText(String.valueOf(habit.getCount()));  // set text on the count TextView
//                        alertDialog.dismiss(); // dismiss the alert dialog (modify count)
//                    }
//                });
//            }
//        });
//
//        // set onClickListener on edit button
//        editBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // create an alert dialog (edit habit)
//                final AlertDialog.Builder builder = new AlertDialog.Builder(HabitActivity.this,R.style.CustomAlertDialog); // use the custom alert dialog type
//                ViewGroup viewGroup = findViewById(android.R.id.content);
//                final View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.habit_edit, viewGroup, false); // inflate the view
//                builder.setView(dialogView); // set view to the builder
//                final AlertDialog alertDialog = builder.create(); // build the dialog
//
//                // initialise the widgets
//                final TextView habit_name = dialogView.findViewById(R.id.add_habit_name);
//                final TextView habit_occur = dialogView.findViewById(R.id.habit_occurence);
//                final TextView period_text = dialogView.findViewById(R.id.period_txt);
//                final TextView habit_reminder_indicate_text = dialogView.findViewById(R.id.reminder_indicate_text);
//                final TextView group_indicate_text = dialogView.findViewById(R.id.group_indicate_text);
//                Button buttonClose = dialogView.findViewById(R.id.habit_close);
//                Button buttonOk = dialogView.findViewById(R.id.create_habit);
//
//
//
//                // initialise period section
//                final int[] _period = new int[1]; // this is used to store the chosen period
//                populatePeriodBtn(dialogView, _period, period_text);
//                habit_edit_initialise_periodSection(dialogView, habit, _period, period_text);
//
//                // initialise color section
//                final String[] _color = new String[1]; // this is used to store the chosen color
//                populateColorBtn(dialogView, _color);
//                habit_edit_initialise_colorSection(dialogView, habit, _color);
//
//                // set text on the input fields based on habit
//                habit_name.setText(habit.getTitle());
//                habit_occur.setText(String.valueOf(habit.getOccurrence()));
//
//                // Retrieve tha habitGroup object
//                final HabitGroup habitGroup = habit.getGroup();
//
//                if (habitGroup != null ){ // if habitGroup object is not null, set text based on its group name on the TextView
//                    group_indicate_text.setText(habitGroup.getGrp_name());
//                }else{ // if habitGroup object is null, set "NONE" name on the TextView
//                    group_indicate_text.setText("NONE");
//                }
//
//                // Group section
//                // initialise group
//                final String[] _grp_name = {null};  // this is used to store the chosen group name
//                final long[] _grp_id = new long[1]; // this is used to store the chosen group id
//                final boolean[] modified_grp = {false}; // this is to record whether the group is modified (meaning user choose a group in the section)
//                final boolean[] _cancel = {false}; // this is to record whether the group is cancelled (meaning the HabitGroup is null)
//
//                // set onClickListener on the group indicate text
//                group_indicate_text.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Log.d(TAG, "Edit Habit: Group section ");
//                        // Create an alert dialog (habitGroup view)
//                        final AlertDialog.Builder builder = new AlertDialog.Builder(HabitActivity.this,R.style.CustomAlertDialog); // use the custom alert dialog type
//                        LayoutInflater inflater = getLayoutInflater();
//                        View convertView = inflater.inflate(R.layout.habit_group, null);  // inflate the layout
//
//                        // initialise widgets
//                        ImageView close = convertView.findViewById(R.id.habit_group_view_close);
//                        final Button cancel = convertView.findViewById(R.id.habit_group_view_cancel);
//                        Button create_grp = convertView.findViewById(R.id.habit_group_view_create_group);
//                        TextView curr_grp = convertView.findViewById(R.id.current_grp);
//
//                        // set the current group text based on the chosen group
//                        if (habitGroup != null && !modified_grp[0]){
//                            // set the current group text based on the habitGroup object if the group is not modified
//                            curr_grp.setText(habitGroup.getGrp_name());   // set the habitGroup object
//                        }else if (modified_grp[0] && _grp_name[0] != null){
//                            // set the current group text based on the chosen group name when the group is modified
//                            curr_grp.setText(_grp_name[0]); // set the chosen group name
//                        }else{
//                            // if no habitGroup object is found and the group is not modified
//                            curr_grp.setText("None"); // set "None"
//                        }
//
//
//                        // inflate habitGroup RecyclerView
//                        habitGroupRecyclerView = convertView.findViewById(R.id.habit_recycler_view);
//                        habitGroupRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//                        // initialise group adapter
//                        groupAdapter= new HabitGroupAdapter(group_dbhandler.getAllGroups(), getApplicationContext());
//                        // set groupAdapter on RecyclerView
//                        habitGroupRecyclerView.setAdapter(groupAdapter);
//                        builder.setView(convertView); // set the view of the builder
//                        final AlertDialog alertDialog = builder.create(); // build the dialog
//
//                        // set OnItemClickListener on group adapter
//                        groupAdapter.setOnItemClickListener(new OnItemClickListener() {
//                            @Override
//                            public void onItemClick(int position) {
//                                // when a holder is clicked
//
//                                // retrieve the group of the holder
//                                HabitGroup grp = groupAdapter._habitGroupList.get(position);
//                                group_indicate_text.setText(grp.getGrp_name()); // update the chosen group on the group_indicate_text TextView
//                                _grp_name[0] = grp.getGrp_name(); // update the chosen group name
//                                _grp_id[0] = grp.getGrp_id(); // update the chosen group id
//                                modified_grp[0] = true; // record down the group has been modified
//                                _cancel[0] = false; // record down the group is not cancelled
//                                alertDialog.dismiss(); // dismiss the alert dialog (habitGroup view)
//                            }
//                        });
//
//                        // set onClickListener on close button
//                        close.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                alertDialog.dismiss(); // dismiss the alert dialog (habitGroup view)
//                            }
//                        });
//
//                        // set onClickListener on cancel group button
//                        cancel.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                // cancel the existing group
//                                _grp_name[0] = null; // update the chosen group name to null
//                                _grp_id[0] = -1;  // update the chosen group id to negative(ineffective)
//                                group_indicate_text.setText("NONE"); // set "None" on group_indicate_text
//                                _cancel[0] = true; // record down the group is cancelled
//                                alertDialog.dismiss(); // dismiss the alert dialog (habitGroup view)
//                            }
//                        });
//
//                        // set onClickListener on create group button
//                        create_grp.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                // create an alert dialog (create group)
//                                AlertDialog.Builder builder = new AlertDialog.Builder(HabitActivity.this);
//                                ViewGroup viewGroup = findViewById(android.R.id.content);
//                                View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.habit_group_create, viewGroup, false); // inflate the view
//                                builder.setView(dialogView); // set the view of the builder
//                                final AlertDialog alertDialog = builder.create(); // build the dialog
//
//                                // initialise widgets
//                                final Button cancelBtn = dialogView.findViewById(R.id.group_cancel);
//                                final Button saveBtn = dialogView.findViewById(R.id.group_save);
//                                final EditText name = dialogView.findViewById(R.id.creating_group_name);
//
//                                // setonClickListener on cancel button
//                                cancelBtn.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        alertDialog.dismiss(); // dismiss the dialog (create group)
//                                    }
//                                });
//
//                                // setonClickListener on save button
//                                saveBtn.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        // Retrieve the values from the input field
//                                        String grp_name = name.getText().toString();
//                                        HabitGroup grp = new HabitGroup(grp_name);
//
//                                        // Insert the group into SQLiteDatabase
//                                        long grp_id = group_dbhandler.insertGroup(grp);
//                                        // The insert process is success if group id returned is not equal to 1
//
//                                        if (grp_id != -1){ // check if group id returned is not equal to 1
//                                            grp.setGrp_id(grp_id); // set the group id to the group object
//                                            groupAdapter._habitGroupList.add(grp); // add the group to the adapter list
//                                            groupAdapter.notifyDataSetChanged(); // notify data set has changed
//                                            writeHabitGroup_Firebase(grp, user.getUID()); // write habitGroup to firebase
//                                            Toast.makeText(HabitActivity.this, "New group has been created.", Toast.LENGTH_SHORT).show();
//                                        }
//
//                                        alertDialog.dismiss(); // dismiss the dialog (create group)
//                                    }
//                                });
//
//                                alertDialog.show();  // show the create group dialog
//                            }
//                        });
//
//                        alertDialog.show(); // show the group view dialog
//                    }
//                });
//
//                // Reminder section
//
//                // initialise reminder
//                final boolean[] reminder_flag = {false};  // this is used to store the reminder flag which indicates active or inactive of the reminder
//                final String[] txt = {""}; // this is used to store the chosen reminder's custom text
//                final boolean[] modified_reminder = {false}; // this is to record whether the reminder is modified (meaning user choose a reminder in the section)
//                final int[] chosen_hours = new int[1]; // this is used to store the chosen reminder hours
//                final int[] chosen_minutes = new int[1]; // this is used to store the chosen reminder minutes
//
//                // Retrieve tha habitGroup object
//                final HabitReminder habitReminder = habit.getHabitReminder();
//
//                if (habitReminder != null){ // if habitReminder object is not null, set text based on its reminder timing on the TextView
//                    habit_reminder_indicate_text.setText((format("%d:%d",habitReminder.getHours(),habitReminder.getMinutes()))); // set the timing on the TextView
//                    reminder_flag[0] = true; // store the active reminder flag
//                    minutes = habitReminder.getMinutes(); // retrieve the minutes from the habitReminder object
//                    hours = habitReminder.getHours(); // retrieve the hours from the habitReminder object
//                    txt[0] = habitReminder.getCustom_text(); // retrieve the custom text from the habitReminder object
//                }
//
//                // set onClickListener on habit_reminder_indicate_text
//                habit_reminder_indicate_text.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Log.d(TAG, "Edit Habit: Reminder section ");
//                        // Create an alert dialog (edit reminder)
//                        final AlertDialog.Builder builder = new AlertDialog.Builder(HabitActivity.this,R.style.CustomAlertDialog); // use the custom alert dialog type
//                        ViewGroup viewGroup = findViewById(android.R.id.content);
//                        final View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.habit_reminder_view, viewGroup, false); // inflate the view
//                        builder.setView(dialogView); // set the view of builder
//                        final AlertDialog alertDialog = builder.create(); // build the dialog
//
//                        // initialise widgets
//                        final ImageView close_btn = dialogView.findViewById(R.id.habit_reminder_view_close);
//                        final Switch reminder_switch = dialogView.findViewById(R.id.habit_reminder_view_switch);
//                        final TextView reminder_displayTime = dialogView.findViewById(R.id.habit_reminder_view_displaytime);
//                        final TimePicker timePicker = dialogView.findViewById(R.id.habit_reminder_view_timepicker);
//                        final TextView _custom_text = dialogView.findViewById(R.id.habit_reminder_view_customtext);
//                        final ImageView save_btn = dialogView.findViewById(R.id.habit_reminder_view_save);
//
//                        // to determine what should be displayed on timePicker and time indicate field
//                        if (!reminder_flag[0]){  // if the flag is false which indicates inactive reminder
//                            // set the minutes and hours based on the current time
//                            if (Build.VERSION.SDK_INT <= 23) {
//                                minutes = timePicker.getCurrentMinute(); // before api level 23
//                                hours = timePicker.getCurrentHour(); // before api level 23
//                            }else{
//                                minutes = timePicker.getMinute(); // after api level 23
//                                hours  = timePicker.getHour(); // after api level 23
//                            }
//                            reminder_switch.setChecked(false); // set the switch unchecked as the reminder is inactive
//                            reminder_displayTime.setText(format("%d:%d",hours,minutes)); // set the text based on the chosen timing
//
//                        }else if (habitReminder != null && !modified_reminder[0]){
//                            // set the current reminder based on the reminder object if the reminder is not modified
//                            _custom_text.setText(habitReminder.getCustom_text()); // set the custom text based on the habitReminder object
//                            reminder_switch.setChecked(true); // check the switch as reminder is active
//                            reminder_displayTime.setText(habit_reminder_indicate_text.getText().toString()); // set the reminder timing based on the habitReminder object
//                            // setting the timePicker
//                            Calendar c = Calendar.getInstance();
//                            c.set(Calendar.HOUR_OF_DAY,habitReminder.getHours());
//                            c.set(Calendar.MINUTE, habitReminder.getMinutes());
//                            // set the minutes and hours based on the habitReminder object
//                            timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
//                            timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
//
//                        }else if (modified_reminder[0] && reminder_flag[0]){
//                            // if the reminder is modified and the flag is active
//                            reminder_switch.setChecked(true); // check the switch as reminder is active
//                            _custom_text.setText(txt[0]); // set the custom text based on the stored and chosen custom text value
//                            reminder_displayTime.setText(format("%d:%d",hours,minutes)); // set the reminder timing based on the stored and chosen custom text value
//                            // setting the timePicker
//                            Calendar c = Calendar.getInstance();
//                            c.set(Calendar.HOUR_OF_DAY,hours);
//                            c.set(Calendar.MINUTE, minutes);
//                            // set the minutes and hours based on the stored and chosen custom text value
//                            timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
//                            timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
//                        }
//
//                        // set onTimeChangedListener on timePicker
//                        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
//                            @Override
//                            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
//
//                                if (Build.VERSION.SDK_INT <= 23) {
//                                    minutes = timePicker.getCurrentMinute(); // before api level 23
//                                    hours = timePicker.getCurrentHour(); // before api level 23
//                                }else{
//                                    minutes = timePicker.getMinute(); // after api level 23
//                                    hours  = timePicker.getHour(); // after api level 23
//                                }
//                                reminder_displayTime.setText(format("%d:%d",hours,minutes)); // update the text based on the chosen timing
//                            }
//                        });
//
//                        // set onClickListener on save button
//                        save_btn.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                if (reminder_switch.isChecked()){ // if switch is switched, turn the reminder active
//                                    // set the text on habit_reminder_indicate_text based on chosen timing
//                                    habit_reminder_indicate_text.setText((format("%d:%d",hours,minutes)));
//                                    if (!_custom_text.getText().toString().equals("")){
//                                        // update the chosen custom text if it is not blank
//                                        txt[0] = _custom_text.getText().toString();
//                                    }
//                                    reminder_flag[0] = true; // turn on the flag
//                                    modified_reminder[0] = true; // // record down the reminder has been modified
//                                    chosen_hours[0] = hours; // record down the chosen hours
//                                    chosen_minutes[0] = minutes; // record down the chosen minutes
//                                }else{ // if the switch is not checked which indicates inactive reminder
//                                    habit_reminder_indicate_text.setText("NONE"); // set "NONE" on the habit_reminder_indicate_text
//                                    reminder_flag[0] = false; // turn down the flag
//                                    txt[0] = ""; // set the custom text as blank
//                                }
//
//                                alertDialog.dismiss(); // dismiss the alert dialog (edit reminder)
//                            }
//                        });
//
//                        close_btn.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                alertDialog.dismiss();  // dismiss the alert dialog (edit reminder)
//                            }
//                        });
//
//                        alertDialog.show();  // show the alert dialog (edit reminder)
//                    }
//                });
//
//
//                // set onClickListener on close button
//                buttonClose.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        alertDialog.dismiss(); // dismiss the edit habit dialog
//                    }
//                });
//
//                // set onClickListener on save button (edit habit)
//                buttonOk.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        int affected_row = 0; // record down how many row is edited and affected
//
//                        // update habit title if modified
//                        if (!habit.getTitle().equals(habit_name.getText().toString())){
//                            Log.d(TAG, "HabitReminder: Update habit title");
//                            habit.modifyTitle(habit_name.getText().toString()); // modify the title
//                            affected_row++; // increase the affected row by 1
//                            modified_reminder[0] = true; // to trigger changes to alarm since the reminder title is modified
//                        }
//
//                        // update habit occurrence if modified
//                        if (habit.getOccurrence() != Integer.parseInt(habit_occur.getText().toString())){
//                            Log.d(TAG, "HabitReminder: Update habit occurrence");
//                            habit.setOccurrence(Integer.parseInt(habit_occur.getText().toString())); // modify the occurrence
//                            affected_row++; // increase the affected row by 1
//                        }
//
//                        // update habit period if modified
//                        if (habit.getPeriod() != _period[0]){
//                            Log.d(TAG, "HabitReminder: Update habit period");
//                            habit.setPeriod(_period[0]); // modify the period
//                            affected_row++; // increase the affected row by 1
//                        }
//
//                        // update habit holder color if modified
//                        if (!habit.getHolder_color().equals(_color[0])){
//                            Log.d(TAG, "HabitReminder: Update habit holder color");
//                            habit.setHolder_color(_color[0]); // modify the holder color
//                            affected_row++; // increase the affected row by 1
//                        }
//
//
//                        // update habitGroup if modified
//                        if (modified_grp[0] && _grp_name[0] != null){ // if group has been modified
//                            Log.d(TAG, "HabitGroup: Modified group ");
//                            habit.setGroup(new HabitGroup(_grp_id[0],_grp_name[0])); // modified group
//                            affected_row++; // increase the affected row by 1
//                        }else if (_cancel[0] && habit.getGroup() != null){ // if cancel flag is true and habitGroup exists
//                            Log.d(TAG, "HabitGroup: Removed group");
//                            habit.setGroup(null); // set group to null
//                            affected_row++; // increase the affected row by 1
//                        }
//
//                        // retrieve the habit reminder
//                        HabitReminder check_reminder = habit.getHabitReminder();
//
//                        // it the reminder flag is true which indicates active reminder
//                        if (reminder_flag[0]){
//                            String _txt = txt[0]; //retrieve custom text from stored and chosen custom text value
//                            if (check_reminder == null){ // if reminder is not set before
//                                Log.d(TAG, "HabitReminder: Set a new alarm");
//                                int id = getUniqueHabitReminderID(); // assign a new id to habit reminder
//
//                                habit.setHabitReminder(new HabitReminder(habit.getTitle(),id,minutes,hours,_txt)); // bind the reminder to the habit object
//                                setReminder(habit.getTitle(),minutes,hours,id,txt[0]); // set the reminder
//                                affected_row++; // increase the affected row by 1
//                            }else{ // if reminder is set before
//                                if (modified_reminder[0]){ // if reminder section is modified
//
//                                    Log.d(TAG, "HabitReminder: Update an existing alarm");
//                                    // cancel the previous alarm since we going to set a new one
//                                    cancelReminder(habit.getTitle(),habit.getHabitReminder().getId(),habit.getHabitReminder().getCustom_text());
//
//                                    // update custom text if modified
//                                    if (!_txt.equals(check_reminder.getCustom_text())){
//                                        Log.d(TAG, "HabitReminder: Update custom text");
//                                        check_reminder.setCustom_text(_txt); // update the custom text value on reminder object
//                                        affected_row++; // increase the affected row by 1
//                                    }
//
//                                    // update reminder timing if modified
//                                    if (check_reminder.getMinutes() != minutes || check_reminder.getHours()!= hours){
//
//                                        // update reminder minutes if modified
//                                        if (check_reminder.getMinutes() != minutes){
//                                            Log.d(TAG, "HabitReminder: Update minutes");
//                                            check_reminder.setMinutes(minutes); // modify minutes on reminder object
//                                            affected_row++; // increase the affected row by 1
//                                        }
//
//                                        // update reminder hours if modified
//                                        if (check_reminder.getHours() != hours){
//                                            Log.d(TAG, "HabitReminder: Update hours");
//                                            check_reminder.setHours(hours); // modify hours on reminder object
//                                            affected_row++; // increase the affected row by 1
//                                        }
//                                    }
//
//                                    // set a new reminder
//                                    // ** the reminder will use the same reminder id **
//                                    setReminder(habit.getTitle(),check_reminder.getMinutes(),check_reminder.getHours(),check_reminder.getId(),check_reminder.getCustom_text());
//
//                                }
//                            }
//                        }else{ // if the reminder flag is false which indicates inactive reminder
//                            // update the reminder
//                            Log.d(TAG, "HabitReminder: Cancel HabitReminder");
//                            if (habit.getHabitReminder() != null){ // if the reminder object is not null
//                                // cancel the reminder
//                                cancelReminder(habit.getTitle(),habit.getHabitReminder().getId(),habit.getHabitReminder().getCustom_text());
//                                affected_row++; // increase the affected row by 1
//                            }
//                            habit.setHabitReminder(null); // set the habitReminder object to null
//                        }
//
//                        // update the TextView on habit view
//                        title.setText(habit_name.getText().toString());
//                        occurrence.setText(String.valueOf(habit.getOccurrence()));
//                        period.setText(habit.returnPeriodText(habit.getPeriod()));
//                        habit_view_upper.setBackgroundResource(habit.returnColorID(habit.getHolder_color()));
//
//                        if (affected_row > 0){ // if the row is affected
//                            // update the habit
//                            habit_dbHandler.updateHabit(habit); // update the habit in SQLiteDatabase
//                            habitAdapter.notifyDataSetChanged(); // notify the data set has changed in the adapter
//                            writeHabit_Firebase(habit, user.getUID(), false); // write the habit to the firebase
//                            Log.d(TAG, "Edit Habit: Habit Edited! ");
//                            Log.d(TAG, "Edit Habit: Affeceted rows: "+ affected_row);
//                        }
//
//                        alertDialog.dismiss(); // dismiss the alert dialog (edit habit)
//                    }
//                });
//
//                alertDialog.show(); // show the alert dialog (edit habit)
//            }
//        });
//
//        // delete the habit
//        deletebtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // create an alert dialog (delete habit)
//                AlertDialog.Builder builder = new AlertDialog.Builder(HabitActivity.this); // initialise the builder of alert dialog
//                builder.setTitle("Delete");
//                builder.setMessage("Are you sure you want to delete this habit?");
//                builder.setCancelable(false);
//                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // if the user choose to delete the habit
//
//                        // retrieve the habit object
//                        Habit deleted_habit = habitAdapter._habitList.getItemAt(position);
//                        Log.v(TAG, format("%s deleted!",deleted_habit.getTitle()));
//
//                        if (deleted_habit.getHabitReminder() != null){ // if the reminder of the habit object is not null
//                            // cancel the reminder if existed
//                            cancelReminder(deleted_habit.getTitle(),deleted_habit.getHabitReminder().getId(),deleted_habit.getHabitReminder().getCustom_text());
//                        }
//
//                        // delete the habit
//                        habit_dbHandler.deleteHabit(deleted_habit); // delete the habit in SQLiteDatabase
//                        habitAdapter._habitList.removeItemAt(position); // delete the habit in the habit adapter list
//                        habitAdapter.notifyItemRemoved(position); // notify the adapter the item is removed in which position
//                        habitAdapter.notifyItemRangeChanged(position, habitAdapter._habitList.size()); // notify the adapter the range of the adapter list has changed
//                        habitAdapter.notifyDataSetChanged(); // notify the adapter the data set has changed
//
//                        writeHabit_Firebase(habit, user.getUID(), true); // delete the habit in the firebase
//                        alertDialog.dismiss(); // dismiss the alert dialog (delete habit)
//                    }
//                });
//
//                builder.setNegativeButton("No", new DialogInterface.OnClickListener(){
//                    public void onClick(DialogInterface dialog, int id){
//                        // if the user refused to delete the habit
//                        Log.v(TAG,"User refuses to delete!");
//                    }
//                });
//
//                AlertDialog alert = builder.create(); // build the dialog
//                alert.show(); // show the alert dialog (delete habit)
//            }
//        });
//
//        alertDialog.show(); //show the alert dialog (habit view)
    }
}
