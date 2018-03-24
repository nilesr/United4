package com.angryburg.uapp.utils;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import com.angryburg.uapp.API.NotificationWorker;
import com.angryburg.uapp.API.ThreadWatcher;
import com.angryburg.uapp.activities.UnitedActivity;
import com.angryburg.uapp.application.United;
import com.angryburg.uapp.fragments.GenericAlertDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This is the class exposed to the web views
 */

public class UnitedPropertiesIf {
    private WeakReference<UnitedActivity> activity;
    public UnitedPropertiesIf(Activity activity) {
        this.activity = new WeakReference<>((UnitedActivity) activity);
    }
    @JavascriptInterface public static String getProperty(String key) {
        if ("password".equals(key)) return ""; // just in case
        return P.get(key);
    }
    @JavascriptInterface public static void setProperty(String key, String value) {
        P.set(key, value);
    }
    @JavascriptInterface public String getSessionVariable(String key) {
        return activity.get().getSessionVariable(key);
    }
    @JavascriptInterface public void setSessionVariable(String key, String value) {
        activity.get().setSessionVariable(key, value);
    }
    @JavascriptInterface public void launchHTML(String resource) {
        activity.get().launchHTML(resource);
    }
    @JavascriptInterface public void closeWindow(String refresh) {
        activity.get().closeWindow("true".equalsIgnoreCase(refresh));
    }
    @JavascriptInterface public void doAction(String componentPackage, String componentKey, String encodedExtras) {
        JSONObject extras = null;
        try {
            if (encodedExtras != null) extras = new JSONObject(encodedExtras);
        } catch (JSONException ignored) {
            //
        }
        activity.get().doAction(componentPackage, componentKey, extras);
    }
    @JavascriptInterface public static void playSong(String name, String reload) throws Exception {
        United.playSong(name, "true".equalsIgnoreCase(reload));
    }
    @JavascriptInterface public static void playSound(String name) {
        United.playSound(name);
    }
    @JavascriptInterface public static void toast(@SuppressWarnings("TypeMayBeWeakened") String text) {
        Toast.makeText(United.getContext(), text, Toast.LENGTH_LONG).show();
    }
    @JavascriptInterface public static void stopSong() {
        United.stop();
    }
    @JavascriptInterface public static void watchThread(String id) {
        int iid;
        try {
            iid = Integer.valueOf(id);
            // since they're adding this through the web interface, it MAY be the case that it's being added
            // because they replied to it, in which case we want to wait for their reply to register on the server
            // before refreshing the thread watcher (or they will see the +1 for their own reply)
            ThreadWatcher.watchThread(iid, true);
        } catch (Exception ignored) {
            //
        }
    }
    @JavascriptInterface public String getVersionCode() {
        try {
            PackageInfo packageInfo = activity.get().asActivity().getPackageManager().getPackageInfo(activity.get().asActivity().getPackageName(), 0);
            return String.valueOf(packageInfo.versionCode);
        } catch (Exception ignored) {
            GenericAlertDialogFragment.newInstance("Unexpected error getting app version", activity.get().asActivity().getFragmentManager());
            return "0";
        }
    }
    @JavascriptInterface public static void addPosted(String post_id, String parent_id, String hash, String is_reply) {
        int post_id_int, parent_id_int;
        try {
            post_id_int = Integer.valueOf(post_id);
            parent_id_int = Integer.valueOf(parent_id);
            NotificationWorker.addPosted(post_id_int, parent_id_int, hash);
            if (is_reply.equalsIgnoreCase("false")) {
                NotificationWorker.addPostedOP(parent_id_int);
            }
        } catch (Exception ignored) {
            //
        }
    }
}
