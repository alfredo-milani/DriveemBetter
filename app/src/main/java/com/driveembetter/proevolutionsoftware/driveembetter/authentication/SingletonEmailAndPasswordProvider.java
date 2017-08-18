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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

/**
 * Created by alfredo on 17/08/17.
 */

public class SingletonEmailAndPasswordProvider extends FirebaseProvider {

    private static final String TAG = "SEmailAndPswProvider";

    private static SingletonEmailAndPasswordProvider singletonInstance;

    private SingletonEmailAndPasswordProvider(Context context, Handler handler) {
        super(context, handler);

        Log.d(TAG, "Instantiated SingleEmailAndPasswordProvider.\nContext: " + this.mContext + " Handler: " + this.mContext);
    }



    // Singleton
    public static SingletonEmailAndPasswordProvider getSingletonInstance(Context context, Handler handler){
        if(SingletonEmailAndPasswordProvider.singletonInstance == null){
            synchronized (SingletonEmailAndPasswordProvider.class) {
                if(SingletonEmailAndPasswordProvider.singletonInstance == null){
                    SingletonEmailAndPasswordProvider.singletonInstance =
                            new SingletonEmailAndPasswordProvider(context, handler);
                }
            }
        }

        return SingletonEmailAndPasswordProvider.getSingletonInstance();
    }

    public static SingletonEmailAndPasswordProvider getSingletonInstance() {
        return SingletonEmailAndPasswordProvider.singletonInstance;
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

                            Message message;
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                Log.d(TAG, "signInWithEmail:failed", task.getException());
                                message = mHandler.obtainMessage(INVALID_CREDENTIALS);
                            } catch (FirebaseAuthInvalidUserException e3) {
                                Log.d(TAG, "signInWithEmail:failed", task.getException());
                                message = mHandler.obtainMessage(INVALID_USER);
                            } catch (Exception e1) {
                                Log.w(TAG, "signInWithEmail:failed", task.getException());
                                message = mHandler.obtainMessage(UNKNOWN_EVENT);
                            }
                            if (message != null)
                                mHandler.sendMessage(message);

                        } else {
                            checkIfEmailVerified();
                        }
                    }
                });
    }

    private void checkIfEmailVerified() {
        this.getCurrentFirebaseUser();
        Message message;
        if (this.firebaseUser.isEmailVerified()) {
            // User verified
            Log.d(TAG, "checkIfEmailVerified:success");
            message = this.mHandler.obtainMessage(USER_LOGIN);
        } else {
            // Email is not verified
            Log.d(TAG, "checkIfEmailVerified:failure");
            message = this.mHandler.obtainMessage(EMAIL_NOT_VERIFIED);
            // Log out user
            this.signOut();
        }
        if (message != null)
            this.mHandler.sendMessage(message);
    }

    public void signUp(String email, String password) {
        this.mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {

                            Message message;
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e) {
                                Log.d(TAG, "createUserWithEmailAndPassword:failed", task.getException());
                                message = mHandler.obtainMessage(USER_ALREADY_EXIST);
                            } catch (Exception e1) {
                                Log.w(TAG, "createUserWithEmailAndPassword:failed", task.getException());
                                message = mHandler.obtainMessage(UNKNOWN_EVENT);
                            }
                            if (message != null)
                                mHandler.sendMessage(message);

                        } else {
                            sendVerificationEmail();
                        }
                    }
                });
    }

    private void sendVerificationEmail() {
        this.getCurrentFirebaseUser();
        // Send email and wait for confirmation
        this.firebaseUser.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Message message;
                        if (task.isSuccessful()) {
                            // Email sent
                            Log.d(TAG, "sendVerificationEmail:success");
                            message = mHandler.obtainMessage(VERIFICATION_EMAIL_SENT);
                        } else {
                            // Email not sent
                            Log.d(TAG, "sendVerificationEmail:failure");
                            message = mHandler.obtainMessage(VERIFICATION_EMAIL_NOT_SENT);
                        }
                        mHandler.sendMessage(message);
                        signOut();
                    }
                });
    }

    @Override
    public void signOut() {
        this.mAuth.signOut();
    }
}
