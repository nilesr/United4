package us.dangeru.united4.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.JsonReader;
import android.util.JsonWriter;
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

import us.dangeru.united4.R;
import us.dangeru.united4.application.United;

/**
 * Created by Niles on 8/18/17.
 */

public final class PropertiesSingleton {
    private static final String TAG = PropertiesSingleton.class.getSimpleName();
    private static final String CONFIG = "united4_config.json";
    private static PropertiesSingleton singleton;

    static {
        singleton = new PropertiesSingleton();
    }

    private Map<String, String> properties;

    private PropertiesSingleton() {
        properties = new HashMap<>();
        try {
            JsonReader reader = new JsonReader(new InputStreamReader(United.getContext().openFileInput(CONFIG)));
            reader.beginObject();
            int read = 0;
            while (reader.hasNext()) {
                read++;
                Log.i(TAG, "Read a prop");
                properties.put(reader.nextName(), reader.nextString());
            }
            resetForAppStart();
            if (read > 0) {
                return;
            }
        } catch (Exception ignored) {
            //
        }
        properties.put("starting_music", "true");
        properties.put("theme", "normal");
        properties.put("looping", "false");
        properties.put("shuffle", "false");
        properties.put("current_song", "");
        resetForAppStart();
    }

    public static PropertiesSingleton get() {
        return singleton;
    }

    private void resetForAppStart() {
        properties.put("version_notes", "Version 4.0.2!\nTap for Patch Notes");
        properties.put("is_playing", "false");
        List<String> themes = Arrays.asList("normal", "dotted", "steam", "kira", "meme");
        String str = new JSONArray(themes).toString();
        properties.put("all_themes", str);
        HashMap<String, String> map = new HashMap<>();
        ArrayList<String> ordered_songs = new ArrayList<>();
        put(map, ordered_songs, R.raw.hopes_and_dreams, "Hopes and Dreams");
        put(map, ordered_songs, R.raw.a_neon_glow_lights_the_way, "A Neon Glow Lights the Way");
        put(map, ordered_songs, R.raw.welcome_to_va_11_hall_a, "Welcome To VA-11 HALL-A");
        put(map, ordered_songs, R.raw.every_day_is_night, "Every Day is Night");
        put(map, ordered_songs, R.raw.commencing_simulation, "Commencing Simulation");
        put(map, ordered_songs, R.raw.drive_me_wild, "Drive Me Wild");
        put(map, ordered_songs, R.raw.good_for_health_bad_for_education, "Good for Health, Bad for Education");
        put(map, ordered_songs, R.raw.who_was_i, "Who Was I?");
        put(map, ordered_songs, R.raw.troubling_news, "Troubling News");
        put(map, ordered_songs, R.raw.a_gaze_that_invited_disaster, "A Gaze That Invited Disaster");
        put(map, ordered_songs, R.raw.friendly_conversation, "Friendly Conversation");
        put(map, ordered_songs, R.raw.youve_got_me, "You've Got Me");
        put(map, ordered_songs, R.raw.umemoto, "Umemoto");
        put(map, ordered_songs, R.raw.jc_eltons, "JC Elton's");
        put(map, ordered_songs, R.raw.go_go_streaming_chan, "Go! Go! Streaming-chan!");
        put(map, ordered_songs, R.raw.all_systems_go, "All Systems, Go!");
        put(map, ordered_songs, R.raw.where_do_i_go_from_here, "Where Do I Go From Here?");
        put(map, ordered_songs, R.raw.will_you_remember_me, "Will You Remember Me?");
        put(map, ordered_songs, R.raw.everything_will_be_okay, "Everything Will Be Okay");
        put(map, ordered_songs, R.raw.march_of_the_white_knights, "March of the White Knights");
        put(map, ordered_songs, R.raw.a_rene, "A. Rene");
        put(map, ordered_songs, R.raw.neo_avatar, "Neo Avatar");
        put(map, ordered_songs, R.raw.those_who_dwell_in_the_shadows, "Those Who Dwell in The Shadows");
        put(map, ordered_songs, R.raw.nightime_maneuvers, "Nighttime Manuvers");
        put(map, ordered_songs, R.raw.a_star_pierces_the_darkness, "A Star Pierces the Darkness");
        put(map, ordered_songs, R.raw.your_love_is_a_drug, "Your Love is a Drug");
        put(map, ordered_songs, R.raw.through_the_storm_we_will_find_a_way, "Through the Storm We Will Find a Way");
        put(map, ordered_songs, R.raw.synthestitch, "Synthestitch");
        put(map, ordered_songs, R.raw.snowfall, "Snowfall");
        put(map, ordered_songs, R.raw.the_answer_lies_within, "The Answer Lies Within");
        put(map, ordered_songs, R.raw.dawn_approaches, "Dawn Approaches");
        put(map, ordered_songs, R.raw.with_renewed_hope_we_continue_forward, "With Renewed Hope, We Continue Forward");
        put(map, ordered_songs, R.raw.last_call, "Last Call");
        put(map, ordered_songs, R.raw.reminescence, "Reminiscence");
        put(map, ordered_songs, R.raw.believe_in_me_who_believes_in_you, "Believe in Me Who Believes in You");
        put(map, ordered_songs, R.raw.final_result, "Final Result");
        put(map, ordered_songs, R.raw.until_we_meet_again, "Until We Meet Again");
        put(map, ordered_songs, R.raw.digital_drive, "Digital Drive");
        put(map, ordered_songs, R.raw.safe_haven, "Safe Haven");
        str = new JSONObject(map).toString();
        properties.put("songs", str);
        properties.put("ordered_songs", new JSONArray(ordered_songs).toString());
    }

    private static void put(Map<String, String> map, @SuppressWarnings("TypeMayBeWeakened") List<String> ordered_songs, int id, String s) {
        map.put(s, Integer.toString(id));
        ordered_songs.add(s);
    }

    public String getProperty(String key) {
        String res = properties.get(key);
        if (res == null) return "";
        return res;
    }

    public void setProperty(String key, String value) {
        properties.put(key, value);
        try {
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(United.getContext().openFileOutput(CONFIG, Context.MODE_PRIVATE)));
            writer.beginObject();
            for (Map.Entry<String, String> i : properties.entrySet()) {
                Log.i(TAG, "Writing prop " + i.getKey());
                writer.name(i.getKey());
                writer.value(i.getValue());
            }
            writer.endObject();
            writer.close();
        } catch (Exception ignored) {
            //
        }
    }
}
