package us.dangeru.la_u_ncher.API;

import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import us.dangeru.la_u_ncher.utils.P;

/**
 * An object that can be used to authenticate as a janitor against an awoo endpoint.
 * NetworkUtils::fetch can optionally take an authorizer and will use it to authenticate
 * to the server before sending its request.
 */
public class Authorizer {
    /**
     * The username
     */
    public String username;
    /**
     * The password
     */
    public String password;
    /**
     * The rack session cookie set by the server. This value is cached, and should be accessed using
     * getCookie(). When NetworkUtils::fetch is directed to get a url after authorizing, it will try
     * to pull this rack session cookie from the authorizer using getCookie. If getCookie determines
     * that the cookie has not been set yet, it will send a POST request to /mod to get a new one
     * However once it has sent that request once, future calls to getCookie will return the same cookie
     * without the need for additional requests. In that way, United can make multiple API calls and
     * only ever worry about setting an authorizer once, any API call after the first one will be
     * executed using the saved cookie
     *
     * All accesses to this cookie are synchronized, so if one thread is already in the process
     * of making a post request, another thread that requests the cookie before the request is
     * finished will block until the request is finished, then return the cookie from the first request
     * instead of sending off a new one
     */
    private String cookie;

    /**
     * Generic constructor for a new authorizer
     * @param username the username
     * @param password the password
     */
    public Authorizer(String username, String password) {
        this.username = username;
        this.password = password;
        this.cookie = null;
    }

    /**
     * Attempts to pull the saved rack session cookie. If it had not been previously saved,
     * executes a POST request to /mod to get a new one and saves it for future use.
     * @return a cookie that can be set using HttpURLConnection::setRequestProperty
     * @throws AuthorizationFailureException if an error occurred during authorization
     */
    synchronized String getCookie() throws AuthorizationFailureException {
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
                // incorrect password
                throw new AuthorizationFailureException();
            } else if (responseCode == 200) {
                // success, save the cookie and return it.
                String cookie = connection.getHeaderField("Set-Cookie");
                HttpCookie parsed = HttpCookie.parse(cookie).get(0);
                this.cookie = parsed.toString();
                return this.cookie;
            } else {
                // unexpected response code
                throw new AuthorizationFailureException(responseCode);
            }
        } catch (Exception e) {
            // if we got an incorrect password or unexpected response code error, throw that
            if (e instanceof AuthorizationFailureException) {
                throw (AuthorizationFailureException) e;
            }
            // if we got some other kind of error, throw a generic unexpected exception
            throw new AuthorizationFailureException(e);
        }
    }

    /**
     * Clears the saved rack session cookie and attempts to reauthorize. Used in JanitorLoginFragment
     * to check if the given username and password are correct
     * @return the cookie
     * @throws AuthorizationFailureException if the username and password could not be authorized
     */
    public synchronized String reauthorize() throws AuthorizationFailureException {
        this.cookie = null;
        return getCookie();
    }

    /**
     * An exception class for dealing with failures to authenticate against the awoo endpoint
     */
    public static class AuthorizationFailureException extends Exception {
        /**
         * What kind of error occurred
         */
        public Type type;
        /**
         * The response code, if the type of error was an unexpected response code
         */
        public int responseCode;
        /**
         * The causing exception, if the type of error was an unexpected exception
         */
        public Exception cause;

        /**
         * Constructor used for a bad username/password combination
         */
        AuthorizationFailureException() {
            super();
            this.type = Type.AUTH;
        }

        /**
         * Constructor used for an unexpected response code
         * @param responseCode the response code
         */
        AuthorizationFailureException(int responseCode) {
            super();
            this.type = Type.UNEXPECTED_RESPONSE;
            this.responseCode = responseCode;
        }

        /**
         * Constructor used for an unexpected exception
         * @param e the exception that triggered the failure
         */
        AuthorizationFailureException(Exception e) {
            super();
            this.cause = e;
            this.type = Type.OTHER;
        }

        /**
         * The three different types of authorization failure exceptions
         */
        @SuppressWarnings({"JavaDoc", "WeakerAccess" /* JanitorLoginFragment needs this */})
        public enum Type {AUTH, UNEXPECTED_RESPONSE, OTHER}
    }
}
