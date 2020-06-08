package com.mad.p03.np2020.routine.Class;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.mad.p03.np2020.routine.R;
/**
 *
 * CardNotification Class for setting notification
 *
 * @author Pritheev
 * @since 02-06-2020
 *
 */
public class CardNotification extends BroadcastReceiver {

    private static final String TAG = "Card Notification";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.v(TAG, "Building Notification");
        Bundle bundle = intent.getExtras();
        String CardName = bundle.getString("CardName");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyCard")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(CardName)
                .setContentText("You have a task due now!")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        notificationManagerCompat.notify(200,  builder.build());
        Log.v(TAG, "Notification built!");

    }
}
