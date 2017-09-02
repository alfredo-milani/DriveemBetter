package com.driveembetter.proevolutionsoftware.driveembetter.utils;

/**
 * Created by matti on 16/08/2017.
 */

public class StringParser {

    public static String[] getCoordinates(String coordinates) {
        return coordinates.split(";");
     }

     public static String trimString(String input) {
         return input.replaceAll("\\s","");
     }
}
