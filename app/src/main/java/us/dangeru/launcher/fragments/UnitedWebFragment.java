package us.dangeru.launcher.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.CookieHandler;
import java.net.HttpCookie;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import us.dangeru.launcher.R;
import us.dangeru.launcher.utils.ParcelableMap;
import us.dangeru.launcher.utils.PropertiesSingleton;
import us.dangeru.launcher.utils.UnitedPropertiesIf;

/**
 * Created by Niles on 8/18/17.
 */

public class UnitedWebFragment extends Fragment {
    public static final String RESOURCE_FOLDER = "file:///android_res/raw/";
    public static final String TAG = UnitedWebFragment.class.getSimpleName();
    // Url to load in the page on creation of view
    public String starting_url = null;
    Map<String, String> headers = new HashMap<>();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // If we're being created for the first time, pull the URL from or arguments. Otherwise,
        // pull it from the saved instance state (in case we rotated, something)
        if (savedInstanceState != null && savedInstanceState.containsKey("URL")) {
            starting_url = savedInstanceState.getString("URL");
        } else {
            starting_url = getArguments().getString("URL");
        }
        if (getArguments() != null && getArguments().containsKey("headers")) {
            headers = ParcelableMap.fromParcel(getArguments().getParcelable("headers")).asMap();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View res = inflater.inflate(R.layout.main, container, false);
        CookieManager manager = CookieManager.getInstance();
        manager.setAcceptCookie(true);
        res.post(new Runnable() {
            @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
            @Override
            public void run() {
                // Set up our web view with a unitedPropertiesIf and the right starting url
                WebView webview = res.findViewById(R.id.main_webkit);
                webview.getSettings().setJavaScriptEnabled(true);
                webview.getSettings().setAllowFileAccess(true);
                //webview.getSettings().setDomStorageEnabled(true);
                //webview.getSettings().setAllowFileAccessFromFileURLs(true);
                //webview.getSettings().setAllowUniversalAccessFromFileURLs(true);
                webview.addJavascriptInterface(new UnitedPropertiesIf(getActivity()), "unitedPropertiesIf");
                webview.setWebViewClient(new UnitedWebFragmentWebViewClient());
                webview.loadUrl(starting_url, headers);
            }
        });
        return res;
    }

    // save url to saved instance state so we can restore after rotation, etc
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (getView() == null) return;
        outState.putString("URL", ((WebView) getView().findViewById(R.id.main_webkit)).getUrl());
    }

    // Never open the url in chrome, always stay within the web view
    private static class UnitedWebFragmentWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return false;
        }
    }
}
