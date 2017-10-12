package com.driveembetter.proevolutionsoftware.driveembetter.utils;

/**
 * Created by matti on 16/08/2017.
 */

public class StringParser {

    private final static String TAG = StringParser.class.getSimpleName();

    public final static String itemSeparator = ";";
    public final static String subItemSeparator = "_";

    public static String getStringFromCoordinates(double latitude, double longitude) {
        return Double.toString(latitude) + ";" + Double.toString(longitude);
    }

    public static String[] getCoordinates(String coordinates) {
        return coordinates == null ? null : coordinates.split(";");
    }

    public static String trimString(String input) {
        return input.replaceAll("\\s", "");
    }

    public static String fromArrayToString(String[] array) {
        String string = "";
        if (array != null) {
            for (String s : array) {
                string = string.concat(s + StringParser.itemSeparator);
            }
        }

        return string;
    }
}
