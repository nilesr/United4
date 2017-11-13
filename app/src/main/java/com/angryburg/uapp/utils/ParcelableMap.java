package com.angryburg.uapp.utils;

import android.os.Bundle;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * A map that can be put in a parcelable, used to store session variables in MainActivity
 */

public class ParcelableMap {
    /**
     * Delegated object
     */
    private final Map<String, String> map = new HashMap<>();

    /**
     * Puts the keys and values in a bundle
     * @return a parcelable representation of the map
     */
    public Parcelable parcel() {
        Bundle b = new Bundle();
        for (String i : map.keySet()) {
            b.putString(i, get(i));
        }
        return b;
    }
    /**
     * Factory method to read a ParcelableMap from a bundle
     * @param p the bundle to read from
     * @return the reconstructed ParcelableMap object
     */
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
