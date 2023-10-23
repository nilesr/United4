package com.angryburg.uapp.fragments;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.CompoundButton;

import com.angryburg.uapp.API.NotificationWorker;
import com.angryburg.uapp.API.Thread;
import com.angryburg.uapp.R;
import com.angryburg.uapp.activities.HiddenSettingsActivity;
import com.angryburg.uapp.utils.P;

import java.util.List;

/**
 * Created by Niles on 3/21/18.
 */

public class NotificationSettingsFragment extends Fragment implements HiddenSettingsFragment {
        @Override
        public HiddenSettingsActivity.FragmentType getType() {
                return HiddenSettingsActivity.FragmentType.NOTIFICATION_SETTINGS;
        }

        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
                final View res = inflater.inflate(R.layout.notification_settings, container, false);
                res.post(new Runnable() {
                        @Override
                        public void run() {
                                ((Checkable) res.findViewById(R.id.notifications_enabled))
                                                .setChecked(P.getBool("notifications"));
                                ((CompoundButton) res.findViewById(R.id.notifications_enabled))
                                                .setOnCheckedChangeListener(enable_disable_listener);
                                ((CompoundButton) res.findViewById(R.id.notifications_disabled))
                                                .setOnCheckedChangeListener(enable_disable_listener);
                                enable_disable_listener.onCheckedChanged(null, false);
                                ((Checkable) res.findViewById(R.id.notify_all))
                                                .setChecked("ALL".equalsIgnoreCase(P.get("which_notifications")));
                                ((Checkable) res.findViewById(R.id.notify_direct)).setChecked(P
                                                .get("which_notifications").isEmpty()
                                                || "DIRECT".equalsIgnoreCase(P.get("which_notifications")));
                                ((Checkable) res.findViewById(R.id.notify_direct_and_created))
                                                .setChecked("DIRECT_AND_CREATED"
                                                                .equalsIgnoreCase(P.get("which_notifications")));
                                ((Checkable) res.findViewById(R.id.hour)).setChecked(
                                                P.get("alarm_interval").isEmpty()
                                                                || "HOUR".equalsIgnoreCase(P.get("alarm_interval")));
                                ((Checkable) res.findViewById(R.id.half_day))
                                                .setChecked("HALF_DAY".equalsIgnoreCase(P.get("alarm_interval")));
                                ((Checkable) res.findViewById(R.id.day))
                                                .setChecked("DAY".equalsIgnoreCase(P.get("alarm_interval")));
                                ((CompoundButton) res.findViewById(R.id.notify_all))
                                                .setOnCheckedChangeListener(notify_which_listener);
                                ((CompoundButton) res.findViewById(R.id.notify_direct))
                                                .setOnCheckedChangeListener(notify_which_listener);
                                ((CompoundButton) res.findViewById(R.id.notify_direct_and_created))
                                                .setOnCheckedChangeListener(notify_which_listener);
                                ((CompoundButton) res.findViewById(R.id.hour))
                                                .setOnCheckedChangeListener(notify_duration_listener);
                                ((CompoundButton) res.findViewById(R.id.half_day))
                                                .setOnCheckedChangeListener(notify_duration_listener);
                                ((CompoundButton) res.findViewById(R.id.day))
                                                .setOnCheckedChangeListener(notify_duration_listener);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        GenericAlertDialogFragment.newInstance(
                                                        "Your version of android is too new, and notifications are not available. ",
                                                        getParentFragmentManager());
                                        // force the check changed listener to run to disable the rest of the UI
                                        ((Checkable) res.findViewById(R.id.notifications_disabled)).setChecked(true);
                                        enable_disable_listener.onCheckedChanged(null, false);
                                        res.findViewById(R.id.notifications_enabled).setEnabled(false);
                                        res.findViewById(R.id.notifications_disabled).setEnabled(false);
                                }
                        }
                });
                return res;
        }

        private CompoundButton.OnCheckedChangeListener notify_which_listener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (getView() == null)
                                return;
                        if (((Checkable) getView().findViewById(R.id.notify_all)).isChecked())
                                P.set("which_notifications", "ALL");
                        else if (((Checkable) getView().findViewById(R.id.notify_direct)).isChecked())
                                P.set("which_notifications", "DIRECT");
                        else if (((Checkable) getView().findViewById(R.id.notify_direct_and_created)).isChecked())
                                P.set("which_notifications", "DIRECT_AND_CREATED");
                        if (!b)
                                return;
                        GenericProgressDialogFragment.newInstance(
                                        "Marking existing replies as notified, please wait...",
                                        getParentFragmentManager());
                        final FragmentManager mgr = getParentFragmentManager();
                        final Context act = getActivity();
                        new java.lang.Thread(new Runnable() {
                                @Override
                                public void run() {
                                        List<Thread> threads = NotificationWorker.pullNotifications(act);
                                        for (Thread t : threads) {
                                                NotificationWorker.setNotified(t.post_id);
                                        }
                                        // without this, sometimes we try to dismiss the dialog
                                        // before it's actually created, and it stays up forever
                                        try {
                                                java.lang.Thread.sleep(100);
                                        } catch (Exception ignored) {
                                        }
                                        GenericProgressDialogFragment.dismiss(mgr);
                                }
                        }).start();
                }
        };
        private CompoundButton.OnCheckedChangeListener notify_duration_listener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (!b)
                                return;
                        if (getView() == null)
                                return;
                        if (((Checkable) getView().findViewById(R.id.hour)).isChecked())
                                P.set("alarm_interval", "HOUR");
                        else if (((Checkable) getView().findViewById(R.id.half_day)).isChecked())
                                P.set("alarm_interval", "HALF_DAY");
                        else if (((Checkable) getView().findViewById(R.id.day)).isChecked())
                                P.set("alarm_interval", "DAY");
                        NotificationWorker.setAlarm(getActivity());
                }
        };
        private CompoundButton.OnCheckedChangeListener enable_disable_listener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        View res = getView();
                        if (res == null)
                                return;
                        boolean enabled = ((Checkable) res.findViewById(R.id.notifications_enabled)).isChecked();
                        P.set("notifications", enabled ? "true" : "false");
                        res.findViewById(R.id.notify_all).setEnabled(enabled);
                        res.findViewById(R.id.notify_direct).setEnabled(enabled);
                        res.findViewById(R.id.notify_direct_and_created).setEnabled(enabled);
                        res.findViewById(R.id.hour).setEnabled(enabled);
                        res.findViewById(R.id.half_day).setEnabled(enabled);
                        res.findViewById(R.id.day).setEnabled(enabled);
                        if (enabled && b) {
                                notify_which_listener.onCheckedChanged(null, true);
                                notify_duration_listener.onCheckedChanged(null, true);
                        }
                }
        };
}
