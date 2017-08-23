package us.dangeru.launcher.API;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import us.dangeru.launcher.utils.P;

/**
 * A utilities class for getting a list of boards from the awoo endpoint
 */

public final class BoardsList {
    private BoardsList() {
    }

    /**
     * Returns a list of boards supported by the awoo endpoint. If an authorizer is given, it will use
     * the authorizer to authenticate beforehand, so the returned boards list will contain hidden
     * boards like /staff/
     * @param authorizer The authorizer to use, or null if the request should not be authenticated
     * @return A list of boards supported by the endpoint, or null if an error occurred
     */
    public static List<String> getBoardsList(Authorizer authorizer) {
        try {
            List<String> results = new ArrayList<>();
            String jsonText = NetworkUtils.fetch(P.get("awoo_endpoint") + NetworkUtils.API + "/boards", authorizer);
            JSONArray arr = new JSONArray(jsonText);
            for (int i = 0; i < arr.length(); i++) {
                results.add(arr.getString(i));
            }
            return results;
        } catch (Exception e) {
            //return Collections.singletonList(e.toString());
            return null;
        }
    }
}
