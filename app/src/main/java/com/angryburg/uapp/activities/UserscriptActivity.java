package com.angryburg.uapp.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Pair;
import android.view.MenuItem;
import android.view.SubMenu;
import android.webkit.WebView;
import android.widget.Toast;

import java.util.List;

import com.angryburg.uapp.API.BoardsList;
import com.angryburg.uapp.API.BoardsListListener;
import com.angryburg.uapp.API.ThreadWatcher;
import com.angryburg.uapp.API.ThreadWatcherListener;
import com.angryburg.uapp.API.URLUtils;
import com.angryburg.uapp.API.WatchableThread;
import com.angryburg.uapp.R;
import com.angryburg.uapp.fragments.GenericAlertDialogFragment;
import com.angryburg.uapp.utils.P;
import com.angryburg.uapp.utils.WindowUtils;

/**
 * This is almost exactly the same as MainActivity, but there's a menu bar at the top.
 * Just about the entire class deals with updating the menu at the top
 */

public class UserscriptActivity extends MainActivity implements ThreadWatcherListener, BoardsListListener {
    /**
     * Sets up the view with the userscript activity layout and puts the web fragment in the main fragment frame
     * @param savedInstanceState the previously saved state
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView(R.layout.userscript_activity, R.id.userscript_activity_main_fragment);
        invalidateToolbar(null);
        ThreadWatcher.registerListener(this);
    }

    /**
     * Clears the toolbar, then adds options to it.
     *
     * It will always display a refresh button to notify the webview
     *
     * Then, if there are updated threads in the thread watcher, it will display the thread watcher
     * notification, which will always show as a button and won't be hidden in the menu. If there are
     * no updated threads, it will instead add a regular thread watcher item which will only be shown
     * as a button if there's room
     *
     * After that, it will parse the URL of the web view if possible, and determine if the user is
     * on a url like /:board, /:board/thread/:thread, or /ip/:addr. If the user is on /:board, it adds
     * a rules button to visit /:board/rules in a new activity. If the user is on /:board/thread/:thread
     * it will detect if the ThreadWatcher is watching :thread. If not, it will display a watch thread (+)
     * button, if so it will display an unwatch thread (-) button.
     *
     * Then, it will see if the United application has pulled down the list of boards, and if so, it
     * will create a submenu with each of the boards in the list. United.authorizer will be set if
     * the user is logged in as a janitor, and United and BoardsList should automatically take care
     * of showing hidden boards like /staff/ if the user is logged in.
     *
     * Then, it will check if there are any threads watched in the thread watcher. If so, it will
     * create a submenu called "Watched Threads", and populate it with the title of each watched
     * thread. If the watched thread has new replies, it will add (+3) to the end, replacing `3` with
     * the actual number of new replies
     *
     * Also adds the settings item
     *
     * Also adds the back item
     *
     * @param url the URL of the web view (for detecting /:board, /:board/thread/:thread, /ip/:addr, etc...) or null if the web view isn't ready yet
     */
    public void invalidateToolbar(String url) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(P.getColor("toolbar_color"));
        WindowUtils.updateWindowBarColor(this);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.refresh_button);
        if (ThreadWatcher.updated_threads > 0) {
            toolbar.inflateMenu(R.menu.userscript_menu_with_notification);
        } else {
            toolbar.inflateMenu(R.menu.userscript_menu);
        }
        final String url_board;
        final Pair<String, Integer> url_thread;
        final String url_ip;
        if (url != null) {
            url_board = URLUtils.isBoard(url);
            url_thread = URLUtils.isThread(url);
            url_ip = URLUtils.isIpList(url);
            if (url_board != null) {
                toolbar.inflateMenu(R.menu.userscript_board_menu);
            }
            if (url_thread != null) {
                toolbar.inflateMenu(R.menu.share_button);
                toolbar.inflateMenu(R.menu.userscript_thread_menu);
                MenuItem item = toolbar.getMenu().findItem(R.id.watch_thread);
                if (ThreadWatcher.isWatching(url_thread.second)) {
                    item.setIcon(R.mipmap.unwatch);
                    item.setTitle("Unwatch Thread");
                } else {
                    item.setIcon(R.mipmap.watch);
                    item.setTitle("Watch Thread");
                }
            }
            if (url_ip != null) {
                toolbar.inflateMenu(R.menu.userscript_ip_menu);
            }
        } else {
            url_board = null;
            url_thread = null;
            url_ip = null;
        }
        // boards may not be loaded yet
        // don't show submenu if they aren't
        if (BoardsList.boards != null) {
            SubMenu submenu = toolbar.getMenu().addSubMenu("Boards");
            for (BoardsList.Board board : BoardsList.boards) {
                submenu.add("/" + board.name + "/").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent i = new Intent(UserscriptActivity.this, UserscriptActivity.class);
                        i.putExtra("URL", P.get("awoo_endpoint") + item.getTitle());
                        startActivity(i);
                        return true;
                    }
                });
            }
        }
        if (ThreadWatcher.threads != null && ThreadWatcher.threads.length > 0) {
            SubMenu submenu = toolbar.getMenu().addSubMenu("Watched Threads");
            for (int i = 0; i < ThreadWatcher.threads.length; i++) {
                final WatchableThread thread = ThreadWatcher.threads[i];
                if (thread == null) {
                    submenu.add("This thread failed to load, please try again later");
                    continue;
                }
                String title = thread.title;
                if (thread.new_replies > 0) {
                    // prepend count instead of append because the screen might not be wide enough to show the whole title
                    title = "(+" + thread.new_replies + ") " + title;
                }
                MenuItem item = submenu.add(title);
                final int finalI = i;
                item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        ThreadWatcher.setRead(finalI);
                        Intent i = new Intent(UserscriptActivity.this, UserscriptActivity.class);
                        i.putExtra("URL", P.get("awoo_endpoint") + "/" + thread.board + "/thread/" + thread.post_id);
                        startActivity(i);
                        return true;
                    }
                });
            }
        }
        toolbar.inflateMenu(R.menu.settings_item);
        if (P.getBool("force_show_back_btn")) {
            toolbar.inflateMenu(R.menu.back_item_forced);
        } else {
            toolbar.inflateMenu(R.menu.back_item);
        }
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.hasSubMenu()) return true;
                int itemId = item.getItemId();
                if (itemId == R.id.refresh) {
                    getWebView().reload();
                } else if (itemId == R.id.thread_watcher || itemId == R.id.thread_watcher_with_notification) {
                    Intent i = new Intent(UserscriptActivity.this, HiddenSettingsActivity.class);
                    i.putExtra("fragment", HiddenSettingsActivity.FragmentType.THREAD_WATCHER.toString());
                    startActivity(i);
                } else if (itemId == R.id.rules) {
                    Intent i2 = new Intent(UserscriptActivity.this, UserscriptActivity.class);
                    i2.putExtra("URL", P.get("awoo_endpoint") + "/" + url_board + "/rules");
                    startActivity(i2);
                } else if (itemId == R.id.watch_thread) {
                    if (url_thread == null) {
                        GenericAlertDialogFragment.newInstance("This shouldn't be possible", getSupportFragmentManager());
                        return true;
                    }
                    if (ThreadWatcher.isWatching(url_thread.second)) {
                        ThreadWatcher.unwatchThread(url_thread.second);
                    } else {
                        ThreadWatcher.watchThread(url_thread.second, false);
                    }
                } else if (itemId == R.id.settings) {
                    Intent i3 = new Intent(UserscriptActivity.this, HiddenSettingsActivity.class);
                    i3.putExtra("fragment", HiddenSettingsActivity.FragmentType.SETTINGS_LIST.toString());
                    startActivity(i3);
                } else if (itemId == R.id.back_item || itemId == R.id.back_item_forced) {
                    setResult(0);
                    finish();
                } else if (itemId == R.id.share) {
                    Intent i4 = new Intent(Intent.ACTION_SEND);
                    i4.setType("text/plain");
                    //i4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    i4.putExtra(Intent.EXTRA_SUBJECT, getTitle());
                    i4.putExtra(Intent.EXTRA_TEXT, getWebView().getUrl());
                    startActivity(Intent.createChooser(i4, "Share"));
                } else if (itemId == R.id.hide_thread) {
                    if (url_thread == null) return false;
                    String key = url_thread.first + ":" + String.valueOf(url_thread.second);
                    P.set(key, "hide");
                    WebView v = getWebView();
                    if (v != null && v.canGoBack()) v.goBack();
                    Toast.makeText(UserscriptActivity.this, "You won't see this thread again", Toast.LENGTH_LONG).show();

                    return false;
                } else {
                    return false;
                }
                return true;
            }
        });
    }

    /**
     * Called when the webview has finished loading a page. Sets the title on the fragment,
     * but also because the page has changed, and we may have moved from something like `/` to `/:board`
     * or `/:board` to `/:board/thread/:thread`, we may need to update which buttons are shown in the
     * toolbar
     * @param title
     */
    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        invalidateToolbar();
    }

    /**
     * Tries to update which buttons should be shown in the toolbar, if possible
     */
    public void invalidateToolbar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    invalidateToolbar(getWebView().getUrl());
                } catch (Exception e) {
                    if (!(e instanceof NullPointerException)) e.printStackTrace();
                    // eh it's probably fine
                    invalidateToolbar(null);
                }
            }
        });
    }

    /**
     * When the user clicks the watch thread or unwatch thread button, we need to redraw
     * the options menu with or without that thread. The ThreadWatcher will instruct us to do that
     * using this method
     */
    @Override
    public void threadsUpdated() {
        invalidateToolbar();
    }

    @Override
    public void boardsListReady(List<BoardsList.Board> list) {
        invalidateToolbar();
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateToolbar();
    }
}
