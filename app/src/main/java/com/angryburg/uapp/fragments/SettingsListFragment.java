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

import java.util.Arrays;

/**
 * A list of all the debug settings that you can edit
 */

public class SettingsListFragment extends Fragment implements HiddenSettingsFragment {
    static int startup_toggle_count = 0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View res = inflater.inflate(R.layout.hidden_settings_list, container, false);
        res.post(new Runnable() {
            @Override
            public void run() {
                ListView list = res.findViewById(R.id.settings_list);
                final String[] settings = new String[] {
                        "Userscript - Currently " + P.getReadable("userscript"),
                        "Startup music - Currently " + P.getReadable("startup_music"),
                        "Janitor Login",
                        "Change Toolbar Color",
                        "Always show activity back button in toolbar - Currently " + P.getReadable("force_show_back_btn"),
                        "Update window bar color to match toolbar (Android 5+) - Currently " + P.get("window_bar_color"),
                        "Mute sound effects (no effect on music) - Currently " + P.getReadable("mute_sounds"),
                };
                list.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, settings));
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        switch (i) {
                            case 0:
                                P.toggle("userscript");
                                run();
                                break;
                            case 1:
                                P.toggle("startup_music");
                                startup_toggle_count++;
                                if (startup_toggle_count >= 5) {
                                    P.set("debug", "true");
                                    P.set("testing", "true");
                                    Toast.makeText(getActivity(), "You are now a developer", Toast.LENGTH_LONG).show();
                                    NotifierService.notify(NotifierService.NotificationType.RELOAD_INDEX);
                                    getActivity().setResult(1); // 1 for reload, see MainActivity
                                    getActivity().finish();
                                } else {
                                    run();
                                }
                                break;
                            case 2:
                                ((HiddenSettingsActivity) getActivity()).swapScreens(HiddenSettingsActivity.FragmentType.JANITOR_LOGIN);
                                break;
                            case 3:
                                ((HiddenSettingsActivity) getActivity()).swapScreens(HiddenSettingsActivity.FragmentType.COLOR_PICKER);
                                break;
                            case 4:
                                P.toggle("force_show_back_btn");
                                NotifierService.notify(NotifierService.NotificationType.INVALIDATE_TOOLBAR);
                                run();
                                break;
                            case 5:
                                String[] values = {"false", "-25", "match", "+25"};
                                String new_value = values[(Arrays.asList(values).indexOf(P.get("window_bar_color")) + 1) % values.length];
                                P.set("window_bar_color", new_value);
                                ((HiddenSettingsActivity) getActivity()).invalidateToolbarColor();
                                NotifierService.notify(NotifierService.NotificationType.INVALIDATE_TOOLBAR);
                                run();
                                break;
                            case 6:
                                P.toggle("mute_sounds");
                                run();
                                break;
                            default:
                                GenericAlertDialogFragment.newInstance("Should never happen", getFragmentManager());
                                break;
                        }
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
        return HiddenSettingsActivity.FragmentType.SETTINGS_LIST;
    }

    /**
     * adds a close button to the menu bar
     * @param toolbar the toolbar
     */
    public void addOptions(Toolbar toolbar) {
        toolbar.setTitle(R.string.app_name);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.back_item);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                getActivity().finish();
                return true;
            }
        });
    }
}
