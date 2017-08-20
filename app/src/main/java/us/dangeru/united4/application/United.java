package us.dangeru.united4.application;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.JsonReader;
import android.util.Log;
import android.webkit.WebView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import us.dangeru.united4.utils.PropertiesSingleton;
import us.dangeru.united4.utils.UnitedActivity;

import static java.lang.Integer.parseInt;

/**
 * Created by Niles on 8/19/17.
 */

public class United extends Application {
    private static final String TAG = United.class.getSimpleName();
    private static WeakReference<Context> singleton = null;
    private static MediaPlayer player = null;

    public static void playSound(String file) {
        SoundPool pool = buildPool();
        try {
            AssetFileDescriptor fd = getContext().getAssets().openFd(file);
            pool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                    soundPool.play(i, 1, 1, 1, 0, 1);
                }
            });
            pool.load(fd, 1);
        } catch (Exception ignored) {
            //
        }
    }

    private static SoundPool buildPool() {
        SoundPool pool;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pool = new SoundPool.Builder().setMaxStreams(10).build();
        } else {
            //noinspection deprecation
            pool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        }
        return pool;
    }

    public static Context getContext() {
        return singleton.get();
    }

    public void onCreate() {
        super.onCreate();
        singleton = new WeakReference<>(getApplicationContext());
    }

    public static void playSong(String song, final boolean reload, final UnitedActivity activity) {
        try {
            Log.i(TAG, "playSong called on " + song);
            String songs = PropertiesSingleton.get().getProperty("songs");
            JsonReader reader = new JsonReader(new StringReader(songs));
            reader.beginObject();
            int id = -1;
            while (reader.hasNext()) {
                String name = reader.nextName();
                int read = parseInt(reader.nextString());
                if (name.equals(song)) {
                    id = read;
                    Log.i(TAG, "Song id is " + id);
                    break;
                }
            }
            if (id == -1) {
                Log.e(TAG, "Song not found");
                throw new IllegalArgumentException("song not found");
            }
            Log.i(TAG, "Loading sound");
            stop();
            PropertiesSingleton.get().setProperty("is_playing", "true");
            player = MediaPlayer.create(getContext(), id);
            player.start();
            if (reload) {
                WebView webview = activity.getWebView();
                webview.reload();
            }
            // make methods for these two things so they can be changed from the webkit
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer player) {
                    boolean looping = "true".equalsIgnoreCase(PropertiesSingleton.get().getProperty("looping"));
                    boolean shuffling = "true".equalsIgnoreCase(PropertiesSingleton.get().getProperty("shuffle"));
                    if (looping) {
                        player.seekTo(0);
                        player.start();
                        return;
                    }
                    int idx;
                    try {
                        JSONArray parsed = new JSONArray(PropertiesSingleton.get().getProperty("ordered_songs"));
                        List<String> all_songs = new ArrayList<>();
                        for (int i = 0; i < parsed.length(); i++) {
                            all_songs.add(parsed.getString(i));
                        }
                        if (shuffling) {
                            idx = new Random().nextInt(all_songs.size());
                        } else {
                            idx = all_songs.indexOf(PropertiesSingleton.get().getProperty("current_song"));
                            idx = (idx + 1) % all_songs.size();
                        }
                        String next_song = all_songs.get(idx);
                        playSong(next_song, reload, activity);
                    } catch (Exception ignored) {
                        //
                    }
                }
            });
        } catch (Exception ignored) {
            //
        }
    }
    public static void stop() {
        if (player != null) player.stop();
    }
}
