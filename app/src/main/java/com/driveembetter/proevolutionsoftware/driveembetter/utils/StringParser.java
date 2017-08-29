package com.driveembetter.proevolutionsoftware.driveembetter.utils;

/**
 * Created by matti on 16/08/2017.
 */

public class StringParser {
    private String coordinates;

    public StringParser(String coordinates) {
        this.coordinates = coordinates;
    }

    public String[] getCoordinates() {
        return this.coordinates.split(";");
     }
}
