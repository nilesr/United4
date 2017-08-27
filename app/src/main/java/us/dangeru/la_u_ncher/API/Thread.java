package us.dangeru.la_u_ncher.API;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class representing an awoo thread
 */
public class Thread {
    /**
     * The id of the post
     */
    public int post_id;
    /**
     * The board the thread was posted on
     */
    public String board;
    /**
     * Whether the post is a new thread or a reply
     */
    public boolean is_op;
    /**
     * The content of the post
     */
    public String comment;
    /**
     * The time the post was made
     */
    public long date_posted;
    /**
     * The IP of the poster, only retrievable if you moderate the board the post was made to, otherwise null
     */
    public String ip;
    /**
     * The capcode of the posting janitor, or null if not applicable
     */
    public String capcode;
    /**
     * The title of the post, or null if the post is a reply
     */
    public String title;
    /**
     * The date the thread was most recently bumped, or null if it's a reply
     */
    public long last_bumped;
    /**
     * Represents whether this post is locked. Only applicable if is_op is true
     */
    public boolean is_locked;
    /**
     * The number of replies the post has, only applicable if is_op is true
     */
    public int number_of_replies;
    /**
     * Whether this post is stickied or not, only applicable if is_ip is true
     */
    public boolean sticky;
    /**
     * The post_id of the parent post, only applicable if is_op is false
     */
    public int parent;
    /**
     * The stickyness level, posts with higher stickyness will appear above unstickied posts
     */
    public int stickyness;
    /**
     * The unique identifier associated with the poster, or "FFFFFF" if the ip in the database was null
     */
    public String hash;

    /**
     * Constructs a new thread from the given JSON object, typically received from the API
     * @param object the object to deserialize
     * @throws JSONException if the give object wasn't in the expected format (for example if the object had a "post_id" key but it was a string instead of an integer)
     */
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
        if (object.has("stickyness")) {
            this.stickyness = object.getInt("stickyness");
        }
        if (object.has("hash")) {
            this.hash = object.getString("hash");
        }
    }
}
