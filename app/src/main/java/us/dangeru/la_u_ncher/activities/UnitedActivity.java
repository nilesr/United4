package us.dangeru.la_u_ncher.activities;

import android.app.Activity;
import android.webkit.WebView;

/**
 * Created by Niles on 8/18/17.
 */

public interface UnitedActivity {
    /**
     * launches a HTML page in a new activity
     * @param url the url to load in that new activity
     */
    void launchHTML(String url);

    /**
     * Gets a session variable that was previously set by the javascript layer
     * Session variables are activity-wide, and are not preserved through finish()es or app restarts
     * but are preserved through rotations, etc...
     * @param key the variable to get
     * @return the value of that session variable
     */
    String getSessionVariable(String key);
    /**
     * Sets a session variable to the requested value
     * Session variables are activity-wide, and are not preserved through finish()es or app restarts
     * but are preserved through rotations, etc...
     * @param key the variable to set
     * @param value the new value for that variable
     */
    void setSessionVariable(String key, String value);

    /**
     * finishes() the activity, optionally reloading the page that launched this activity.
     * For example, the nanocamo "app" changes the theme, and index.html only checks the theme
     * on page load. So when you change theme in nanocamo, it needs to reload index.html and will set
     * refresh to true.
     * @param refresh true if the page that opened this activity with launchHTML should be refreshed
     */
    void closeWindow(boolean refresh);

    /**
     * An interface can't extend or implement Activity, so to call methods like setTitle on it a UnitedActivity
     * chain into asActivity first
     * @return this
     */
    Activity asActivity();

    /**
     * Gets the web view from the activity
     * @return the web view, or null if not applicable
     */
    WebView getWebView();

    /**
     * Starts the requested activity
     * @param componentPackage the package of the activity to start
     * @param componentKey the full name of the activity to start
     */
    void doAction(String componentPackage, String componentKey);
}
