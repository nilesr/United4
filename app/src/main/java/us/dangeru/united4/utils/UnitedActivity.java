package us.dangeru.united4.utils;

import android.app.Application;

/**
 * Created by Niles on 8/18/17.
 */

public interface UnitedActivity {
    void launchHTML(String resource);
    String getSessionVariable(String key);
    void setSessionVariable(String key, String value);
    void closeWindow();
    Application getApplication();
}
