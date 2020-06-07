package com.mad.p03.np2020.routine.Class;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.database.TaskDBHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 *
 * CardActivity class used to manage card activities
 *
 * @author Pritheev
 * @since 02-06-2020
 *
 */
public class PopUp extends Activity {
    //Initializing variables

    //TAG for logging
    private static final String TAG = "CardNotification PopUp";

    //Used for Date Display
    private TextView DisplayDate;

    //Used for Date Picker Pop Up
    private DatePickerDialog.OnDateSetListener dateSetListener;

    //Used for hours add button
    ImageButton UpArrowLeft;

    //Used for minutes add button
    ImageButton UpArrowRight;

    //Used for hours reduce button
    ImageButton DownArrowLeft;

    //Used for minutes reduce button
    ImageButton DownArrowRight;

    //Used for Set Timer Button
    Button SetTimer;

    //Used for hours timer
    TextView TimerLeft;

    //Used for minutes timer
    TextView TimerRight;

    Calendar dateInitializer = Calendar.getInstance();
    //Initializing hours variable
    public int hours = dateInitializer.get(dateInitializer.HOUR_OF_DAY);

    //Initializing minutes variable
    public int minutes = dateInitializer.get(dateInitializer.MINUTE);

    //Initializing task variable
    Task mTask;

    //Date variable to store date as a string
    String date;

    //Initializing year variable
    public int Year = dateInitializer.get(dateInitializer.YEAR);

    //Initializing month variable
    public int Month = dateInitializer.get(dateInitializer.MONTH);

    //Initializing day variable
    public int Day = dateInitializer.get(dateInitializer.DAY_OF_MONTH);

    //Used to check if date is set on notification setter
    public boolean dateSet = false;

    //USed to check if time is set on notification setter
    public boolean timeSet = false;

    /**
     * This is to initialize the variables with ids form views
     *
     * and call listeners for buttons
     *
     * @param savedInstanceState Set the bundle data to this content
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popupwindow);

        //Bundle to get intent from CardActivity
        Bundle bundle = getIntent().getExtras();

        mTask = (Task) getIntent().getSerializableExtra("task");

        //Create a notification channel for Routine to use for card notifications
        createNotificationChannel();

        //Identifying date display view
        DisplayDate = (TextView) findViewById(R.id.datePicker);

        //Identifying hours add button
        UpArrowLeft = findViewById(R.id.LeftTop);

        //Identifying minutes add button
        UpArrowRight = findViewById(R.id.RightTop);

        //Identifying hours reduce button
        DownArrowLeft = findViewById(R.id.LeftDown);

        //Identifying minutes reduce button
        DownArrowRight = findViewById(R.id.RightDown);

        //Identifying Set Timer Button
        SetTimer = findViewById(R.id.setTimer);

        //Identifying hours timer text view
        TimerLeft = findViewById(R.id.timerLeft);

        //Identifying minutes timer text view
        TimerRight = findViewById(R.id.timerRight);

        //Initializing a calender
        final Calendar calendar = Calendar.getInstance();

        /**
         *
         * Creates intent and passes CardName to card notification
         *
         * for setting a notification
         *
         */
        Log.v(TAG, "Timer Button Clicked");
        Intent intent = new Intent(PopUp.this, CardNotification.class);
        intent.putExtra("CardName", mTask.getName());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(PopUp.this, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);


        //Button onClickListener
        UpArrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Hour increasing button pressed");
                //Add 1 to hours when button clicked
                hours += 1;

                //Setting text of hours
                TimerLeft.setText(timeToText(hours, 24));
                timeSet = true;
            }
        });

        //Button onClickListener
        DownArrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Hour decreasing button pressed");
                //Remove 1 to hours when button clicked
                hours -= 1;

                //Setting text of hours
                TimerLeft.setText(timeToText(hours, 24));
                timeSet = true;
            }
        });

        //Button onClickListener
        UpArrowRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Minute increasing button pressed");
                //Add 1 to minutes when button clicked
                minutes += 1;

                //Setting text of minutes
                TimerRight.setText(timeToText(minutes, 60));
                timeSet = true;
            }
        });

        //Button onClickListener
        DownArrowRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Minute decreasing button pressed");
                //Remove 1 to minutes when button clicked
                minutes = 1;

                //Setting text of minutes
                TimerRight.setText(timeToText(minutes, 60));
                timeSet = true;
            }
        });

        //Notification setter onClickListener
        SetTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 *
                 * Intent to open BroadcastReceiver class, CardNotification
                 *
                 * Initializing calendar with current selected time and date
                 *
                 */

                Log.v(TAG, "Timer Button Clicked");
                Intent intent = new Intent(PopUp.this, CardNotification.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(PopUp.this, 0, intent, 0);

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR, hours);
                cal.set(Calendar.MINUTE, minutes);
                cal.set(Calendar.SECOND, 0);

                if (System.currentTimeMillis() > cal.getTimeInMillis()){
                    // increment one day to prevent setting for past alarm
                    cal.add(Calendar.DATE, 1);
                }

                long timeInMillis = cal.getTime().getTime();

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);

                Log.v(TAG, "Alarm Set");

                if (timeSet == true && dateSet == true)
                {
                    Log.v(TAG, "Stopping Pop Up");
                    finish();
                }
                else
                {
                    Log.v(TAG, "Error! Time and date must be selected!");
                    Toast.makeText(getApplicationContext(), "You must select a time or date to set time!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Date selector onClickListener
        DisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Date selector pressed");
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(PopUp.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        //Date picker dialog listener
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                Log.v(TAG, "Date Set");
                Year = year;
                Month = month;
                Day = day;
                date = day + "/" + month + "/" + year;
                DisplayDate.setText(date);
                dateSet = true;
            }
        };

        //Initializing display metrics
        DisplayMetrics dm = new DisplayMetrics();

        //Setting metrics of window manager to dm
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        //Set height with display metric
        int height = dm.heightPixels;

        //Set width with display metric
        int width = dm.widthPixels;

        //Setting layout to 60% width and 40% height
        getWindow().setLayout((int) (width*.7), (int) (height*.6));

    }

    /**
     * Function to set text to 24 hour format
     *
     * @param time to take in time value such as hours or minutes
     * @param limit to specify limit such as 24 for hours and 60 for minutes
     *
     * @return returns time in a string format modified for textview
     * such as a 0 in front for values less than 10
     */
    public String timeToText(int time, int limit)
    {
        //initializing timer
        String timer;

        //Adding "0" to values less than 10
        //then returns timer
        if (time < 10 && time >= 0)
        {
            timer = "0" + time;
            return timer;
        }

        //Disables timer to go into negative
        //then return timer
        else if (time < 0)
        {
            timer = "00";
            return timer;
        }

        //Ensures timer does not go over limits
        //Not more than 23 for hours
        //Not more than 59 minutes
        if (time >= limit)
        {
            //Removes 1 from timer to ensure it does not breach limit
            timer = String.valueOf(limit-1);
            return timer;
        }

        //Turns timer from int into string
        //then returns timer
        timer = String.valueOf(time);
        return timer;
    }

    /**
     *
     * Creates a notification channel that Routine can use for card related notifications
     *
     */
    private void createNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "CardNotificationChannel";
            String description = "Channel for card reminders";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyCard", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        String dateString = String.valueOf(Year) + "-" + String.valueOf(Month) + "-" + String.valueOf(Day) + " " + String.valueOf(hours) + ":" + String.valueOf(minutes) + ":0";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date setDate = dateFormat.parse(dateString);


            mTask.setRemindDate(dateString);
            TaskDBHelper taskDBHelper = new TaskDBHelper(this);
            Log.d(TAG, "onStop: " + mTask.getRemindDate().toString());
            taskDBHelper.update(mTask.getTaskID(), mTask.getRemindDate());

            mTask.executeUpdateFirebase(null);

        }
        catch (ParseException  e) {
            e.printStackTrace();
        }
    }


}
