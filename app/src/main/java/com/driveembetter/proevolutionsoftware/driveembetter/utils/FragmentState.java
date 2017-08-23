package com.driveembetter.proevolutionsoftware.driveembetter.utils;

import android.app.Application;

import com.driveembetter.proevolutionsoftware.driveembetter.FirebaseChatMainApp;

/**
 * Created by matti on 23/08/2017.
 */

public class FragmentState extends Application {
    private static boolean saveMeIsOpen = false;

    public static boolean isSaveMeIsOpen() {
        return saveMeIsOpen;
    }

    public static void setSaveMeIsOpen(boolean saveMeIsOpen) {
        FragmentState.saveMeIsOpen = saveMeIsOpen;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}