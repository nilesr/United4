package us.dangeru.la_u_ncher413.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import us.dangeru.la_u_ncher413.R;
import us.dangeru.la_u_ncher413.activities.HiddenSettingsActivity;
import us.dangeru.la_u_ncher413.utils.P;

/**
 * Created by Niles on 9/1/17.
 */

public class ColorPickerFragment extends android.app.Fragment implements HiddenSettingsFragment {
    @Override
    public HiddenSettingsActivity.FragmentType getType() {
        return HiddenSettingsActivity.FragmentType.COLOR_PICKER;
    }
    private static class ThemeColor {
        String name;
        int color;
        ThemeColor(String name, int color) {
            this.name = name;
            this.color = color;
        }
    }
    private static ThemeColor[] colors = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View res = inflater.inflate(R.layout.hidden_settings_list, container, false);
        res.post(new Runnable() {
            @Override
            public void run() {
                if (colors == null) {
                    colors = makeColors();
                }
                ListView list = res.findViewById(R.id.settings_list);
                list.setAdapter(new ArrayAdapter<ThemeColor>(getActivity(), 0, colors) {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        if (convertView == null) {
                            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
                        }
                        TextView textView = (TextView) convertView;
                        ThemeColor item = getItem(position);
                        textView.setBackgroundColor(item.color);
                        textView.setTextColor(Color.WHITE);
                        textView.setText(item.name);
                        return textView;
                    }
                });
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        P.set("toolbar_color", String.valueOf(colors[i].color));
                        ((HiddenSettingsActivity) getActivity()).invalidateToolbarColor();
                    }
                });
                Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
                addOptions(toolbar);
            }


        });
        return res;
    }

    private static ThemeColor[] makeColors() {
        ArrayList<ThemeColor> l = new ArrayList<>();
        l.add(new ThemeColor("Firebrick", 0xFFB71C1C));
        l.add(new ThemeColor("Royal Health", 0xFF880E4F));
        l.add(new ThemeColor("Indigo", 0xFF4A148C));
        l.add(new ThemeColor("Navy", 0xFF311B92));
        l.add(new ThemeColor("Midnight Blue", 0xFF1A237E));
        l.add(new ThemeColor("Cornflower", 0xFF0D47A1));
        l.add(new ThemeColor("Havelock Blue", 0xFF01579B));
        l.add(new ThemeColor("Atoll", 0xFF006064));
        l.add(new ThemeColor("Aquamarine", 0xFF004D40));
        l.add(new ThemeColor("San Felix", 0xFF1B5E20));
        l.add(new ThemeColor("Japanese Laurel", 0xFF33691E));
        l.add(new ThemeColor("Lemon Ginger", 0xFF827717));
        l.add(new ThemeColor("Yellow", 0xFFF57F17));
        l.add(new ThemeColor("Dark Orange", 0xFFFF6F00));
        l.add(new ThemeColor("Orange Red", 0xFFE65100));
        l.add(new ThemeColor("Tomato", 0xFFBF360C));
        l.add(new ThemeColor("Bean", 0xFF3E2723));
        l.add(new ThemeColor("Nero", 0xFF212121));
        l.add(new ThemeColor("Oxford Blue", 0xFF263238));
        l.add(new ThemeColor("Topaz", 0xFF837D87));
        l.add(new ThemeColor("Black", 0xFF000000));
        return l.toArray(new ThemeColor[l.size()]);
    }

    /**
     * adds a close button to the menu bar
     *
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
