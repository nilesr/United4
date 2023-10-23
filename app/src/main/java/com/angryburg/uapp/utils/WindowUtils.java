package com.angryburg.uapp.utils;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Build;

/**
 * Android 5+ only, changes the window bar color to match the toolbar color
 * using the window_bar_color parameter
 */

public final class WindowUtils {
    private WindowUtils() {
    }

    @SuppressWarnings("SameParameterValue")
    private static int min(int a, int b) {
        return a > b ? b : a;
    }

    /**
     * Sets the window bar color to match the top bar color if applicable
     * 
     * @param act the activity to pull the window from
     */
    @SuppressWarnings("LiteralAsArgToStringEquals")
    public static void updateWindowBarColor(AppCompatActivity act) {
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
            // act.getWindow().setStatusBarColor(P.getColor("toolbar_color"));
            int color = P.getColor("toolbar_color");
            int r = color & 0xFF;
            // noinspection UnnecessaryParentheses
            int g = (color >> 8) & 0xFF;
            // noinspection UnnecessaryParentheses
            int b = (color >> 16) & 0xFF;
            // noinspection NumericCastThatLosesPrecision
            r = min((int) (r * factor), 0xFF);
            // noinspection
            // AssignmentReplaceableWithOperatorAssignment,NumericCastThatLosesPrecision
            g = min((int) (g * factor), 0xFF);
            // noinspection
            // AssignmentReplaceableWithOperatorAssignment,NumericCastThatLosesPrecision
            b = min((int) (b * factor), 0xFF);
            color = 0xFF000000 + r + (g << 8) + (b << 16);
            act.getWindow().setStatusBarColor(color);
        }
    }
}
