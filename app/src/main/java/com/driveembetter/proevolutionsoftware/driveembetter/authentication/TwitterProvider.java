package com.driveembetter.proevolutionsoftware.driveembetter.authentication;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterSession;

/**
 * Created by alfredo on 15/08/17.
 */

public class TwitterProvider extends FirebaseProvider {

    private final static String TAG = "TwitterProvider";

    // TODO apri app --> onBackPressed --> riapri con tasto home --> funziona il tasto Twitter

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

    public void handleTwitterSession(TwitterSession session) {
        Log.d(TAG, "handleTwitterSession:" + session);

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        this.mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) this.mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(mContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    @Override
    public void signIn(String email, String password) {

    }

    @Override
    public void signOut() {

    }
}
