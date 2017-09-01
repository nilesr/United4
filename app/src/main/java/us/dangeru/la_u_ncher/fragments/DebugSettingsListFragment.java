package us.dangeru.la_u_ncher.fragments;

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

import us.dangeru.la_u_ncher.API.ThreadWatcher;
import us.dangeru.la_u_ncher.R;
import us.dangeru.la_u_ncher.activities.HiddenSettingsActivity;
import us.dangeru.la_u_ncher.utils.P;
import us.dangeru.la_u_ncher.utils.PropertiesSingleton;

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
                boolean debug = P.getBool("debug");
                boolean userscript = P.getBool("userscript");
                final String[] settings = new String[] { "Reset all preferences", "Toggle debug button, currently " + debug, "Toggle userscript, currently " + userscript, "Change Awoo Endpoint (currently " + P.get("awoo_endpoint") + ")", "Override UnitedWebFragmentWebViewAuthorizer, currently " + P.getBool("override_authorizer") };
                list.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, settings));
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        switch (i) {
                            case 0:
                                PropertiesSingleton.resetAllAndExit(getFragmentManager());
                                break;
                            case 1:
                                P.toggle("debug");
                                run();
                                break;
                            case 2:
                                P.toggle("userscript");
                                if (P.getBool("userscript")) ThreadWatcher.refreshAll();
                                run();
                                break;
                            case 3:
                                ((HiddenSettingsActivity) getActivity()).swapScreens(HiddenSettingsActivity.FragmentType.AWOO_ENDPOINT);
                                break;
                            case 4:
                                P.toggle("override_authorizer");
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
    /**
     * adds a close button to the menu bar
     * @param toolbar the toolbar
     */
    public void addOptions(Toolbar toolbar) {
        toolbar.setTitle(R.string.app_name);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.hidden_settings_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                getActivity().finish();
                return true;
            }
        });
    }
}
