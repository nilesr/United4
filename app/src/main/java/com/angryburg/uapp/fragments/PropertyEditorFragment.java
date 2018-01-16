package com.angryburg.uapp.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.angryburg.uapp.R;
import com.angryburg.uapp.activities.HiddenSettingsActivity;
import com.angryburg.uapp.utils.P;

/**
 * Created by Niles on 1/15/18.
 */

public class PropertyEditorFragment extends Fragment implements HiddenSettingsFragment {
    private String key;
    private String value;

    @Override
    public HiddenSettingsActivity.FragmentType getType() {
        return HiddenSettingsActivity.FragmentType.PROPERTY_EDITOR;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        key = getArguments().getString("key");
        if (savedInstanceState == null) {
            value = P.get(key);
        } else {
            value = savedInstanceState.getString("value");
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View res = inflater.inflate(R.layout.property_editor, container, false);
        final View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrapeValue();
                P.set(key, value);
            }
        };
        res.post(new Runnable() {
            @Override
            public void run() {
                ((TextView) res.findViewById(R.id.key_text)).setText(key);
                ((TextView) res.findViewById(R.id.value_text)).setText(value);
                res.findViewById(R.id.save).setOnClickListener(listener);
                res.findViewById(R.id.save_back).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onClick(null);
                        ((HiddenSettingsActivity) getActivity()).swapScreens(HiddenSettingsActivity.FragmentType.PROPERTIES_LIST);
                    }
                });
            }
        });
        return res;
    }

    private void scrapeValue() {
        if (getView() == null) return;
        value = ((TextView) getView().findViewById(R.id.value_text)).getText().toString();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("value", value);
    }
}
