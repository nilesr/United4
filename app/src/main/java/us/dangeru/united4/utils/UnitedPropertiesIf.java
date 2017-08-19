package us.dangeru.united4.utils;

import android.app.Activity;

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
    @android.webkit.JavascriptInterface public String getProperty(String key) {
        return PropertiesSingleton.get().getProperty(key);
    }
    @android.webkit.JavascriptInterface public void setProperty(String key, String value) {
        PropertiesSingleton.get().setProperty(key, value);
    }
    @android.webkit.JavascriptInterface public String getSessionVariable(String key) {
        return activity.get().getSessionVariable(key);
    }
    @android.webkit.JavascriptInterface public void setSessionVariable(String key, String value) {
        activity.get().setSessionVariable(key, value);
    }
    @android.webkit.JavascriptInterface public void launchHTML(String resource) {
        activity.get().launchHTML(resource);
    }
    @android.webkit.JavascriptInterface public void closeWindow() {
        activity.get().closeWindow();
    }
    @android.webkit.JavascriptInterface public void playSong(String name) throws Exception {
        ((United) activity.get().getApplication()).playSong(name);
    }
}
