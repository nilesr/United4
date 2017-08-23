package us.dangeru.launcher.API;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import us.dangeru.launcher.utils.P;

/**
 * Created by Niles on 8/22/17.
 */

public class BoardsList {
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
            return Collections.singletonList(e.toString());
        }
    }
}
