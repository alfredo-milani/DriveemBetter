package com.proevolutionsoftware.driveembetter.utils;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by Mattia on 29/10/2017.
 */

public class DailyMetrometer {


    public static void firstFeedbackTime(Activity activity) {
        SharedPreferences pref = activity.getSharedPreferences("feedback_preferences", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong("first_feedback_time", System.currentTimeMillis());
        editor.putInt("feedback_number", 0);
        editor.apply();
    }

    public static int checkFeedbackNumber(Activity activity) {
        SharedPreferences pref = activity.getSharedPreferences("feedback_preferences", 0);
        return pref.getInt("feedback_number", 0);
    }

    public static void updateFeedbackNumber(Activity activity) {
        SharedPreferences pref = activity.getSharedPreferences("feedback_preferences", 0);
        SharedPreferences.Editor editor = pref.edit();
        int feedbackNumber = pref.getInt("feedback_number", 0);
        editor.putInt("feedback_number", feedbackNumber + 1);
        editor.apply();
    }

    public static Boolean dayExpired(Activity activity) {

        Long dayMillis = Long.valueOf(24*60*60*1000);

        SharedPreferences pref = activity.getSharedPreferences("feedback_preferences", 0);
        Long firstFeedbackTime = pref.getLong("first_feedback_time", 0);
        Long tDay = System.currentTimeMillis();
        if (tDay - firstFeedbackTime >= dayMillis) {
            return true;
        }
        return false;
    }

}
