package com.driveembetter.proevolutionsoftware.driveembetter.utils;

import com.driveembetter.proevolutionsoftware.driveembetter.entity.MeanDay;

/**
 * Created by matti on 16/08/2017.
 */

public class StringParser {

    public static String getStringFromCoordinates(double latitude, double longitude) {
        return Double.toString(latitude) + ";" + Double.toString(longitude);
    }

    public static String[] getCoordinates(String coordinates) {
        return coordinates == null ? null : coordinates.split(";");
    }

    public static String trimString(String input) {
        return input.replaceAll("\\s", "");
    }

    public static String createMeanDaySrtrng(MeanDay meanDay) {
        return "";
    }
}
