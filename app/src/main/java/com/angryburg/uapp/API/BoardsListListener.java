package com.angryburg.uapp.API;

import java.util.List;

/**
 * Created by Niles on 8/23/17.
 */

public interface BoardsListListener {
    void boardsListReady(List<BoardsList.Board> list);
}
