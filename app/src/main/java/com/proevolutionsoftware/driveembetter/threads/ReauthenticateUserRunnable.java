package com.proevolutionsoftware.driveembetter.threads;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.proevolutionsoftware.driveembetter.authentication.SingletonFirebaseProvider;
import com.proevolutionsoftware.driveembetter.utils.NetworkConnectionUtil;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by alfredo on 10/09/17.
 */

public class ReauthenticateUserRunnable
        implements Runnable {

    private final static String TAG = ReauthenticateUserRunnable.class.getSimpleName();

    // Resources
    private int timeToSleep; // seconds
    private Context context;

    public ReauthenticateUserRunnable(Context context) {
        this.context = context;
        // Default value 5 minutes
        this.timeToSleep = 60 * 5;
    }

    public ReauthenticateUserRunnable(Context context, int timeToSleep) {
        this(context);
        this.timeToSleep = timeToSleep;
    }



    @Override
    public void run() {
        ActivityManager am = (ActivityManager) this.context.getSystemService(ACTIVITY_SERVICE);
        boolean isAppInForeground = true;

        do {
            if (NetworkConnectionUtil.isConnectedToInternet(this.context)) {
                SingletonFirebaseProvider.getInstance().reauthenticateUser();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Log.d(TAG, "over LOLLIPOP");
                    // TODO in questo branch sostituire metodi deprecati. Vedere anche nel manifest.

                    // The first in the list of RunningTasks is always the foreground task.
                    ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
                    isAppInForeground = foregroundTaskInfo.topActivity.getPackageName()
                            .equalsIgnoreCase(this.context.getPackageName());
                } else {
                    Log.d(TAG, "under LOLLOPOP");
                    // The first in the list of RunningTasks is always the foreground task.
                    ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
                    isAppInForeground = foregroundTaskInfo.topActivity.getPackageName()
                            .equalsIgnoreCase(this.context.getPackageName());
                }
            }

            try {
                Thread.sleep(this.timeToSleep * 1000);
            } catch (InterruptedException e) {
                Log.d(TAG, "Thread interrupted");
                break;
            }
        } while (isAppInForeground && !Thread.currentThread().isInterrupted());
    }
}
