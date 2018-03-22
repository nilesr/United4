package com.angryburg.uapp.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Niles on 3/21/18.
 */

public class AwooBootReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        int minutes = P.getMinutes();
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AwooNotificationService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        if (am == null) return;
        am.cancel(pi); // by my own convention, minutes <= 0 means notifications are disabled
        am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + minutes*60*1000, minutes*60*1000, pi);
    }

}
