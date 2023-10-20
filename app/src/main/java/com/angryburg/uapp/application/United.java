package com.angryburg.uapp.application;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.Nullable;
import android.util.JsonReader;
import android.util.Log;

import com.angryburg.uapp.API.Authorizer;
import com.angryburg.uapp.API.BoardsList;
import com.angryburg.uapp.API.ThreadWatcher;
import com.angryburg.uapp.utils.NotifierService;
import com.angryburg.uapp.utils.P;

import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.ExoPlaybackException;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.LoadControl;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.common.Timeline;
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter;
import androidx.media3.extractor.DefaultExtractorsFactory;
import androidx.media3.extractor.ExtractorsFactory;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.TrackGroupArray;
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.exoplayer.trackselection.TrackSelection;
import androidx.media3.exoplayer.trackselection.TrackSelectionArray;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultDataSourceFactory;
import androidx.media3.common.util.Util;

import org.json.JSONArray;

import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static androidx.media3.common.Player.STATE_ENDED;
import static androidx.media3.common.Player.STATE_IDLE;
import static androidx.media3.common.Player.STATE_READY;

@UnstableApi
/**
 * Created by Niles on 8/19/17.
 */

public class United extends Application {
    private static final String TAG = United.class.getSimpleName();
    /**
     * this has to be public for the background notifications
     * don't use it
     */
    public static WeakReference<Context> singleton = null;
    /**
     * The object to use to authenticate API requests
     */
    public static Authorizer authorizer = null;

    public static Player player = null;

    /**
     * Makes a new sound pool, loads the requested sound and plays it once it's
     * loaded
     * Could be made a LOT better by reusing the same pool and checking if it's
     * already loaded
     *
     * @param file the sound to play
     */
    public static void playSound(String file) {
        if (P.getBool("mute_sounds"))
            return;
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
        pool = new SoundPool.Builder().setMaxStreams(10).build();
        return pool;
    }

    /**
     * Hold on to ApplicationContext statically so we can always get it
     *
     * @return an application context object that can be used to open private files
     */
    public static Context getContext() {
        return singleton.get();
    }

    /**
     * Plays the given song, by finding its R.raw id in the songs map, stored in
     * properties
     * If notify is set set to true, will notify the webpage. For more information,
     * see NotifierService's documentation
     *
     * @param song   the full name of the song to play
     * @param reload whether to notify the web page
     */
    public static void playSong(String song, final boolean reload) {
        Log.i(TAG, "playSong called on " + song);
        // Find the filename in the map
        String songs = P.get("songs");
        JsonReader reader = new JsonReader(new StringReader(songs));
        String id = null;
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                String read = reader.nextString();
                // noinspection EqualsReplaceableByObjectsCall
                if (name.equals(song)) {
                    id = read;
                    Log.i(TAG, "Song id is " + id);
                    break;
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        // If we couldn't find it, fucking die
        if (id == null) {
            Log.e(TAG, "Song not found");
            throw new IllegalArgumentException("song not found");
        }
        Log.i(TAG, "Loading sound");
        // Stop any song in progress
        stop();
        // Set some properties in case the javascript forgot to set them
        P.set("is_playing", "true");
        P.set("current_song", song);
        if (player != null) {
            player.stop();
        }
        player = new ExoPlayer.Builder(getContext()).build();
        player.addListener(new SongDoneListener());

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(),
                Util.getUserAgent(getContext(), "la/u/ncher"));

        //ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse("https://niles.xyz/valhalla_music/" + id + ".mp3")));
                //dataSourceFactory, extractorsFactory, null, null);
        player.setPlayWhenReady(true);
        player.setMediaItem(mediaSource.getMediaItem());
        player.prepare();
        if (reload) {
            NotifierService.notify(NotifierService.NotificationType.RELOAD_MUSIC);
        }
    }

    /**
     * Stops the currently playing song
     */
    public static void stop() {
        if (player != null)
            player.stop();
    }

    /**
     * Set up the application context singleton, start pulling down updated threads
     * and the list of boards
     */
    public void onCreate() {
        super.onCreate();
        singleton = new WeakReference<>(getApplicationContext());
        if (P.getBool("userscript"))
            ThreadWatcher.initialize();
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
     * Called when a song is done playing. If we're looping, seek to the beginning
     * and start again
     * If we're shuffling, pick a random song and play it, and notify music.html
     * Otherwise, go to the next song in the list, play it and notify music.html
     */
    private static class SongDoneListener implements Player.Listener {
        public static void onCompletion(Player player) {
            boolean looping = P.getBool("looping");
            boolean shuffling = P.getBool("shuffle");
            // If we're looping, play the song again
            if (looping) {
                player.seekTo(0);
                player.setPlayWhenReady(true);
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
                // Play it and notify music.html if it's up. For more information see the
                // documentation for
                // NotifierService
                playSong(next_song, true);
            } catch (Exception ignored) {
                //
            }
        }


        @Override
        public void onPlaybackStateChanged(int playbackState) {
            Log.i(TAG, "stateChanged to "+ playbackState);
            if (playbackState == STATE_ENDED) {
                onCompletion(player);
            }
        }

        @Override
        public void onPlayWhenReadyChanged(boolean shuffleModeEnabled, int playbackState) {
            Log.i(TAG, "onPlayReadystateChanged to "+ playbackState);
            if (playbackState == STATE_IDLE) {
                Log.i(TAG, "starting playback");
                //player.play();
            }
            if (playbackState == STATE_READY) {
                Log.i(TAG, "would be starting playback from ready");
                //player.play();
            }
        }

    }
}
