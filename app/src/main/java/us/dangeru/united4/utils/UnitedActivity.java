package us.dangeru.united4.utils;

import android.app.Activity;
import android.app.Application;
import android.webkit.WebView;

/**
 * Created by Niles on 8/18/17.
 */

public interface UnitedActivity {
    void launchHTML(String resource);
    String getSessionVariable(String key);
    void setSessionVariable(String key, String value);
    void closeWindow(boolean refresh);
    Activity asActivity();
    WebView getWebView();
}
