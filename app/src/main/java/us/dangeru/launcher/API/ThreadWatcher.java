package us.dangeru.launcher.API;

import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;

import us.dangeru.launcher.fragments.ThreadWatcherFragment;
import us.dangeru.launcher.utils.P;

import static us.dangeru.launcher.fragments.ThreadWatcherFragment.makeLabel;

/**
 * Created by Niles on 8/22/17.
 */

public final class ThreadWatcher {
    private static final String TAG = ThreadWatcher.class.getSimpleName();
    public static WatchableThread[] threads;
    public static int updated_threads = 0;
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
        threads = new WatchableThread[parallelIds.length];
        updated_threads = 0;
        for (int i = 0; i < parallelIds.length; i++) {
            final int finalI = i;
            final int[] finalParallelIds = parallelIds;
            new java.lang.Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.i(TAG, "Fetching thread " + finalParallelIds[finalI]);
                        WatchableThread thread = WatchableThread.getThreadById(finalParallelIds[finalI]);
                        threads[finalI] = thread;
                        if (thread.new_replies > 0) updated_threads++;
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "Error on thread " + finalParallelIds[finalI]);
                    }
                    updateView();
                }
            }).start();
            updateView(); // to set the Loading... messages
        }
    }
    public static void updateNewThreadCounts() {
        updated_threads = 0;
        for (WatchableThread thread : threads) {
            thread.updateNewRepliesCount();
            if (thread.new_replies > 0) updated_threads++;
        }
    }

    private ThreadWatcher() {
    }

    public static void setRead(int idx) {
        threads[idx].new_replies = 0;
        Thread thread = threads[idx];
        P.set(thread.board + ":" + thread.post_id, String.valueOf(thread.number_of_replies));
        updateNewThreadCounts();
        updateView();
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
    // Called to initialize the static { } block above
    public static void initialize() {

    }
}
