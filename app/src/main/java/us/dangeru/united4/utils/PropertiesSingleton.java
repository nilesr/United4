package us.dangeru.united4.utils;

import java.util.HashMap;
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
        properties = new HashMap<>();
        properties.put("version_notes", "Version 3.6!\nTap for Patch Notes");
        properties.put("theme", "normal");
        properties.put("is_playing", "false");
    }
    public static PropertiesSingleton get() {
        return singleton;
    }
    public String getProperty(String key) {
        return properties.get(key);
    }
    public void setProperty(String key, String value) {
        properties.put(key, value);
    }
}
