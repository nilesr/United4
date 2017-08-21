package us.dangeru.launcher.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import us.dangeru.launcher.R;
import us.dangeru.launcher.fragments.GenericAlertDialogFragment;
import us.dangeru.launcher.utils.ParcelableMap;
import us.dangeru.launcher.utils.PropertiesSingleton;
import us.dangeru.launcher.utils.UnitedPropertiesIf;

/**
 * Created by Niles on 8/20/17.
 */

public class JanitorLoginActivity extends Activity {
    private static final String TAG = JanitorLoginActivity.class.getSimpleName();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.janitor_login_activity);
        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);
        username.setText(PropertiesSingleton.get().getProperty("username"));
        password.setText(PropertiesSingleton.get().getProperty("password"));
    }
    public void buttonClicked(View view) {
        getPropertiesFromView();
        UnitedPropertiesIf.authenticate(this);
    }
    public void getPropertiesFromView() {
        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);
        PropertiesSingleton.get().setProperty("username", username.getText().toString());
        PropertiesSingleton.get().setProperty("password", password.getText().toString());
    }
    @Override public void finish() {
        getPropertiesFromView();
        super.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            boolean logged_in = "true".equalsIgnoreCase(PropertiesSingleton.get().getProperty("logged_in"));
            if (logged_in) {
                GenericAlertDialogFragment.newInstance("Success! You are now logged in as " + PropertiesSingleton.get().getProperty("username"), getFragmentManager());
            } else {
                GenericAlertDialogFragment.newInstance("Error - Check your username and password?", getFragmentManager());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
