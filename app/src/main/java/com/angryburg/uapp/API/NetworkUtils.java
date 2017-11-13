package com.angryburg.uapp.API;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Helper class for executing API calls
 */

public final class NetworkUtils {
    private NetworkUtils() {
    }

    /**
     * The path that all API routes begin with
     */
    public static final String API = "/api/v2";

    /**
     * Gets the contents of the url, authenticating the request beforehand if authorizer is not null
     * @param url The url to request
     * @param authorizer The authorizer to use, or null if the request should not be authenticated
     * @return The contents of that url
     * @throws IOException If there was an error receiving the data from the server
     * @throws Authorizer.AuthorizationFailureException If the request could not be authorized
     */
    static String fetch(String url, Authorizer authorizer) throws Authorizer.AuthorizationFailureException, IOException {
        // If we have an authorizer, get its cookie before calling openConnection so that
        // sending the second request is not blocking on the first one returning.
        String cookie = null;
        if (authorizer != null) {
            cookie = authorizer.getCookie();
        }
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        // If we need to authenticate, give the rack session cookie pulled from the authorizer
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
