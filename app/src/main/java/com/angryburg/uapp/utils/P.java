package com.angryburg.uapp.utils;

/**
 * Shortcuts for accessing PropertiesSingleton
 */

public final class P {
    private P() {
    }

    /**
     * @see PropertiesSingleton#getProperty(String)
     * @param prop the property to get
     * @return the value of that property
     */
    public static String get(String prop) {
        return PropertiesSingleton.getProperty(prop);
    }
    /**
     * @see PropertiesSingleton#setProperty(String, String)
     * @param prop the property to set
     * @param value the value to set it to
     */
    public static void set(String prop, String value) {
        PropertiesSingleton.setProperty(prop, value);
    }

    /**
     * Gets the property as a boolean value, defaulting to false
     * @param prop the property to read
     * @return the value of that property as a boolean
     */
    public static boolean getBool(String prop) {
        return "true".equalsIgnoreCase(get(prop));
    }

    /**
     * Gets the property as a string value, defaulting to false
     * @param prop the property to read
     * @return the value of that property as a human-readable boolean
     */
    public static String getReadable(String prop) {
        return getBool(prop) ? "enabled" : "disabled";
    }

    /**
     * Toggles a boolean property
     * @param prop the property to toggle
     */
    public static void toggle(String prop) {
        //noinspection CallToNumericToString
        set(prop, Boolean.valueOf(!getBool(prop)).toString());
    }

    /**
     * Gets a color from its key or defaults to Topaz
     * @param prop the key of the property
     * @return the associated value from PropertiesSingleton, or Topaz
     */
    public static int getColor(String prop) {
        String val = get(prop);
        int _default = 0xFF837D87; // Topaz
        if (val.isEmpty()) return _default;
        try {
            return Integer.valueOf(val);
        } catch (Exception e) {
            e.printStackTrace();
            return _default;
        }
    }

    public static int getMinutes() {
        //if (true) return 1; // TEMP DEBUG STOPSHIP
        if (P.get("alarm_interval").isEmpty() || P.get("alarm_interval").equalsIgnoreCase("HOUR")) {
            return 60;
        } else if (P.get("alarm_interval").equalsIgnoreCase("HALF_DAY")) {
            return 60 * 12;
        } else {
            return 60 * 24;
        }
    }

    /*
    public static int getInt(String key, int _default) {
        try {
            return Integer.valueOf(get(key));
        } catch (NumberFormatException e) {
            return _default;
        }
    }
    */
}
