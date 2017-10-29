package com.proevolutionsoftware.driveembetter.entity;

import java.util.HashMap;
import java.util.Map;

import static com.proevolutionsoftware.driveembetter.constants.Constants.HOURS;

/**
 * Created by FabianaRossi94 on 10/09/2017.
 */

public class MeanDay {

    private final static String TAG = MeanDay.class.getSimpleName();

    private Map<Integer, Mean> meanOfDays;
    private boolean clearDay;
    private long timestamp;

    public MeanDay() {
        this.meanOfDays = new HashMap<Integer,Mean>(HOURS);
        this.clearDay = false;
        this.timestamp = System.currentTimeMillis();
    }



    public Map<Integer,Mean> getMap() {
        return this.meanOfDays;
    }

    public void setMeanOfDays(Map<Integer, Mean> meanOfDays) {
        this.meanOfDays = meanOfDays;
    }

    public void clear() {
        this.meanOfDays.clear();
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isClearDay() {
        return this.clearDay;
    }

    public void setClearDay(boolean clearDay) {
        this.clearDay = clearDay;
    }
}
