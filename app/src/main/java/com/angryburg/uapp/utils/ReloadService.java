package com.angryburg.uapp.utils;

import android.webkit.WebView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.angryburg.uapp.activities.UnitedActivity;

/**
 * When music.html changes the song, it redraws everything itself. However, when United reaches the end of a song
 * it will chug along and play the next one. However, music.html won't know that the song was changed because it
 * was done on the java side, so it won't redraw, and the text in the currently playing (#song) box won't get
 * updated, and currently playing song won't get turned gold until the user changes the current page or something
 *
 * So this reloads music.html if it exists when you call reload(), fixing the text in the current song box when
 * United automatically plays the next song
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
                        try {
                            if (webview == null) return;
                            if (webview.getUrl().contains("music.html")) {
                                webview.reload();
                            }
                        } catch (Throwable ignored) {
                            //
                        }
                    }
                });
            } catch (Throwable ignored) {
                //
            }
        }
    }
}
