package com.driveembetter.proevolutionsoftware.driveembetter.authentication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.driveembetter.proevolutionsoftware.driveembetter.R;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.SingletonUser;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.GoogleAuthProvider;

import static android.app.Activity.RESULT_OK;

/**
 * Created by alfredo on 17/08/17.
 */

public class SingletonGoogleProvider
        implements
        BaseProvider,
        TypeMessages,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = SingletonGoogleProvider.class.getSimpleName();

    public static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInAccount account;
    private SingletonFirebaseProvider singletonFirebaseProvider;



    // Singleton
    private SingletonGoogleProvider() {
        this.singletonFirebaseProvider = SingletonFirebaseProvider.getInstance();

        Log.d(TAG, "Instantiated SingletonGoogleProvider.");

        // Configure Google sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(this.singletonFirebaseProvider
                        .getContext()
                        .getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        this.mGoogleApiClient = new GoogleApiClient.Builder(this.singletonFirebaseProvider.getContext())
                .enableAutoManage((FragmentActivity) this.singletonFirebaseProvider.getContext(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private static class GoogleProviderContainer {
        private final static SingletonGoogleProvider INSTANCE = new SingletonGoogleProvider();
    }

    static SingletonGoogleProvider getInstance() {
        return GoogleProviderContainer.INSTANCE;
    }



    public void activityResult(int requestCode, int resultCode, Intent data) {
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...); IN SignInActivity
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            Log.d(TAG, "SingletonGoogleProvider:activityResult RC: " + requestCode);
            this.handleSignInResult(Auth.GoogleSignInApi.getSignInResultFromIntent(data));
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        this.account = result.getSignInAccount();
        if (result.isSuccess() && this.account != null) {
            // Google Sign In was successful, authenticate with Firebase
            Log.d(TAG, "Google auth: user: " + this.account.getEmail());
            this.firebaseAuthWithGoogle(this.account);
        } else {
            // Google Sign In failed, update UI appropriately
            Log.d(TAG, "Google authentication failed: " + result.getStatus().getStatusMessage());
            this.singletonFirebaseProvider.sendMessageToUI(GOOGLE_SIGNIN_ERROR);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "Authentication with Firebase account: " + acct.getId());

        AuthCredential credential = GoogleAuthProvider
                .getCredential(acct.getIdToken(), null);
        SingletonFirebaseProvider
                .getAuth()
                .signInWithCredential(credential)
                .addOnCompleteListener((Activity) this.singletonFirebaseProvider.getContext(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d(TAG, "signInWithCredential:success");

                            // Connecting to PlayServices
                            connectToPlayStore();
                        } else {
                            // Sign in fails
                            try {
                                throw task.getException();
                            } catch (FirebaseException e2) {
                                Log.d(TAG, "firebaseAuthWithGoogle:failed", task.getException());
                                singletonFirebaseProvider.sendMessageToUI(INTERNAL_FIREBASE_ERROR_LOGIN);
                            } catch (Exception e1) {
                                Log.w(TAG, "firebaseAuthWithGoogle:failed", task.getException());
                                singletonFirebaseProvider.sendMessageToUI(UNKNOWN_ERROR);
                            }

                            signOut();
                        }
                    }
                });
    }

    @Override
    public void signIn(String email, String password) {
        if (email != null || password != null) {
            Log.w(TAG, "Google:signIn: received argument: email: " + email + " password: " + password);
        }

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi
                .silentSignIn(this.mGoogleApiClient);
        if (this.account != null) {
            Log.d(TAG, "Google account not null");
            this.firebaseAuthWithGoogle(this.account);
        } else if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            this.handleSignInResult(opr.get());
        } else {
            Log.d(TAG, "Launch intent");
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(this.mGoogleApiClient);
            ((Activity) this.singletonFirebaseProvider.getContext())
                    .startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    @Override
    public void signOut() {
        Log.d(TAG, "Google sign out:isConnected/ing: " + this.mGoogleApiClient.isConnected() + " / " + this.mGoogleApiClient.isConnecting());

        // Google sign out
        if (this.mGoogleApiClient.isConnected()) {
            Log.d(TAG, "Google signing out");
            // this.mGoogleApiClient.clearDefaultAccountAndReconnect();
            Auth.GoogleSignInApi.signOut(this.mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            Log.d(TAG, "Sign Out using Google Api: status: " + status.getStatus());
                            //CALL TO DISCONNECT GoogleApiClient
                            mGoogleApiClient.disconnect();
                        }
                    });
        }

        // Firebase sign out
        SingletonFirebaseProvider.getAuth().signOut();
    }

    /**
     * METODI DI DEBUG
     */
    public boolean diodio() {
        return this.mGoogleApiClient.isConnected();
    }
    public void cancan() {
        if (this.account != null) {
            // Google Sign In was successful, authenticate with Firebase
            Log.d(TAG, "Google auth: user: " + this.account.getEmail());
        } else {
            // Google Sign In failed, update UI appropriately
            Log.d(TAG, "Google authentication failed: " + this.account);
        }
    }

    // To disconnect from current Google account
    public void revokeAccess() {
        if (this.mGoogleApiClient.isConnected()) {
            this.mGoogleApiClient.stopAutoManage((FragmentActivity) this.singletonFirebaseProvider.getContext());
            // Google revoke access
            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            Log.d(TAG, "STATUS_REVOKE: " + status.getStatus());
                        }
                    });
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this.singletonFirebaseProvider.getContext(), "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: " + this.mGoogleApiClient.isConnected());
    }

    public void connectToPlayStore() {
        if (this.mGoogleApiClient != null) {
            if (!this.mGoogleApiClient.isConnected()) {
                // per i nostri scopi forse non è necessario connettersi a Google Play Services
                Log.d(TAG, "Connecting to PlayStoreServices");
                this.mGoogleApiClient.connect();
            }
        } else {
            Log.w(TAG, "mGoogleApiClient NULL");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    public SingletonUser getGoogleUserInformations() {
        if (this.account != null) {
            return SingletonUser.getInstance(
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

    public void connectAfterResume() {
        Log.d(TAG, "connectionAfterResume");
        if (!this.mGoogleApiClient.isConnected()) {
            Log.d(TAG, "connectionAfterResume:reconnection");
            this.mGoogleApiClient.connect();
        }
    }

    public void managePendingOperations() {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(this.mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            this.handleSignInResult(opr.get());
        }
    }

    public void asyncReSign() {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(this.mGoogleApiClient);
        if (!opr.isDone()) {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    Log.d(TAG, "Silent sign-in");
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    public void silentSignIn() {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(this.mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            this.handleSignInResult(opr.get());
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    Log.d(TAG, "Silent sign-in");
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public boolean isSignIn() {
        return this.account != null;
    }
}
