package com.driveembetter.proevolutionsoftware.driveembetter.authentication;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by alfredo on 17/08/17.
 */

public abstract class Provider
        implements TypeMessages {
    private static String TAG = "Provider";

    protected Context mContext;
    protected final Handler mHandler;

    // variables for authentication with Firebase platoform
    protected FirebaseAuth mAuth;
    protected FirebaseAuth.AuthStateListener mAuthListener;
    protected FirebaseUser firebaseUser;

    protected boolean signIn = false;

    Provider(Context context, Handler handler) {
        this.mHandler = handler;
        this.mContext = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                getCurrentFirebaseUser();
                if (firebaseUser != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + firebaseUser.getUid());
                    Toast.makeText(mContext, "Signed in as: " + firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();

                    sendVerificationEmail();

                    Message msg = mHandler.obtainMessage(USER_LOGIN_EMAIL_PSW);
                    mHandler.sendMessage(msg);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Toast.makeText(mContext, "Signed out", Toast.LENGTH_SHORT).show();

                    Message msg = mHandler.obtainMessage(USER_LOGOUT);
                    mHandler.sendMessage(msg);
                }
            }
        };
    }

    public abstract void signIn(String email, String password);

    public boolean isSignIn() {
        this.getCurrentFirebaseUser();
        return this.signIn = this.firebaseUser != null;
    }

    public abstract void signUp(String email, String password);

    public abstract void signOut();

    public void getCurrentFirebaseUser() {
        this.firebaseUser = this.mAuth.getCurrentUser();
    }

    public User getUserInformation() {
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

    public void checkIfEmailVerified() {
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

    public void sendVerificationEmail() {
        getCurrentFirebaseUser();
        // Send email and wait for confirmation
        // NOTE: Although this method is called multiple times,
        //   it will not run until all emails sent will no longer be valid
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

    // TODO: -vedi se è possibile inviare 1 sola email di verifica;
    // TODO: -segui il flusso delle chiamate dei metodi dai Log... troppi toast visualizzati...
}
