package com.driveembetter.proevolutionsoftware.driveembetter.authentication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.driveembetter.proevolutionsoftware.driveembetter.entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by alfredo on 11/08/17.
 */

public abstract class Authentication {

    private static String TAG = "Authentication";

    protected Context mContext;

    // variables for authentication with Firebase platoform
    protected FirebaseAuth mAuth;
    protected FirebaseAuth.AuthStateListener mAuthListener;
    protected FirebaseUser firebaseUser;

    public Authentication(Context context) {
        this.mContext = context;
        // this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        this.mAuth = FirebaseAuth.getInstance();
        this.mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAu_thStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }



    public abstract void signIn(String email, String password);

    public abstract void signUp(String email, String password);

    public abstract void signOut();

    public void getCurrentFirebaseUser() {
        this.firebaseUser = this.mAuth.getCurrentUser();
    }

    public User getUserInformation() {
        if (this.firebaseUser != null) {
            // Name, email address, and profile photo Url
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
}
