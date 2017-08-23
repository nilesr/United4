package us.dangeru.launcher.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;

import us.dangeru.launcher.API.ThreadWatcher;
import us.dangeru.launcher.R;

/**
 * Created by Niles on 8/21/17.
 */

public class UserscriptActivity extends MainActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreate(savedInstanceState, R.layout.userscript_activity, R.id.userscript_activity_main_fragment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.thread_watcher_menu);
        if (ThreadWatcher.updated_threads > 0) {
            toolbar.inflateMenu(R.menu.userscript_menu_with_notification);
        } else {
            toolbar.inflateMenu(R.menu.userscript_menu);
        }
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.refresh) {
                    getWebView().reload();
                } else {
                    Intent i = new Intent(UserscriptActivity.this, HiddenSettingsActivity.class);
                    i.putExtra("fragment", HiddenSettingsActivity.FragmentType.THREAD_WATCHER.toString());
                    startActivity(i);
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
}
