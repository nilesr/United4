package com.angryburg.uapp.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.angryburg.uapp.API.ThreadWatcher;
import com.angryburg.uapp.API.ThreadWatcherListener;
import com.angryburg.uapp.API.WatchableThread;
import com.angryburg.uapp.R;
import com.angryburg.uapp.activities.HiddenSettingsActivity;
import com.angryburg.uapp.activities.UserscriptActivity;
import com.angryburg.uapp.utils.P;

/**
 * Fragment that lets the user view the threads that they're watching
 */

public class ThreadWatcherFragment extends Fragment implements HiddenSettingsFragment, ThreadWatcherListener {
    private static final String TAG = ThreadWatcherFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View res = inflater.inflate(R.layout.thread_watcher_list, container, false);
        final AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // we map 1 to 1 with ThreadWatcher.threads, and if that's null, it's not loaded yet
                if (ThreadWatcher.threads[i] == null) {
                    GenericAlertDialogFragment.newInstance("That thread hasn't loaded yet, please wait.", getFragmentManager());
                    return;
                }
                // Set the thread as read and open the thread in a new UserscriptActivity
                // do NOT ThreadWatcher.setRead(i), it will update the board:id property, and then
                // when the webpage loads, the userscript will think that there are no new replies
                // and will not draw the bar or jump to the bar
                WatchableThread thread = ThreadWatcher.threads[i];
                String url = P.get("awoo_endpoint") + "/" + thread.board + "/thread/" + thread.post_id;
                Intent intent = new Intent(getActivity(), UserscriptActivity.class);
                intent.putExtra("URL", url);
                getActivity().startActivity(intent);
            }
        };
        res.post(new Runnable() {
            @Override
            public void run() {
                //noinspection OverlyStrongTypeCast
                ((ListView) res.findViewById(R.id.thread_list)).setOnItemClickListener(listener);
                setAdapter();
                Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
                addOptions(toolbar);
            }
        });
        return res;
    }

    /**
     * Invalidates the list view, called when the list view changes
     * Also handles showing the "You are not currently watching any threads" message
     */
    public void setAdapter() {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "setAdapter, running on ui thread, ");
                if (getView() == null) return;
                ListView list = getView().findViewById(R.id.thread_list);
                TextView message = getView().findViewById(R.id.no_threads_message);
                if (ThreadWatcher.threads.length == 0) {
                    message.setVisibility(View.VISIBLE);
                    list.setVisibility(View.GONE);
                } else {
                    message.setVisibility(View.GONE);
                    list.setVisibility(View.VISIBLE);
                }
                list.setAdapter(new ThreadWatcherAdapter(getActivity(), ThreadWatcher.threads));
                list.invalidate();
                Log.i(TAG, "success, invalidating...");
            }
        });
    }

    @Override
    public HiddenSettingsActivity.FragmentType getType() {
        return HiddenSettingsActivity.FragmentType.THREAD_WATCHER;
    }

    /**
     * Adds the Refresh button to the toolbar
     * @param toolbar the toolbar to add the item to
     */
    public static void addOptions(Toolbar toolbar) {
        toolbar.setTitle("Thread Watcher");
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.refresh_button);
        toolbar.inflateMenu(R.menu.thread_watcher_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                if (i == R.id.clear_closed) {
                    com.angryburg.uapp.API.Thread[] threads = ThreadWatcher.threads;
                    for (com.angryburg.uapp.API.Thread t : threads) {
                        if (t != null && t.is_locked)
                            ThreadWatcher.unwatchThread(t.post_id);
                    }
                    return true;
                } else if (i == R.id.refresh) {
                    ThreadWatcher.refreshAll();
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Registers us as a listener for changes in ThreadWatcher (refreshes, etc..)
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThreadWatcher.registerListener(this);
    }

    /**
     * Invalidates the list view when a thread changes in ThreadWatcher
     */
    @Override
    public void threadsUpdated() {
        setAdapter();
    }

    /**
     * user visiting the thread probably updated its board:id property because it marked it as read
     * so stop showing "1 new reply" and change to "No new replies"
     */
    @Override
    public void onResume() {
        super.onResume();
        ThreadWatcher.updateNewThreadCounts();
        setAdapter();
    }

    /**
     * The array adapter for the list view, uses thread_watcher_list_item
     * If the thread is loading, it shows a spinner and hides everything else
     * Otherwise it hides the spinner and shows the three text boxes
     * It also sets the click listener for the button and the text in the boxes
     */
    private static class ThreadWatcherAdapter extends ArrayAdapter<WatchableThread> {
        public ThreadWatcherAdapter(Context context, WatchableThread[] list) {
            super(context, 0, list);
        }
        @SuppressLint("SetTextI18n")
        @NonNull
        @Override public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
            final WatchableThread thread = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.thread_watcher_list_item, parent, false);
            }
            TextView title = convertView.findViewById(R.id.thread_title);
            TextView subtitle = convertView.findViewById(R.id.new_replies);
            TextView subtitleContinuation = convertView.findViewById(R.id.subtitle_continuation);
            // If the thread is loading, jump down and make the spinner visible
            // Otherwise hide the spinner and update the text and colors
            if (thread != null) {
                convertView.findViewById(R.id.rel_layout_inner).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.unwatch_button).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.spinner).setVisibility(View.GONE);
                title.setText((thread.is_locked ? "(Locked) " : "") + thread.title);
                // Possible options are:
                // No new replies
                //      "No" must be grey
                // 1 new reply
                // 3 new replies
                //      "1", "3" must be red
                // then we add the total count after
                if (thread.new_replies == 0) {
                    subtitle.setText("No");
                    subtitle.setTextColor(Color.parseColor("#AAAAAA"));
                } else {
                    subtitle.setText(String.valueOf(thread.new_replies));
                    subtitle.setTextColor(Color.RED);
                }
                String subtitleContinuationText;
                if (thread.new_replies == 1) {
                    subtitleContinuationText = " new reply";
                } else {
                    subtitleContinuationText = " new replies";
                }
                subtitleContinuationText += " (" + thread.number_of_replies + " total)";
                subtitleContinuationText += " - /" + thread.board + "/";
                subtitleContinuation.setText(subtitleContinuationText);
            } else {
                convertView.findViewById(R.id.rel_layout_inner).setVisibility(View.GONE);
                convertView.findViewById(R.id.unwatch_button).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.spinner).setVisibility(View.VISIBLE);
            }
            convertView.findViewById(R.id.unwatch_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //if (thread == null) {
                        //GenericAlertDialogFragment.newInstance("This shouldn't happen, user tried to unwatch a thread that was null", getFragmentManager());
                        //return;
                    //}
                    //ThreadWatcher.unwatchThread(thread.post_id); // will invalidate the list view for us
                    ThreadWatcher.unwatchThreadByIndex(position);
                }
            });
            return convertView;
        }

    }
}
