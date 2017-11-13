package com.angryburg.uapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import com.angryburg.uapp.API.BoardsList;
import com.angryburg.uapp.API.BoardsListListener;
import com.angryburg.uapp.R;
import com.angryburg.uapp.utils.P;

/**
 * Created by Niles on 8/23/17.
 */

public class AndroidShortcuts extends Activity implements BoardsListListener {
    BoardsListAdapter adapter = null;
    private ListView.OnItemClickListener listener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            BoardsList.Board board = (BoardsList.Board) adapterView.getItemAtPosition(i);
            Intent launchIntent = new Intent();
            launchIntent.putExtra("URL", P.get("awoo_endpoint") + "/" + board.name);
            launchIntent.setAction("com.angryburg.la_u_ncher.intent.action.BOARD");
            Intent result = new Intent();
            result.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launchIntent);
            result.putExtra(Intent.EXTRA_SHORTCUT_NAME, "danger/" + board.name + "/");
            result.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, makeIcon());
            setResult(RESULT_OK, result);
            finish();
        }
    };

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.board_list);
        BoardsList.registerListener(this);
        findViewById(R.id.button_to_launch_to_thread_watcher).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent launchIntent = new Intent();
                launchIntent.putExtra("fragment", HiddenSettingsActivity.FragmentType.THREAD_WATCHER.toString());
                launchIntent.setAction("com.angryburg.la_u_ncher.intent.action.THREAD_WATCHER");
                Intent result = new Intent();
                result.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launchIntent);
                result.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Thread Watcher");
                result.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, makeIcon());
                setResult(RESULT_OK, result);
                finish();
            }
        });
    }
    private Parcelable makeIcon() {
        String field_name = P.get("theme") + "_dangeru";
        try {
            Integer field = (Integer) R.raw.class.getField(field_name).get(null);
            return Intent.ShortcutIconResource.fromContext(AndroidShortcuts.this, field);
        } catch (Throwable e) {
            e.printStackTrace();
            return Intent.ShortcutIconResource.fromContext(AndroidShortcuts.this, R.raw.normal_dangeru);
        }
    }
    boolean firstReady = true;
    @Override
    public synchronized void boardsListReady(final List<BoardsList.Board> list) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView listview = findViewById(R.id.board_list_list_view);
                if (firstReady) {
                    findViewById(R.id.boards_loading).setVisibility(View.GONE);
                    listview.setOnItemClickListener(listener);
                    for (BoardsList.Board b : list) {
                        if (b.description == null) b.getDescription(AndroidShortcuts.this);
                    }
                    firstReady = false;
                }
                adapter = new BoardsListAdapter(AndroidShortcuts.this, 0, list);
                listview.setAdapter(adapter);
            }
        });
    }

    private static class BoardsListAdapter extends ArrayAdapter<BoardsList.Board> {
        public BoardsListAdapter(@NonNull Context context, @LayoutRes int unused, @NonNull List<BoardsList.Board> objects) {
            super(context, 0, objects);
        }
        @NonNull
        @Override public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            BoardsList.Board board = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.board_list_item, parent, false);
            }
            if (board != null) {
                ((TextView) convertView.findViewById(R.id.list_item_board_name)).setText(board.name);
                if (board.description != null) {
                    ((TextView) convertView.findViewById(R.id.list_item_board_description)).setText(board.description);
                }
            }
            return convertView;
        }
    }
}
