package com.driveembetter.proevolutionsoftware.driveembetter.authentication;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

/**
 * Created by alfredo on 17/08/17.
 */

public class EmailAndPasswordProvider extends Provider {

    private static final String TAG = "EmailAndPswProvider";

    EmailAndPasswordProvider(Context context, Handler handler) {
        super(context, handler);
        Log.d(TAG, "Instantiated Provider class");
    }

    @Override
    public void signIn(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            Message msg = this.mHandler.obtainMessage(EMAIL_REQUIRED);
            this.mHandler.sendMessage(msg);
            return;
        } else if (TextUtils.isEmpty(password)) {
            Message msg = this.mHandler.obtainMessage(PASSWORD_REQUIRED);
            this.mHandler.sendMessage(msg);
            return;
        }

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
