package us.dangeru.launcher.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;

import us.dangeru.launcher.API.Thread;
import us.dangeru.launcher.R;
import us.dangeru.launcher.activities.HiddenSettingsActivity;
import us.dangeru.launcher.activities.UserscriptActivity;
import us.dangeru.launcher.utils.P;

/**
 * Created by Niles on 8/21/17.
 */

public class ThreadWatcherFragment extends Fragment implements HiddenSettingsFragment {
    int[] parallelIds;
    String[] parallelLabels;
    String[] parallelBoards;

    private static String[] arrayFromJsonArray(String inp) throws Exception {
        JSONArray arr = new JSONArray(inp);
        String res[] = new String[arr.length()];
        for (int i = 0; i < arr.length(); i++) {
            res[i] = arr.getString(i);
        }
        return res;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View res = inflater.inflate(R.layout.hidden_settings_list, container, false);
        final AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (parallelBoards[i] == null) {
                    GenericAlertDialogFragment.newInstance("That thread hasn't loaded yet, please wait.", getFragmentManager());
                    return;
                }
                int id = parallelIds[i];
                parallelLabels[i] = "todo replace with 'no new replies' message";
                setAdapter();
                String url = P.get("awoo_endpoint") + "/" + parallelBoards[i] + "/thread/" + id;
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
            }
        });
        return res;
    }

    private void setAdapter() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (getView() == null) return;
                ListView list = getView().findViewById(R.id.settings_list);
                list.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, parallelLabels));
            }
        });
    }

    @Override
    public HiddenSettingsActivity.FragmentType getType() {
        return HiddenSettingsActivity.FragmentType.THREAD_WATCHER;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            String[] parallelIdsAsStrings = arrayFromJsonArray(P.get("watched_threads"));
            parallelIds = new int[parallelIdsAsStrings.length];
            for (int i = 0; i < parallelIdsAsStrings.length; i++) {
                parallelIds[i] = Integer.valueOf(parallelIdsAsStrings[i]);
            }
        } catch (Exception ignored) {
            parallelIds = new int[0];
        }
        if (savedInstanceState == null) {
            parallelBoards = new String[parallelIds.length];
            parallelLabels = new String[parallelIds.length];
            for (int i = 0; i < parallelLabels.length; i++) {
                parallelLabels[i] = "Thread " + parallelIds[i] + " Loading...";
                final int finalI = i;
                new java.lang.Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread thread = Thread.getThreadById(parallelIds[finalI]);
                            parallelBoards[finalI] = thread.board;
                            int last_seen_replies = 0;
                            String last_seen_replies_string = P.get(thread.board + ":" + thread.post_id);
                            if (!last_seen_replies_string.isEmpty()) {
                                last_seen_replies = Integer.valueOf(last_seen_replies_string);
                            }
                            int new_replies = thread.number_of_replies - last_seen_replies;
                            String label;
                            if (new_replies <= 0) {
                                label = "No new replies to thread " + thread.post_id + " - \"" + thread.title + "\" (" + thread.number_of_replies + " replies in total)";
                            } else {
                                label = new_replies + " new " + (new_replies == 1 ? "reply" : "replies") + " to thread " + thread.post_id + " - \"" + thread.title + "\"";
                            }
                            parallelLabels[finalI] = label;
                            setAdapter();
                        } catch (Exception e) {
                            GenericAlertDialogFragment.newInstance("Unknown error loading thread " + finalI + " - " + e, getFragmentManager());
                        }
                    }
                }).start();
            }
        } else {
            parallelBoards = savedInstanceState.getStringArray("parallelBoards");
            parallelLabels = savedInstanceState.getStringArray("parallelLabels");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray("parallelBoards", parallelBoards);
        outState.putStringArray("parallelLabels", parallelLabels);
    }
}
