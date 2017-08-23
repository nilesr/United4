package us.dangeru.launcher.fragments;

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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import us.dangeru.launcher.API.ThreadWatcher;
import us.dangeru.launcher.API.WatchableThread;
import us.dangeru.launcher.R;
import us.dangeru.launcher.activities.HiddenSettingsActivity;
import us.dangeru.launcher.activities.UserscriptActivity;
import us.dangeru.launcher.utils.P;

/**
 * Created by Niles on 8/21/17.
 */

public class ThreadWatcherFragment extends Fragment implements HiddenSettingsFragment {
    private static final String TAG = ThreadWatcherFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View res = inflater.inflate(R.layout.hidden_settings_list, container, false);
        final AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (ThreadWatcher.threads[i] == null) {
                    GenericAlertDialogFragment.newInstance("That thread hasn't loaded yet, please wait.", getFragmentManager());
                    return;
                }
                WatchableThread thread = ThreadWatcher.threads[i];
                ThreadWatcher.setRead(i);
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
                ((ListView) res.findViewById(R.id.settings_list)).setOnItemClickListener(listener);
                setAdapter();
                Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
                addOptions(toolbar);
            }
        });
        return res;
    }

    public void setAdapter() {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "setAdapter, running on ui thread, ");
                if (getView() == null) return;
                ListView list = getView().findViewById(R.id.settings_list);
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

    public static void addOptions(Toolbar toolbar) {
        toolbar.setTitle("Thread Watcher");
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.thread_watcher_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ThreadWatcher.refreshAll();
                return true;
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThreadWatcher.registerListener(this);
    }

    public static String makeLabel(WatchableThread thread) {
        if (thread.new_replies <= 0) {
            return "No new replies to thread " + thread.post_id + " - \"" + thread.title + "\" (" + thread.number_of_replies + " replies in total)";
        } else {
            return thread.new_replies + " new " + (thread.new_replies == 1 ? "reply" : "replies") + " to thread " + thread.post_id + " - \"" + thread.title + "\"";
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    private class ThreadWatcherAdapter extends ArrayAdapter<WatchableThread> {
        public ThreadWatcherAdapter(Context context, WatchableThread[] list) {
            super(context, 0, list);
        }
        @NonNull
        @Override public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            WatchableThread thread = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.thread_watcher_list_item, parent, false);
            }
            TextView title = convertView.findViewById(R.id.thread_title);
            TextView subtitle = convertView.findViewById(R.id.new_replies);
            TextView subtitleContinuation = convertView.findViewById(R.id.subtitle_continuation);
            if (thread != null) {
                title.setText(thread.title);
                if (thread.new_replies == 0) {
                    subtitle.setText("No");
                    subtitle.setTextColor(Color.parseColor("#AAAAAA"));
                } else {
                    subtitle.setText(String.valueOf(thread.new_replies));
                    subtitle.setTextColor(Color.RED);
                }
                if (thread.new_replies == 1) {
                    subtitleContinuation.setText(" new reply");
                } else {
                    subtitleContinuation.setText(" new replies");
                }
            } else {
                title.setText("Thread loading...");
                subtitle.setText("Loading");
                subtitleContinuation.setText(" new replies");
            }
            return convertView;
        }

    }
}
