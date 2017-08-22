package us.dangeru.launcher.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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
 * Created by Niles on 8/22/17.
 */

public class AwooEndpointFragment extends Fragment implements HiddenSettingsFragment {
    @Override
    public HiddenSettingsActivity.FragmentType getType() {
        return HiddenSettingsActivity.FragmentType.AWOO_ENDPOINT;
    }
    ArrayList<CompoundButton> buttons;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View res = inflater.inflate(R.layout.awoo_endpoint_picker, container, false);
        buttons = new ArrayList<>();
        res.post(new Runnable() {
            @Override
            public void run() {
                setupButton(res, R.id.local_ip);
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
            }
        });
        return res;
    }
    private void setupButton(View res, int id) {
        ((CompoundButton) res.findViewById(id)).setOnCheckedChangeListener(listener);
        buttons.add((CompoundButton) res.findViewById(id));
    }
    final CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton button, boolean b) {
            if (!b) return;
            for (CompoundButton other : buttons) {
                if (!other.equals(button)) other.setChecked(false);
            }
            String text = String.valueOf(button.getText());
            if (getView() == null) return;
            if ("Other".equals(text)) {
                getView().findViewById(R.id.other_box).setVisibility(View.VISIBLE);
                ((TextView) getView().findViewById(R.id.other_box)).setText(P.get("awoo_endpoint"), TextView.BufferType.EDITABLE);
            } else {
                getView().findViewById(R.id.other_box).setVisibility(View.GONE);
                P.set("awoo_endpoint", text);
            }
        }
    };
}
