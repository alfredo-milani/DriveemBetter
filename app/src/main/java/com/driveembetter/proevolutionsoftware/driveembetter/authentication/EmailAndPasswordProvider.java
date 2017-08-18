package com.driveembetter.proevolutionsoftware.driveembetter.authentication;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

/**
 * Created by alfredo on 17/08/17.
 */

public class EmailAndPasswordProvider extends FirebaseProvider {

    private static final String TAG = "EmailAndPswProvider";

    public EmailAndPasswordProvider(Context context, Handler handler) {
        super(context, handler);
        Log.d(TAG, "Instantiated FirebaseProvider class");
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

                            Message message = null;
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
                            } finally {
                                if (message != null)
                                    mHandler.sendMessage(message);
                            }

                        } else {
                            checkIfEmailVerified();
                        }
                    }
                });
    }

    public void signUp(String email, String password) {
        this.mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {

                            Message message = null;
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e) {
                                Log.d(TAG, "createUserWithEmailAndPassword:failed", task.getException());
                                message = mHandler.obtainMessage(USER_ALREADY_EXIST);
                            } catch (Exception e1) {
                                Log.w(TAG, "createUserWithEmailAndPassword:failed", task.getException());
                                message = mHandler.obtainMessage(UNKNOWN_EVENT);
                            } finally {
                                if (message != null)
                                    mHandler.sendMessage(message);
                            }

                        }
                    }
                });
    }

    @Override
    public void signOut() {
        this.mAuth.signOut();
    }

    private void checkIfEmailVerified() {
        this.getCurrentFirebaseUser();
        if (this.firebaseUser.isEmailVerified()) {
            // User verified
            Log.d(TAG, "checkIfEmailVerified:success");
        } else {
            // Email is not verified, so just prompt the message to the user and restart this activity.
            Log.d(TAG, "checkIfEmailVerified:failure");
            Toast.makeText(this.mContext, "Email not verified: " + this.firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
            // Log out user
            this.signOut();
        }
    }

    private void sendVerificationEmail() {
        this.getCurrentFirebaseUser();
        // Send email and wait for confirmation
        this.firebaseUser.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Email sent
                            Log.d(TAG, "sendVerificationEmail:success");
                            Toast.makeText(mContext, "Verification email sent", Toast.LENGTH_SHORT).show();
                            // After email is sent just logout the user
                            signOut();
                        } else {
                            // Email not sent, so display message
                            Log.d(TAG, "sendVerificationEmail:failure");
                            Toast.makeText(mContext, "Verification email not sent", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
