package com.driveembetter.proevolutionsoftware.driveembetter.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matti on 16/08/2017.
 */

public class StringParser {
    String coordinates;

    public StringParser(String coordinates) {
        this.coordinates = coordinates;
    }

    public String[] getCoordinates() {
        String[] tokens = coordinates.split(";");
        return tokens;
     }
}
