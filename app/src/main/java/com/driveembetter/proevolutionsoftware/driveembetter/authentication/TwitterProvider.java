package com.driveembetter.proevolutionsoftware.driveembetter.authentication;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

/**
 * Created by alfredo on 15/08/17.
 */

public class TwitterProvider extends Provider {

    private final static String TAG = "TwitterProvider";

    public TwitterProvider(Context context, Handler handler) {
        super(context, handler);

        // Configure Twitter SDK
        /*
        TwitterAuthConfig authConfig =  new TwitterAuthConfig(
                this.mContext.getString(R.string.com_twitter_sdk_android_CONSUMER_KEY),
                this.mContext.getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET));
        Fabric.with(this, new Twitter(authConfig));
        */

        // Twitter.initialize(this.mContext);
        TwitterConfig config = new TwitterConfig.Builder(this.mContext)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(
                        this.mContext.getString(R.string.com_twitter_sdk_android_CONSUMER_KEY),
                        this.mContext.getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET)
                ))
                .debug(true)
                .build();
        Twitter.initialize(config);
    }

    @Override
    public void signIn(String email, String password) {

    }

    @Override
    public void signUp(String email, String password) {

    }

    @Override
    public void signOut() {

    }
}
