package com.driveembetter.proevolutionsoftware.driveembetter.services;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.driveembetter.proevolutionsoftware.driveembetter.boundary.MainFragmentActivity;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.PositionManager;

import static android.content.ContentValues.TAG;

/**
 * Created by Mattia Ponza on 04/09/2017.
 */

public class SwipeClosureHandler extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //stars self
        Intent myIntent = new Intent(this, SwipeClosureHandler.class);
        startService(myIntent);

        //TODO
        return null;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onTaskRemoved (Intent rootIntent){
        PositionManager.getInstance((MainFragmentActivity)getApplicationContext()).setUserUnavailable();
        this.stopSelf();
    }
}
