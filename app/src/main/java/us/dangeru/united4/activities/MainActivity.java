package us.dangeru.united4.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import us.dangeru.united4.R;
import us.dangeru.united4.application.United;
import us.dangeru.united4.fragments.UnitedWebFragment;
import us.dangeru.united4.utils.ParcelableMap;
import us.dangeru.united4.utils.UnitedActivity;

import static us.dangeru.united4.fragments.UnitedWebFragment.RESOURCE_FOLDER;

/**
 * Main activity for danger/u/
 */
public class MainActivity extends Activity implements UnitedActivity {
    private ParcelableMap session;
    @SuppressWarnings("FieldCanBeLocal")
    private UnitedWebFragment webFragment;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        FragmentManager manager = getFragmentManager();
        if (savedInstanceState != null) {
            session = ParcelableMap.fromParcel(savedInstanceState.getParcelable("session"));
        } else {
            session = new ParcelableMap();
        }
        webFragment = (UnitedWebFragment) manager.findFragmentByTag("main_webkit_wrapper");
        if (webFragment == null) {
            webFragment = new UnitedWebFragment();
            Bundle args = new Bundle();
            if (getIntent() != null && getIntent().hasExtra("URL")) {
                args.putString("URL", getIntent().getStringExtra("URL"));
            } else {
                args.putString("URL", RESOURCE_FOLDER + "index.html");
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
        outState.putParcelable("session", session.parcel());
    }

    @Override
    public void launchHTML(String resource) {
        Intent i = new Intent(this, MainActivity.class);
        Bundle extras = new Bundle();
        extras.putString("URL", resource);
        extras.putBoolean("support_back_button", true);
        i.putExtras(extras);
        startActivity(i);
    }

    @Override
    public String getSessionVariable(String key) {
        return session.get(key);
    }

    @Override
    public void setSessionVariable(String key, String value) {
        session.put(key, value);
    }

    @Override
    public void closeWindow() {
        finish();
    }

    @Override
    public void onBackPressed() {
        if (getIntent() != null && getIntent().hasExtra("support_back_button") && getIntent().getBooleanExtra("support_back_button", false)) {
            WebView webview = findViewById(R.id.main_webkit);
            if (webview.canGoBack()) {
                webview.goBack();
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

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
