package com.driveembetter.proevolutionsoftware.driveembetter.utils;

/**
 * Created by matti on 09/10/2017.
 */

public class NumberManager {

    public static Double round(Double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
