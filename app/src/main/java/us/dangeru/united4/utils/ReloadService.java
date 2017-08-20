package us.dangeru.united4.utils;

import android.webkit.WebView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Niles on 8/19/17.
 */

public final class ReloadService {
    private static ArrayList<WeakReference<UnitedActivity>> list;
    static {
        list = new ArrayList<>();
    }
    private ReloadService() {}
    public static void register(UnitedActivity act) {
        list.add(new WeakReference<>(act));
    }
    public static void reload() {
        for (WeakReference<UnitedActivity> item : list) {
            try {
                final WebView webview = item.get().getWebView();
                item.get().asActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        webview.reload();
                    }
                });
            } catch (Throwable ignored) {
                //
            }
        }
    }
}
