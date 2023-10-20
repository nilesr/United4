package com.angryburg.uapp.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.net.MailTo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
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
import java.util.Collection;
import java.util.List;

import com.angryburg.uapp.R;
import com.angryburg.uapp.application.United;
import com.angryburg.uapp.utils.P;
import com.angryburg.uapp.utils.UnitedPropertiesIf;

/**
 * A web view containing fragment
 */

public class UnitedWebFragment extends Fragment {
    /**
     * URI start for accessing app/src/main/res/raw
     */
    public static final String RESOURCE_FOLDER = "file:///android_res/raw/";
    private static final String TAG = UnitedWebFragment.class.getSimpleName();
    /**
     * Url to load in the page on creation of view
     */
    public String starting_url = null;
    /**
     * Whether we've authenticated in this fragment yet or not
     * Unfortunately we can't just call United.authenticator.getCookie() and set that on the webview,
     * webviews are especially picky about having cookies set on them and it just won't work.
     * So what we do is POST to /mod IN THE WEB VIEW, and request a redirect to starting_url.
     * This holds a boolean on whether we've done that or not yet, so we only do it once and
     * not every time the user rotates the screen
     */
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
                webview.setBackgroundColor(Color.rgb(0, 0, 0));
                webview.getSettings().setJavaScriptEnabled(true);
                webview.getSettings().setAllowFileAccess(true);
                //webview.getSettings().setDomStorageEnabled(true);
                //webview.getSettings().setAllowFileAccessFromFileURLs(true);
                //webview.getSettings().setAllowUniversalAccessFromFileURLs(true);
                webview.addJavascriptInterface(new UnitedPropertiesIf(getActivity()), "unitedPropertiesIf");
                UnitedWebFragmentWebViewClient client = new UnitedWebFragmentWebViewClient();
                webview.setWebViewClient(client);
                // If it's not safe to view this page, finish() the activity. `client` will open the url in the default web browser
                if (client.shouldOverrideUrlLoading(starting_url)) {
                    getActivity().finish();
                    return;
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
                // If we don't need to authenticate or we've already authenticated, just load the url
                webview.loadUrl(starting_url);
            }
        });
        return res;
    }


    /**
     * save url to saved instance state so we can restore after rotation, etc
     * @param outState the state to be saved
     */
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

    /**
     * Do not expose the injected javascript interface to unauthorized websites
     */
    private class UnitedWebFragmentWebViewClient extends WebViewClient {
        @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return shouldOverrideUrlLoading(url);
        }
        boolean shouldOverrideUrlLoading(String url) {
            Log.i(TAG, "URL: " + url);
            if (P.getBool("override_authorizer")) return false;
            if (url.startsWith(RESOURCE_FOLDER)) return false;
            if (url.startsWith("mailto:")) {
                MailTo mt = MailTo.parse(url);
                Intent i = newEmailIntent(mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
                UnitedWebFragment.this.getActivity().startActivity(i);
                return true;
            }
            try {
                String authority = new URL(url).getAuthority();
                Collection<String> allowed = new ArrayList<>();
                allowed.add(new URL(P.get("awoo_endpoint")).getAuthority());
                allowed.add(new URL("https://dangeru.us").getAuthority());
                allowed.add(new URL("https://boards.dangeru.us").getAuthority());
                allowed.add(new URL("http://augmented.dangeru.us").getAuthority());
                allowed.add(new URL("http://prefetcher.dangeru.us").getAuthority());
                allowed.add(new URL("http://kiramiki.dangeru.us").getAuthority());
                boolean allow = false;
                // boolean allow = allowed.stream().map((x) -> x.equalsIgnoreCase(authority)).filter(x -> x).findAny().orElse(false);
                for (String allowed_authority : allowed) {
                    if (allowed_authority.equalsIgnoreCase(authority)) {
                        allow = true;
                        break;
                    }
                }
                if (!allow) {
                    //GenericAlertDialogFragment.newInstance("Refusing page load for unsafe url " + url + " -- Not in list of allowed authorities " + allowed, getFragmentManager());
                    Log.w(TAG, "Refusing page load for unsafe url " + url + " -- Not in list of allowed authorities " + allowed);
                    // launch the url in the default web browser, and since we'll return true (we should override the url) it won't get opened in the web view
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    getActivity().startActivity(i);
                }
                return !allow;
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
                return true;
            }
        }
        @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return shouldOverrideUrlLoading(view, request.getUrl().toString());
        }
        @Override public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            try {
                if (getActivity() == null) return; // happens sometimes when the internet is disconnected
                getActivity().setTitle(view.getTitle());
            } catch (Exception e) {
                // the activity was finished before we could set it, happens sometimes with launching external urls.
                e.printStackTrace();
            }
        }
    }

    /**
     * Restore web view state
     * @param savedInstanceState state to be restored
     */
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (getView() != null && savedInstanceState != null) {
            WebView webview = getView().findViewById(R.id.main_webkit);
            webview.restoreState(savedInstanceState);
        }
    }

    /**
     * Creates an intent for an email activity
     * @param address the email address to send to
     * @param subject the subject
     * @param body the body
     * @param cc any email addresses to cc
     * @return an intent that when started, prompts the user to send the email
     */
    private static Intent newEmailIntent(String address, String subject, String body, String cc) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { address });
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_CC, cc);
        intent.setType("message/rfc822");
        return intent;
    }
}
