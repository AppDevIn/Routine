package com.mad.p03.np2020.routine.Habit;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.google.gson.Gson;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.Habit.models.Habit;
import com.mad.p03.np2020.routine.Habit.models.HabitReminder;

import java.util.Calendar;
import java.util.Locale;

import static java.lang.String.format;

/**
 *
 * Habit activity used to manage the habit reminder layout section
 *
 * @author Hou Man
 * @since 02-06-2020
 */

public class HabitReminderActivity extends AppCompatActivity {

    private static final String TAG = "HabitReminderActivity";

    // Widgets
    private ImageView close_btn;
    private Switch reminder_switch;
    private TextView reminder_displayTime;
    private TimePicker timePicker;
    private TextView customText;
    private ImageView save_btn;

    // Habit
    private Habit habit;

    // HabitReminder
    private HabitReminder reminder;

    // to record the time of the timepicker
    private int minutes, hours;

    // to check the timepicker is modified
    private boolean isModified;

    // to record the initial custom text
    private String initial_customText;

    // to record the activity action
    private String action;

    /**
     *
     * This method will be called when the start of the HabitActivity.
     * This will initialise the widgets and set onClickListener on them.
     *
     * @param savedInstanceState This parameter refers to the saved state of the bundle object.
     *
     * */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.habit_reminder_view);

        // set the layout in full screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // initialise widgets
        close_btn = findViewById(R.id.habit_reminder_view_close);
        reminder_switch = findViewById(R.id.habit_reminder_view_switch);
        reminder_displayTime = findViewById(R.id.habit_reminder_view_displaytime);
        timePicker = findViewById(R.id.habit_reminder_view_timepicker);
        customText = findViewById(R.id.habit_reminder_view_customtext);
        save_btn = findViewById(R.id.habit_reminder_view_save);

        isModified = false; // set as false at the beginning

        // This is to get the habit object from intent bundle
        Intent intent = getIntent();
        if (intent.hasExtra("recorded_habit")){
            habit = deserializeFromJson(intent.getExtras().getString("recorded_habit"));

            if (habit.getHabitReminder() != null){
                reminder = habit.getHabitReminder();
            }else{
                reminder = null;
            }
        }else{
            reminder = null;
        }

        // get the activity action
        if (intent.hasExtra("action")){
            action = intent.getExtras().getString("action");
        }

        // to determine what should be displayed on timePicker and time indicate field
        if (reminder != null){ // if the flag is true which indicates active reminder
            Calendar c = Calendar.getInstance(Locale.ENGLISH);
            // display time and timePicker based on the chosen hours and minutes
            c.set(Calendar.HOUR_OF_DAY, reminder.getHours());
            c.set(Calendar.MINUTE, reminder.getMinutes());
            timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
            reminder_switch.setChecked(true); // set the switch checked as the reminder is active
            reminder_displayTime.setText(format("%02d:%02d",reminder.getHours(),reminder.getMinutes())); // set the text based on the chosen timing

            // leave the custom text input field as blank if nothing has been filled and recorded down
            // or set the custom text based on the chosen custom text
            if (!reminder.getCustom_text().equals("")){
                customText.setText(reminder.getCustom_text());
            }

        }else{ // if the flag is false which indicates inactive reminder
            // set the minutes and hours based on the current time
            if (Build.VERSION.SDK_INT <= 23) {
                minutes = timePicker.getCurrentMinute(); // before api level 23
                hours = timePicker.getCurrentHour(); // before api level 23
            }else{
                minutes = timePicker.getMinute(); // after api level 23
                hours  = timePicker.getHour(); // after api level 23
            }
            reminder_switch.setChecked(false); // set the switch unchecked as the reminder is inactive
            reminder_displayTime.setText(format("%02d:%02d",hours,minutes)); // set the text based on the chosen timing
        }

        initial_customText = customText.getText().toString();

        // set onTimeChangedListener on timePicker
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                if (Build.VERSION.SDK_INT <= 23) {
                    minutes = timePicker.getCurrentMinute(); // before api level 23
                    hours = timePicker.getCurrentHour(); // before api level 23
                }else{
                    minutes = timePicker.getMinute(); // after api level 23
                    hours  = timePicker.getHour(); // after api level 23
                }
                reminder_displayTime.setText(format("%02d:%02d",hours,minutes)); // update the text based on the chosen timing
                isModified = true;
            }
        });

        // set onClickListener on save button
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reminder_switch.isChecked()){ // if switch is switched, turn the reminder active
                    String chosen_txt = customText.getText().toString();

                    if (reminder!= null && !chosen_txt.equals(initial_customText)){
                        // update the text if the text is modified
                        habit.getHabitReminder().setCustom_text(chosen_txt);
                    }

                    if (isModified){
                        // set a new reminder if the reminder is modified
                        habit.setHabitReminder(new HabitReminder(habit.getTitle(),minutes, hours, chosen_txt));
                    }

                    // go the respective activity based on action
                    Intent activityName = new Intent(HabitReminderActivity.this, HabitAddActivity.class);
                    if (action.equals("edit")){
                        activityName = new Intent(HabitReminderActivity.this, HabitEditActivity.class);
                    }

                    Bundle extras = new Bundle();
                    extras.putString("recorded_habit", habit_serializeToJson(habit));
                    activityName.putExtras(extras);

                    finish();
                    startActivity(activityName);

                }else{ // if switch is unchecked, turn the reminder inactive
                    // go the respective activity based on action
                    Intent activityName = new Intent(HabitReminderActivity.this, HabitAddActivity.class);
                    if (action.equals("edit")){
                        activityName = new Intent(HabitReminderActivity.this, HabitEditActivity.class);
                    }

                    habit.setHabitReminder(null);
                    Bundle extras = new Bundle();
                    extras.putString("recorded_habit", habit_serializeToJson(habit));
                    activityName.putExtras(extras);

                    finish();
                    startActivity(activityName);
                }

            }
        });

        // set onClickListener on close button
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go the respective activity based on action
                Intent activityName = new Intent(HabitReminderActivity.this, HabitAddActivity.class);
                if (action.equals("edit")){
                    activityName = new Intent(HabitReminderActivity.this, HabitEditActivity.class);
                }

                finish();
                Bundle extras = new Bundle();
                extras.putString("recorded_habit", habit_serializeToJson(habit));
                activityName.putExtras(extras);
                startActivity(activityName);
            }
        });

    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
    }

    /**
     *
     * This method is used to deserialize to single object. (from Json)
     *
     * @param jsonString This parameter is used to get json string
     *
     * @return String This returns the deserialized Habit object.
     *
     * */
    private Habit deserializeFromJson(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, Habit.class);
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
}
