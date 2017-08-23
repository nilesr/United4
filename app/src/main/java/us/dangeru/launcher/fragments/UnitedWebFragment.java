package us.dangeru.launcher.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.solver.ArrayLinkedVariables;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import us.dangeru.launcher.R;
import us.dangeru.launcher.application.United;
import us.dangeru.launcher.utils.P;
import us.dangeru.launcher.utils.UnitedPropertiesIf;

/**
 * Created by Niles on 8/18/17.
 */

public class UnitedWebFragment extends Fragment {
    public static final String RESOURCE_FOLDER = "file:///android_res/raw/";
    public static final String TAG = UnitedWebFragment.class.getSimpleName();
    // Url to load in the page on creation of view
    public String starting_url = null;
    boolean authenticated = false;
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
        if (savedInstanceState != null && savedInstanceState.containsKey("authenticated")) authenticated = savedInstanceState.getBoolean("authenticated");
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
                UnitedWebFragmentWebViewClient client = new UnitedWebFragmentWebViewClient();
                webview.setWebViewClient(client);
                if (client.shouldOverrideUrlLoading(webview, starting_url, false)) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(starting_url));
                    getActivity().startActivity(i);
                    getActivity().finish();
                }
                try {
                    // If we're logged in, and we're about to connect to the awoo endpoint, and we haven't authenticated in this fragment yet, then authenticate
                    if (P.getBool("logged_in") && new URL(starting_url).getAuthority().equals(new URL(P.get("awoo_endpoint")).getAuthority()) && !authenticated) {
                        authenticated = true;
                        String data = "username=" + URLEncoder.encode(United.authorizer.username, "UTF-8");
                        data += "&password=" + URLEncoder.encode(United.authorizer.password, "UTF-8");
                        data += "&redirect=" + URLEncoder.encode(starting_url, "UTF-8");
                        webview.postUrl(P.get("awoo_endpoint") + "/mod", data.getBytes());
                        return;
                    }
                } catch (Exception ignored) {
                    //
                }
                webview.loadUrl(starting_url);
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
        outState.putBoolean("authenticated", authenticated);
        if (getView() == null) return;
        WebView webview = getView().findViewById(R.id.main_webkit);
        webview.saveState(outState);
    }

    // Never open the url in chrome, always stay within the web view
    private class UnitedWebFragmentWebViewClient extends WebViewClient {
        @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return shouldOverrideUrlLoading(view, url, true);
        }
        boolean shouldOverrideUrlLoading(WebView view, String url, boolean alert) {
            Log.i(TAG, "URL: " + url);
            if (P.getBool("override_authorizer")) return false;
            if (url.startsWith(RESOURCE_FOLDER)) return false;
            try {
                String authority = new URL(url).getAuthority();
                List<String> allowed = new ArrayList<>();
                allowed.add(new URL(P.get("awoo_endpoint")).getAuthority());
                allowed.add(new URL("https://boards.dangeru.us").getAuthority());
                allowed.add(new URL("http://augmented.dangeru.us").getAuthority());
                allowed.add(new URL("http://prefetcher.dangeru.us").getAuthority());
                boolean allow = false;
                // boolean allow = allowed.stream().map((x) -> x.equalsIgnoreCase(authority)).filter(x -> x).findAny().orElse(false);
                for (String allowed_authority : allowed) {
                    if (allowed_authority.equalsIgnoreCase(authority)) {
                        allow = true;
                        break;
                    }
                }
                if (!allow && alert) {
                    GenericAlertDialogFragment.newInstance("Refusing page load for unsafe url " + url + " -- Not in list of allowed authorities " + allowed, getFragmentManager());
                }
                return !allow;
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
                return true;
            }
        }
        @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return shouldOverrideUrlLoading(view, request.getUrl().toString());
            }
            GenericAlertDialogFragment.newInstance("Something went wrong in UnitedWebFragmentWebViewClient, this method should only be called on API level 24+, but it was called on an API level below 21", getFragmentManager());
            return true;
        }
        @Override public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            try {
                getActivity().setTitle(view.getTitle());
            } catch (Exception e) {
                // the activity was finished before we could set it, happens sometimes with launching external urls.
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (getView() != null && savedInstanceState != null) {
            WebView webview = getView().findViewById(R.id.main_webkit);
            webview.restoreState(savedInstanceState);
        }
    }
}
