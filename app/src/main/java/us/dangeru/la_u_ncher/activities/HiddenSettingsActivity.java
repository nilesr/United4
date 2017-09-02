package us.dangeru.la_u_ncher.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import us.dangeru.la_u_ncher.R;
import us.dangeru.la_u_ncher.fragments.AwooEndpointFragment;
import us.dangeru.la_u_ncher.fragments.ColorPickerFragment;
import us.dangeru.la_u_ncher.fragments.DebugSettingsListFragment;
import us.dangeru.la_u_ncher.fragments.SettingsListFragment;
import us.dangeru.la_u_ncher.fragments.HiddenSettingsFragment;
import us.dangeru.la_u_ncher.fragments.JanitorLoginFragment;
import us.dangeru.la_u_ncher.fragments.ThreadWatcherFragment;
import us.dangeru.la_u_ncher.utils.P;

/**
 * An activity that can display multiple fragments.
 * It expects a "fragment" to be passed in through the intent, and it will put that fragment
 * in its frame. That fragment can call swapScreen on this activity to switch to a different fragment
 * When the user presses the back button, it will swapScreen back to the fragment from the intent if
 * it wasn't already there, or finish() if it was.
 */

public class HiddenSettingsActivity extends Activity {
    private static final String TAG = HiddenSettingsActivity.class.getSimpleName();
    private FragmentType type = FragmentType.DEBUG_SETTINGS_LIST;
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userscript_activity);
        invalidateToolbarColor();
        if (savedInstanceState != null && savedInstanceState.containsKey("fragment")) {
            type = FragmentType.valueOf(savedInstanceState.getString("fragment"));
        } else if (getIntent().hasExtra("fragment")){
            type = FragmentType.valueOf(getIntent().getStringExtra("fragment"));
        }
        swapScreens(type);
    }
    @Override public void onBackPressed() {
        FragmentType starting_type = FragmentType.DEBUG_SETTINGS_LIST;
        if (getIntent().hasExtra("fragment")) {
            starting_type = FragmentType.valueOf(getIntent().getStringExtra("fragment"));
        }
        if (type != starting_type) {
            swapScreens(starting_type);
        } else {
            finish();
        }
    }

    /**
     * Switches to the passed fragment
     * If the currently shown fragment has the same type as the argument, does nothing. Otherwise,
     * removes the current fragment and makes a new fragment of the passed in type and shows it
     * @param type the fragment to switch to
     */
    public void swapScreens(FragmentType type) {
        this.type = type;
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        // if we already have a fragment and that fragment is not the fragment we want to show, remove it
        // otherwise, it is the fragment we want to show, so we're done, just return and abort the transaction
        if (manager.findFragmentByTag("fragment") != null) {
            if (((HiddenSettingsFragment) manager.findFragmentByTag("fragment")).getType() != type) {
                transaction.remove(manager.findFragmentByTag("fragment"));
            } else {
                return;
            }
        }
        // Make a new fragment
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
            case AWOO_ENDPOINT:
                newFragment = new AwooEndpointFragment();
                break;
            case DEBUG_SETTINGS_LIST:
                newFragment = new DebugSettingsListFragment();
                break;
            case COLOR_PICKER:
                newFragment = new ColorPickerFragment();
                break;
        }
        // Put the fragment in our layout
        //transaction.add(newFragment, "fragment");
        transaction.replace(R.id.userscript_activity_main_fragment, newFragment, "fragment");
        transaction.addToBackStack("fragment"); // TODO is this needed?
        transaction.commit();
    }

    /**
     * Save the currently shown fragment type to the saved instance state so it can be restored
     * @param outState state to be saved
     */
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("fragment", type.toString());
    }

    public void invalidateToolbarColor() {
        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setBackgroundColor(P.getColor("toolbar_color"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Types of fragments that can be embedded in this activity
     */
    public enum FragmentType {
        SETTINGS_LIST,
        DEBUG_SETTINGS_LIST,
        JANITOR_LOGIN,
        THREAD_WATCHER,
        COLOR_PICKER,
        AWOO_ENDPOINT
    }
}
