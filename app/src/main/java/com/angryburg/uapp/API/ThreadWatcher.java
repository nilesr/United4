package com.angryburg.uapp.API;

import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;

import com.angryburg.uapp.application.United;
import com.angryburg.uapp.utils.P;


/**
 * Class that watches threads for updates.
 */

public final class ThreadWatcher {
    private static final String TAG = ThreadWatcher.class.getSimpleName();
    /**
     * Global array of watched threads. You can always get threads.length to see the number of
     * watched threads, or access the threads directly to get their post id, title, etc..
     * While a thread is loading, its entry in this variable is null.
     */
    public static WatchableThread[] threads;
    /**
     * Holds the global number of threads that have one or more new replies.
     * Can be refreshed with updateNewThreadsCount()
     */
    public static int updated_threads = 0;
    /**
     * A list of listeners to be updated when refresh is called (threads start loading) or when
     * a thread finishes loading, so the listener can update the view
     */
    static ArrayList<ThreadWatcherListener> listeners = new ArrayList<>();

    /*
     * Begin getting the list of threads the first time we're accessed
     */
    static {
        refreshAll();
    }

    /**
     * Gets the array of threads that we're watching from the properties singleton
     * @return an array of all the IDs of the threads that we're watching
     */
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

    /**
     * Gets a list of all the threads we're supposed to be watching, sets `threads` to all nulls,
     * fires off an update to the listeners, then makes a new (java) thread for each thread and
     * uses that to fetch the thread against the API. On a thread becoming complete, it will put
     * the thread object in `threads` and update listeners
     */
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

    /**
     * Updates the updated_threads variable with the new count of updated threads
     */
    private static void updateNewThreadCounts() {
        updated_threads = 0;
        for (WatchableThread thread : threads) {
            if (thread == null) continue;
            thread.updateNewRepliesCount();
            if (thread.new_replies > 0) updated_threads++;
        }
    }

    private ThreadWatcher() {
    }

    /**
     * Marks the thread at the given index as read without refreshing the threads list.
     * @param idx the index into the threads object.
     */
    public static void setRead(int idx) {
        WatchableThread thread = threads[idx];
        thread.new_replies = 0;
        P.set(thread.board + ":" + thread.post_id, String.valueOf(thread.number_of_replies));
        updateNewThreadCounts();
        updateView();
    }


    /**
     * Updates all listeners about a change in the `threads` object. Used to tell ThreadWatcherFragment
     * and UserscriptActivity to redraw their lists or menus
     */
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

    /**
     * Registers a new listener
     * @param listener a listener to receive callbacks when the `threads` object is changed
     */
    public static void registerListener(ThreadWatcherListener listener) {
        listeners.add(listener);
    }
    /**
     * Called to initialize the static { } block at the top of this file
     */
    public static void initialize() {

    }

    /**
     * Returns whether the thread with the given ID is being watched
     * @param id the ID of the thread to check
     * @return true if we are watching that thread, false otherwise
     */
    public static boolean isWatching(Integer id) {
        for (int other_id : pullParallelIds()) {
            if (id == other_id) return true;
        }
        return false;
    }

    /**
     * Called to watch a new thread. Updates the watched_threads property and refreshes all the threads
     * @param id the id of the new thread to watch
     */
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
    /**
     * Called to stop watching thread. Updates the watched_threads property and refreshes all the threads
     * @param id the id of the thread to stop watching
     */
    public static void unwatchThread(int id) {
        int[] old = pullParallelIds();
        ArrayList<String> new_string_list = new ArrayList<>();
        for (int old_id : old) {
            if (old_id != id) {
                new_string_list.add(String.valueOf(old_id));
            }
        }
        P.set("watched_threads", new JSONArray(new_string_list).toString());
        refreshAll();
    }
}
