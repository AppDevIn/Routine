package com.mad.p03.np2020.routine.Class;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.mad.p03.np2020.routine.R;
/**
 *
 * CardActivity class used to manage card activities
 *
 * @author Pritheev
 * @since 02-06-2020
 *
 */
public class CardNotification extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent)
    {

        Bundle bundle = intent.getExtras();
        String CardName = bundle.getString("CardName");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyCard")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(CardName)
                .setContentText("This is a test")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        notificationManagerCompat.notify(200,  builder.build());

    }
}
