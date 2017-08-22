package us.dangeru.launcher.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
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
                list.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, ThreadWatcher.parallelLabels));
                list.invalidate();
                Log.i(TAG, "success, invalidating...");
                if (ThreadWatcher.updated_threads == 0) {
                    //((Toolbar) getView().findViewById(R.id.toolbar)
                } else {

                }
            }
        });
    }

    @Override
    public HiddenSettingsActivity.FragmentType getType() {
        return HiddenSettingsActivity.FragmentType.THREAD_WATCHER;
    }

    public void addOptions(Toolbar toolbar) {
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
