package us.dangeru.launcher.API;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

import us.dangeru.launcher.utils.P;

import static us.dangeru.launcher.API.NetworkUtils.API;
import static us.dangeru.launcher.API.NetworkUtils.fetch;

/**
 * Created by Niles on 8/21/17.
 */

public class Thread {
    public int post_id;
    public String board;
    public boolean is_op;
    public String comment;
    public long date_posted;
    public String ip;
    public String capcode;
    public String title;
    public long last_bumped;
    public boolean is_locked;
    public int number_of_replies;
    public boolean sticky;
    public int parent;
    public Thread(JSONObject object) throws JSONException {
        if (object.has("post_id")) {
            this.post_id = object.getInt("post_id");
        }
        if (object.has("board")) {
            this.board = object.getString("board");
        }
        if (object.has("is_op")) {
            this.is_op = object.getBoolean("is_op");
        }
        if (object.has("comment")) {
            this.comment = object.getString("comment");
        }
        if (object.has("date_posted")) {
            this.date_posted = object.getInt("date_posted");
        }
        if (object.has("ip")) {
            this.ip = object.getString("ip");
        }
        if (object.has("capcode")) {
            this.capcode = object.getString("capcode");
        }
        if (object.has("title")) {
            this.title = object.getString("title");
        }
        if (object.has("last_bumped")) {
            this.last_bumped = object.getLong("last_bumped");
        }
        if (object.has("is_locked")) {
            this.is_locked = object.getBoolean("is_locked");
        }
        if (object.has("number_of_replies")) {
            this.number_of_replies = object.getInt("number_of_replies");
        }
        if (object.has("sticky")) {
            this.sticky = object.getBoolean("sticky");
        }
        if (object.has("parent")) {
            this.parent = object.getInt("parent");
        }
    }
    public static Thread getThreadById(int id) throws Exception {
        String result = fetch(P.get("awoo_endpoint") + API + "/thread/" + id + "/metadata");
        return new Thread(new JSONObject(result));
    }
}
