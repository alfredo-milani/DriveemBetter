package com.driveembetter.proevolutionsoftware.driveembetter.authentication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by alfredo on 11/08/17.
 */

class Google extends Authentication {

    private static final String TAG = "Google";

    Google(Context context) {
        super(context);
        Log.d(TAG, "Instantiated Google class");

        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
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
