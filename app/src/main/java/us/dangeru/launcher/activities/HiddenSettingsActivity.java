package us.dangeru.launcher.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import us.dangeru.launcher.R;
import us.dangeru.launcher.fragments.SettingsListFragment;
import us.dangeru.launcher.fragments.HiddenSettingsFragment;
import us.dangeru.launcher.fragments.JanitorLoginFragment;
import us.dangeru.launcher.fragments.ThreadWatcherFragment;

/**
 * Created by Niles on 8/21/17.
 */

public class HiddenSettingsActivity extends Activity {
    FragmentType type = FragmentType.SETTINGS_LIST;
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userscript_activity);
        if (savedInstanceState != null && savedInstanceState.containsKey("fragment")) {
            type = FragmentType.valueOf(savedInstanceState.getString("fragment"));
        } else if (getIntent().hasExtra("fragment")){
            type = FragmentType.valueOf(getIntent().getStringExtra("fragment"));
        }
        swapScreens(type);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.hidden_settings_menu);
        //toolbar.setSubtitle("Developer Options");
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                finish();
                return true;
            }
        });
    }
    @Override public void onBackPressed() {
        FragmentType starting_type = FragmentType.SETTINGS_LIST;
        if (getIntent().hasExtra("fragment")) {
            starting_type = FragmentType.valueOf(getIntent().getStringExtra("fragment"));
        }
        if (type != starting_type) {
            swapScreens(starting_type);
        } else {
            finish();
        }
    }
    public void swapScreens(FragmentType type) {
        this.type = type;
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (manager.findFragmentByTag("fragment") != null) {
            if (((HiddenSettingsFragment) manager.findFragmentByTag("fragment")).getType() != type) {
                transaction.remove(manager.findFragmentByTag("fragment"));
            } else {
                return;
            }
        }
        Fragment newFragment = null;
        switch (type) {
            case SETTINGS_LIST:
                newFragment = new SettingsListFragment();
                break;
            case JANITOR_LOGIN:
                newFragment = new JanitorLoginFragment();
                break;
            case THREAD_WATCHER:
                newFragment = new ThreadWatcherFragment();
                break;
        }
        //transaction.add(newFragment, "fragment");
        transaction.replace(R.id.userscript_activity_main_fragment, newFragment, "fragment");
        transaction.addToBackStack("fragment");
        transaction.commit();
    }
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("fragment", type.toString());
    }
    public enum FragmentType {
        SETTINGS_LIST,
        JANITOR_LOGIN,
        THREAD_WATCHER
    }
}
