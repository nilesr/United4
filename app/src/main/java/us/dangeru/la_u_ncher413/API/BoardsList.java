package us.dangeru.la_u_ncher413.API;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import us.dangeru.la_u_ncher413.application.United;
import us.dangeru.la_u_ncher413.utils.P;

/**
 * A utilities class for getting a list of boards from the awoo endpoint
 */

public final class BoardsList {
    private static final String TAG = BoardsList.class.getSimpleName();
    /**
     * The list of all boards supported by the awoo endpoint, or null if they haven't finished loading yet
     */
    public static List<Board> boards = null;
    public static List<BoardsListListener> listeners = new ArrayList<>();
    private BoardsList() {
    }

    /**
     * Returns a list of boards supported by the awoo endpoint. If an authorizer is given, it will use
     * the authorizer to authenticate beforehand, so the returned boards list will contain hidden
     * boards like /staff/
     * @param authorizer The authorizer to use, or null if the request should not be authenticated
     */
    public static void refreshAllBoards(Authorizer authorizer) {
        try {
            String jsonText = NetworkUtils.fetch(P.get("awoo_endpoint") + NetworkUtils.API + "/boards", authorizer);
            JSONArray arr = new JSONArray(jsonText);
            BoardsList.boards = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                BoardsList.boards.add(new Board(arr.getString(i)));
            }
        } catch (Exception e) {
            BoardsList.boards = Collections.singletonList(new Board(e.toString()));
        }
        notifyListeners();
    }

    private static void notifyListeners() {
        for (BoardsListListener listener : listeners) {
            try {
                Log.i(TAG, "Boards list population success, calling boardsListReady on " + listener);
                listener.boardsListReady(boards);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void registerListener(BoardsListListener listener) {
        Log.i(TAG, "Listener " + listener + " added");
        listeners.add(listener);
        if (boards != null) {
            Log.i(TAG, "Boards list was already populated, calling boardsListReady on " + listener);
            listener.boardsListReady(boards);
        }
    }
    public static class Board {
        public String name;
        public String description;
        public Board(String name) {
            this.name = name;
        }
        public void getDescription(final BoardsListListener listener) {
            new java.lang.Thread(new Runnable() {
                @Override
                public void run() {
                    if (Board.this.description != null) {
                        listener.boardsListReady(BoardsList.boards);
                        return;
                    }
                    try {
                        Board.this.description = new JSONObject(NetworkUtils.fetch(P.get("awoo_endpoint") + NetworkUtils.API + "/board/" + Board.this.name + "/detail", United.authorizer)).getString("desc");
                        listener.boardsListReady(BoardsList.boards);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
