package com.driveembetter.proevolutionsoftware.driveembetter.entity;

/**
 * Created by FabianaRossi94 on 10/09/2017.
 */

public class Mean {
    private float sampleSum;
    private int sampleSize;

    public Mean() {
        this.sampleSum = 0;
        this.sampleSize = 0;
    }

    public float getSampleSum() {
        return sampleSum;
    }

    public void setSampleSum(float value) {
        this.sampleSum = value + this.sampleSum;
    }

    public int getSampleSize() {
        return sampleSize;
    }

    public void setSampleSize() {
        this.sampleSize++;
    }
}
