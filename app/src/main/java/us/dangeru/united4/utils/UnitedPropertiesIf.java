package us.dangeru.united4.utils;

import android.app.Activity;

/**
 * Created by Niles on 8/18/17.
 */

public class UnitedPropertiesIf {
    UnitedActivity activity;
    public UnitedPropertiesIf(Activity activity) {
        this.activity = (UnitedActivity) activity;
    }
    @android.webkit.JavascriptInterface public String getProperty(String key) {
        return PropertiesSingleton.get().getProperty(key);
    }
    @android.webkit.JavascriptInterface public void setProperty(String key, String value) {
        PropertiesSingleton.get().setProperty(key, value);
    }
}
