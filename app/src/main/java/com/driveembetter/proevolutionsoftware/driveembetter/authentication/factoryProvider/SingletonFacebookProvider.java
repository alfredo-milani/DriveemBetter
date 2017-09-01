package com.driveembetter.proevolutionsoftware.driveembetter.authentication.factoryProvider;

import android.util.Log;

import com.driveembetter.proevolutionsoftware.driveembetter.authentication.BaseProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.SingletonFirebaseProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.authentication.TypeMessages;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.SingletonUser;

/**
 * Created by alfredo on 17/08/17.
 */

public class SingletonFacebookProvider
        implements BaseProvider, TypeMessages {

    private final static String TAG = "SFacebookProvider";

    private SingletonFirebaseProvider singletonFirebaseProvider;



    // Singleton
    private SingletonFacebookProvider() {
        this.singletonFirebaseProvider = SingletonFirebaseProvider.getInstance();

        Log.d(TAG, "Instantiated SingleEmailAndPasswordProvider.");
    }

    private static class FacebookProviderContainer {
        private final static SingletonFacebookProvider INSTANCE = new SingletonFacebookProvider();
    }

    static SingletonFacebookProvider getInstance() {
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
        return false;
    }

    @Override
    public SingletonUser getUserInformations() {
        return null;
    }
}
