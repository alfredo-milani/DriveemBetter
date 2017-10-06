package com.driveembetter.proevolutionsoftware.driveembetter.events;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.driveembetter.proevolutionsoftware.driveembetter.utils.FirebaseDatabaseManager;

import static com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants.UNAVAILABLE;

/**
 * Created by alfredo on 14/09/17.
 */

public class ShutdownReceiver extends BroadcastReceiver {

    private final static String TAG = ShutdownReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Turning off smartphone");
        FirebaseDatabaseManager.manageUserInformations(UNAVAILABLE);
        FirebaseDatabaseManager.managePositionAvailability(UNAVAILABLE);
    }
}
