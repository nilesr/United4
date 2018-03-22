package com.angryburg.uapp.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.CompoundButton;

import com.angryburg.uapp.R;
import com.angryburg.uapp.activities.HiddenSettingsActivity;
import com.angryburg.uapp.utils.P;

/**
 * Created by Niles on 3/21/18.
 */

public class NotificationSettingsFragment extends Fragment implements HiddenSettingsFragment {
    @Override
    public HiddenSettingsActivity.FragmentType getType() {
        return HiddenSettingsActivity.FragmentType.NOTIFICATION_SETTINGS;
    }
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View res = inflater.inflate(R.layout.hidden_settings_list, container, false);
        res.post(new Runnable() {
            @Override
            public void run() {
                ((Checkable) res.findViewById(R.id.notifications_enabled)).setChecked(P.getBool("notifications"));
                ((Checkable) res.findViewById(R.id.notifications_disabled)).setChecked(!P.getBool("notifications"));
                ((CompoundButton) res.findViewById(R.id.notifications_enabled)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        P.set("notifications", ((Checkable) res.findViewById(R.id.notifications_enabled)).isChecked() ? "true" : "false");
                    }
                });
                ((Checkable) res.findViewById(R.id.notify_all)).setChecked("ALL".equalsIgnoreCase(P.get("which_notifications")));
                ((Checkable) res.findViewById(R.id.notify_direct)).setChecked(P.get("which_notifications").isEmpty() || "DIRECT".equalsIgnoreCase(P.get("which_notifications")));
                ((Checkable) res.findViewById(R.id.notify_direct_and_created)).setChecked("DIRECT_AND_CREATED".equalsIgnoreCase(P.get("which_notifications")));
                ((Checkable) res.findViewById(R.id.hour)).setChecked(P.get("alarm_interval").isEmpty() || "HOUR".equalsIgnoreCase(P.get("alarm_interval")));
                ((Checkable) res.findViewById(R.id.half_day)).setChecked("HALF_DAY".equalsIgnoreCase(P.get("alarm_interval")));
                ((Checkable) res.findViewById(R.id.day)).setChecked("DAY".equalsIgnoreCase(P.get("alarm_interval")));
                ((CompoundButton) res.findViewById(R.id.notify_all)).setOnCheckedChangeListener(notify_which_listener);
                ((CompoundButton) res.findViewById(R.id.notify_direct)).setOnCheckedChangeListener(notify_which_listener);
                ((CompoundButton) res.findViewById(R.id.notify_direct_and_created)).setOnCheckedChangeListener(notify_which_listener);
                ((CompoundButton) res.findViewById(R.id.hour)).setOnCheckedChangeListener(notify_duration_listener);
                ((CompoundButton) res.findViewById(R.id.half_day)).setOnCheckedChangeListener(notify_duration_listener);
                ((CompoundButton) res.findViewById(R.id.day)).setOnCheckedChangeListener(notify_duration_listener);
            }
        });
        return res;
    }
    private CompoundButton.OnCheckedChangeListener notify_which_listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (getView() == null) return;
            if (((Checkable) getView().findViewById(R.id.notify_all)).isChecked())
                P.set("which_notifications", "ALL");
            else if (((Checkable) getView().findViewById(R.id.notify_direct)).isChecked())
                P.set("which_notifications", "DIRECT");
            else if (((Checkable) getView().findViewById(R.id.notify_direct_and_created)).isChecked())
                P.set("which_notifications", "DIRECT_AND_CREATED");
        }
    };
    private CompoundButton.OnCheckedChangeListener notify_duration_listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (getView() == null) return;
            if (((Checkable) getView().findViewById(R.id.hour)).isChecked())
                P.set("alarm_interval", "HOUR");
            else if (((Checkable) getView().findViewById(R.id.half_day)).isChecked())
                P.set("alarm_interval", "HALF_DAY");
            else if (((Checkable) getView().findViewById(R.id.day)).isChecked())
                P.set("alarm_interval", "DAY");
        }
    };
}
