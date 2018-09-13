package com.angryburg.uapp.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.angryburg.uapp.R;
import com.angryburg.uapp.activities.HiddenSettingsActivity;
import com.angryburg.uapp.utils.NotifierService;
import com.angryburg.uapp.utils.P;

/**
 * TODO
 */
public class ColorPickerFragment extends Fragment implements HiddenSettingsFragment {
    @Override
    public HiddenSettingsActivity.FragmentType getType() {
        return HiddenSettingsActivity.FragmentType.COLOR_PICKER;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View res = inflater.inflate(R.layout.color_picker, container, false);
        final int color = P.getColor("toolbar_color");
        final int b = color & 0xFF;
        final int g = color >> 8 & 0xFF;
        final int r = color >> 16 & 0xFF;
        res.post(new Runnable() {
            @Override
            public void run() {
                ((ProgressBar) res.findViewById(R.id.red_bar)).setMax(255);
                ((ProgressBar) res.findViewById(R.id.green_bar)).setMax(255);
                ((ProgressBar) res.findViewById(R.id.blue_bar)).setMax(255);
                ((ProgressBar) res.findViewById(R.id.red_bar)).setProgress(r);
                ((ProgressBar) res.findViewById(R.id.green_bar)).setProgress(g);
                ((ProgressBar) res.findViewById(R.id.blue_bar)).setProgress(b);
                ((TextView) res.findViewById(R.id.red_text)).setText(String.valueOf(r));
                ((TextView) res.findViewById(R.id.green_text)).setText(String.valueOf(g));
                ((TextView) res.findViewById(R.id.blue_text)).setText(String.valueOf(b));
                ((SeekBar) res.findViewById(R.id.red_bar)).setOnSeekBarChangeListener(new SeekListener(res.findViewById(R.id.red_text)));
                ((SeekBar) res.findViewById(R.id.green_bar)).setOnSeekBarChangeListener(new SeekListener(res.findViewById(R.id.green_text)));
                ((SeekBar) res.findViewById(R.id.blue_bar)).setOnSeekBarChangeListener(new SeekListener(res.findViewById(R.id.blue_text)));
                ((TextView) res.findViewById(R.id.red_text)).setOnEditorActionListener(new TextListener(res.findViewById(R.id.red_bar)));
                ((TextView) res.findViewById(R.id.green_text)).setOnEditorActionListener(new TextListener(res.findViewById(R.id.green_bar)));
                ((TextView) res.findViewById(R.id.blue_text)).setOnEditorActionListener(new TextListener(res.findViewById(R.id.blue_bar)));
                Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
                addOptions(toolbar);
            }
        });
        return res;
    }

    /**
     * adds a close button to the menu bar
     *
     * @param toolbar the toolbar
     */
    public void addOptions(Toolbar toolbar) {
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

    private void setColor() {
        if (getView() == null) return;
        try {
            int r = Integer.valueOf(((TextView) getView().findViewById(R.id.red_text)).getText().toString());
            int g = Integer.valueOf(((TextView) getView().findViewById(R.id.green_text)).getText().toString());
            int b = Integer.valueOf(((TextView) getView().findViewById(R.id.blue_text)).getText().toString());
            int color = 0xFF000000 + (r << 16) + (g << 8) + b;
            P.set("toolbar_color", String.valueOf(color));
            ((HiddenSettingsActivity) getActivity()).invalidateToolbarColor();
            NotifierService.notify(NotifierService.NotificationType.INVALIDATE_TOOLBAR);
        } catch (Exception ignored) {
            // drop
        }
    }

    private class SeekListener implements SeekBar.OnSeekBarChangeListener {
        TextView text;
        public SeekListener(View text) {
            this.text = (TextView) text;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            text.setText(String.valueOf(progress));
            setColor();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { }
    }

    private class TextListener implements TextView.OnEditorActionListener {
        SeekBar bar;
        public TextListener(View bar) {
            this.bar = (SeekBar) bar;
        }

        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            int x = -1;
            try {
                x = Integer.valueOf(textView.getText().toString());
            } catch (Exception ignored) {
                // drop
            }
            if (x >= 0 && x <= 255) {
                bar.setProgress(x);
                setColor();
                return true;
            }
            return false;
        }
    }
}
