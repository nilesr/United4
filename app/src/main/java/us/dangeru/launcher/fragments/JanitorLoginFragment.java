package us.dangeru.launcher.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import us.dangeru.launcher.R;
import us.dangeru.launcher.activities.HiddenSettingsActivity;
import us.dangeru.launcher.utils.P;

/**
 * Created by Niles on 8/20/17.
 */

public class JanitorLoginFragment extends Fragment implements  HiddenSettingsFragment {
    @SuppressWarnings("unused")
    private static final String TAG = JanitorLoginFragment.class.getSimpleName();
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View res = inflater.inflate(R.layout.janitor_login, container, false);
        res.post(new Runnable() {
            @Override
            public void run() {
                EditText username = res.findViewById(R.id.username);
                EditText password = res.findViewById(R.id.password);
                username.setText(P.get("username"));
                password.setText(P.get("password"));
                res.findViewById(R.id.button).setOnClickListener(new LoginButtonClickListener());
                updateLoggedInText();
            }
        });
        return res;
    }
    private class LoginButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(final View view) {
            getPropertiesFromView();
            GenericProgressDialogFragment.newInstance("Logging in...", getFragmentManager());
            view.findViewById(R.id.button).setClickable(false);
            // can't do network on ui thread
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        authenticate();
                    } catch (Exception e) {
                        GenericAlertDialogFragment.newInstance("Unexpected error " + e, getFragmentManager());
                    }
                    view.findViewById(R.id.button).setClickable(true);
                    GenericProgressDialogFragment.dismiss(getFragmentManager());
                    updateLoggedInText();
                }
            }).start();
        }
    }

    private void updateLoggedInText() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (getView() == null) return;
                ((TextView) getView().findViewById(R.id.logged_in)).setText("You are " + (P.getBool("logged_in") ? "currently" : "not") + " logged in" + (P.getBool("logged_in") ? " as " + P.get("username") : ""));
            }
        });
    }

    private void authenticate() throws Exception {
        String username = P.get("username");
        URL uri = new URL(P.get("awoo_endpoint") + "/mod");
        HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        OutputStream os = connection.getOutputStream();
        String data = "username=" + URLEncoder.encode(username, "UTF-8");
        data += "&password=" + URLEncoder.encode(P.get("password"), "UTF-8");
        //data += "&redirect=" + URLEncoder.encode("file:///android_res/success.html", "UTF-8");
        os.write(data.getBytes());
        os.flush(); os.close();
        int responseCode = connection.getResponseCode();
        P.set("logged_in", responseCode == 200 ? "true" : "false");
        if (responseCode == 403) {
            GenericAlertDialogFragment.newInstance("Error - Check your username and password", getFragmentManager());
        } else if (responseCode == 200) {
            GenericAlertDialogFragment.newInstance("Success! You are logged in as " + username, getFragmentManager());
        } else {
            GenericAlertDialogFragment.newInstance("Unexpected response code " + responseCode, getFragmentManager());
        }

    }
    public void getPropertiesFromView() {
        if (getView() == null) return;
        EditText username = getView().findViewById(R.id.username);
        EditText password = getView().findViewById(R.id.password);
        P.set("username", username.getText().toString());
        P.set("password", password.getText().toString());
    }
    /*
    @Override public void finish() {
        getPropertiesFromView();
        super.finish();
    }
    */

    @Override
    public HiddenSettingsActivity.FragmentType getType() {
        return HiddenSettingsActivity.FragmentType.JANITOR_LOGIN;
    }
}
