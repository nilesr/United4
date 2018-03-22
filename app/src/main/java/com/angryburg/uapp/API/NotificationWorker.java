package com.angryburg.uapp.API;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.angryburg.uapp.R;
import com.angryburg.uapp.activities.UserscriptActivity;
import com.angryburg.uapp.application.United;
import com.angryburg.uapp.utils.AwooNotificationService;
import com.angryburg.uapp.utils.P;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static com.angryburg.uapp.API.NetworkUtils.API;
import static com.angryburg.uapp.API.NetworkUtils.fetch;
import static com.angryburg.uapp.application.United.authorizer;

/**
 * Created by Niles on 3/21/18.
 */

public class NotificationWorker {
    private static final String TAG = NotificationWorker.class.getSimpleName();
    private static List<Thread> pullDirect(Context context) {
        List<Thread> result = new ArrayList<>();
        String posted_ids = P.get("posted_ids");
        if (posted_ids.isEmpty() || posted_ids.equals("[]")) return result;
        String my_hashes = P.get("my_hashes");
        if (my_hashes.isEmpty() || my_hashes.equals("[]")) return result;
        try {
            String responses = fetch(P.get("awoo_endpoint") + API + "/replies" +
                    "?list=" + URLEncoder.encode(posted_ids, "UTF-8") +
                    "&hashes=" + URLEncoder.encode(my_hashes, "UTF-8")
                    , authorizer);
            JSONArray arr = new JSONArray(responses);
            for (int i = 0; i < arr.length(); i++) {
                int id = arr.getInt(i);
                if (hasNotified(id)) continue;
                String object = fetch(P.get("awoo_endpoint") + API + "/thread/" + id + "/metadata", authorizer);
                result.add(new Thread(new JSONObject(object)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    private static List<Thread> pullDirectAndCreated(Context context) {
        List<Thread> result = pullDirect(context);
        String posted_ops_str = P.get("posted_ops");
        if (posted_ops_str.isEmpty() || posted_ops_str.equals("[]")) return result;
        try {
            JSONArray posted_ops = new JSONArray(posted_ops_str);
            for (int i = 0; i < posted_ops.length(); i++) {
                int id = posted_ops.getInt(i);
                String this_thread_str = fetch(P.get("awoo_endpoint") + API + "/thread/" + id + "/replies", authorizer);
                JSONArray this_thread = new JSONArray(this_thread_str);
                // j = 1 instead of 0 to skip the OP
                for (int j = 1; j < this_thread.length(); j++) {
                    Thread this_reply = new Thread(this_thread.getJSONObject(j));
                    if (hasNotified(this_reply.post_id)) continue;
                    result.add(this_reply);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    private static List<Thread> pullAll(Context context) {
        List<Thread> result = new ArrayList<>();
        String watched_threads_str = P.get("watched_threads");
        if (watched_threads_str.isEmpty() || watched_threads_str.equals("[]")) return result;
        try {
            JSONArray arr = new JSONArray(watched_threads_str);
            for (int i = 0; i < arr.length(); i++) {
                int id = Integer.valueOf(arr.getString(i));
                String this_thread_str = fetch(P.get("awoo_endpoint") + API + "/thread/" + id + "/replies", authorizer);
                JSONArray this_thread = new JSONArray(this_thread_str);
                // j = 1 instead of 0 to skip the OP
                for (int j = 1; j < this_thread.length(); j++) {
                    Thread this_reply = new Thread(this_thread.getJSONObject(j));
                    if (hasNotified(this_reply.post_id)) continue;
                    result.add(this_reply);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public static List<Thread> pullNotifications(Context context) {
        Log.i(TAG, "pulling notifications");
        if (United.singleton == null || United.getContext() == null)
            United.singleton = new WeakReference<>(context);
        cached_notified_ids = null;
        if (P.get("which_notifications").isEmpty() || "DIRECT".equalsIgnoreCase(P.get("which_notifications"))) {
            return pullDirect(context);
        } else if ("DIRECT_AND_CREATED".equalsIgnoreCase(P.get("which_notifications"))) {
            return pullDirectAndCreated(context);
        } else if ("ALL".equalsIgnoreCase(P.get("which_notifications"))) {
            return pullAll(context);
        }
        Log.e(TAG, "This should not happen");
        return new ArrayList<>();
    }
    private static List<Integer> cached_notified_ids = null;
    private static boolean hasNotified(int id) {
        if (cached_notified_ids != null) return cached_notified_ids.contains(id);
        String notified_ids = P.get("notified_ids");
        if (notified_ids.isEmpty()) notified_ids = "[]";
        try {
            cached_notified_ids = new ArrayList<>();
            JSONArray arr = new JSONArray(notified_ids);
            for (int i = 0; i < arr.length(); i++) {
                int inner_id = arr.getInt(i);
                cached_notified_ids.add(inner_id);
            }
            return hasNotified(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public static void setNotified(int id) {
        if (cached_notified_ids != null) cached_notified_ids.add(id);
        String notified_ids = P.get("notified_ids");
        if (notified_ids.isEmpty()) notified_ids = "[]";
        try {
            JSONArray arr = new JSONArray(notified_ids);
            arr.put(id);
            P.set("notified_ids", arr.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Adds the ID to the list of threads and replies posted by this user
     * @param id the id of the new thread or reply
     */
    public static void addPosted(int id, int parent_id, String hash) {
        String old = P.get("posted_ids");
        if (old.isEmpty()) old = "[]";
        JSONArray new_arr = new JSONArray();
        try {
            new_arr = new JSONArray(old);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new_arr.put(id);
        P.set("my_hashes", new_arr.toString());
        old = P.get("my_hashes");
        if (old.isEmpty()) old = "[]";
        new_arr = new JSONArray();
        try {
            new_arr = new JSONArray(old);
            JSONObject obj = new JSONObject();
            obj.put("op", parent_id);
            obj.put("hash", hash);
            new_arr.put(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        P.set("my_hashes", new_arr.toString());
    }
    /**
     * Adds the ID to the list of new threads posted by this user
     * @param id the id of the new thread
     */
    public static void addPostedOP(int id) {
        String old = P.get("posted_op_ids");
        if (old.isEmpty()) old = "[]";
        JSONArray new_arr = new JSONArray();
        try {
            new_arr = new JSONArray(old);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new_arr.put(id);
        P.set("posted_op_ids", new_arr.toString());
    }

    public static void showNotifications(List<Thread> threads, Context context) {
        if (threads.isEmpty()) return;
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            CharSequence name = "Channel Name";
            String description = "Channel Description";
            int importance = NotificationManagerCompat.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channel", name, importance);
            channel.setDescription(description);
            // Register the channel with the system
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(United.getContext());
            notificationManager.createNotificationChannel(channel);
        }
        */
        for (Thread thread : threads) {
            Log.i(TAG, "notifying about thread with content " + thread.comment);
            String title = thread.comment;
            if (thread.comment.length() > 51)
                title = thread.comment.substring(0, 50) + "...";
            Intent intent = new Intent(context, UserscriptActivity.class);
            intent.putExtra("URL", P.get("awoo_endpoint") + "/" + thread.board + "/thread/" + thread.parent + "#comment-" + thread.comment);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent i2 = PendingIntent.getActivity(context, 0, intent, 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("New reply")
                    .setContentText(title)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(thread.comment))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setContentIntent(i2)
                    .setAutoCancel(true);
            NotificationManagerCompat.from(context).notify(0,builder.build());
            setNotified(thread.post_id);
        }
    }
    public static void setAlarm(Context context) {
        if (United.singleton == null || United.getContext() == null)
            United.singleton = new WeakReference<>(context);
        int minutes = P.getMinutes();
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AwooNotificationService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        if (am == null) return;
        am.cancel(pi);
        if (!P.getBool("notifications")) return;
        am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + minutes*60*1000, minutes*60*1000, pi);
    }
}
