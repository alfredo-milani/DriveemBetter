package com.proevolutionsoftware.driveembetter.authentication.factoryProvider;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.proevolutionsoftware.driveembetter.authentication.BaseProvider;

import java.util.ArrayList;

/**
 * Created by alfredo on 17/08/17.
 */

// This class creates and keeps track of individual instances of providers
public class FactoryProviders {

    private final static String TAG = FactoryProviders.class.getSimpleName();

    // Resources from UI
    private final Context context;
    private final Handler handler;

    // SingletonFirebaseProvider types
    public final static short EMAIL_AND_PASSWORD_PROVIDER = 0;
    public final static short GOOGLE_PROVIDER = 1;
    public final static short FACEBOOK_PROVIDER = 2;
    public final static short TWITTER_PROVIDER = 3;

    public FactoryProviders(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
        Log.d(TAG, "FactoryProviders: Context: " + this.context + " handler: " + this.handler);
    }



    public BaseProvider getProvider(int type) {
        switch (type) {
            case FactoryProviders.EMAIL_AND_PASSWORD_PROVIDER:
                return SingletonEmailAndPasswordProvider
                        .getInstance();

            case FactoryProviders.GOOGLE_PROVIDER:
                return SingletonGoogleProvider
                        .getInstance();

            case FactoryProviders.TWITTER_PROVIDER:
                return SingletonTwitterProvider
                        .getInstance();

            case FactoryProviders.FACEBOOK_PROVIDER:
                return SingletonFacebookProvider
                        .getInstance();

            default:
                Log.e(TAG, "Provider not found; type: " + type);
                return null;
        }
    }

    public ArrayList<BaseProvider> getAllProviders() {
        ArrayList<BaseProvider> baseProviderArrayList = new ArrayList<>(4);

        baseProviderArrayList.add(
                SingletonEmailAndPasswordProvider.getInstance()
        );
        baseProviderArrayList.add(
                SingletonGoogleProvider.getInstance()
        );
        baseProviderArrayList.add(
                SingletonFacebookProvider.getInstance()
        );
        baseProviderArrayList.add(
                SingletonTwitterProvider.getInstance()
        );

        return baseProviderArrayList;
    }

    public SingletonEmailAndPasswordProvider getEmailAndPasswordProvider() {
        Log.d(TAG, "Get object " + SingletonEmailAndPasswordProvider.class.toString());
        return SingletonEmailAndPasswordProvider.getInstance();
    }

    public SingletonGoogleProvider getGoogleProvider() {
        Log.d(TAG, "Get object " + SingletonGoogleProvider.class.toString());
        return SingletonGoogleProvider.getInstance();
    }

    public SingletonFacebookProvider getFacebookProvider() {
        Log.d(TAG, "Get object " + SingletonFacebookProvider.class.toString());
        return SingletonFacebookProvider.getInstance();
    }

    public SingletonTwitterProvider getTwitterProvider() {
        Log.d(TAG, "Get object " + SingletonTwitterProvider.class.toString());
        return SingletonTwitterProvider.getInstance();
    }
}