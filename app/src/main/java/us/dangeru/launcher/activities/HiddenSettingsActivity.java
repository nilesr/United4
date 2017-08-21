package us.dangeru.launcher.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;

import us.dangeru.launcher.R;
import us.dangeru.launcher.fragments.SettingsListFragment;
import us.dangeru.launcher.fragments.HiddenSettingsFragment;
import us.dangeru.launcher.fragments.JanitorLoginFragment;

/**
 * Created by Niles on 8/21/17.
 */

public class HiddenSettingsActivity extends Activity {
    FragmentType type = FragmentType.SETTINGS_LIST;
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        if (savedInstanceState != null && savedInstanceState.containsKey("fragment")) {
            type = FragmentType.valueOf(savedInstanceState.getString("fragment"));
        } else if (getIntent().hasExtra("fragment")){
            type = FragmentType.valueOf(getIntent().getStringExtra("fragment"));
        }
        swapScreens(type);
    }
    @Override public void onBackPressed() {
        if (type != FragmentType.SETTINGS_LIST) {
            swapScreens(FragmentType.SETTINGS_LIST);
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
        }
        //transaction.add(newFragment, "fragment");
        transaction.replace(R.id.activity_main_activity, newFragment, "fragment");
        transaction.addToBackStack("fragment");
        transaction.commit();
    }
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("fragment", type.toString());
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.hidden_settings_menu, menu);
        return true;
    }
    public enum FragmentType {
        SETTINGS_LIST,
        JANITOR_LOGIN
    }
}
