package com.mad.p03.np2020.routine.Class;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import com.mad.p03.np2020.routine.HabitActivity;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    private String channelId = "001";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("HabitTracker")) {
            Bundle bundle = intent.getExtras();
            int id = bundle.getInt("id");
            String txt = bundle.getString("custom_txt").length() > 1 ? bundle.getString("custom_txt"): "Please remember to check in your habit!";
            PendingIntent pendingIntent = PendingIntent.getActivity(context,id, new Intent(context, HabitActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notify = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(android.R.drawable.arrow_up_float)
                    .setContentTitle("Habit Tracker")
                    .setContentText(bundle.getString("Name") + " : " + txt)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent).setNumber(1)
                    .setShowWhen(true)
                    .setWhen(Calendar.getInstance().getTimeInMillis())
                    .build();

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(id, notify);

        }
    }
}