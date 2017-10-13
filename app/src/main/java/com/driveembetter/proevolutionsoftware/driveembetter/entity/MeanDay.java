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
    private boolean clearDay;
    private Date localDate;

    public MeanDay() {
        this.meanOfDays = new HashMap<Integer,Mean>();
        this.clearDay = false;
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

    public boolean isClearDay() {
        return this.clearDay;
    }

    public void setClearDay(boolean clearDay) {
        this.clearDay = clearDay;
    }
}
