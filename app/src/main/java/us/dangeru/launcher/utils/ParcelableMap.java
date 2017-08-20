package us.dangeru.launcher.utils;

import android.os.Bundle;
import android.os.Parcelable;

import java.util.HashMap;

/**
 * Created by Niles on 8/19/17.
 */

public class ParcelableMap extends HashMap<String, String> {
    public Parcelable parcel() {
        Bundle b = new Bundle();
        for (String i : keySet()) {
            b.putString(i, get(i));
        }
        return b;
    }
    public static ParcelableMap fromParcel(Parcelable p) {
        ParcelableMap res = new ParcelableMap();
        if (!(p instanceof Bundle)) {
            throw new IllegalArgumentException();
        }
        //noinspection TypeMayBeWeakened
        Bundle b = (Bundle) p;
        for (String i : b.keySet()) {
            res.put(i, b.getString(i));
        }
        return res;
    }
}
