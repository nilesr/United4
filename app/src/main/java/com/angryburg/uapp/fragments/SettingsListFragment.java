package com.angryburg.uapp.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
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
    /**
     * Holds the number of times that the user has toggled startup music. At 5 it enables developer settings
     */
    static int startup_toggle_count = 0;
    /**
     * List of settings and the actions to take when they are clicked
     */
    private final SettingInterface[] settings = new SettingInterface[] {
            new Setting("userscript", null) {
                @Override
                public String getText() {
                    return "Userscript - Currently " + P.getReadable("userscript");
                }
            },
            new Setting("startup_music", new Runnable() {
                @Override
                public void run() {
                    startup_toggle_count++;
                    if (startup_toggle_count >= 5) {
                        P.set("debug", "true");
                        P.set("testing", "true");
                        Toast.makeText(getActivity(), "You are now a developer", Toast.LENGTH_LONG).show();
                        NotifierService.notify(NotifierService.NotificationType.RELOAD_INDEX);
                        getActivity().setResult(1); // 1 for reload, see MainActivity
                        getActivity().finish();
                    }
                }
            }) {
                @Override
                public String getText() {
                    return "Startup music - Currently " + P.getReadable("startup_music");
                }
            },
            new Setting(null, new Runnable() {
                @Override
                public void run() {
                    ((HiddenSettingsActivity) getActivity()).push(HiddenSettingsActivity.FragmentType.NOTIFICATION_SETTINGS);
                }
            }) {
                @Override
                public String getText() {
                    return "Notification Settings";
                }
            },
            new Setting(null, new Runnable() {
                @Override
                public void run() {
                    ((HiddenSettingsActivity) getActivity()).push(HiddenSettingsActivity.FragmentType.JANITOR_LOGIN);
                }
            }) {
                @Override
                public String getText() {
                    return "Janitor Login";
                }
            },
            new Setting(null, new Runnable() {
                @Override
                public void run() {
                    ((HiddenSettingsActivity) getActivity()).push(HiddenSettingsActivity.FragmentType.COLOR_LIST);
                }
            }) {
                @Override
                public String getText() {
                    return "Change Toolbar Color";
                }
            },
            new Setting("force_show_back_btn", new Runnable() {
                @Override
                public void run() {
                    NotifierService.notify(NotifierService.NotificationType.INVALIDATE_TOOLBAR);
                }
            }) {
                @Override
                public String getText() {
                    return "Always show activity back button in toolbar - Currently " + P.getReadable("force_show_back_btn");
                }
            },
            new Setting(null, new Runnable() {
                @Override
                public void run() {
                    String[] values = {"false", "-25", "match", "+25"};
                    String new_value = values[(Arrays.asList(values).indexOf(P.get("window_bar_color")) + 1) % values.length];
                    P.set("window_bar_color", new_value);
                    ((HiddenSettingsActivity) getActivity()).invalidateToolbarColor();
                    NotifierService.notify(NotifierService.NotificationType.INVALIDATE_TOOLBAR);
                }
            }) {
                @Override
                public String getText() {
                    return "Update window bar color to match toolbar (Android 5+) - Currently " + P.get("window_bar_color");
                }
            },
            new Setting("mute_sounds", null) {
                @Override
                public String getText() {
                    return "Mute sound effects (no effect on music) - Currently " + P.getReadable("mute_sounds");
                }
            },
            new Setting("watch_on_reply", null) {
                @Override
                public String getText() {
                    return "Watch thread on reply - Currently " + P.getReadable("watch_on_reply");
                }
            },
            new Setting("infscroll", null) {
                @Override
                public String getText() {
                    return "Infinite scrolling (requires userscript) - Currently " + P.getReadable("infscroll");
                }
            },
            new Setting("invert", null) {
                @Override
                public String getText() {
                    return "Invert Colors (requires userscript) - Currently " + P.getReadable("invert");
                }
            },
            new Setting("bar", null) {
                @Override
                public String getText() {
                    return "Draw bar at beginning of new replies (requires userscript) - Currently " + P.getReadable("bar");
                }
            },
            new Setting("scroll_to_bar", null) {
                @Override
                public String getText() {
                    return "Jump to bar on load (requires userscript) - Currently " + P.getReadable("scroll_to_bar");
                }
            },
            new Setting("hide_version", null) {
                @Override
                public String getText() {
                    return "Hide version text on homescreen - Currently " + P.getReadable("hide_version");
                }
            },
            new Setting("show_yous", null) {
                @Override
                public String getText() {
                    return "Show (You)s and (OP)s (requires userscript) - Currently " + P.getReadable("show_yous");
                }
            },
            new Setting("display_my_id", null) {
                @Override
                public String getText() {
                    return "Show me my ID (requires userscript) - Currently " + P.getReadable("display_my_id");
                }
            },
            new Setting("dark_mode", new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "Change will take full effect after restarting the app", Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                public String getText() {
                    return "Dark mode - Currently " + P.getReadable("dark_mode");
                }
            },
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View res = inflater.inflate(R.layout.hidden_settings_list, container, false);
        res.post(new Runnable() {
            @Override
            public void run() {
                final ListView list = res.findViewById(R.id.settings_list);
                list.setAdapter(new ArrayAdapter<SettingInterface>(getActivity(), 0, settings) {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        if (convertView == null) {
                            convertView = LayoutInflater.from(getContext()).inflate(R.layout.settings_list_item, parent, false);
                        }
                        final SettingInterface setting = settings[position];
                        ViewGroup view = (ViewGroup) convertView;
                        view.removeAllViews();
                        if (setting.toggle() == null) {
                            TextView tv = new TextView(getActivity());
                            tv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 0.85f));
                            tv.setText(setting.getText());
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    setting.click();
                                    list.invalidateViews();
                                }
                            });
                            view.addView(tv);
                            ImageView iv = new ImageView(getContext());
                            iv.setImageResource(android.R.drawable.progress_horizontal);
                            iv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 0.15f));
                            view.addView(iv);
                        } else {
                            CompoundButton sw = new Switch(getActivity());
                            view.addView(sw);
                            sw.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                            sw.setText(setting.getText());
                            sw.setChecked(P.getBool(setting.toggle()));
                            //sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                //public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                public void onClick(View view) {
                                    setting.click();
                                    list.invalidateViews();
                                }
                            });
                        }
                        return convertView;
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

    /**
     * An interface that each setting needs to implement, allows the menu handling system to
     * get the text to display and how it should be displayed, as well as trigger an action
     * when the user clicks on the item
     */
    private interface SettingInterface {
        /**
         * Gets the text to display to the user
         * @return the text to display to the user
         */
        String getText();

        /**
         * Called when the user clicks on the option
         */
        void click();

        /**
         * For non-submenus, returns a property that can be read as a boolean to see if the toggle
         * should be shown in the enabled state or the disabled state, or null if there is no toggle
         * @return a property name to check, or null
         */
        String toggle();
    }

    /**
     * Helper implementation of some of SettingsInterface, with some common logic abstracted out
     */
    private abstract static class Setting implements SettingInterface {
        /**
         * The property to be toggled on click, or null
         */
        String toggle;
        /**
         * An action to perform on click, or null
         */
        Runnable click;

        /**
         * Simple constructor that stores its arguments
         * @param toggle The property to be toggled on click, or null
         * @param click An action to perform on click, or null
         */
        Setting(String toggle, Runnable click) {
            this.toggle = toggle;
            this.click = click;
        }

        /**
         * Toggles the given toggle if not null, runs the given click action if not null
         */
        public void click() {
            if (toggle != null) {
                P.toggle(toggle);
            }
            if (click != null) {
                click.run();
            }
        }

        public String toggle() { return toggle; }
    }
}
