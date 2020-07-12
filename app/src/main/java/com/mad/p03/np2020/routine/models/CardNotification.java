package com.mad.p03.np2020.routine.models;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.mad.p03.np2020.routine.R;

public class CardNotification extends BroadcastReceiver {

    private String channelID = "CardNotification";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals("CardNotification"))
        {
            Bundle bundle = intent.getExtras();

            NotificationCompat.Builder notification = new NotificationCompat.Builder(context, channelID)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle("Routine")
                    .setContentText("Your card is due!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

            notificationManagerCompat.notify(200, notification.build());
        }


    }
}
