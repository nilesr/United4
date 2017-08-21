package us.dangeru.launcher.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.Certificate;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

import us.dangeru.launcher.R;
import us.dangeru.launcher.fragments.GenericAlertDialogFragment;
import us.dangeru.launcher.utils.PropertiesSingleton;

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
                authenticate();
            }
        }).start();
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
        try {
            getPropertiesFromView();
            String username = PropertiesSingleton.get().getProperty("username");
            String password = PropertiesSingleton.get().getProperty("username");
            URL loginUrl = new URL(PropertiesSingleton.get().getProperty("awoo_endpoint") + "/mod");
            HttpURLConnection connection = (HttpURLConnection) loginUrl.openConnection();
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            String post_params = "username=" + URLEncoder.encode(username, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8");
            os.write(post_params.getBytes());
            os.flush();
            os.close();
            Log.i(TAG, "Response: " + connection.getResponseCode());
            if (connection.getResponseCode() == HttpsURLConnection.HTTP_FORBIDDEN) {
                GenericAlertDialogFragment.newInstance("Username or password incorrect (HTTP_FORBIDDEN)", getFragmentManager());
            } else if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                List<String> cookies = connection.getHeaderFields().get("Set-Cookie");
                boolean success = false;
                for (String cookie : cookies) {
                    HttpCookie f = HttpCookie.parse(cookie).get(0);
                    if ("rack.session".equals(f.getName())) {
                        PropertiesSingleton.get().setProperty("cookie", cookie);
                        success = true;
                    }
                }
                BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder b = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    b.append(line);
                }
                r.close();
                if (success) {
                    String hack = b.toString();
                    hack = hack.substring(0, hack.indexOf('&'));
                    GenericAlertDialogFragment.newInstance("Success - " + hack, getFragmentManager());
                } else {
                    GenericAlertDialogFragment.newInstance("Unknown error, 200 response but rack session cookie not set", getFragmentManager());
                }
            } else {
                GenericAlertDialogFragment.newInstance("Unexpected response code " + connection.getResponseCode(), getFragmentManager());
            }
        } catch (Exception e) {
            GenericAlertDialogFragment.newInstance("Unknown error - " + e, getFragmentManager());
        }
    }

}
