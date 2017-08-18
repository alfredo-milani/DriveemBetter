package com.driveembetter.proevolutionsoftware.driveembetter.authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Created by alfredo on 17/08/17.
 */

public class SingletonGoogleProvider
        extends FirebaseProvider
        implements
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "SingletonGoogleProvider";
    public static final int RC_SIGN_IN = 9001;

    private static SingletonGoogleProvider singletonInstance;

    private GoogleApiClient mGoogleApiClient;
    private boolean isAccntConnected = false;
    private GoogleSignInAccount account;

    private SingletonGoogleProvider(Context context, Handler handler) {
        super(context, handler);

        Log.d(TAG, "Instantiated SingletonGoogleProvider.\nContext: " + this.mContext + " Handler: " + this.mContext);

        // Configure Google sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(this.mContext.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        this.mGoogleApiClient = new GoogleApiClient.Builder(this.mContext)
                .enableAutoManage((FragmentActivity) this.mContext /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }



    // Singleton
    public static SingletonGoogleProvider getSingletonInstance(Context context, Handler handler) {
        if(SingletonGoogleProvider.singletonInstance == null){
            synchronized (SingletonGoogleProvider.class) {
                if(SingletonGoogleProvider.singletonInstance == null){
                    SingletonGoogleProvider.singletonInstance =
                            new SingletonGoogleProvider(context, handler);
                }
            }
        }

        return SingletonGoogleProvider.getSingletonInstance();
    }

    public static SingletonGoogleProvider getSingletonInstance() {
        return SingletonGoogleProvider.singletonInstance;
    }



    public void activityResult(int requestCode, int resultCode, Intent data) {
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...); IN SignInActivity
        Log.d(TAG, "SingletonGoogleProvider:activityResult");
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                this.account = result.getSignInAccount();
                Log.d(TAG, "Google auth: user: " + account.getEmail());
                Message message = mHandler.obtainMessage(USER_LOGIN_GOOGLE);
                mHandler.sendMessage(message);

                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                Log.d(TAG, "Google authentication failed");
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "Authentication with Firebase account: " + acct.getId());

        AuthCredential credential = GoogleAuthProvider
                .getCredential(acct.getIdToken(), null);
        super.mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) this.mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d(TAG, "signInWithCredential:success");
                            getCurrentFirebaseUser();
                        } else {
                            // Sign in fails
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    @Override
    public void signIn(String email, String password) {
        if (email != null || password != null) {
            Log.w(TAG, "Google:signIn: received argument: email: " + email + " password: " + password);
        }

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(this.mGoogleApiClient);
        ((Activity) this.mContext).startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void signOut() {
        // Firebase sign out
        super.mAuth.signOut();

        // Google sign out
        if (this.mGoogleApiClient.isConnected()) {
            this.mGoogleApiClient.clearDefaultAccountAndReconnect();
            Auth.GoogleSignInApi.signOut(this.mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            Log.d(TAG, "Sign Out using Google Api.");
                            mGoogleApiClient.disconnect();
                            //CALL TO DISCONNECT GoogleApiClient
                            isAccntConnected = false;
                        }
                    });
        }
    }

    // To disconnect from current Google account
    public void revokeAccess() {
        // Firebase sign out
        super.mAuth.signOut();

        if (!this.isAccntConnected) {
            return;
        }
        // Google revoke access
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        Toast.makeText(mContext, "SIGNOUT: " + status, Toast.LENGTH_LONG).show();

                        Log.d(TAG, "STATUS_REVOKE: " + status.getStatus());
                    }
                });
    }

    public void clearAccount() {
        this.mGoogleApiClient.clearDefaultAccountAndReconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this.mContext, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnection:true");
        this.isAccntConnected = true;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public User getGoogleUserInformations() {
        if (this.account != null) {
            return new User(
                    this.account.getDisplayName(),
                    this.account.getEmail(),
                    this.account.getPhotoUrl(),
                    this.account.getId(),
                    true,
                    null,
                    null
            );
        }

        return null;
    }
}
