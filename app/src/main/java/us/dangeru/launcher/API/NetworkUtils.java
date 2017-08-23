package us.dangeru.launcher.API;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Niles on 8/21/17.
 */

public final class NetworkUtils {
    private NetworkUtils() {
    }
    public static final String API = "/api/v2";

    public static String fetch(String url, Authorizer authorizer) throws Exception {
        String cookie = null;
        if (authorizer != null) {
            cookie = authorizer.getCookie();
        }
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        if (cookie != null) {
            connection.setRequestProperty("Cookie", cookie);
        }
        connection.setRequestMethod("GET");
        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        StringBuilder b = new StringBuilder();
        char buf[] = new char[1024];
        int read;
        while ((read = reader.read(buf, 0, 1023)) > 0) {
            b.append(buf, 0, read);
        }
        return b.toString();
    }
}
