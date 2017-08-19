package us.dangeru.united4.utils;

/**
 * Created by Niles on 8/18/17.
 */

public class PropertiesSingleton {
    private static PropertiesSingleton singleton;
    static {
        singleton = new PropertiesSingleton();
    }
    private PropertiesSingleton() {
    }
    public static PropertiesSingleton get() {
        return singleton;
    }
    public String getProperty(String key) {
        return null;
    }
    public void setProperty(String key, String value) {

    }
}
