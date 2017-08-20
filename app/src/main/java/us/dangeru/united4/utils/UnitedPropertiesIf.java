package us.dangeru.united4.utils;

import android.app.Activity;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import us.dangeru.united4.application.United;

/**
 * Created by Niles on 8/18/17.
 */

public class UnitedPropertiesIf {
    WeakReference<UnitedActivity> activity;
    public UnitedPropertiesIf(Activity activity) {
        this.activity = new WeakReference<>((UnitedActivity) activity);
    }
    @JavascriptInterface public static String getProperty(String key) {
        return PropertiesSingleton.get().getProperty(key);
    }
    @JavascriptInterface public static void setProperty(String key, String value) {
        PropertiesSingleton.get().setProperty(key, value);
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
    @JavascriptInterface public void closeWindow() {
        activity.get().closeWindow();
    }
    @JavascriptInterface public static void playSong(String name) throws Exception {
        United.playSong(name);
    }
    @JavascriptInterface public static void playSound(String name) {
        United.playSound(name);
    }
    @JavascriptInterface public static void toast(String text) {
        Toast.makeText(United.getContext(), text, Toast.LENGTH_LONG).show();
    }
    @JavascriptInterface public static void stopSong() {
        United.stop();
    }
}
