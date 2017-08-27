package us.dangeru.la_u_ncher.fragments;

import us.dangeru.la_u_ncher.activities.HiddenSettingsActivity;

/**
 * Represents a fragment that can be embedded in a HiddenSettingsActivity
 */
public interface HiddenSettingsFragment {
    /**
     * Returns which fragment it is
     * @return the current fragment's type
     */
    HiddenSettingsActivity.FragmentType getType();
}
