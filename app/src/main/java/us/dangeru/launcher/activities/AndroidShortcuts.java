package us.dangeru.launcher.activities;

import android.app.Activity;
import android.content.ComponentName;
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

import us.dangeru.launcher.API.BoardsList;
import us.dangeru.launcher.API.BoardsListListener;
import us.dangeru.launcher.R;
import us.dangeru.launcher.utils.P;

/**
 * Created by Niles on 8/23/17.
 */

public class AndroidShortcuts extends Activity implements BoardsListListener {
    BoardsListAdapter adapter = null;
    private ListView.OnItemClickListener listener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String board = (String) adapterView.getItemAtPosition(i);
            Intent launchIntent = new Intent();
            //launchIntent.setComponent(new ComponentName(AndroidShortcuts.this.getPackageName(), ".activities.UserscriptActivity"));
            launchIntent.putExtra("URL", P.get("awoo_endpoint") + "/" + board);
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //launchIntent.addCategory("launcher.intent.category.BOARD");
            launchIntent.setAction("us.dangeru.launcher.intent.action.BOARD");
            Intent result = new Intent();
            result.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launchIntent);
            result.putExtra(Intent.EXTRA_SHORTCUT_NAME, "danger/" + board + "/");
            Parcelable iconResource = Intent.ShortcutIconResource.fromContext(AndroidShortcuts.this, android.R.drawable.ic_lock_idle_alarm);
            result.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);
            //result.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            //sendBroadcast(result);
            setResult(RESULT_OK, result);
            finish();
        }
    };

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.board_list);
        BoardsList.registerListener(this);
    }

    @Override
    public void boardsListReady(final List<String> list) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter = new BoardsListAdapter(AndroidShortcuts.this, 0, list);
                ListView listview = findViewById(R.id.board_list_list_view);
                listview.setAdapter(adapter);
                listview.setOnItemClickListener(listener);
            }
        });
    }

    private static class BoardsListAdapter extends ArrayAdapter<String> {
        public BoardsListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects) {
            super(context, 0, objects);
        }
        @NonNull
        @Override public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            String board = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.board_list_item, parent, false);
            }
            ((TextView) convertView.findViewById(R.id.list_item_board_name)).setText(board);
            return convertView;
        }
    }
}
