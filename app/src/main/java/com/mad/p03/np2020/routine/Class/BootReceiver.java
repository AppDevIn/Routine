package com.mad.p03.np2020.routine.Class;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            // TODO
            // get the stored alarms from your database
            // re-register them with the alarm manager
        }
        
    }
}
