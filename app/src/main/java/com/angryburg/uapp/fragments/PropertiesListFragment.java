package com.angryburg.uapp.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.angryburg.uapp.R;
import com.angryburg.uapp.activities.HiddenSettingsActivity;
import com.angryburg.uapp.utils.NotifierService;
import com.angryburg.uapp.utils.P;
import com.angryburg.uapp.utils.PropertiesSingleton;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

/**
 * A list of all the properties that you can edit
 */
public class PropertiesListFragment extends Fragment implements HiddenSettingsFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View res = inflater.inflate(R.layout.hidden_settings_list, container, false);
        res.post(new Runnable() {
            @Override
            public void run() {
                ListView list = res.findViewById(R.id.settings_list);
                final String[] settings = PropertiesSingleton.get().getKeys();
                Arrays.sort(settings, new UserscriptAwareComparator());
                list.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, settings));
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Bundle args = new Bundle();
                        args.putString("key", settings[i]);
                        ((HiddenSettingsActivity) getActivity()).push(HiddenSettingsActivity.FragmentType.PROPERTY_EDITOR, args);
                    }
                });
                Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
                addOptions(toolbar);
            }
        });
        return res;
    }

    @Override
    public HiddenSettingsActivity.FragmentType getType() {
        return HiddenSettingsActivity.FragmentType.PROPERTIES_LIST;
    }

    /**
     * adds a close button to the menu bar
     * @param toolbar the toolbar
     */
    public void addOptions(Toolbar toolbar) {
        toolbar.setTitle(R.string.app_name);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.back_item);
        toolbar.inflateMenu(R.menu.add_property);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.add_property) {
                    Bundle args = new Bundle();
                    args.putString("create", "true");
                    ((HiddenSettingsActivity) getActivity()).push(HiddenSettingsActivity.FragmentType.PROPERTY_EDITOR_NEW, args);
                } else {
                    getActivity().finish();
                }
                return true;
            }
        });
    }

    private static class UserscriptAwareComparator implements Comparator<String>, Serializable {
        @Override
        public int compare(String s1, String s2) {
            if (s1.contains(":") && !s2.contains(":")) return 1;
            if (!s1.contains(":") && s2.contains(":")) return -1;
            return s1.compareTo(s2);
        }
    }
}
