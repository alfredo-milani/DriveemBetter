package com.driveembetter.proevolutionsoftware.driveembetter.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


/**
 * Created by Mattia Ponza on 04/09/2017.
 */

public class SwipeClosureHandler extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (startId == START_NOT_STICKY) {
            Log.d("DEBUG", "Service was stopped and automatically restarted by the system. Stopping self now.");
            stopSelf();
        }
        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved (Intent rootIntent){
        stopSelf();
    }
}
