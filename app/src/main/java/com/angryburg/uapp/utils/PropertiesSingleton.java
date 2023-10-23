package com.angryburg.uapp.utils;

import android.annotation.SuppressLint;
import androidx.fragment.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import androidx.preference.PreferenceManager;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.angryburg.uapp.application.United;
import com.angryburg.uapp.fragments.GenericAlertDialogFragment;

/**
 * Stores app-wide properties that should be saved between launches of the app
 */

public final class PropertiesSingleton {
    private static final String TAG = PropertiesSingleton.class.getSimpleName();
    /**
     * used to be "united4_config.json" but it's not called /u/nited anymore
     */
    private static final String CONFIG = "launcher_config.json";
    static {
        init();
    }

    /**
     * Do not instantiate this class
     */
    private PropertiesSingleton() {
    }

    /**
     * Reads in all the properties from the json file or some generic default
     * properties if that failed
     */
    private static void init() {
        resetForAppStart();
        if (getProperty("migrated").isEmpty()) {
            try {
                JsonReader reader = new JsonReader(new InputStreamReader(United.getContext().openFileInput(CONFIG)));
                reader.beginObject();
                int read = 0;
                while (reader.hasNext()) {
                    read++;
                    String key = reader.nextName();
                    String value = reader.nextString();
                    Log.d(TAG, "MIGRATION: Read prop " + key);
                    setProperty(key, value);
                }
                resetForAppStart();
                if (read == 0) {
                    setFirstRunProperties();
                }
                setProperty("migrated", "success");
            } catch (Exception e) {
                e.printStackTrace();
                setFirstRunProperties();
                setProperty("migrated", "errors");
            }
        } else if (getProperty("migrated").equalsIgnoreCase("reset")) {
            setFirstRunProperties();
            setProperty("migrated", "reset_complete");
        }
    }

    private static void setFirstRunProperties() {
        // default properties that should only be set on the very first run of the app
        setProperty("startup_music", "false");
        setProperty("theme", "normal");
        setProperty("looping", "false");
        setProperty("shuffle", "false");
        setProperty("current_song", "");
        setProperty("debug", "false");
        setProperty("bar", "true");
        setProperty("scroll_to_bar", "true");
        setProperty("hide_version", "false");
        setProperty("show_yous", "true");
        setProperty("display_my_id", "false");
        setProperty("dark_mode", "false");
        setProperty("which_notifications", "DIRECT");
        setProperty("alarm_interval", "HALF_DAY");
        setProperty("notifications", "true");
    }

    /**
     * static properties that never change
     */
    private static void resetForAppStart() {
        // the default value for infinite scrolling is set by the userscript, so it
        // doesn't need to be included here
        if (getProperty("userscript").isEmpty())
            setProperty("userscript", "true");
        if (getProperty("force_show_back_btn").isEmpty())
            setProperty("force_show_back_btn", "true");
        if (getProperty("window_bar_color").isEmpty())
            setProperty("window_bar_color", "-25");
        if (getProperty("watch_on_reply").isEmpty())
            setProperty("watch_on_reply", "true");
        if (getProperty("which_notifications").isEmpty())
            setProperty("which_notifications", "DIRECT");
        if (getProperty("alarm_interval").isEmpty())
            setProperty("alarm_interval", "HALF_DAY");
        if (getProperty("notifications").isEmpty())
            setProperty("notifications", Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? "false" : "true");
        // Pull the current version and put it in the main screen
        String version = "error";
        try {
            PackageInfo packageInfo = United.getContext().getPackageManager()
                    .getPackageInfo(United.getContext().getPackageName(), 0);
            version = String.valueOf(packageInfo.versionName);
        } catch (Exception ignored) {
            //
        }
        setProperty("version_notes", "Version " + version + "!\nTap for Patch Notes");
        // index.html expects is_playing to be false on first load so it can play
        // startup music
        // if it was told to. It doesn't start the music if `is_playing` is set to true,
        // so if you go
        // to music.html, change the song, rotate and come back, it won't override the
        // song you just picked
        // with the startup music again
        setProperty("is_playing", "false");
        // List<String> themes = Arrays.asList("normal", "dotted", "steam", "kira",
        // "meme", "vaporwave");
        // rip the meme theme, it was my favorite
        List<String> themes = Arrays.asList("normal", "vaporwave", "noir", "burg", "unity", "empire", "classy", "lain",
                "neon", "kids", "motif");
        String str = new JSONArray(themes).toString();
        setProperty("all_themes", str);
        HashMap<String, String> map = new HashMap<>();
        ArrayList<String> ordered_songs = new ArrayList<>();
        put(map, ordered_songs, "hopes_and_dreams", "Hopes and Dreams");
        put(map, ordered_songs, "a_neon_glow_lights_the_way", "A Neon Glow Lights the Way");
        put(map, ordered_songs, "welcome_to_va_11_hall_a", "Welcome To VA-11 HALL-A");
        put(map, ordered_songs, "every_day_is_night", "Every Day is Night");
        put(map, ordered_songs, "commencing_simulation", "Commencing Simulation");
        put(map, ordered_songs, "drive_me_wild", "Drive Me Wild");
        put(map, ordered_songs, "good_for_health_bad_for_education", "Good for Health, Bad for Education");
        put(map, ordered_songs, "who_was_i", "Who Was I?");
        put(map, ordered_songs, "troubling_news", "Troubling News");
        put(map, ordered_songs, "a_gaze_that_invited_disaster", "A Gaze That Invited Disaster");
        put(map, ordered_songs, "friendly_conversation", "Friendly Conversation");
        put(map, ordered_songs, "youve_got_me", "You've Got Me");
        put(map, ordered_songs, "umemoto", "Umemoto");
        put(map, ordered_songs, "jc_eltons", "JC Elton's");
        put(map, ordered_songs, "go_go_streaming_chan", "Go! Go! Streaming-chan!");
        put(map, ordered_songs, "all_systems_go", "All Systems, Go!");
        put(map, ordered_songs, "where_do_i_go_from_here", "Where Do I Go From Here?");
        put(map, ordered_songs, "will_you_remember_me", "Will You Remember Me?");
        put(map, ordered_songs, "everything_will_be_okay", "Everything Will Be Okay");
        put(map, ordered_songs, "march_of_the_white_knights", "March of the White Knights");
        put(map, ordered_songs, "a_rene", "A. Rene");
        put(map, ordered_songs, "neo_avatar", "Neo Avatar");
        put(map, ordered_songs, "those_who_dwell_in_the_shadows", "Those Who Dwell in The Shadows");
        put(map, ordered_songs, "nightime_maneuvers", "Nighttime Manuvers");
        put(map, ordered_songs, "a_star_pierces_the_darkness", "A Star Pierces the Darkness");
        put(map, ordered_songs, "your_love_is_a_drug", "Your Love is a Drug");
        put(map, ordered_songs, "through_the_storm_we_will_find_a_way", "Through the Storm We Will Find a Way");
        put(map, ordered_songs, "synthestitch", "Synthestitch");
        put(map, ordered_songs, "snowfall", "Snowfall");
        put(map, ordered_songs, "the_answer_lies_within", "The Answer Lies Within");
        put(map, ordered_songs, "dawn_approaches", "Dawn Approaches");
        put(map, ordered_songs, "with_renewed_hope_we_continue_forward", "With Renewed Hope, We Continue Forward");
        put(map, ordered_songs, "last_call", "Last Call");
        put(map, ordered_songs, "reminescence", "Reminiscence");
        put(map, ordered_songs, "believe_in_me_who_believes_in_you", "Believe in Me Who Believes in You");
        put(map, ordered_songs, "final_result", "Final Result");
        put(map, ordered_songs, "until_we_meet_again", "Until We Meet Again");
        put(map, ordered_songs, "digital_drive", "Digital Drive");
        put(map, ordered_songs, "safe_haven", "Safe Haven");
        str = new JSONObject(map).toString();
        setProperty("songs", str);
        setProperty("ordered_songs", new JSONArray(ordered_songs).toString());
        setProperty("awoo_endpoint", "https://boards.dangeru.us");
    }

    /**
     * helper function for putting things in both `songs` and `ordered_songs`
     * 
     * @param map           a map from song name to resource ID
     * @param ordered_songs the list of songs
     * @param id            The resource ID
     * @param s             The song name
     */
    private static void put(Map<String, String> map, @SuppressWarnings("TypeMayBeWeakened") List<String> ordered_songs,
            String id, String s) {
        map.put(s, id);
        ordered_songs.add(s);
    }

    /**
     * Gets a property, or an empty string if it wasn't set
     * 
     * @param key the key for the property
     * @return the value of that property or an empty string if it isn't set
     */
    public static String getProperty(String key) {
        return PreferenceManager.getDefaultSharedPreferences(United.getContext()).getString(key, "");
    }

    /**
     * sets a property, then writes the entire json file out to the disk
     * 
     * @param key   The property to set
     * @param value the value for that property
     */
    public static void setProperty(String key, String value) {
        // If we're playing or pausing the music, reload index.html
        // noinspection EqualsReplaceableByObjectsCall
        if (key.equals("is_playing")) {
            if (!getProperty(key).equalsIgnoreCase(value)) {
                // Thread.dumpStack();
                NotifierService.notify(NotifierService.NotificationType.RELOAD_INDEX);
            }
        }
        PreferenceManager.getDefaultSharedPreferences(United.getContext()).edit().putString(key, value).apply();
    }

    public static String[] getKeys() {
        Set<String> set = PreferenceManager.getDefaultSharedPreferences(United.getContext()).getAll().keySet();
        return set.toArray(new String[set.size()]);
    }

    /**
     * Resets all properties and forcibly closes the program to prevent them from
     * being rewritten
     * on the next setProperty call
     * 
     * @param fragman a fragment manager used to show an error message, if
     *                applicable
     */
    public static void resetAllAndExit(FragmentManager fragman) {
        try {
            for (String key : getKeys()) {
                setPropertySynchronous(key, "");
            }
            setPropertySynchronous("migrated", "reset");
            OutputStreamWriter writer = new OutputStreamWriter(
                    United.getContext().openFileOutput(CONFIG, Context.MODE_PRIVATE));
            writer.write("{}");
            writer.flush();
            writer.close();
            System.exit(0);
        } catch (Exception e) {
            GenericAlertDialogFragment.newInstance("Unexpected error - " + e, fragman);
        }
    }

    /**
     * Like setProperty but uses commit() instead of apply()
     * 
     * @param key
     * @param value
     */
    @SuppressLint("ApplySharedPref")
    private static void setPropertySynchronous(String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(United.getContext()).edit().putString(key, value).commit();
    }
}
