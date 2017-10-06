package com.driveembetter.proevolutionsoftware.driveembetter.entity;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by FabianaRossi94 on 03/10/2017.
 */

public class MeanWeek {
    private static MeanWeek instance = null;
    private Map<Integer, Mean> meanOfWeek;
    private Date localDate;

    private MeanWeek() {
        this.meanOfWeek = new HashMap<Integer,Mean>();
        this.localDate = Calendar.getInstance().getTime();
    }

    public static final MeanWeek getInstance() {
        if (instance == null) {
            instance = new MeanWeek();
        }
        return instance;

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
}
