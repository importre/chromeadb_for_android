package io.github.importre.android.chromeadb;

import android.text.TextUtils;

import java.util.regex.Pattern;

public class Command {
    public static final String sCmdMove = "move ";

    private static final Pattern sCoordPattern = Pattern.compile("\\d+,\\d+(,\\d+,\\d+)*");

    /**
     * Returns coordinate list
     *
     * @param s line
     * @return returns coordinate list if command is move, otherwise returns null
     */
    public static String getCoordinates(String s) {
        if (!TextUtils.isEmpty(s)) {
            s = s.trim();
            if (s.startsWith(Command.sCmdMove)) {
                s = s.substring(Command.sCmdMove.length());
                if (Command.sCoordPattern.matcher(s).matches()) {
                    return s;
                }
            }
        }
        return null;
    }
}
