package us.dangeru.launcher.utils;

/**
 * Created by Niles on 8/21/17.
 */

public final class P {
    private P() {
    }

    public static String get(String prop) {
        return PropertiesSingleton.get().getProperty(prop);
    }
    public static void set(String prop, String value) {
        PropertiesSingleton.get().setProperty(prop, value);
    }
    public static boolean getBool(String prop) {
        return "true".equalsIgnoreCase(get(prop));
    }
    public static void toggle(String prop) {
        //noinspection CallToNumericToString
        set(prop, Boolean.valueOf(!getBool(prop)).toString());
    }
}
