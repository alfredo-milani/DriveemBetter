package com.driveembetter.proevolutionsoftware.driveembetter.utils;

/**
 * Created by matti on 16/08/2017.
 */

public class StringParser {

    public String[] getCoordinates(String coordinates) {
        return coordinates.split(";");
     }

     public String trimString(String input) {
         return input.replaceAll("\\s","");
     }
}
