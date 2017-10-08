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
        // Default value 5 minutes
        this.timeToSleep = 60 * 5;
    }

    public SaveUserStatisticsRunnable(Context context, int timeToSleep) {
        this(context);
        this.timeToSleep = timeToSleep;
    }



    @Override
    public void run() {
        do {
            try {
                Thread.sleep(this.timeToSleep * 1000);
            } catch (InterruptedException e) {
                Log.d(TAG, "Thread interrupted");
                break;
            }

            if (NetworkConnectionUtil.isConnectedToInternet(this.context) &&
                    PositionManager.getInstance(this.context).getInitialSpeed() != -1) {
                FirebaseDatabaseManager.manageUserStatistics();
            }
        } while (!Thread.currentThread().isInterrupted());
    }
}
