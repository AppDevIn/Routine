package com.mad.p03.np2020.routine.Profile;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.mad.p03.np2020.routine.DAL.HabitDBHelper;
import com.mad.p03.np2020.routine.LoginActivity;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.models.AlarmReceiver;
import com.mad.p03.np2020.routine.models.Habit;
import com.mad.p03.np2020.routine.models.HabitReminder;

import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private final String TAG = "ProfileActivity";

    FirebaseAuth mAuth;
    Button logoutButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth = FirebaseAuth.getInstance();

        logoutButton = findViewById(R.id.logoutButton);

        /*
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Logout Clicked!");
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                mAuth.signOut();
                startActivity(intent);
            }
        });

         */

        logoutButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.logoutButton:
                Log.v(TAG, "User Logging out!");
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                clearHabitAlarm(this);
                cancelRepeatingHabit();
                mAuth.signOut();
                startActivity(intent);
        }

    }

    /**
     *
     * This method is used to re-register the habitReminder after rebooting(boot completed).
     *
     * @param context This parameter is used to get the context.
     *
     * */
    public void clearHabitAlarm(Context context){
        HabitDBHelper habitDBHelper = new HabitDBHelper(context);
        Habit.HabitList habitList = habitDBHelper.getAllHabits();
        // looping through each reminder
        for (int i = 0; i < habitList.size(); i++){
            // get the habit object
            Habit habit = habitList.getItemAt(i);
            // get the habit reminder object
            HabitReminder reminder = habit.getHabitReminder();
            // jump to next loop if reminder is null
            if (reminder == null){ continue;}
            Log.d(TAG, "clearHabitAlarm: ");
            // get the reminder attributes
            String title = habit.getTitle();
            int id = reminder.getId();
            String custom_text = reminder.getCustom_text();
            // register the reminder again
            cancelReminder(title, id, custom_text);
        }
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

    /**
     *
     * This method is used to call to reset the repeat the habit.
     *
     * */
    public void cancelRepeatingHabit(){
        Log.d(TAG, "cancelRepeatingHabit: ");
        int id = 873162723;
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.setAction("RepeatingHabit");
        intent.putExtra("id", id);
        // This initialise the pending intent which will be sent to the broadcastReceiver
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        am.cancel(pi);
    }
}
