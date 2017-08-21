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
        //GenericAlertDialogFragment.newInstance("Not implemented yet", getFragmentManager());
        // can't do network work on UI thread
        // TODO throbber or something
        new Thread(new Runnable() {
            @Override
            public void run() {
                //authenticate();
            }
        }).start();
        authenticate();
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
    private void authenticate() {
        getPropertiesFromView();
        String username = PropertiesSingleton.get().getProperty("username");
        String password = PropertiesSingleton.get().getProperty("username");
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        Bundle extras = new Bundle();
        extras.putString("URL", PropertiesSingleton.get().getProperty("awoo_endpoint") + "/mod");
        ParcelableMap headers = new ParcelableMap();
        headers.put("X-Awoo-Username", username);
        headers.put("X-Awoo-Password", password);
        extras.putParcelable("headers", headers.parcel());
        i.putExtras(extras);
        startActivityForResult(i, 10);
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
