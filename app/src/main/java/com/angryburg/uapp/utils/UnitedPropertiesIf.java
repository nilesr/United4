package com.angryburg.uapp.utils;

import android.app.Activity;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import com.angryburg.uapp.API.ThreadWatcher;
import com.angryburg.uapp.activities.UnitedActivity;
import com.angryburg.uapp.application.United;

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
    @JavascriptInterface public static void threadWatcherRefreshAll() {
        ThreadWatcher.refreshAll();
    }
}
