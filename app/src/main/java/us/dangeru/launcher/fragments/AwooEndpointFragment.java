package us.dangeru.launcher.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import us.dangeru.launcher.R;
import us.dangeru.launcher.activities.HiddenSettingsActivity;
import us.dangeru.launcher.utils.P;

/**
 * Class that lets you pick the awoo endpoint fragment to use
 */
public class AwooEndpointFragment extends Fragment implements HiddenSettingsFragment {
    @Override
    public HiddenSettingsActivity.FragmentType getType() {
        return HiddenSettingsActivity.FragmentType.AWOO_ENDPOINT;
    }
    private ArrayList<CompoundButton> buttons;

    /**
     * Sets up each of the radio buttons, checking the currently selected one, and sets up the
     * text changed listener on the other text box
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return the inflated view
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View res = inflater.inflate(R.layout.awoo_endpoint_picker, container, false);
        buttons = new ArrayList<>();
        res.post(new Runnable() {
            @Override
            public void run() {
                setupButton(res, R.id.local_ip);
                setupButton(res, R.id.local_ip_2);
                setupButton(res, R.id.lain_city);
                setupButton(res, R.id.lolis_download);
                setupButton(res, R.id.awoo_other);
                res.findViewById(R.id.other_box).setVisibility(View.GONE);
                boolean found = false;
                for (CompoundButton b : buttons) {
                    b.setChecked(false);
                    if (b.getText().toString().equals(P.get("awoo_endpoint"))) {
                        found = true;
                        b.setChecked(true);
                    }
                }
                ((TextView) res.findViewById(R.id.other_box)).addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
                    @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
                    @Override public void afterTextChanged(Editable editable) {
                        P.set("awoo_endpoint", editable.toString());
                    }
                });
                if (!found) {
                    ((Checkable) res.findViewById(R.id.awoo_other)).setChecked(true);
                }
                Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
                addOptions(toolbar);
            }
        });
        return res;
    }

    /**
     * Helper method to set the on checked changed listener and add the button to the buttons list
     * @param res the view to search in
     * @param id the id of the button in the view
     */
    private void setupButton(View res, int id) {
        ((CompoundButton) res.findViewById(id)).setOnCheckedChangeListener(listener);
        buttons.add((CompoundButton) res.findViewById(id));
    }

    /**
     * On checked changed listener to show or hide the other box if necessary and set the awoo_endpoint property
     */
    final CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton button, boolean b) {
            // if we're being *UNchecked*, we don't care about it.
            if (!b) return;
            // uncheck all the other buttons
            for (CompoundButton other : buttons) {
                if (!other.equals(button)) other.setChecked(false);
            }
            // awoo_endpoint will be set to the text of the button
            String text = String.valueOf(button.getText());
            if (getView() == null) return;
            // if it's the other button, show the other box and populate it with the awoo endpoint.
            // no need to save the awoo_endpoint property, that's done in the ontextchanged listener
            if ("Other".equals(text)) {
                getView().findViewById(R.id.other_box).setVisibility(View.VISIBLE);
                ((TextView) getView().findViewById(R.id.other_box)).setText(P.get("awoo_endpoint"), TextView.BufferType.EDITABLE);
            } else {
                // if it's not the other button, hide the other box and update the property
                getView().findViewById(R.id.other_box).setVisibility(View.GONE);
                P.set("awoo_endpoint", text);
            }
        }
    };

    /**
     * adds a close option to the toolbar
     * @param toolbar the toolbar to update
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
