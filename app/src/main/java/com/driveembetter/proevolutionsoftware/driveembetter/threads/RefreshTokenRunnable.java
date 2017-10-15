package com.driveembetter.proevolutionsoftware.driveembetter.threads;

import com.driveembetter.proevolutionsoftware.driveembetter.entity.SingletonUser;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.FirebaseDatabaseManager;

/**
 * Created by alfredo on 14/10/17.
 */

public class RefreshTokenRunnable
        implements Runnable {

    private final static String TAG = RefreshTokenRunnable.class.getSimpleName();

    private final SingletonUser user;
    private final String token;



    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    public RefreshTokenRunnable(String token) {
        this.token = token;
        this.user = SingletonUser.getInstance();
    }

    @Override
    public void run() {
        if (this.user != null) {
            this.user.getMtxSyncData().lock();
            FirebaseDatabaseManager.refreshUserToken(this.token);
            this.user.getMtxSyncData().unlock();
        }
    }
}
