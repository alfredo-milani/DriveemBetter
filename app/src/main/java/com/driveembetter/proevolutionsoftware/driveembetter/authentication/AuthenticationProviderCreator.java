package com.driveembetter.proevolutionsoftware.driveembetter.authentication;

import android.content.Context;
import android.util.Log;

/**
 * Created by alfredo on 11/08/17.
 */

public class AuthenticationProviderCreator {

    private final static String TAG = "AutenticationProvider";

    private static Authentication singletonAuthenticationProvider = null;

    private AuthenticationProviderCreator() {
    }

    public static Authentication getSingletonAuthenticationProvider() {
        if (singletonAuthenticationProvider == null) {
            Log.e(TAG, "Errore: nessun provider istanziato");
        }

        return singletonAuthenticationProvider;
    }

    /*
    public static Authentication getSingletonAuthenticationProvider(int provider) {
        if (singletonAuthenticationProvider == null) {
            Log.e(TAG, "Errore: nessun provider istanziato");

            switch (provider) {
                // Firebase provider
                case 1:
                    singletonAuthenticationProvider = new Provider();
                    break;

                // Google provider
                case 2:
                    singletonAuthenticationProvider = new Google();
                    break;

                // Facebook provider
                case 3:
                    Log.d(TAG, "Not yet implemented");
                    break;
            }
        }

        return singletonAuthenticationProvider;
    }
    */

    public static Authentication getSingletonAuthenticationProvider(int provider, Context mContext) {
        if (singletonAuthenticationProvider == null) {
            switch (provider) {
                // Firebase provider
                case 1:
                    singletonAuthenticationProvider = new Provider(mContext);
                break;

                // Google provider
                case 2:
                    singletonAuthenticationProvider = new Google(mContext);
                    break;

                // Facebook provider
                case 3:
                    Log.d(TAG, "Not yet implemented");
                    break;
            }
        }

        return singletonAuthenticationProvider;
    }

    public static void resetSingletonAuthenticationProvider() {
        Log.d(TAG, "Resetted provider");
        singletonAuthenticationProvider = null;
    }
}
