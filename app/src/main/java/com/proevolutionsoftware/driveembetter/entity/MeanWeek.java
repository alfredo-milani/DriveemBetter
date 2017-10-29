package com.proevolutionsoftware.driveembetter.entity;

import java.util.HashMap;
import java.util.Map;

import static com.proevolutionsoftware.driveembetter.constants.Constants.DAYS;

/**
 * Created by FabianaRossi94 on 03/10/2017.
 */

public class MeanWeek {

    private Map<Integer, Mean> meanOfWeek;
    private boolean clearWeek;
    private long timestamp;

    public MeanWeek() {
        this.meanOfWeek = new HashMap<Integer,Mean>(DAYS);
        this.clearWeek = false;
        this.timestamp = System.currentTimeMillis();
    }



    public Map<Integer,Mean> getMap() {
        return this.meanOfWeek;
    }

    public void clear() {
        this.meanOfWeek.clear();
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isClearWeek() {
        return this.clearWeek;
    }

    public void setClearWeek(boolean clearWeek) {
        this.clearWeek = clearWeek;
    }
}
