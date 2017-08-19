package us.dangeru.united4.utils;

import org.json.JSONArray;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Niles on 8/18/17.
 */

public final class PropertiesSingleton {
    private static PropertiesSingleton singleton;
    private Map<String, String> properties;
    static {
        singleton = new PropertiesSingleton();
    }
    private PropertiesSingleton() {
        // TODO load from a file
        properties = new HashMap<>();
        properties.put("version_notes", "Version 4.0!\nTap for Patch Notes");
        properties.put("theme", "normal");
        properties.put("is_playing", "false");
        List<String> themes = Arrays.asList("normal", "dotted", "steam", "kira", "meme");
        String str = new JSONArray(themes).toString();
        properties.put("all_themes", str);
    }
    public static PropertiesSingleton get() {
        return singleton;
    }
    public String getProperty(String key) {
        return properties.get(key);
    }
    public void setProperty(String key, String value) {
        properties.put(key, value);
        // TODO save to a file
    }
}
