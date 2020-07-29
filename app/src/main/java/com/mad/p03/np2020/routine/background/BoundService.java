package com.mad.p03.np2020.routine.background;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.mad.p03.np2020.routine.Focus.FocusActivity;
import com.mad.p03.np2020.routine.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * Bound Service Class Activity to manage background services
 *
 * @author Lee Quan Sheng
 * @since 08-06-2020
 *
 */

public class BoundService extends Service {
    private static String LOG_TAG = "BoundService";
    private IBinder mBinder = new MyBinder();



    public static Context currentContext;
    /**Notification channel ID is set to channel 1*/
    private static final String CHANNEL_1_ID = "channel1";

    final String title = "You have an ongoing Focus";
    final String message = "Come back now before your Sun becomes SuperNova!";

    public void setmContext(Context mContext) {
        currentContext = mContext;
    }

    public BoundService(){
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(LOG_TAG, "in onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(LOG_TAG, "in onBind");
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.v(LOG_TAG, "in onRebind");
        super.onRebind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(LOG_TAG, "in onUnbind");
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "in onDestroy");
    }

    public String getTimestamp() {
        long elapsedMillis = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        Date date = new Date(elapsedMillis);
        return format.format(date);
    }

    public class MyBinder extends Binder {
        public BoundService getService() {
            return BoundService.this;
        }
    }

    /***
     *
     * Set Notification only for Focus
     *
     */
    public void createNotification() {
        Log.e(LOG_TAG, "Notification is pushed");

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) { //API level for Kitkat
            Log.e(LOG_TAG, "Notification is pushed for kitkat level");

            Intent intent = new Intent(getApplicationContext(), FocusActivity.class);

            PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Notification noti = new Notification.Builder(getApplicationContext()).setContentTitle(title).setContentText(message).setSmallIcon(R.drawable.focus).setContentIntent(pIntent).build();
            if (notificationManager != null) {
                notificationManager.notify(0, noti);
            } else {
                Log.e(LOG_TAG, "Notification manager is a null pointer");
            }
            Log.v("Notification", "Pushed");
        } else {//API level for other than kitkat
            Log.e(LOG_TAG, "Notification is pushed for higher than kitkat level");

            //Creation Channel
            NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID, "Channel 1", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Focus");
            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(channel);
            sendChannel1();
        }
    }



    /**
     *
     * Send notification to channel 1, used for api above 24
     */
    public void sendChannel1() {

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(getApplicationContext(), FocusActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_1_ID).setContentIntent(pIntent).setSmallIcon(R.drawable.focus).setContentTitle(title).setContentText(message).setPriority(NotificationCompat.PRIORITY_HIGH).setCategory(NotificationCompat.CATEGORY_MESSAGE).build();
        assert notificationManager != null;
        notificationManager.notify(1, notification);

    }

}
