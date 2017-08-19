package com.driveembetter.proevolutionsoftware.driveembetter.authentication;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.driveembetter.proevolutionsoftware.driveembetter.entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by alfredo on 17/08/17.
 */

public abstract class FirebaseProvider
        implements TypeMessages {

    private static String TAG = "FirebaseProvider";

    // Link to UI
    protected Context mContext;
    protected Handler mHandler;

    // Variables for authentication with Firebase platoform
    protected FirebaseAuth mAuth;
    protected FirebaseAuth.AuthStateListener mAuthListener;
    protected FirebaseUser firebaseUser;

    protected boolean firebaseSignIn = false;

    FirebaseProvider(Context context, Handler handler) {
        this.mHandler = handler;
        this.mContext = context;
        this.mAuth = FirebaseAuth.getInstance();
    }

    public abstract void signIn(String email, String password);

    public boolean isFirebaseSignIn() {
        this.getCurrentFirebaseUser();
        return this.firebaseSignIn =
                this.firebaseUser != null &&
                this.firebaseUser.isEmailVerified();
    }

    public abstract void signOut();

    public void getCurrentFirebaseUser() {
        this.firebaseUser = this.mAuth.getCurrentUser();
    }

    public User getUserInformations() {
        if (this.firebaseUser != null) {
            return new User(
                    this.firebaseUser.getDisplayName(),
                    this.firebaseUser.getEmail(),
                    this.firebaseUser.getPhotoUrl(),
                    // The user's ID, unique to the Firebase project. Do NOT use this value to
                    // authenticate with your backend server, if you have one. Use
                    // FirebaseUser.getToken() instead.
                    this.firebaseUser.getUid(),
                    this.firebaseUser.isEmailVerified(),
                    this.firebaseUser.getProviderId(),
                    this.firebaseUser.getProviderData()
            );
        }

        return null;
    }

    public void initStateListener(final int success, final int failure) {
        this.mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                getCurrentFirebaseUser();
                if (firebaseUser != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + firebaseUser.getUid());

                    Message msg = mHandler.obtainMessage(success);
                    mHandler.sendMessage(msg);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");

                    Message msg = mHandler.obtainMessage(failure);
                    mHandler.sendMessage(msg);
                }
            }
        };
    }

    public void setStateListener() {
        if (this.mAuth != null && this.mAuthListener != null) {
            this.mAuth.addAuthStateListener(this.mAuthListener);
        }
    }

    public void removeStateListener() {
        if (this.mAuth != null && this.mAuthListener != null) {
            this.mAuth.removeAuthStateListener(this.mAuthListener);
        }
    }

    public void changeContext(Context context) {
        if (context != null) {
            this.mContext = context;
        }
    }

    public void changeHandler(Handler handler) {
        if (handler != null) {
            this.mHandler = handler;
        }
    }
}
