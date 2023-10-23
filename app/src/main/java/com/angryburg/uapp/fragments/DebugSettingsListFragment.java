package com.angryburg.uapp.fragments;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.angryburg.uapp.API.ThreadWatcher;
import com.angryburg.uapp.R;
import com.angryburg.uapp.activities.HiddenSettingsActivity;
import com.angryburg.uapp.utils.NotifierService;
import com.angryburg.uapp.utils.P;
import com.angryburg.uapp.utils.PropertiesSingleton;

/**
 * Created by Niles on 9/1/17.
 */

public class DebugSettingsListFragment extends Fragment implements HiddenSettingsFragment {
    @Override
    public HiddenSettingsActivity.FragmentType getType() {
        return HiddenSettingsActivity.FragmentType.DEBUG_SETTINGS_LIST;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View res = inflater.inflate(R.layout.hidden_settings_list, container, false);
        res.post(new Runnable() {
            @Override
            public void run() {
                ListView list = res.findViewById(R.id.settings_list);
                final String[] settings = new String[] {
                        "Reset all preferences",
                        "Toggle debug button, currently " + P.getReadable("debug"),
                        "Toggle userscript, currently " + P.getReadable("userscript"),
                        "Change Awoo Endpoint (currently " + P.get("awoo_endpoint") + ")",
                        "Override UnitedWebFragmentWebViewAuthorizer, currently "
                                + P.getReadable("override_authorizer"),
                        "Edit Properties",
                };
                list.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, settings));
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        switch (i) {
                            case 0:
                                PropertiesSingleton.resetAllAndExit(getParentFragmentManager());
                                break;
                            case 1:
                                P.toggle("debug");
                                NotifierService.notify(NotifierService.NotificationType.RELOAD_INDEX);
                                run();
                                break;
                            case 2:
                                P.toggle("userscript");
                                if (P.getBool("userscript"))
                                    ThreadWatcher.refreshAll();
                                run();
                                break;
                            case 3:
                                ((HiddenSettingsActivity) getActivity())
                                        .push(HiddenSettingsActivity.FragmentType.AWOO_ENDPOINT);
                                break;
                            case 4:
                                P.toggle("override_authorizer");
                                run();
                                break;
                            case 5:
                                ((HiddenSettingsActivity) getActivity())
                                        .push(HiddenSettingsActivity.FragmentType.PROPERTIES_LIST);
                                break;
                            default:
                                GenericAlertDialogFragment.newInstance("Should never happen",
                                        getParentFragmentManager());
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

    /**
     * adds a close button to the menu bar
     * 
     * @param toolbar the toolbar
     */
    public void addOptions(Toolbar toolbar) {
        toolbar.setTitle(R.string.app_name);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.debug_menu);
        toolbar.inflateMenu(R.menu.back_item);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // noinspection SwitchStatementWithoutDefaultBranch
                int itemId = item.getItemId();
                if (itemId == R.id.back_item) {
                    getActivity().finish();
                    return true;
                } else if (itemId == R.id.reload_all) {
                    NotifierService.notify(NotifierService.NotificationType.RELOAD_ALL);
                    return true;
                } else if (itemId == R.id.reload_index) {
                    NotifierService.notify(NotifierService.NotificationType.RELOAD_INDEX);
                    return true;
                } else if (itemId == R.id.reload_music) {
                    NotifierService.notify(NotifierService.NotificationType.RELOAD_MUSIC);
                    return true;
                } else if (itemId == R.id.invalidate_toolbar) {
                    ((HiddenSettingsActivity) getActivity()).invalidateToolbarColor();
                    NotifierService.notify(NotifierService.NotificationType.INVALIDATE_TOOLBAR);
                    return true;
                }
                return false;
            }
        });
    }
}
