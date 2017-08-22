package us.dangeru.launcher.API;

import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;

import us.dangeru.launcher.fragments.ThreadWatcherFragment;
import us.dangeru.launcher.utils.P;

/**
 * Created by Niles on 8/22/17.
 */

public final class ThreadWatcher {
    private static final String TAG = ThreadWatcher.class.getSimpleName();
    public static WatchableThread[] threads;
    public static String[] parallelLabels;
    static {
        refreshAll();
    }
    public static void refreshAll() {
        int[] parallelIds;
        try {
            String[] parallelIdsAsStrings = arrayFromJsonArray(P.get("watched_threads"));
            parallelIds = new int[parallelIdsAsStrings.length];
            for (int i = 0; i < parallelIdsAsStrings.length; i++) {
                parallelIds[i] = Integer.valueOf(parallelIdsAsStrings[i]);
            }
        } catch (Exception e) {
            parallelIds = new int[0];
        }
        parallelLabels = new String[parallelIds.length];
        threads = new WatchableThread[parallelIds.length];
        for (int i = 0; i < parallelIds.length; i++) {
            parallelLabels[i] = "Thread " + parallelIds[i] + " Loading...";
            final int finalI = i;
            final int[] finalParallelIds = parallelIds;
            new java.lang.Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.i(TAG, "Fetching thread " + finalParallelIds[finalI]);
                        WatchableThread thread = WatchableThread.getThreadById(finalParallelIds[finalI]);
                        threads[finalI] = thread;
                        String label = makeLabel(thread);
                        parallelLabels[finalI] = label;
                        Log.i(TAG, "Label for thread " + finalParallelIds[finalI] + " - " + parallelLabels[finalI]);
                    } catch (Exception e) {
                        e.printStackTrace();
                        parallelLabels[finalI] = "Error - " + e;
                        Log.e(TAG, "Error on thread " + finalParallelIds[finalI]);
                    }
                    updateView();
                }
            }).start();
            updateView(); // to set the Loading... messages
        }
    }

    private ThreadWatcher() {
    }

    public static void setRead(int idx) {
        threads[idx].new_replies = 0;
        parallelLabels[idx] = makeLabel(threads[idx]);
        updateView();
    }

    private static String makeLabel(WatchableThread thread) {
        if (thread.new_replies <= 0) {
            return "No new replies to thread " + thread.post_id + " - \"" + thread.title + "\" (" + thread.number_of_replies + " replies in total)";
        } else {
            return thread.new_replies + " new " + (thread.new_replies == 1 ? "reply" : "replies") + " to thread " + thread.post_id + " - \"" + thread.title + "\"";
        }
    }

    private static void updateView() {
        if (listeners == null) return;
        Log.i(TAG, "Updating " + listeners.size() + " listeners");
        for (ThreadWatcherFragment listener : listeners) {
            try {
                listener.setAdapter();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String[] arrayFromJsonArray(String inp) throws Exception {
        JSONArray arr = new JSONArray(inp);
        String res[] = new String[arr.length()];
        for (int i = 0; i < arr.length(); i++) {
            res[i] = arr.getString(i);
        }
        return res;
    }

    static ArrayList<ThreadWatcherFragment> listeners = new ArrayList<>();
    public static void registerListener(ThreadWatcherFragment listener) {
        listeners.add(listener);
    }
}
