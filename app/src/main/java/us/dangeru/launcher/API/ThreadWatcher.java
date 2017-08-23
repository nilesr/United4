package us.dangeru.launcher.API;

import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;

import us.dangeru.launcher.application.United;
import us.dangeru.launcher.utils.P;


/**
 * Created by Niles on 8/22/17.
 */

public final class ThreadWatcher {
    private static final String TAG = ThreadWatcher.class.getSimpleName();
    public static WatchableThread[] threads;
    public static int updated_threads = 0;
    static ArrayList<ThreadWatcherListener> listeners = new ArrayList<>();
    static {
        refreshAll();
    }
    private static int[] pullParallelIds() {
        int[] parallelIds;
        try {
            String[] parallelIdsAsStrings = arrayFromJsonArray(P.get("watched_threads"));
            Log.i(TAG, "parallelIds pulled as " + Arrays.toString(parallelIdsAsStrings));
            parallelIds = new int[parallelIdsAsStrings.length];
            for (int i = 0; i < parallelIdsAsStrings.length; i++) {
                parallelIds[i] = Integer.valueOf(parallelIdsAsStrings[i]);
            }
        } catch (Exception ignored) {
            parallelIds = new int[0];
        }
        return parallelIds;
    }
    public static void refreshAll() {
        int[] parallelIds = pullParallelIds();
        threads = new WatchableThread[parallelIds.length];
        updated_threads = 0;
        if (parallelIds.length == 0) {
            // If you're only watching one thread and you unwatch it, updateView otherwise wouldn't be
            // called because no thread download complete action would ever trigger, so you would still
            // see the one, old thread until you rotated or changed activities and came back.
            // This fixes that
            updateView();
            return;
        }
        for (int i = 0; i < parallelIds.length; i++) {
            final int finalI = i;
            final int[] finalParallelIds = parallelIds;
            new java.lang.Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.i(TAG, "Fetching thread " + finalParallelIds[finalI]);
                        WatchableThread thread = WatchableThread.getThreadById(finalParallelIds[finalI], United.authorizer);
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
        WatchableThread thread = threads[idx];
        thread.new_replies = 0;
        P.set(thread.board + ":" + thread.post_id, String.valueOf(thread.number_of_replies));
        updateNewThreadCounts();
        updateView();
    }


    private static void updateView() {
        if (listeners == null) return;
        Log.i(TAG, "Updating " + listeners.size() + " listeners");
        for (ThreadWatcherListener listener : listeners) {
            try {
                listener.threadsUpdated();
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

    public static void registerListener(ThreadWatcherListener listener) {
        listeners.add(listener);
    }
    // Called to initialize the static { } block above
    public static void initialize() {

    }

    public static boolean isWatching(Integer id) {
        for (int other_id : pullParallelIds()) {
            if (id == other_id) return true;
        }
        return false;
    }
    public static void watchThread(int id) {
        int[] old = pullParallelIds();
        String[] new_string_array = new String[old.length + 1];
        for (int i = 0; i < old.length; i++) {
            new_string_array[i] = String.valueOf(old[i]);
        }
        new_string_array[old.length] = String.valueOf(id);
        P.set("watched_threads", new JSONArray(Arrays.asList(new_string_array)).toString());
        refreshAll();
    }
    public static void unwatchThread(int id) {
        int[] old = pullParallelIds();
        ArrayList<String> new_string_list = new ArrayList<>();
        for (int i = 0; i < old.length; i++) {
            if (old[i] != id) {
                new_string_list.add(String.valueOf(old[i]));
            }
        }
        P.set("watched_threads", new JSONArray(new_string_list).toString());
        refreshAll();
    }
}
