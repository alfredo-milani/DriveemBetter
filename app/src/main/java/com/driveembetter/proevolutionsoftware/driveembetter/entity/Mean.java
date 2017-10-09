package com.driveembetter.proevolutionsoftware.driveembetter.entity;

/**
 * Created by FabianaRossi94 on 10/09/2017.
 */

public class Mean {
    private float sampleSumVelocity, sampleSumAcceleration;
    private int sampleSizeVelocity, sampleSizeAcceleration;

    public Mean() {
        this.sampleSumVelocity = 0;
        this.sampleSizeVelocity = 0;
        this.sampleSizeAcceleration = 0;
        this.sampleSumAcceleration = 0;
    }

    public Mean(float sampleSumAcceleration, float sampleSumVelocity, int sampleSizeVelocity, int sampleSizeAcceleration) {
        this.sampleSumVelocity = sampleSumVelocity;
        this.sampleSizeVelocity = sampleSizeVelocity;
        this.sampleSizeAcceleration = sampleSizeAcceleration;
        this.sampleSumAcceleration = sampleSumAcceleration;
    }

    public float getSampleSumVelocity() {
        return sampleSumVelocity;
    }

    public void setSampleSumVelocity(float sampleSumVelocity) {
        this.sampleSumVelocity = sampleSumVelocity + this.sampleSumVelocity;
    }

    public float getSampleSumAcceleration() {
        return sampleSumAcceleration;
    }

    public void setSampleSumAcceleration(float sampleSumAcceleration) {
        this.sampleSumAcceleration = sampleSumAcceleration + this.sampleSumAcceleration;
    }

    public int getSampleSizeVelocity() {
        return sampleSizeVelocity;
    }

    public void setSampleSizeVelocity() {
        this.sampleSizeVelocity = this.sampleSizeVelocity + 1;
    }

    public int getSampleSizeAcceleration() {
        return sampleSizeAcceleration;
    }

    public void setSampleSizeAcceleration() {
        this.sampleSizeAcceleration = sampleSizeAcceleration + 1;
    }
}