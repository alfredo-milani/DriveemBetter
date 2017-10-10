package com.driveembetter.proevolutionsoftware.driveembetter.entity;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by FabianaRossi94 on 10/09/2017.
 */

public class MeanDay {

    private Map<Integer, Mean> meanOfDays;
    private Date localDate;

    public MeanDay() {
        this.meanOfDays = new HashMap<Integer,Mean>();
        this.localDate = Calendar.getInstance().getTime();
    }



    public Map<Integer,Mean> getMap() {
        return this.meanOfDays;
    }

    public void clear() {
        this.meanOfDays.clear();
    }

    public Date getLocalDate() {
        return localDate;
    }

    public void setLocalDate(Date localDate) {
        this.localDate = localDate;
    }
}
