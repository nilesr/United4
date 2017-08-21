package us.dangeru.launcher.utils;

import android.os.Bundle;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * A map that can be put in a parcelable, used to store session variables in MainActivity
 */

public class ParcelableMap {
    // delegated object
    private final Map<String, String> map = new HashMap<>();
    // puts the shit in a bundle
    public Parcelable parcel() {
        Bundle b = new Bundle();
        for (String i : map.keySet()) {
            b.putString(i, get(i));
        }
        return b;
    }
    // takes the shit out of the bundle
    public static ParcelableMap fromParcel(Parcelable p) {
        ParcelableMap res = new ParcelableMap();
        if (!(p instanceof Bundle)) {
            throw new IllegalArgumentException();
        }
        //noinspection TypeMayBeWeakened
        Bundle b = (Bundle) p;
        for (String i : b.keySet()) {
            res.map.put(i, b.getString(i));
        }
        return res;
    }

    // regular hashmap method
    public String get(String key) {
        return map.get(key);
    }

    // regular hashmap method
    public void put(String key, String value) {
        map.put(key, value);
    }
}
