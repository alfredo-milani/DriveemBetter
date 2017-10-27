package com.driveembetter.proevolutionsoftware.driveembetter.threads;

import android.content.Context;
import android.util.Log;

import com.driveembetter.proevolutionsoftware.driveembetter.utils.FirebaseDatabaseManager;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.NetworkConnectionUtil;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.PositionManager;

/**
 * Created by alfredo on 08/10/17.
 */

public class SaveUserStatisticsRunnable 
        implements Runnable {
    
    private final static String TAG = SaveUserStatisticsRunnable.class.getSimpleName();

    // Resources
    private int timeToSleep; // seconds
    private Context context;

    public SaveUserStatisticsRunnable(Context context) {
        this.context = context;
        // Default value 2 minutes
        this.timeToSleep = 60 * 2;
    }

    public SaveUserStatisticsRunnable(Context context, int timeToSleep) {
        this(context);
        this.timeToSleep = timeToSleep;
    }



    @Override
    public void run() {
        do {
            if (PositionManager.isStatisticsToPush() &&
                    NetworkConnectionUtil.isConnectedToInternet(this.context) &&
                    PositionManager.getInitialSpeed() != -1) {
                PositionManager.setStatisticsToPush(false);
                FirebaseDatabaseManager.manageUserStatistics();
            }

            try {
                Thread.sleep(this.timeToSleep * 1000);
            } catch (InterruptedException e) {
                Log.d(TAG, "Thread interrupted");
                break;
            }
        } while (!Thread.currentThread().isInterrupted());
    }
}
