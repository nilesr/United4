package com.angryburg.uapp.fragments;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.angryburg.uapp.API.Authorizer;
import com.angryburg.uapp.API.BoardsList;
import com.angryburg.uapp.R;
import com.angryburg.uapp.activities.HiddenSettingsActivity;
import com.angryburg.uapp.application.United;
import com.angryburg.uapp.utils.P;

import static com.angryburg.uapp.application.United.authorizer;

/**
 * A fragment that a janitor can use to log in
 */

public class JanitorLoginFragment extends Fragment implements HiddenSettingsFragment {
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
                Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
                addOptions(toolbar);
            }
        });
        return res;
    }

    /**
     * Pulls the username and password from their text boxes, makes a progress
     * dialog,
     * tries to authenticate, then dismisses the progress dialog and updates the
     * logged in text
     */
    private class LoginButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(final View view) {
            getPropertiesFromView();
            GenericProgressDialogFragment.newInstance("Logging in...", getParentFragmentManager());
            view.findViewById(R.id.button).setClickable(false);
            // can't do network on ui thread
            new Thread(new Runnable() {
                @Override
                public void run() {
                    authenticate();
                    view.findViewById(R.id.button).setClickable(true);
                    GenericProgressDialogFragment.dismiss(getParentFragmentManager());
                    updateLoggedInText();
                }
            }).start();
        }
    }

    /**
     * Sets the logged in text at the bottom to either "You are not logged in" or
     * "You are currently logged in as :username"
     */
    private void updateLoggedInText() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (getView() == null)
                    return;
                ((TextView) getView().findViewById(R.id.logged_in))
                        .setText("You are " + (P.getBool("logged_in") ? "currently" : "not") + " logged in"
                                + (P.getBool("logged_in") ? " as " + P.get("username") : ""));
            }
        });
    }

    /**
     * Attempts authentication and handles errors if they occur
     * Will update the boards list and set "logged_in" on success
     */
    private void authenticate() {
        P.set("logged_in", "false");
        United.authorizer = new Authorizer(P.get("username"), P.get("password"));
        try {
            United.authorizer.reauthorize();
            GenericAlertDialogFragment.newInstance("Success! You are logged in as " + authorizer.username,
                    getParentFragmentManager());
            P.set("logged_in", "true");
            // the /staff/ board will only be shown when you're logged in, so update the
            // boards list in the background
            BoardsList.refreshAllBoards(authorizer);
        } catch (Authorizer.AuthorizationFailureException e) {
            switch (e.type) {
                case AUTH:
                    GenericAlertDialogFragment.newInstance("Error - Check your username and password",
                            getParentFragmentManager());
                    break;
                case UNEXPECTED_RESPONSE:
                    GenericAlertDialogFragment.newInstance("Unexpected response code - " + e.responseCode,
                            getParentFragmentManager());
                    break;
                case OTHER:
                    GenericAlertDialogFragment.newInstance("Unexpected error - " + e.cause,
                            getParentFragmentManager());
                    break;
            }
        }
    }

    /**
     * Pulls the username and password from their screen values
     */
    public void getPropertiesFromView() {
        if (getView() == null)
            return;
        EditText username = getView().findViewById(R.id.username);
        EditText password = getView().findViewById(R.id.password);
        P.set("username", username.getText().toString());
        P.set("password", password.getText().toString());
    }

    @Override
    public HiddenSettingsActivity.FragmentType getType() {
        return HiddenSettingsActivity.FragmentType.JANITOR_LOGIN;
    }

    /**
     * adds a back button to the toolbar
     * 
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
}
