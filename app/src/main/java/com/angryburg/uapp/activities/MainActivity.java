package com.angryburg.uapp.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.webkit.WebView;

import java.net.URI;
import java.util.Iterator;

import com.angryburg.uapp.API.NotificationWorker;
import com.angryburg.uapp.R;
import com.angryburg.uapp.application.United;
import com.angryburg.uapp.fragments.UnitedWebFragment;
import com.angryburg.uapp.utils.AwooNotificationService;
import com.angryburg.uapp.utils.NotifierService;
import com.angryburg.uapp.utils.P;
import com.angryburg.uapp.utils.ParcelableMap;

import org.json.JSONException;
import org.json.JSONObject;

import static com.angryburg.uapp.fragments.UnitedWebFragment.RESOURCE_FOLDER;

/**
 * Main activity for danger/u/
 */
public class MainActivity extends Activity implements UnitedActivity {
    private ParcelableMap session;
    @SuppressWarnings("FieldCanBeLocal")
    private UnitedWebFragment webFragment;

    /**
     * Restores session variables from the saved instance state and sets up the view
     * @param savedInstanceState the previously saved state
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (P.getBool("dark_mode")) setTheme(R.style.AppTheme_Dark);
        // Register to receive reloads to music.html later on
        NotifierService.register(this);
        // load and retrieve session variables
        if (savedInstanceState != null) {
            session = ParcelableMap.fromParcel(savedInstanceState.getParcelable("session"));
        } else {
            session = new ParcelableMap();
        }
        // we wrap this in an if statement because subclasses (like UserscriptActivity) want to inherit
        // the behavior above (restoring saved session variables), but want to inflate a different layout
        // by calling setupView themselves.
        if (getClass().equals(MainActivity.class)) {
            setupView(R.layout.main_activity, R.id.activity_main_activity);
        }
    }

    /**
     * Inflates the given layout and puts the web fragment in the given ID in that layout
     * @param layout the layout to inflate
     * @param id the element in that layout to replace with the web fragment
     */
    protected void setupView(int layout, int id) {
        // blocks until layout is inflated
        setContentView(layout);
        // put our UnitedWebFragment on there
        FragmentManager manager = getFragmentManager();
        webFragment = (UnitedWebFragment) manager.findFragmentByTag("main_webkit_wrapper");
        if (webFragment == null) {
            webFragment = new UnitedWebFragment();
            // if this is the first-time startup (no web fragment created yet), pull the
            // URL from the intent. If there was no URL in the intent, load index.html
            // Give that as the starting URL to the web fragment. The webview fragment will
            // save its own URL to its state and will only pull from arguments on first startup
            Bundle args = new Bundle();
            if (getIntent() != null && getIntent().hasExtra("URL")) {
                args.putString("URL", getIntent().getStringExtra("URL"));
            } else {
                args.putString("URL", RESOURCE_FOLDER + "index.html");
                //args.putString("URL", P.get("awoo_endpoint"));
            }
            webFragment.setArguments(args);
            FragmentTransaction trans = manager.beginTransaction();
            // replace the passed in view ID with the fragment
            trans.replace(id, webFragment, "main_webkit_wrapper");
            trans.addToBackStack("main_webkit_wrapper");
            trans.commit();
        }
    }

    /**
     * Saves the session variables to our saved instance state
     * @param outState instance state to save
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("session", session.parcel());
    }


    @Override
    public void launchHTML(String url) {
        Intent i = new Intent(this, MainActivity.class);
        try {
            // If we're launching to awoo and the userscript is enabled, use the activity with the menu
            if (new URI(P.get("awoo_endpoint")).getAuthority().equals(new URI(url).getAuthority()) && P.getBool("userscript")) {
                i = new Intent(this, UserscriptActivity.class);
            }
        } catch (Exception ignored) {

        }
        Bundle extras = new Bundle();
        extras.putString("URL", url);
        i.putExtras(extras);
        // get notified when it exits
        startActivityForResult(i, 0);
    }
    @Override protected void onActivityResult(int request_code, int result_code, Intent bundle) {
        // if the page we opened requested that we notify, do so.
        // currently used by camo_customize.html to notify the home screen because the theme has changed
        if (result_code == 1) {
            try {
                getWebView().reload();
            } catch (Throwable ignored) {
                //
            }
        } else {
            super.onActivityResult(request_code, result_code, bundle);
        }
    }

    @Override
    public String getSessionVariable(String key) {
        String res = session.get(key);
        if (res == null) return "";
        return res;
    }

    @Override
    public void setSessionVariable(String key, String value) {
        session.put(key, value);
    }

    // finishes the current activity, requesting that the opener refresh the page if needed
    @Override
    public void closeWindow(boolean refresh) {
        if (refresh) {
            setResult(1);
        } else {
            setResult(0);
        }
        finish();
    }

    // since UnitedActivity can't extend Activity, if you want to call Activity methods on one (getApplication, etc..) you need to chain this
    @Override
    public Activity asActivity() {
        return this;
    }

    // Gets the webview from the view, used in NotifierService for reloading
    @Override
    public WebView getWebView() {
        return findViewById(R.id.main_webkit);
    }

    @Override
    public void doAction(String componentPackage, String componentKey, JSONObject extras) {
        ComponentName component = new ComponentName(componentPackage, componentKey);
        Intent i = new Intent();
        if (extras != null) {
            Iterator<String> it = extras.keys();
            while (it.hasNext()) {
                String key = it.next();
                try {
                    i.putExtra(key, extras.getString(key));
                } catch (JSONException ignored) {
                    //
                }
            }
        }
        i.setComponent(component);
        startActivity(i);
    }

    @Override
    public void invalidateToolbar() {
        // There is no toolbar to invalidate!
    }

    @Override
    public void onBackPressed() {
        WebView webview = getWebView();
        if (webview.canGoBack()) {
            webview.goBack();
        } else {
            finish();
        }
    }

    // play a dumb sound when closing an activity
    // only there because the original app had it
    @Override
    public void finish() {
        new Thread() {
            @Override
            public void run() {
                United.playSound("back_sound.mp3");
            }
        }.start();
        super.finish();
    }
    public void onResume() {
        super.onResume();
        NotificationWorker.setAlarm(this);
    }

}
