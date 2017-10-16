package com.driveembetter.proevolutionsoftware.driveembetter.utils;

/**
 * Created by Mattia on 16/10/2017.
 */

public class PointManager {

    //gravity acceleration
    final static float g =  9.8066f;


    //static bonus

    final static long INSURANCE_EXPIRED = -1000;

    final static long BASIC_SPEED_BONUS = 3;
    final static long GENERAL_OVERSPEED_MALUS = -100;
    final static float FIRST_OVERSPEED_COEFFICIENT = -2.5f;
    final static float SECOND_OVERSPEED_COEFFICIENT = -5;
    final static float THIRD_OVERSPEED_COEFFICIENT = -10;

    //they should be considered even in the case of deceleration
    final static long BASIC_ACCELERATION_BONUS = 5;
    final static float FIRST_OVERACCELERATION_COEFFICIENT = -2.5f;
    final static float SECOND_OVERACCELERATION_COEFFICIENT = -6;
    final static float THIRD_OVERACCELERATION_COEFFICIENT =-12;

    final static long FEEDBACK_BONUS_ONE = -500;
    final static long FEEDBACK_BONUS_TWO = -250;
    final static long FEEDBACK_BONUS_THREE = 0;
    final static long FEEDBACK_BONUS_FOUR = 250;
    final static long FEEDBACK_BONUS_FIVE = 500;


    //end static bonus

    //static acceleration bounds

    final static float FIRST_ACCELERATION_BOUND = 0.2f * g;
    final static float SECOND_ACCELERATION_BOUND = 0.35f * g;
    final static float THIRD_ACCELERATION_BOUND = 0.5f * g;

    //static deceleration bounds

    final static float FIRST_DECELERATION_BOUND = -0.7f * g;
    final static float SECOND_DECELERATION_BOUND = -0.85f * g;
    final static float THIRD_DECELERATION_BOUND = -g;

    //static over speed bounds

    final static float FIRST_OVERSPEED_BOUND = 0.1f;
    final static float SECOND_OVERSPEED_BOUND = 0.2f;
    final static float THIRD_OVERSPEED_BOUND = 0.3f;


    /**
     * this function update user points every time that is usefull
     * @param type what kind of characteristic we have to consider?
     * @param value the real value of the characteristic we are considering
     * @param boundValue the potential bound value that this characteristic may have
     *
     * note that: if potential bound is not required, it will be set to -1
     */
    public static void updatePoints(int type, double value, double boundValue) {
        switch (type) {
            //we are considering speed
            case 0:
                updateSpeedPoints(value, boundValue);
                break;
            //we are considering acceleration & deceleration
            case 1:
                updateAccelerationPoints(value);
                break;
            case 3:
                updateInsurancePoints();
                break;
            case 4:
                updateGeneralOverSpeed();
                break;
        }
    }

    /**
     *
     * @param feedback feedback sent from user
     * @param uid uid of selected user
     */
    public static void updatePoints(double feedback, String uid){
        switch ((int) feedback) {
            case 1:
                FirebaseDatabaseManager.updateUserPoints(FEEDBACK_BONUS_ONE, uid);
                break;
            case 2:
                FirebaseDatabaseManager.updateUserPoints(FEEDBACK_BONUS_TWO, uid);
                break;
            case 3:
                FirebaseDatabaseManager.updateUserPoints(FEEDBACK_BONUS_THREE, uid);
                break;
            case 4:
                FirebaseDatabaseManager.updateUserPoints(FEEDBACK_BONUS_FOUR, uid);
                break;
            case 5:
                FirebaseDatabaseManager.updateUserPoints(FEEDBACK_BONUS_FIVE, uid);

        }
    }


    private static void updateSpeedPoints(double speed, double maxSpeed) {
        if (maxSpeed == -1) {
            FirebaseDatabaseManager.updateUserPoints(BASIC_SPEED_BONUS);
            return;
        }
        if (speed <= maxSpeed) {
            FirebaseDatabaseManager.updateUserPoints(BASIC_SPEED_BONUS);
        } else {
            if (speed <= maxSpeed + maxSpeed * FIRST_OVERSPEED_BOUND)
                FirebaseDatabaseManager.updateUserPoints(BASIC_SPEED_BONUS * (long) FIRST_OVERSPEED_COEFFICIENT);
            if (speed > maxSpeed + maxSpeed * SECOND_OVERSPEED_BOUND && speed <= maxSpeed + maxSpeed * SECOND_OVERSPEED_BOUND)
                FirebaseDatabaseManager.updateUserPoints(BASIC_SPEED_BONUS * (long) SECOND_OVERSPEED_COEFFICIENT);
            if (speed > maxSpeed + maxSpeed * THIRD_OVERSPEED_BOUND)
                FirebaseDatabaseManager.updateUserPoints(BASIC_SPEED_BONUS * (long) THIRD_OVERSPEED_COEFFICIENT);
        }
    }

    private static void updateAccelerationPoints(double acceleration) {
        if (acceleration >= 0) {
            if (acceleration <= FIRST_ACCELERATION_BOUND)
                FirebaseDatabaseManager.updateUserPoints(BASIC_ACCELERATION_BONUS);
            if (acceleration > FIRST_ACCELERATION_BOUND && acceleration <= SECOND_ACCELERATION_BOUND)
                FirebaseDatabaseManager.updateUserPoints(BASIC_ACCELERATION_BONUS * (long) FIRST_OVERACCELERATION_COEFFICIENT);
            if (acceleration > SECOND_ACCELERATION_BOUND && acceleration <= THIRD_ACCELERATION_BOUND)
                FirebaseDatabaseManager.updateUserPoints(BASIC_ACCELERATION_BONUS * (long) SECOND_OVERACCELERATION_COEFFICIENT);
            if (acceleration > THIRD_ACCELERATION_BOUND)
                FirebaseDatabaseManager.updateUserPoints(BASIC_ACCELERATION_BONUS * (long) THIRD_OVERACCELERATION_COEFFICIENT);
        } else {
            if (acceleration >= FIRST_DECELERATION_BOUND)
                FirebaseDatabaseManager.updateUserPoints(BASIC_ACCELERATION_BONUS);
            if (acceleration < FIRST_DECELERATION_BOUND && acceleration >= SECOND_DECELERATION_BOUND)
                FirebaseDatabaseManager.updateUserPoints(BASIC_ACCELERATION_BONUS * (long) FIRST_OVERACCELERATION_COEFFICIENT);
            if (acceleration < SECOND_DECELERATION_BOUND && acceleration >= THIRD_DECELERATION_BOUND)
                FirebaseDatabaseManager.updateUserPoints(BASIC_ACCELERATION_BONUS * (long) SECOND_OVERACCELERATION_COEFFICIENT);
            if (acceleration < THIRD_DECELERATION_BOUND)
                FirebaseDatabaseManager.updateUserPoints(BASIC_ACCELERATION_BONUS * (long) THIRD_OVERACCELERATION_COEFFICIENT);
        }
    }


    private static void updateInsurancePoints() {
        FirebaseDatabaseManager.updateUserPoints(INSURANCE_EXPIRED);
    }

    private static void updateGeneralOverSpeed() {
        FirebaseDatabaseManager.updateUserPoints(GENERAL_OVERSPEED_MALUS);
    }
}
