package com.angryburg.uapp.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.angryburg.uapp.R;
import com.angryburg.uapp.activities.HiddenSettingsActivity;
import com.angryburg.uapp.activities.UserscriptActivity;
import com.angryburg.uapp.utils.P;
import com.angryburg.uapp.utils.PropertiesSingleton;

import java.util.ArrayList;

/**
 * Fragment that can be used to view hidden threads and unhide them
 */
public class HiddenThreadListFragment extends Fragment implements HiddenSettingsFragment {
    /**
     * List of keys of threads that are hidden
     */
    private ArrayList<String> hidden_list = new ArrayList<>();

    /**
     * Constructor that populates `hidden_list`
     */
    public HiddenThreadListFragment() {
        super();
        for (String key : PropertiesSingleton.getKeys()) {
            //noinspection EqualsReplaceableByObjectsCall
            if (key.contains(":") && "hide".equals(P.get(key))) {
                hidden_list.add(key);
            }
        }
    }
    @Override
    public HiddenSettingsActivity.FragmentType getType() {
        return HiddenSettingsActivity.FragmentType.HIDDEN_LIST;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View res = inflater.inflate(R.layout.thread_watcher_list, container, false);

        final AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String key = hidden_list.get(i);
                @SuppressWarnings("DynamicRegexReplaceableByCompiledPattern")
                String url = P.get("awoo_endpoint") + "/" + key.replace(":", "/thread/");
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
                Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
                setAdapter();
                addOptions(toolbar);
            }
        });
        return res;
    }

    /**
     * Invalidates the list view, called when the list view changes
     * Also handles showing the "You have no hidden threads" message
     */
    public void setAdapter() {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (getView() == null) return;
                ListView list = getView().findViewById(R.id.thread_list);
                TextView message = getView().findViewById(R.id.no_threads_message);
                message.setText("You have no hidden threads");
                if (hidden_list.isEmpty()) {
                    message.setVisibility(View.VISIBLE);
                    list.setVisibility(View.GONE);
                } else {
                    message.setVisibility(View.GONE);
                    list.setVisibility(View.VISIBLE);
                }
                list.setAdapter(new HiddenThreadListAdapter(getActivity(), hidden_list.toArray(new String[hidden_list.size()])));
                list.invalidate();
            }
        });
    }

    /**
     * Adds the back button to the toolbar
     * @param toolbar the toolbar to add the item to
     */
    public void addOptions(Toolbar toolbar) {
        toolbar.setTitle("Thread Watcher");
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.back_item);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                HiddenThreadListFragment.this.getActivity().finish();
                return true;
            }
        });
    }

    /**
     * The array adapter for the list view, uses thread_watcher_list_item
     * If the thread is loading, it shows a spinner and hides everything else
     * Otherwise it hides the spinner and shows the three text boxes
     * It also sets the click listener for the button and the text in the boxes
     */
    private class HiddenThreadListAdapter extends ArrayAdapter<String> {
        HiddenThreadListAdapter(Context context, String[] list) {
            super(context, 0, list);
        }
        @SuppressLint("SetTextI18n")
        @NonNull
        @Override public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
            final String thread = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.thread_watcher_list_item, parent, false);
            }
            TextView title = convertView.findViewById(R.id.thread_title);
            title.setText(thread);
            convertView.findViewById(R.id.rel_layout_inner).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.unwatch_button).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.spinner).setVisibility(View.GONE);
            convertView.findViewById(R.id.new_replies).setVisibility(View.GONE);
            convertView.findViewById(R.id.subtitle_continuation).setVisibility(View.GONE);
            ((TextView) convertView.findViewById(R.id.unwatch_button)).setText("Unhide");
            convertView.findViewById(R.id.unwatch_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    P.set(thread, "0");
                    hidden_list.remove(thread);
                    setAdapter();
                }
            });
            return convertView;
        }

    }
}
