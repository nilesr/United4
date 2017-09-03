package us.dangeru.la_u_ncher413.fragments;

import us.dangeru.la_u_ncher413.activities.HiddenSettingsActivity;

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
