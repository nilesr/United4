package us.dangeru.launcher.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.os.Bundle;
import android.webkit.WebView;

import us.dangeru.launcher.R;
import us.dangeru.launcher.application.United;
import us.dangeru.launcher.fragments.UnitedWebFragment;
import us.dangeru.launcher.utils.ParcelableMap;
import us.dangeru.launcher.utils.ReloadService;
import us.dangeru.launcher.utils.UnitedActivity;

import static us.dangeru.launcher.fragments.UnitedWebFragment.RESOURCE_FOLDER;

/**
 * Main activity for danger/u/
 */
public class MainActivity extends Activity implements UnitedActivity {
    private ParcelableMap session;
    @SuppressWarnings("FieldCanBeLocal")
    private UnitedWebFragment webFragment;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Register to receive reloads to music.html later on
        ReloadService.register(this);
        setContentView(R.layout.main_activity);
        // load and retrieve session variables
        if (savedInstanceState != null) {
            session = ParcelableMap.fromParcel(savedInstanceState.getParcelable("session"));
        } else {
            session = new ParcelableMap();
        }
        // put our UnitedWebFragment on there
        FragmentManager manager = getFragmentManager();
        webFragment = (UnitedWebFragment) manager.findFragmentByTag("main_webkit_wrapper");
        if (webFragment == null) {
            webFragment = new UnitedWebFragment();
            // if we have a URL in the intent, load that, otherwise load index.html
            Bundle args = new Bundle();
            if (getIntent() != null && getIntent().hasExtra("URL")) {
                args.putString("URL", getIntent().getStringExtra("URL"));
            } else {
                args.putString("URL", RESOURCE_FOLDER + "index.html");
            }
            if (getIntent().hasExtra("headers")) {
                args.putParcelable("headers", getIntent().getParcelableExtra("headers"));
            }
            webFragment.setArguments(args);
            FragmentTransaction trans = manager.beginTransaction();
            trans.replace(R.id.activity_main_activity, webFragment, "main_webkit_wrapper");
            trans.addToBackStack("main_webkit_wrapper");
            trans.commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save session variables to the instance state
        outState.putParcelable("session", session.parcel());
    }

    // launches a HTML page in a new activity
    @Override
    public void launchHTML(String resource) {
        Intent i = new Intent(this, MainActivity.class);
        Bundle extras = new Bundle();
        extras.putString("URL", resource);
        extras.putBoolean("support_back_button", true);
        i.putExtras(extras);
        // get notified when it exits
        startActivityForResult(i, 0);
    }
    @Override protected void onActivityResult(int request_code, int result_code, Intent bundle) {
        // if the page we opened requested that we reload, do so.
        // currently used by camo_customize.html to reload the home screen because the theme has changed
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

    // Gets the webview from the view, used in ReloadService for reloading
    @Override
    public WebView getWebView() {
        return findViewById(R.id.main_webkit);
    }

    @Override
    public void doAction(String componentPackage, String componentKey) {
        ComponentName component = new ComponentName(componentPackage, componentKey);
        Intent i = new Intent();
        i.setComponent(component);
        startActivity(i);
    }

    // If support_back_button is unset or false, tries to go back in the webview, and if there's no more history, will finish the activity
    // if support_back_button is true (it never is), just finish immediately
    @Override
    public void onBackPressed() {
        if (getIntent() != null && getIntent().hasExtra("support_back_button") && getIntent().getBooleanExtra("support_back_button", false)) {
            WebView webview = getWebView();
            if (webview.canGoBack()) {
                webview.goBack();
            } else {
                finish();
            }
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
}
