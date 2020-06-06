package com.mad.p03.np2020.routine;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.google.gson.Gson;
import com.mad.p03.np2020.routine.Class.HabitReminder;

import java.util.Calendar;

import static java.lang.String.format;

public class HabitReminderActivity extends AppCompatActivity {

    private static final String TAG = "HabitReminderActivity";
    private ImageView close_btn;
    private Switch reminder_switch;
    private TextView reminder_displayTime;
    private TimePicker timePicker;
    private TextView customText;
    private ImageView save_btn;
    private HabitReminder reminder;
    private int minutes, hours;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.habit_reminder_view);

        // initialise widgets
        close_btn = findViewById(R.id.habit_reminder_view_close);
        reminder_switch = findViewById(R.id.habit_reminder_view_switch);
        reminder_displayTime = findViewById(R.id.habit_reminder_view_displaytime);
        timePicker = findViewById(R.id.habit_reminder_view_timepicker);
        customText = findViewById(R.id.habit_reminder_view_customtext);
        save_btn = findViewById(R.id.habit_reminder_view_save);

        // TODO receive the intent package and initialise the reminder

        // to determine what should be displayed on timePicker and time indicate field
        if (reminder != null){ // if the flag is true which indicates active reminder
            Calendar c = Calendar.getInstance();
            // display time and timePicker based on the chosen hours and minutes
            c.set(Calendar.HOUR_OF_DAY, reminder.getHours());
            c.set(Calendar.MINUTE, reminder.getMinutes());
            timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
            reminder_switch.setChecked(true); // set the switch checked as the reminder is active
            reminder_displayTime.setText(format("%d:%d",reminder.getHours(),reminder.getMinutes())); // set the text based on the chosen timing

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
            reminder_displayTime.setText(format("%d:%d",hours,minutes)); // set the text based on the chosen timing
        }

        // leave the custom text input field as blank if nothing has been filled and recorded down
        // or set the custom text based on the chosen custom text
        if (!reminder.getCustom_text().equals("")){
            customText.setText(reminder.getCustom_text());
        }

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
                reminder_displayTime.setText(format("%d:%d",hours,minutes)); // update the text based on the chosen timing
            }
        });

        // set onClickListener on save button
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reminder_switch.isChecked()){ // if switch is switched, turn the reminder active
                    String chosen_txt = customText.getText().toString();

                    HabitReminder chosen_habitReminder = new HabitReminder(minutes, hours, chosen_txt);
                    Intent activityName = new Intent(HabitReminderActivity.this, HabitActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("chosenHabitReminder", habitReminder_serializeToJson(chosen_habitReminder));
                    activityName.putExtras(extras);

                    startActivity(activityName);


                }else{ // if switch is unchecked, turn the reminder inactive
                    Intent activityName = new Intent(HabitReminderActivity.this, HabitActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("chosenHabitReminder","null");
                    activityName.putExtras(extras);

                    startActivity(activityName);
                }

            }
        });

        // set onClickListener on close button
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activityName = new Intent(HabitReminderActivity.this, HabitActivity.class);
                startActivity(activityName);
            }
        });

    }

    /**
     *
     * This method is used to serialize a single object. (into Json String)
     *
     * @param habitReminder This parameter is used to get the habitReminder object
     *
     * @return String This returns the serialized object.
     *
     * */
    public String habitReminder_serializeToJson(HabitReminder habitReminder) {
        Gson gson = new Gson();
        Log.i(TAG,"Object serialize");
        return gson.toJson(habitReminder);
    }
}
