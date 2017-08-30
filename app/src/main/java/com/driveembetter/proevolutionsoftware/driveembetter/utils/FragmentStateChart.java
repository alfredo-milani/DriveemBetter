package com.driveembetter.proevolutionsoftware.driveembetter.utils;

import android.app.Application;

/**
 * Created by FabianaRossi94 on 31/08/2017.
 */

public class FragmentStateChart extends Application {
    private static boolean chartIsOpen = false;

    public static boolean ischartIsOpen() {
        return chartIsOpen;
    }

    public static void setchartIsOpen(boolean chartIsOpen) {
        FragmentStateChart.chartIsOpen = chartIsOpen;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
