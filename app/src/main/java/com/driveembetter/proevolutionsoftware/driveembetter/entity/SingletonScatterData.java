package com.driveembetter.proevolutionsoftware.driveembetter.entity;

/**
 * Created by FabianaRossi94 on 30/08/2017.
 */

import com.github.mikephil.charting.data.ScatterDataSet;

import java.util.ArrayList;

/**
 * Created by FabianaRossi94 on 30/08/2017.
 */

public  class SingletonScatterData {
    private static SingletonScatterData instance = null;
    private ScatterDataSet data ;
    private boolean valid;
    private ArrayList<String> xVals;

    private SingletonScatterData() {
        this.valid = false;
        this.xVals = new ArrayList<>();
    }
    public static final SingletonScatterData getInstance() {
        if (instance == null) {
            instance = new SingletonScatterData();
        }
        return instance;

    }

    public ArrayList<String> getxVals() {
        return xVals;
    }

    public void setxVals(ArrayList<String> xVals) {
        this.xVals = xVals;
    }

    public ScatterDataSet getData() {
        return data;
    }

    public void setData(ScatterDataSet data) {
        this.data = data;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}

