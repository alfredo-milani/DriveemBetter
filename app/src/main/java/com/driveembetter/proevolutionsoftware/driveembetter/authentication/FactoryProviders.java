package com.driveembetter.proevolutionsoftware.driveembetter.authentication;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by alfredo on 17/08/17.
 */

// This class creates and keeps track of individual instances of providers
public class FactoryProviders {

    private final static String TAG = "FactoryProvider";

    // Resources from UI
    private final Context context;
    private final Handler handler;

    // FirebaseProvider types
    public final static int EMAIL_AND_PASSWORD_PROVIDER = 0;
    public final static int GOOGLE_PROVIDER = 1;
    public final static int FACEBOOK_PROVIDER = 2;
    public final static int TWITTER_PROVIDER = 3;

    public FactoryProviders(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
        Log.d(TAG, "FactoryProviders: Context: " + this.context + " handler: " + this.handler);
    }



    public FirebaseProvider createProvider(int type) {
        switch (type) {
            case FactoryProviders.EMAIL_AND_PASSWORD_PROVIDER:
                return SingletonEmailAndPasswordProvider
                        .getSingletonInstance(this.context, this.handler);

            case FactoryProviders.GOOGLE_PROVIDER:
                return SingletonGoogleProvider
                        .getSingletonInstance(this.context, this.handler);

            case FactoryProviders.TWITTER_PROVIDER:
                return SingletonTwitterProvider
                        .getSingletonInstance(this.context, this.handler);

            case FactoryProviders.FACEBOOK_PROVIDER:
                return SingletonFacebookProvider
                        .getSingletonInstance(this.context, this.handler);

            default:
                Log.e(TAG, "Provider not found; type: " + type);
                return null;
        }
    }

    public ArrayList<FirebaseProvider> createAllProviders() {
        ArrayList<FirebaseProvider> firebaseProviderArrayList = new ArrayList<>(4);

        firebaseProviderArrayList.add(
                SingletonEmailAndPasswordProvider.getSingletonInstance(this.context, this.handler)
        );
        firebaseProviderArrayList.add(
                SingletonGoogleProvider.getSingletonInstance(this.context, this.handler)
        );
        firebaseProviderArrayList.add(
                SingletonFacebookProvider.getSingletonInstance(this.context, this.handler)
        );
        firebaseProviderArrayList.add(
                SingletonTwitterProvider.getSingletonInstance(this.context, this.handler)
        );

        return firebaseProviderArrayList;
    }

    public SingletonEmailAndPasswordProvider createEmailAndPasswordProvider() {
        Log.d(TAG, "Get object " + SingletonEmailAndPasswordProvider.class.toString());
        return SingletonEmailAndPasswordProvider.getSingletonInstance(
                this.context, this.handler
        );
    }

    public SingletonGoogleProvider createGoogleProvider() {
        Log.d(TAG, "Get object " + SingletonGoogleProvider.class.toString());
        return SingletonGoogleProvider.getSingletonInstance(
                this.context, this.handler
        );
    }

    public SingletonFacebookProvider createFacebookProvider() {
        Log.d(TAG, "Get object " + SingletonFacebookProvider.class.toString());
        return SingletonFacebookProvider.getSingletonInstance(
                this.context, this.handler
        );
    }

    public SingletonTwitterProvider createTwitterProvider() {
        Log.d(TAG, "Get object " + SingletonTwitterProvider.class.toString());
        return SingletonTwitterProvider.getSingletonInstance(
                this.context, this.handler
        );
    }
}