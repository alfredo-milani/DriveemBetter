package com.proevolutionsoftware.driveembetter.fcm;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.proevolutionsoftware.driveembetter.constants.Constants;
import com.proevolutionsoftware.driveembetter.threads.RefreshTokenRunnable;

public class FirebaseUtility extends FirebaseInstanceIdService
        implements Constants {

    private static final String TAG = FirebaseUtility.class.getSimpleName();



    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        new Thread(new RefreshTokenRunnable(refreshedToken)).start();
    }
    // [END refresh_token]
}