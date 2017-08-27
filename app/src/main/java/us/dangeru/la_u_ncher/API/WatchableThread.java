package us.dangeru.la_u_ncher.API;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import us.dangeru.la_u_ncher.utils.P;

import static us.dangeru.la_u_ncher.API.NetworkUtils.API;
import static us.dangeru.la_u_ncher.API.NetworkUtils.fetch;

/**
 * Created by Niles on 8/22/17.
 */

public final class WatchableThread extends Thread {
    /**
     * the number of new replies to this thread since the last time it was viewed
     */
    public int new_replies;

    /**
     * @see Thread#Thread(JSONObject)
     * @param object the object to deserialize
     * @throws JSONException if the object was malformed
     */
    private WatchableThread(JSONObject object) throws JSONException {
        super(object);
    }

    /**
     * Factory method to fetch a thread from the server
     * @param id the ID of the thread to fetch
     * @param authorizer An authorizer if the request should be authenticated, otherwise null
     * @return A new WatchableThread with the correct thread information and new reply count
     * @throws IOException if the network request could not be completed
     * @throws JSONException if the object returned from the server was malformed
     * @throws Authorizer.AuthorizationFailureException if the request could not be authenticated
     */
    static WatchableThread getThreadById(int id, Authorizer authorizer) throws Authorizer.AuthorizationFailureException, IOException, JSONException {
        String result = fetch(P.get("awoo_endpoint") + API + "/thread/" + id + "/metadata", authorizer);
        WatchableThread thread = new WatchableThread(new JSONObject(result));
        thread.updateNewRepliesCount();
        return thread;
    }

    /**
     * Updates the new_replies variable based on the board:id property
     */
    void updateNewRepliesCount() {
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
