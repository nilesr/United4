package com.angryburg.uapp.fragments;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.angryburg.uapp.R;
import com.angryburg.uapp.activities.HiddenSettingsActivity;
import com.angryburg.uapp.utils.P;

/**
 * Created by Niles on 1/15/18.
 */

public class NewPropertyFragment extends Fragment implements HiddenSettingsFragment {
    private String key = "";

    @Override
    public HiddenSettingsActivity.FragmentType getType() {
        return HiddenSettingsActivity.FragmentType.PROPERTY_EDITOR_NEW;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            key = savedInstanceState.getString("key");
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
                scrapeKey();
                Bundle args = new Bundle();
                args.putString("key", key);
                ((HiddenSettingsActivity) getActivity()).replace(HiddenSettingsActivity.FragmentType.PROPERTY_EDITOR,
                        args);
            }
        };
        res.post(new Runnable() {
            @Override
            public void run() {
                ((TextView) res.findViewById(R.id.textView3)).setText("Create Property");
                ((TextView) res.findViewById(R.id.key_text)).setText(key);
                ((TextView) res.findViewById(R.id.key_text)).setInputType(InputType.TYPE_CLASS_TEXT);
                ((TextView) res.findViewById(R.id.value_text)).setInputType(InputType.TYPE_NULL);
                res.findViewById(R.id.value_text).setVisibility(View.GONE);
                res.findViewById(R.id.textView2).setVisibility(View.GONE);
                res.findViewById(R.id.save).setOnClickListener(listener);
                ((TextView) res.findViewById(R.id.save)).setText("Create");
                res.findViewById(R.id.save_back).setVisibility(View.INVISIBLE);
            }
        });
        return res;
    }

    private void scrapeKey() {
        if (getView() == null)
            return;
        key = ((TextView) getView().findViewById(R.id.key_text)).getText().toString();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("key", key);
    }
}
