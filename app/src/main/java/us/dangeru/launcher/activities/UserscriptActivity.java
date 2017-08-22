package us.dangeru.launcher.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import us.dangeru.launcher.API.ThreadWatcher;
import us.dangeru.launcher.R;

/**
 * Created by Niles on 8/21/17.
 */

public class UserscriptActivity extends MainActivity {
    boolean menu_inflated = false;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("menu_inflated")) {
            menu_inflated = savedInstanceState.getBoolean("menu_inflated");
        }
        onCreate(savedInstanceState, R.layout.userscript_activity, R.id.userscript_activity_main_fragment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (!menu_inflated) {
            menu_inflated = true;
            toolbar.inflateMenu(R.menu.userscript_menu);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Intent i = new Intent(UserscriptActivity.this, HiddenSettingsActivity.class);
                    i.putExtra("fragment", HiddenSettingsActivity.FragmentType.THREAD_WATCHER.toString());
                    startActivity(i);
                    return true;
                }
            });
        }
        if (ThreadWatcher.updated_threads > 0) {
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.userscript_menu_with_notification);
        }
    }
    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("menu_inflated", menu_inflated);
    }

}
