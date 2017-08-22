package us.dangeru.launcher.fragments;

import us.dangeru.launcher.activities.HiddenSettingsActivity;

/**
 * Created by Niles on 8/22/17.
 */

public class AwooEndpointFragment implements HiddenSettingsFragment {
    @Override
    public HiddenSettingsActivity.FragmentType getType() {
        return HiddenSettingsActivity.FragmentType.AWOO_ENDPOINT
    }
}
