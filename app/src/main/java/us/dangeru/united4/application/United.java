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

import java.io.StringReader;
import java.lang.ref.WeakReference;

import us.dangeru.united4.utils.PropertiesSingleton;

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

    public void playSong(String song) throws Exception {
        Log.i(TAG, "playSong called on " + song);
        String songs = PropertiesSingleton.get().getProperty("songs");
        JsonReader reader = new JsonReader(new StringReader(songs));
        reader.beginObject();
        int id = -1;
        while (reader.hasNext()) {
            int name = parseInt(reader.nextName());
            if (reader.nextString().equals(song)) {
                id = name;
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
        player = MediaPlayer.create(this, id);
        player.start();
        // make methods for these two things so they can be changed from the webkit
        // TODO get looping from properties singleton
        // TODO get shuffle and set onCompletionListener
    }
    public void stop() {
        if (player != null) player.stop();
    }
}
