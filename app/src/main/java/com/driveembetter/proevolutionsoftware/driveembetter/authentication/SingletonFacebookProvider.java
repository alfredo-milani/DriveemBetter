package com.driveembetter.proevolutionsoftware.driveembetter.authentication;

import android.util.Log;

/**
 * Created by alfredo on 17/08/17.
 */

public class SingletonFacebookProvider
        implements BaseProvider, TypeMessages {

    private final static String TAG = "SFacebookProvider";

    private boolean signIn;
    private SingletonFirebaseProvider singletonFirebaseProvider;



    // Singleton
    private SingletonFacebookProvider() {
        this.singletonFirebaseProvider = SingletonFirebaseProvider.getInstance();

        Log.d(TAG, "Instantiated SingleEmailAndPasswordProvider.");

        this.signIn = false;
    }

    private static class FacebookProviderContainer {
        private final static SingletonFacebookProvider INSTANCE = new SingletonFacebookProvider();
    }

    public static SingletonFacebookProvider getInstance() {
        return SingletonFacebookProvider.FacebookProviderContainer.INSTANCE;
    }



    @Override
    public void signIn(String email, String password) {

    }

    @Override
    public void signOut() {

    }

    @Override
    public boolean isSignIn() {
        return this.signIn;
    }
}
