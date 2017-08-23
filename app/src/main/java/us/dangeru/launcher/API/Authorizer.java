package us.dangeru.launcher.API;

import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import us.dangeru.launcher.utils.P;

/**
 * Created by Niles on 8/22/17.
 */

public class Authorizer {
    public String username;
    String cookie;
    String password;

    public Authorizer(String username, String password) {
        this.username = username;
        this.password = password;
        this.cookie = null;
    }

    public String getCookie() throws AuthorizationFailureException {
        if (this.cookie != null) return this.cookie;
        try {
            URL uri = new URL(P.get("awoo_endpoint") + "/mod");
            HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            String data = "username=" + URLEncoder.encode(username, "UTF-8");
            data += "&password=" + URLEncoder.encode(password, "UTF-8");
            os.write(data.getBytes());
            os.flush();
            os.close();
            int responseCode = connection.getResponseCode();
            if (responseCode == 403) {
                throw new AuthorizationFailureException();
            } else if (responseCode == 200) {
                String cookie = connection.getHeaderField("Set-Cookie");
                HttpCookie parsed = HttpCookie.parse(cookie).get(0);
                this.cookie = parsed.toString();
                return this.cookie;
            } else {
                throw new AuthorizationFailureException(responseCode);
            }
        } catch (Exception e) {
            if (e instanceof AuthorizationFailureException) {
                throw (AuthorizationFailureException) e;
            }
            throw new AuthorizationFailureException(e);
        }
    }

    public String reauthorize() throws AuthorizationFailureException {
        this.cookie = null;
        return getCookie();
    }

    public static class AuthorizationFailureException extends Exception {
        public Type type;
        public int responseCode;
        public Exception cause;

        public AuthorizationFailureException() {
            super();
            this.type = Type.AUTH;
        }
        public AuthorizationFailureException(int responseCode) {
            super();
            this.type = Type.UNEXPECTED_RESPONSE;
            this.responseCode = responseCode;
        }
        public AuthorizationFailureException(Exception e) {
            super();
            this.cause = e;
            this.type = Type.OTHER;
        }

        public enum Type {AUTH, UNEXPECTED_RESPONSE, OTHER}
    }

    ;
}
