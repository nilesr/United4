package us.dangeru.launcher.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
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
        ReloadService.register(this);
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
        startActivityForResult(i, 0);
    }
    @Override protected void onActivityResult(int request_code, int result_code, Intent bundle) {
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

    @Override
    public void closeWindow(boolean refresh) {
        if (refresh) {
            setResult(1);
        } else {
            setResult(0);
        }
        finish();
    }

    @Override
    public Activity asActivity() {
        return this;
    }

    @Override
    public WebView getWebView() {
        return findViewById(R.id.main_webkit);
    }

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
