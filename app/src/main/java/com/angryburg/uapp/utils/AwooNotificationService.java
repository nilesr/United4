package com.angryburg.uapp.utils;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;

/**
* Created by Niles on 3/21/18.
*/
public class AwooNotificationService extends Service {
    private static final String TAG = AwooNotificationService.class.getSimpleName();
    private PowerManager.WakeLock lock;
    @Override public IBinder onBind(Intent intent) {
        return null;
    }
    /**
     * start the task
     */
    private void handleIntent() {
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (pm == null) {
            stopSelf();
            return;
        }
        lock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        lock.acquire(10*60*1000L /*10 minutes*/);
        // check if we're connected
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm == null) {
            stopSelf();
            return;
        }
        if (!cm.getActiveNetworkInfo().isConnected()) {
            stopSelf();
            return;
        }
        // do the actual work, in a separate thread
        new PollTask().execute();
    }
    @SuppressLint("StaticFieldLeak")
    private class PollTask extends AsyncTask<Void, Void, Void> {
        /**
         * This runs on a separate thread
         */
        @Override protected Void doInBackground(Void... params) {
            /* do stuff! */
            return null;
        }
        /**
         * This is run on the UI thread.
         * It pushes notifications, then stops the service.
         */
        @Override protected void onPostExecute(Void result) {
            /* handle your data */
            stopSelf();
        }
    }
    /**
     * Returning START_NOT_STICKY tells the system to not restart the service if it is
     * killed because of poor resource (memory/cpu) conditions.
     */
    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent();
        return START_NOT_STICKY;
    }
    /**
     * Release the wake lock
     */
    public void onDestroy() {
        super.onDestroy();
        lock.release();
    }
}