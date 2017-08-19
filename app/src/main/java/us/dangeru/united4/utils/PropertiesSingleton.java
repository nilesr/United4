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
    private static PropertiesSingleton singleton;
    private Map<String, String> properties;
    private static final String CONFIG = "united4_config.json";
    static {
        singleton = new PropertiesSingleton();
    }
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

    private void resetForAppStart() {
        properties.put("version_notes", "Version 4.0!\nTap for Patch Notes");
        properties.put("is_playing", "false");
        List<String> themes = Arrays.asList("normal", "dotted", "steam", "kira", "meme");
        String str = new JSONArray(themes).toString();
        properties.put("all_themes", str);
        @SuppressLint("UseSparseArrays") HashMap<Integer, String> map = new HashMap<>();
        map.put(R.raw.hopes_and_dreams, "Hopes and Dreams");
        map.put(R.raw.a_neon_glow_lights_the_way, "A Neon Glow Lights the Way");
        map.put(R.raw.welcome_to_va_11_hall_a, "Welcome To VA-11 HALL-A");
        map.put(R.raw.every_day_is_night, "Every Day is Night");
        map.put(R.raw.commencing_simulation, "Commencing Simulation");
        map.put(R.raw.drive_me_wild, "Drive Me Wild");
        map.put(R.raw.good_for_health_bad_for_education, "Good for Health, Bad for Education");
        map.put(R.raw.who_was_i, "Who Was I?");
        map.put(R.raw.troubling_news, "Troubling News");
        map.put(R.raw.a_gaze_that_invited_disaster, "A Gaze That Invited Disaster");
        map.put(R.raw.friendly_conversation, "Friendly Conversation");
        map.put(R.raw.youve_got_me, "You've Got Me");
        map.put(R.raw.umemoto, "Umemoto");
        map.put(R.raw.jc_eltons, "JC Elton's");
        map.put(R.raw.go_go_streaming_chan, "Go! Go! Streaming-chan!");
        map.put(R.raw.all_systems_go, "All Systems, Go!");
        map.put(R.raw.where_do_i_go_from_here, "Where Do I Go From Here?");
        map.put(R.raw.will_you_remember_me, "Will You Remember Me?");
        map.put(R.raw.everything_will_be_okay, "Everything Will Be Okay");
        map.put(R.raw.march_of_the_white_knights, "March of the White Knights");
        map.put(R.raw.a_rene, "A. Rene");
        map.put(R.raw.neo_avatar, "Neo Avatar");
        map.put(R.raw.those_who_dwell_in_the_shadows, "Those Who Dwell in The Shadows");
        map.put(R.raw.nightime_maneuvers, "Nighttime Manuvers");
        map.put(R.raw.a_star_pierces_the_darkness, "A Star Pierces the Darkness");
        map.put(R.raw.your_love_is_a_drug, "Your Love is a Drug");
        map.put(R.raw.through_the_storm_we_will_find_a_way, "Through the Storm We Will Find a Way");
        map.put(R.raw.synthestitch, "Synthestitch");
        map.put(R.raw.snowfall, "Snowfall");
        map.put(R.raw.the_answer_lies_within, "The Answer Lies Within");
        map.put(R.raw.dawn_approaches, "Dawn Approaches");
        map.put(R.raw.with_renewed_hope_we_continue_forward, "With Renewed Hope, We Continue Forward");
        map.put(R.raw.last_call, "Last Call");
        map.put(R.raw.reminescence, "Reminiscence");
        map.put(R.raw.believe_in_me_who_believes_in_you, "Believe in Me Who Believes in You");
        map.put(R.raw.final_result, "Final Result");
        map.put(R.raw.until_we_meet_again, "Until We Meet Again");
        map.put(R.raw.digital_drive, "Digital Drive");
        map.put(R.raw.safe_haven, "Safe Haven");
        HashMap<String, String> map2 = new HashMap<>();
        for (Map.Entry<Integer, String> integerStringEntry : map.entrySet()) {
            //noinspection CallToNumericToString
            map2.put(integerStringEntry.getKey().toString(), integerStringEntry.getValue());
        }
        str = new JSONObject(map2).toString();
        properties.put("songs", str);
    }

    public static PropertiesSingleton get() {
        return singleton;
    }
    public String getProperty(String key) {
        return properties.get(key);
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
