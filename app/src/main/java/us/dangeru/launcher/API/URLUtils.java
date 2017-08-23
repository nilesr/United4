package us.dangeru.launcher.API;

import android.util.Pair;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import us.dangeru.launcher.application.United;
import us.dangeru.launcher.utils.P;

/**
 * Created by Niles on 8/22/17.
 */

public final class URLUtils {
    public static Pair<String, Integer> isThread(String url) {
        url = url.replace(P.get("awoo_endpoint"), "");
        if (United.boards == null) return null;
        for (String board : United.boards) {
            Pattern pattern = Pattern.compile("^/" + board + "/thread/(\\d+)/?");
            Matcher matcher = pattern.matcher(url);
            if (matcher.matches()) {
                return new Pair<>(board, Integer.valueOf(matcher.group(1)));
            }
        }
        return null;
    }
    public static String isBoard(String url) {
        url = url.replace(P.get("awoo_endpoint"), "");
        if (United.boards == null) return null;
        for (String board : United.boards) {
            if (url.matches("^/" + board + "/?")) {
                return board;
            }
        }
        return null;
    }
    public static String isIpList(String url) {
        url = url.replace(P.get("awoo_endpoint"), "");
        Pattern pattern = Pattern.compile("^/ip/(.*)/?");
        Matcher matcher = pattern.matcher(url);
        if (matcher.matches()) return matcher.group(1);
        return null;
    }
    private URLUtils() {}
}
