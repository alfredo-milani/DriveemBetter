package com.driveembetter.proevolutionsoftware.driveembetter.authentication;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

/**
 * Created by alfredo on 15/08/17.
 */

public class SingletonTwitterProvider
        implements BaseProvider, TypeMessages {

    private final static String TAG = "STwitterProvider";

    public static final int RC_SIGN_IN = 140;
    private TwitterSession session;
    private SingletonFirebaseProvider singletonFirebaseProvider;



    // Singleton
    private SingletonTwitterProvider() {
        Log.d(TAG, "Instantiated SingletonTwitterProvider.");

        this.singletonFirebaseProvider = SingletonFirebaseProvider.getInstance();

        TwitterConfig config = new TwitterConfig.Builder(this.singletonFirebaseProvider.getContext())
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(
                        this.singletonFirebaseProvider
                                .getContext()
                                .getString(R.string.com_twitter_sdk_android_CONSUMER_KEY),
                        this.singletonFirebaseProvider
                                .getContext()
                                .getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET)
                ))
                .debug(true)
                .build();
        Twitter.initialize(config);
    }

    private static class TwitterProviderContainer {
        private final static SingletonTwitterProvider INSTANCE = new SingletonTwitterProvider();
    }

    static SingletonTwitterProvider getInstance() {
        return SingletonTwitterProvider.TwitterProviderContainer.INSTANCE;
    }



    private void handleTwitterSession(TwitterSession session) {
        Log.d(TAG, "handleTwitterSession:" + session);

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        // Firebase authentcation
        this.singletonFirebaseProvider
                .getAuth()
                .signInWithCredential(credential)
                .addOnCompleteListener((Activity) this.singletonFirebaseProvider.getContext(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d(TAG, "signInWithCredential:success");
                        } else {
                            // Sign in fails
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    public void setCallback(TwitterLoginButton twitterLoginButton) {
        if (twitterLoginButton != null) {
            twitterLoginButton.setCallback(new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    Log.d(TAG, "twitterLogin:success" + result);

                    session = result.data;
                    handleTwitterSession(result.data);

                    TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                    TwitterAuthToken authToken = session.getAuthToken();
                    String token = authToken.token;
                    String secret = authToken.secret;

                    Log.d(TAG, "USERNAME: " + session.getUserName());
                }

                @Override
                public void failure(TwitterException exception) {
                    Log.w(TAG, "twitterLogin:failure", exception);

                    TwitterConfig config = new TwitterConfig.Builder(singletonFirebaseProvider.getContext())
                            .logger(new DefaultLogger(Log.DEBUG))
                            .twitterAuthConfig(new TwitterAuthConfig(
                                    singletonFirebaseProvider
                                            .getContext()
                                            .getString(R.string.com_twitter_sdk_android_CONSUMER_KEY),
                                    singletonFirebaseProvider
                                            .getContext()
                                            .getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET)
                            ))
                            .debug(true)
                            .build();
                    Twitter.initialize(config);
                }
            });
        }
    }

    public User getTwitterUserInformations() {
        if (this.session != null) {
            return new User(
                    this.session.getUserName(),
                    null,
                    null,
                    Long.toString(this.session.getUserId()),
                    true,
                    null,
                    null
            );
        }

        return null;
    }

    @Override
    public void signIn(String email, String password) {

    }

    @Override
    public void signOut() {

    }

    @Override
    public boolean isSignIn() {
        return this.session != null;
    }
}
