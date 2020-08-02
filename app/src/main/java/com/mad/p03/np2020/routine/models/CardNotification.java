package com.mad.p03.np2020.routine.models;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.Printer;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

    private final String TAG = "CardNotification";

    DatabaseReference notificationRef;
    DatabaseReference mDatabase;
    DatabaseReference latestID;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    String UID;
    String TaskID;
    String CardName;
    int LatestID;
    int NotificationID;

    private String channelID = "CardNotification";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals("CardNotification"))
        {
            Bundle bundle = intent.getExtras();

            TaskID = intent.getStringExtra("TaskID");
            CardName = intent.getStringExtra("CardName");

            NotificationCompat.Builder notification = new NotificationCompat.Builder(context, channelID)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle("Routine")
                    .setContentText("Your Task, " + CardName + " is due now !")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

            notificationManagerCompat.notify(200, notification.build());

            /*

            TaskID = intent.getStringExtra("TaskID");
            CardName = intent.getStringExtra("CardName");

            mAuth = FirebaseAuth.getInstance();
            firebaseUser = mAuth.getCurrentUser();
            UID = firebaseUser.getUid();

            mDatabase = FirebaseDatabase.getInstance().getReference();

            notificationRef = mDatabase.child("users").child(UID);

            notificationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try
                    {
                        String LatestUsedID = dataSnapshot.child("LatestID").getValue().toString();
                        LatestID = Integer.parseInt(LatestUsedID);
                        Log.v(TAG, "LatestID Received: " + LatestID);

                        NotificationID = LatestID;

                        if (NotificationID == 0)
                        {
                            NotificationID = 10;
                        }
                        else
                        {
                            NotificationID += 1;
                        }
                        notificationRef.child("LatestID").setValue(NotificationID);

                        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, channelID)
                                .setSmallIcon(R.drawable.logo)
                                .setContentTitle("Routine")
                                .setContentText("Your Task, " + CardName + "is due now !")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

                        notificationManagerCompat.notify(NotificationID, notification.build());
                    }
                    catch (Exception e)
                    {
                        LatestID = 10;
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

             */

        }

    }

}
