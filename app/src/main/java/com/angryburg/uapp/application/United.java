package com.angryburg.uapp.application;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;

import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.angryburg.uapp.API.Authorizer;
import com.angryburg.uapp.API.BoardsList;
import com.angryburg.uapp.API.ThreadWatcher;
import com.angryburg.uapp.utils.P;
import com.angryburg.uapp.utils.ReloadService;

import static java.lang.Integer.parseInt;

/**
 * Created by Niles on 8/19/17.
 */

public class United extends Application {
    private static final String TAG = United.class.getSimpleName();
    private static WeakReference<Context> singleton = null;
    private static MediaPlayer player = null;
    /**
     * The object to use to authenticate API requests
     */
    public static Authorizer authorizer = null;

    /**
     * Makes a new sound pool, loads the requested sound and plays it once it's loaded
     * Could be made a LOT better by reusing the same pool and checking if it's already loaded
     * @param file the sound to play
     */
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

    /**
      * makes a new sound pool, platform independently
      */
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


    /**
     * Hold on to ApplicationContext statically so we can always get it
     * @return an application context object that can be used to open private files
     */
    public static Context getContext() {
        return singleton.get();
    }


    /**
     * Set up the application context singleton, start pulling down updated threads and the list of boards
     */
    public void onCreate() {
        super.onCreate();
        singleton = new WeakReference<>(getApplicationContext());
        if (P.getBool("userscript")) ThreadWatcher.initialize();
        if (P.getBool("logged_in")) {
            authorizer = new Authorizer(P.get("username"), P.get("password"));
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                BoardsList.refreshAllBoards(authorizer);
            }
        }).start();
    }


    /**
     * Plays the given song, by finding its R.raw id in the songs map, stored in properties
     * If reload is set set to true, will reload the webpage. For more information, see ReloadService's documentation
     * @param song the full name of the song to play
     * @param reload whether to reload the web page
     */
    public static void playSong(String song, final boolean reload) {
        try {
            Log.i(TAG, "playSong called on " + song);
            // Find the R.raw id in the map
            String songs = P.get("songs");
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
            // If we couldn't find it, fucking die
            if (id == -1) {
                Log.e(TAG, "Song not found");
                throw new IllegalArgumentException("song not found");
            }
            Log.i(TAG, "Loading sound");
            // Stop any song in progress
            stop();
            // Set some properties in case the javascript forgot to set them
            P.set("is_playing", "true");
            P.set("current_song", song);
            // Make a new media player and play the file
            player = MediaPlayer.create(getContext(), id);
            player.start();
            if (reload) {
                ReloadService.reload();
            }
            // Call onCompletion when the song is done
            player.setOnCompletionListener(new SongDoneListener());
        } catch (Exception ignored) {
            //
        }
    }

    /**
     * Stops the currently playing song
     */
    public static void stop() {
        if (player != null) player.stop();
    }

    /**
     * Called when a song is done playing. If we're looping, seek to the beginning and start again
     * If we're shuffling, pick a random song and play it, and reload music.html
     * Otherwise, go to the next song in the list, play it and reload music.html
     */
    private static class SongDoneListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer player) {
            boolean looping = P.getBool("looping");
            boolean shuffling = P.getBool("shuffle");
            // If we're looping, play the song again
            if (looping) {
                player.seekTo(0);
                player.start();
                return;
            }
            int idx;
            try {
                // Get the list of all songs
                JSONArray parsed = new JSONArray(P.get("ordered_songs"));
                List<String> all_songs = new ArrayList<>();
                for (int i = 0; i < parsed.length(); i++) {
                    all_songs.add(parsed.getString(i));
                }
                // If we're shuffling, get a random index
                if (shuffling) {
                    idx = new Random().nextInt(all_songs.size());
                } else {
                    // otherwise, find the current index and add one
                    idx = all_songs.indexOf(P.get("current_song"));
                    idx = (idx + 1) % all_songs.size();
                }
                // Figure out what song we're supposed to play
                String next_song = all_songs.get(idx);
                // Play it and reload music.html if it's up. For more information see the documentation for
                // ReloadService
                playSong(next_song, true);
            } catch (Exception ignored) {
                //
            }
        }
    }
}

