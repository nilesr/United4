package com.angryburg.uapp.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.angryburg.uapp.R;
import com.angryburg.uapp.fragments.AwooEndpointFragment;
import com.angryburg.uapp.fragments.ColorListFragment;
import com.angryburg.uapp.fragments.ColorPickerFragment;
import com.angryburg.uapp.fragments.DebugSettingsListFragment;
import com.angryburg.uapp.fragments.NewPropertyFragment;
import com.angryburg.uapp.fragments.NotificationSettingsFragment;
import com.angryburg.uapp.fragments.PropertiesListFragment;
import com.angryburg.uapp.fragments.PropertyEditorFragment;
import com.angryburg.uapp.fragments.SettingsListFragment;
import com.angryburg.uapp.fragments.HiddenSettingsFragment;
import com.angryburg.uapp.fragments.JanitorLoginFragment;
import com.angryburg.uapp.fragments.ThreadWatcherFragment;
import com.angryburg.uapp.utils.P;
import com.angryburg.uapp.utils.WindowUtils;

import java.util.Stack;

/**
 * An activity that can display multiple fragments.
 * It expects a "fragment" to be passed in through the intent, and it will put that fragment
 * in its frame. That fragment can call swapScreen on this activity to switch to a different fragment
 * When the user presses the back button, it will swapScreen back to the fragment from the intent if
 * it wasn't already there, or finish() if it was.
 */

public class HiddenSettingsActivity extends Activity {
    @SuppressWarnings("unused")
    private static final String TAG = HiddenSettingsActivity.class.getSimpleName();
    private Stack<FragmentType> windowStack;
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (P.getBool("dark_mode")) setTheme(R.style.AppTheme_Dark);
        setContentView(R.layout.userscript_activity);
        invalidateToolbarColor();
        FragmentType type = FragmentType.DEBUG_SETTINGS_LIST;
        windowStack = new Stack<>();
        if (savedInstanceState != null && savedInstanceState.containsKey("stack")) {
            //noinspection unchecked
            String[] arr = savedInstanceState.getStringArray("stack");
            if (arr != null) {
                windowStack = new Stack<>();
                for (String val : arr) {
                    windowStack.push(FragmentType.valueOf(val));
                }
                type = windowStack.peek();
            }
        } else if (getIntent().hasExtra("fragment") && savedInstanceState == null){
            type = FragmentType.valueOf(getIntent().getStringExtra("fragment"));
            windowStack.push(type);
        } else {
            windowStack.push(type);
        }
        swapScreens(type);
    }
    @Override public void onBackPressed() {
        if (windowStack.isEmpty() || windowStack.size() == 1) {
            finish();
            return;
        }
        pop();
    }

    /**
     * Replaces the top of the fragment stack with the given fragment type
     * @param type The fragment to replace
     * @param args The arguments to pass to that fragment
     */
    public void replace(FragmentType type, Bundle args) {
        windowStack.pop();
        windowStack.push(type);
        swapScreens(type, args);
    }

    /**
     * Pushes another fragment onto the fragment stack
     * @param type the type of fragment to put on the stack
     * @param args optional arguments to pass to the fragment
     */
    public void push(FragmentType type, Bundle args) {
        windowStack.push(type);
        swapScreens(type, args);
    }

    /**
     * Pushes another fragment onto the fragment stack
     * @param type the type of fragment to put on the stack
     */
    public void push(FragmentType type) {
        push(type, null);
    }

    /**
     * Pops the top fragment off the stack.
     */
    public void pop() {
        windowStack.pop();
        if (windowStack.isEmpty()) {
            finish();
            return;
        }
        swapScreens(windowStack.peek());
    }
    private void swapScreens(FragmentType type) {
        swapScreens(type, null);
    }
    /**
     * Switches to the passed fragment
     * If the currently shown fragment has the same type as the argument, does nothing. Otherwise,
     * removes the current fragment and makes a new fragment of the passed in type and shows it
     * @param type the fragment to switch to
     * @param arguments arguments to give to the fragment
     */
    private void swapScreens(FragmentType type, @Nullable Bundle arguments) {
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
            case COLOR_LIST:
                newFragment = new ColorListFragment();
                break;
            case PROPERTY_EDITOR:
                newFragment = new PropertyEditorFragment();
                break;
            case PROPERTY_EDITOR_NEW:
                newFragment = new NewPropertyFragment();
                break;
            case PROPERTIES_LIST:
                newFragment = new PropertiesListFragment();
                break;
            case NOTIFICATION_SETTINGS:
                newFragment = new NotificationSettingsFragment();
                break;
            case COLOR_PICKER:
                newFragment = new ColorPickerFragment();
                break;
        }
        newFragment.setArguments(arguments);
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
        String[] arr = new String[windowStack.size()];
        int i = 0;
        for (FragmentType t : windowStack.toArray(new FragmentType[windowStack.size()]))
            //noinspection ValueOfIncrementOrDecrementUsed
            arr[i++] = t.toString();
        outState.putStringArray("stack", arr);
    }

    /**
     * Refreshes the toolbar color from the properties.
     */
    public void invalidateToolbarColor() {
        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setBackgroundColor(P.getColor("toolbar_color"));
            WindowUtils.updateWindowBarColor(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Types of fragments that can be embedded in this activity
     */
    @SuppressWarnings("JavaDoc")
    public enum FragmentType {
        SETTINGS_LIST,
        DEBUG_SETTINGS_LIST,
        JANITOR_LOGIN,
        THREAD_WATCHER,
        COLOR_LIST,
        AWOO_ENDPOINT,
        PROPERTY_EDITOR,
        PROPERTY_EDITOR_NEW,
        PROPERTIES_LIST,
        COLOR_PICKER,
        NOTIFICATION_SETTINGS
    }
}
