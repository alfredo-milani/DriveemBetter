package com.proevolutionsoftware.driveembetter.entity;

/**
 * Created by FabianaRossi94 on 10/09/2017.
 */

public class Mean {

    // TODO da implementare con Reflection
    public final static String SUM_VELOCITY = "sampleSumVelocity";
    public final static String SUM_ACCELERATION = "sampleSumAcceleration";
    public final static String SIZE_VELOCITY = "sampleSizeVelocity";
    public final static String SIZE_ACCELERATION = "sampleSizeAcceleration";

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
        this.sampleSumVelocity += sampleSumVelocity;
    }

    public float getSampleSumAcceleration() {
        return sampleSumAcceleration;
    }

    public void setSampleSumAcceleration(float sampleSumAcceleration) {
        this.sampleSumAcceleration += sampleSumAcceleration;
    }

    public int getSampleSizeVelocity() {
        return sampleSizeVelocity;
    }

    public void setSampleSizeVelocity() {
        this.sampleSizeVelocity = ++this.sampleSizeVelocity;
    }

    public int getSampleSizeAcceleration() {
        return sampleSizeAcceleration;
    }

    public void setSampleSizeAcceleration() {
        this.sampleSizeAcceleration = ++sampleSizeAcceleration;
    }
}
