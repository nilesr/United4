package com.angryburg.uapp.utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;

/**
 * Created by Niles on 12/3/17.
 */

public final class WindowUtils {
    private WindowUtils() {
    }

    /**
     * Sets the window bar color to match the top bar color if applicable
     * @param act the activity to pull the window from
     */
    public static void updateWindowBarColor(Activity act) {
        if (Build.VERSION.SDK_INT >= 21) {
            if (P.get("window_bar_color").equalsIgnoreCase("false")) {
                act.getWindow().setStatusBarColor(Color.BLACK);
            } else {
                double factor;
                if (P.get("window_bar_color").equalsIgnoreCase("+25")) {
                    factor = 1.25;
                } else if (P.get("window_bar_color").equalsIgnoreCase("match")) {
                    factor = 1;
                } else {
                    factor = 0.75;
                }
                //act.getWindow().setStatusBarColor(P.getColor("toolbar_color"));
                int color = P.getColor("toolbar_color");
                int r = color & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = (color >> 16) & 0xFF;
                r = (int) (r * factor);
                g = (int) (g * factor);
                b = (int) (b * factor);
                color = 0xFF000000 + r + (g << 8) + (b << 16);
                act.getWindow().setStatusBarColor(color);
            }
        }
    }
}
