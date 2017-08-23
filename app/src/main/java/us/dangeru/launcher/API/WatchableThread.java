package us.dangeru.launcher.API;

import org.json.JSONException;
import org.json.JSONObject;

import us.dangeru.launcher.application.United;
import us.dangeru.launcher.utils.P;

import static us.dangeru.launcher.API.NetworkUtils.API;
import static us.dangeru.launcher.API.NetworkUtils.fetch;

/**
 * Created by Niles on 8/22/17.
 */

public class WatchableThread extends Thread {
    public int new_replies;
    public WatchableThread(JSONObject object) throws JSONException {
        super(object);
    }
    public static WatchableThread getThreadById(int id) throws Exception {
        String result = fetch(P.get("awoo_endpoint") + API + "/thread/" + id + "/metadata", United.authorizer);
        WatchableThread thread = new WatchableThread(new JSONObject(result));
        thread.updateNewRepliesCount();
        return thread;
    }

    public void updateNewRepliesCount() {
        String prev_replies_str = P.get(board + ":" + post_id);
        int prev_replies;
        if (prev_replies_str.isEmpty()) {
            prev_replies = 0;
        } else {
            prev_replies = Integer.valueOf(prev_replies_str);
        }
        new_replies = number_of_replies - prev_replies;
    }
}
