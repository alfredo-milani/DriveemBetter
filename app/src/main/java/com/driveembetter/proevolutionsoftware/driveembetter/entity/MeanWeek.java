package com.driveembetter.proevolutionsoftware.driveembetter.entity;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by FabianaRossi94 on 03/10/2017.
 */

public class MeanWeek {

    private Map<Integer, Mean> meanOfWeek;
    private boolean clearWeek;
    private Date localDate;

    public MeanWeek() {
        this.meanOfWeek = new HashMap<Integer,Mean>();
        this.clearWeek = false;
        this.localDate = Calendar.getInstance().getTime();
    }



    public Map<Integer,Mean> getMap() {
        return this.meanOfWeek;
    }

    public void clear() {
        this.meanOfWeek.clear();
    }

    public Date getLocalDate() {
        return localDate;
    }

    public void setLocalDate(Date localDate) {
        this.localDate = localDate;
    }

    public boolean isClearWeek() {
        return this.clearWeek;
    }

    public void setClearWeek(boolean clearWeek) {
        this.clearWeek = clearWeek;
    }
}
