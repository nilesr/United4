package us.dangeru.launcher.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import us.dangeru.launcher.activities.MainActivity;
import us.dangeru.launcher.application.United;

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
    @JavascriptInterface public void closeWindow(String refresh) {
        activity.get().closeWindow("true".equalsIgnoreCase(refresh));
    }
    @JavascriptInterface public void doAction(String componentPackage, String componentKey) {
        activity.get().doAction(componentPackage, componentKey);
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
    @JavascriptInterface public void authenticate() {
        authenticate(activity.get().asActivity());
    }

    public static void authenticate(Activity act) {
        String username = PropertiesSingleton.get().getProperty("username");
        String password = PropertiesSingleton.get().getProperty("password");
        Intent i = new Intent(United.getContext(), MainActivity.class);
        Bundle extras = new Bundle();
        extras.putString("URL", PropertiesSingleton.get().getProperty("awoo_endpoint") + "/mod");
        ParcelableMap headers = new ParcelableMap();
        headers.put("X-Awoo-Username", username);
        headers.put("X-Awoo-Password", password);
        extras.putParcelable("headers", headers.parcel());
        i.putExtras(extras);
        act.startActivityForResult(i, 10);
    }
}
