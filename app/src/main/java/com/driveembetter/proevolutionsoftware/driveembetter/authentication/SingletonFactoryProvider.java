package com.driveembetter.proevolutionsoftware.driveembetter.authentication;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

/**
 * Created by alfredo on 17/08/17.
 */

// This class creates and keeps track of individual instances of providers
public class SingletonFactoryProvider {

    private final static String TAG = "SFactoryProvider";

    // Resources from UI
    private final Context context;
    private final Handler handler;

    private EmailAndPasswordProvider singletonSingletonEmailAndPasswordProvider;
    private GoogleProvider singletonGoogleProvider;
    private FacebookProvider singletonFacebookProvider;
    private TwitterProvider singletonTwitterProvider;

    // FirebaseProvider types
    public final static int EMAIL_AND_PASSWORD_PROVIDER = 1;
    public final static int GOOGLE_PROVIDER = 2;
    public final static int FACEBOOK_PROVIDER = 3;
    public final static int TWITTER_PROVIDER = 4;

    public SingletonFactoryProvider(Context context, Handler handler) {
        this.singletonSingletonEmailAndPasswordProvider = null;
        this.singletonGoogleProvider = null;
        this.singletonFacebookProvider = null;
        this.singletonTwitterProvider = null;

        this.context = context;
        this.handler = handler;
    }

    public EmailAndPasswordProvider getSingletonSingletonEmailAndPasswordProvider() {
        Log.d(TAG, "Get object " + EmailAndPasswordProvider.class.toString());
        if (this.singletonSingletonEmailAndPasswordProvider == null) {
            this.singletonSingletonEmailAndPasswordProvider =
                    new EmailAndPasswordProvider(this.context, this.handler);
        }
        return this.singletonSingletonEmailAndPasswordProvider;
    }

    public GoogleProvider getSingletonGoogleProvider() {
        Log.d(TAG, "Get object " + GoogleProvider.class.toString());
        if (this.singletonGoogleProvider == null) {
            this.singletonGoogleProvider =
                    new GoogleProvider(this.context, this.handler);
        }
        return this.singletonGoogleProvider;
    }

    public FacebookProvider getSingletonFacebookProvider() {
        Log.d(TAG, "Get object " + FacebookProvider.class.toString());
        if (this.singletonFacebookProvider == null) {
            this.singletonFacebookProvider =
                    new FacebookProvider(this.context, this.handler);
        }
        return this.singletonFacebookProvider;
    }

    public TwitterProvider getSingletonTwitterProvider() {
        Log.d(TAG, "Get object " + TwitterProvider.class.toString());
        if (this.singletonTwitterProvider == null) {
            this.singletonTwitterProvider =
                    new TwitterProvider(this.context, this.handler);
        }
        return this.singletonTwitterProvider;
    }
}