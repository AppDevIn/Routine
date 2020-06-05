package com.mad.p03.np2020.routine.Class;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mad.p03.np2020.routine.database.HabitDBHelper;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";
    private static HabitDBHelper habitDBHelper;
    private Habit.HabitList habitList;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            habitDBHelper = new HabitDBHelper(context);
            habitList = habitDBHelper.getAllHabits();

            re_registerAlarm(context, habitList);
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
    public void setReminder(Context context, String name, int minutes, int hours, int id, String custom_txt){
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction("HabitTracker");
        intent.putExtra("Name", name);
        intent.putExtra("id", id);
        intent.putExtra("custom_txt", custom_txt);
        // This initialise the pending intent which will be sent to the broadcastReceiver
        PendingIntent pi = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
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
     * This method is used to re-register the habitReminder after rebooting(boot completed).
     *
     * @param context This parameter is used to get the context.
     *
     * @param habitList This parameter is used to get the habitList.
     *
     * */
    public void re_registerAlarm(Context context, Habit.HabitList habitList){
        // looping through each reminder
        for (int i = 0; i < habitList.size(); i++){
            // get the habit object
            Habit habit = habitList.getItemAt(i);
            // get the habit reminder object
            HabitReminder reminder = habit.getHabitReminder();
            // jump to next loop if reminder is null
            if (reminder == null){ continue;}
            Log.d(TAG, "Re-registering alarm");
            // get the reminder attributes
            String title = habit.getTitle();
            int minutes = reminder.getMinutes();
            int hours = reminder.getHours();
            int id = reminder.getId();
            String custom_text = reminder.getCustom_text();
            // register the reminder again
            setReminder(context, title, minutes, hours, id, custom_text);
        }
    }

}
