package us.dangeru.la_u_ncher.API;

/**
 * Interface that ThreadWatcher accepts in addListener
 */

public interface ThreadWatcherListener {
    /**
     * Called when ThreadWatcher.threads has been updated, either because the list has
     * been refreshed and some threads are now in a "loading" (null) state, or because a
     * thread has finished loading and can now be displayed to the user
     */
    void threadsUpdated();
}
