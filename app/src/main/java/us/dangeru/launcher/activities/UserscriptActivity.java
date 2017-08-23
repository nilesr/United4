package us.dangeru.launcher.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.SubMenu;

import us.dangeru.launcher.API.ThreadWatcher;
import us.dangeru.launcher.API.ThreadWatcherListener;
import us.dangeru.launcher.API.WatchableThread;
import us.dangeru.launcher.R;
import us.dangeru.launcher.application.United;
import us.dangeru.launcher.utils.P;

/**
 * Created by Niles on 8/21/17.
 */

public class UserscriptActivity extends MainActivity implements ThreadWatcherListener {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreate(savedInstanceState, R.layout.userscript_activity, R.id.userscript_activity_main_fragment);
        invalidateToolbar();
    }
    private void invalidateToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.thread_watcher_menu);
        if (ThreadWatcher.updated_threads > 0) {
            toolbar.inflateMenu(R.menu.userscript_menu_with_notification);
        } else {
            toolbar.inflateMenu(R.menu.userscript_menu);
        }
        // boards may not be loaded yet
        // don't show submenu if they aren't
        if (United.boards != null) {
            SubMenu submenu = toolbar.getMenu().addSubMenu("Boards");
            for (String board : United.boards) {
                submenu.add("/" + board + "/").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
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
        if (ThreadWatcher.threads.length > 0) {
            SubMenu submenu = toolbar.getMenu().addSubMenu("Watched Threads");
            for (int i = 0; i < ThreadWatcher.threads.length; i++) {
                final WatchableThread thread = ThreadWatcher.threads[i];
                if (thread == null) {
                    submenu.add("This thread failed to load, please try again later");
                    continue;
                }
                String title = thread.title;
                if (thread.new_replies > 0) {
                    title += " (+" + thread.new_replies + ")";
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
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.hasSubMenu()) return true;
                switch (item.getItemId()) {
                    case R.id.refresh:
                        getWebView().reload();
                        break;
                    case R.id.thread_watcher:
                    case R.id.thread_watcher_with_notification:
                        Intent i = new Intent(UserscriptActivity.this, HiddenSettingsActivity.class);
                        i.putExtra("fragment", HiddenSettingsActivity.FragmentType.THREAD_WATCHER.toString());
                        startActivity(i);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(title);
    }

    @Override
    public void threadsUpdated() {
        invalidateToolbar();
    }
}
