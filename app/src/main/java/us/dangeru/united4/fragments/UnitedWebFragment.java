package us.dangeru.united4.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import us.dangeru.united4.R;
import us.dangeru.united4.utils.UnitedPropertiesIf;

/**
 * Created by Niles on 8/18/17.
 */

public class UnitedWebFragment extends Fragment {
    public static String RESOURCE_FOLDER = "file:///android_res/raw/";
    public String starting_url = null;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("URL")) {
            starting_url = savedInstanceState.getString("URL");
        } else {
            starting_url = getArguments().getString("URL");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View res = inflater.inflate(R.layout.main, container, false);
        res.post(new Runnable() {
            @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
            @Override
            public void run() {
                WebView webview = res.findViewById(R.id.main_webkit);
                webview.getSettings().setJavaScriptEnabled(true);
                webview.getSettings().setAllowFileAccess(true);
                //webview.getSettings().setDomStorageEnabled(true);
                //webview.getSettings().setAllowFileAccessFromFileURLs(true);
                //webview.getSettings().setAllowUniversalAccessFromFileURLs(true);
                webview.addJavascriptInterface(new UnitedPropertiesIf(getActivity()), "UnitedPropertiesIf");
                if (starting_url != null) {
                    webview.loadUrl(starting_url);
                }
            }
        });
        return res;
    }
    public void loadUrl(final String url) {
        if (getView() == null) {
            throw new IllegalStateException("wtf");
        }
        getView().post(new Runnable() {
            @Override
            public void run() {
                WebView webview = getView().findViewById(R.id.main_webkit);
                webview.loadUrl(RESOURCE_FOLDER + url);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (getView() == null) return;
        outState.putString("URL", ((WebView) getView().findViewById(R.id.main_webkit)).getUrl());
    }
}
