package com.driveembetter.proevolutionsoftware.driveembetter.utils;

import android.util.Log;

import com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.Mean;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.MeanDay;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.MeanWeek;

import java.util.Map;

/**
 * Created by matti on 16/08/2017.
 */

public class StringParser {

    private final static String TAG = StringParser.class.getSimpleName();

    private final static String itemSeparator = ";";
    private final static String subItemSeparator = "_";

    public static String getStringFromCoordinates(double latitude, double longitude) {
        return Double.toString(latitude) + ";" + Double.toString(longitude);
    }

    public static String[] getCoordinates(String coordinates) {
        return coordinates == null ? null : coordinates.split(";");
    }

    public static String trimString(String input) {
        return input.replaceAll("\\s", "");
    }

    public static String getStringFromUserData() {
        String dataDaily = StringParser.getStringFromHashMap(MeanDay.getInstance().getMap(), Constants.HOURS);
        String dataWeekly = StringParser.getStringFromHashMap(MeanWeek.getInstance().getMap(), Constants.DAYS);
        return dataDaily.concat(dataWeekly);
    }

    private static String getStringFromHashMap(Map<Integer, Mean> map, int lenght) {
        String string = "";
        // Al posto di Constants.HOURS ci andrebbe il numero massimo
        for (int i = 0; i < lenght; ++i) {
            Mean mean = map.get(i);
            if (mean != null) {
                string = string.concat(String.format(
                        "%d" + subItemSeparator + "%d" + subItemSeparator + "%.1f" + subItemSeparator + "%d" + subItemSeparator + "%.1f" + itemSeparator,
                        i,
                        mean.getSampleSizeVelocity(),
                        mean.getSampleSumVelocity(),
                        mean.getSampleSizeAcceleration(),
                        mean.getSampleSumAcceleration()
                ));
            } else {
                string = string.concat(itemSeparator);
            }
        }
        Log.d(TAG, "STR - " + string);

        return string;
    }

    public static void setMapFromString(String data) {
        String[] items = data.split(StringParser.itemSeparator);

        try {
            MeanDay meanDay = MeanDay.getInstance();
            for (int i = 0; i < Constants.HOURS; ++i) {
                String[] values = items[i].split(StringParser.subItemSeparator);
                if (values.length != 5) {
                    continue;
                }

                Mean mean = new Mean(
                        Float.valueOf(values[4]),
                        Float.valueOf(values[2]),
                        Integer.valueOf(values[1]),
                        Integer.valueOf(values[3])
                );
                meanDay.getMap().put(Integer.valueOf(values[0]), mean);
            }

            MeanWeek meanWeek = MeanWeek.getInstance();
            for (int i = 0; i < Constants.DAYS; ++i) {
                String[] values = items[i].split(StringParser.subItemSeparator);
                if (values.length != 5) {
                    continue;
                }

                Mean mean = new Mean(
                        Float.valueOf(values[4]),
                        Float.valueOf(values[2]),
                        Integer.valueOf(values[1]),
                        Integer.valueOf(values[3])
                );
                meanWeek.getMap().put(Integer.valueOf(values[0]), mean);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }
}
