package com.driveembetter.proevolutionsoftware.driveembetter.authentication;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

/**
 * Created by alfredo on 11/08/17.
 */

class Provider extends Authentication {

    private static final String TAG = "Provider";

    Provider(Context context, Handler handler) {
        super(context, handler);
        Log.d(TAG, "Instantiated Provider class");
    }

    @Override
    public void signIn(String email, String password) {
        this.mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) this.mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                        } else {
                            checkIfEmailVerified();
                        }
                    }
                });
    }

    @Override
    public void signUp(String email, String password) {
        this.mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "createUserWithEmailAndPassword:failed", task.getException());
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Message msg = mHandler.obtainMessage(USER_ALREADY_EXIST);
                                mHandler.sendMessage(msg);
                            }
                        }
                    }
                });
    }

    @Override
    public void signOut() {
        this.mAuth.signOut();
    }
}