package com.driveembetter.proevolutionsoftware.driveembetter.utils;

import android.util.Log;

import com.driveembetter.proevolutionsoftware.driveembetter.entity.Mean;

import java.util.Map;

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

    public static String getStringFromHashMap(Map<Integer, Mean> map) {
        Log.d("DIO", "SIZE: " + map.size());
        String string = "";
        for (int i = 0; i < map.size(); ++i) {
            Mean mean = map.get(i);
            if (mean != null) {
                string = string.concat(String.format(
                        "%d_%.3f_%d_%.3f;",
                        mean.getSampleSizeVelocity(),
                        mean.getSampleSumVelocity(),
                        mean.getSampleSizeAcceleration(),
                        mean.getSampleSumAcceleration()
                ));
            } else {
                string.concat(";");
            }
        }
        Log.d("DIO", "STR: " + string);

        return string;
    }

    /*
    public static Map<Integer, Mean> getMapFromString(String data) {
        Map<Integer, Mean> map = new Map
    }
    */
}
