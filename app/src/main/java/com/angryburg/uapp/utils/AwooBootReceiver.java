package com.angryburg.uapp.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.angryburg.uapp.API.NotificationWorker;

/**
 * Created by Niles on 3/21/18.
 */

public class AwooBootReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        NotificationWorker.setAlarm(context);
    }
}
