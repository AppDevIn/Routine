package com.mad.p03.np2020.routine.models;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 *
 * The class is used to check the network status of the device
 *
 * @author Lee Quan Sheng
 * @since 01-08-2020
 *
 */
public class InternetStatus {

    private static InternetStatus instance = new InternetStatus();
    static Context context;
    ConnectivityManager connectivityManager;
    boolean connected = false;

    /**
     * get the instance of the internet status
     * @param ctx Current context of the application
     * @return
     */
    public static InternetStatus getInstance(Context ctx) {
        context = ctx.getApplicationContext();
        return instance;
    }

    /**
     * This method is used to check if the device network is online
     * @return return true or false, true - Network connected, false - network not connected
     */
    public boolean isOnline() {
        try {
            connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();
            return connected;


        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
        }
        return connected;
    }


}