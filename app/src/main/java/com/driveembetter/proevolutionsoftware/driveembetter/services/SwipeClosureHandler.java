package com.driveembetter.proevolutionsoftware.driveembetter.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.driveembetter.proevolutionsoftware.driveembetter.boundary.activity.MainFragmentActivity;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.PositionManager;


/**
 * Created by Mattia Ponza on 04/09/2017.
 */

public class SwipeClosureHandler extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
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
        super.onTaskRemoved(rootIntent);
    }
}
