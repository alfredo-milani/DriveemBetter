package com.driveembetter.proevolutionsoftware.driveembetter.authentication;

import android.annotation.SuppressLint;
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

public class SingletonFirebaseProvider
        implements TypeMessages {

    private static String TAG = "SFirebaseProvider";

    // Link to UI
    private Context context;
    private Handler handler;

    // Variables for authentication with Firebase platoform
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private volatile boolean listenerSetted;
    private volatile int listenerOwner; // listener's owner (which activity can set/remove it)



    @SuppressLint("StaticFieldLeak")
    private static SingletonFirebaseProvider singletonInstance;

    private SingletonFirebaseProvider(Context context, Handler handler) {
        this.handler = handler;
        this.context = context;
        this.auth = FirebaseAuth.getInstance();
        this.initStateListener(USER_LOGIN, USER_LOGOUT);
    }



    // Singleton
    public static SingletonFirebaseProvider getInstance(Context context, Handler handler) {
        if(SingletonFirebaseProvider.singletonInstance == null){
            synchronized (SingletonFirebaseProvider.class) {
                if(SingletonFirebaseProvider.singletonInstance == null) {
                    SingletonFirebaseProvider.singletonInstance =
                            new SingletonFirebaseProvider(context, handler);
                }
            }
        } else {
            Log.w(TAG, "getInstance:FirebaseProvider already initialized");
        }

        return SingletonFirebaseProvider.getInstance();
    }

    public static SingletonFirebaseProvider getInstance() {
        return SingletonFirebaseProvider.singletonInstance;
    }



    /**
     * Check if a user is signed in with Firease provider
     * @return true: if user is signed in; false: the user is not signed in.
     */
    public synchronized boolean isFirebaseSignIn() {
        return this.getFirebaseUser() != null;
    }

    public FirebaseUser getFirebaseUser() {
        return this.auth.getCurrentUser();
    }

    public synchronized User getUserInformations() {
        if (this.getFirebaseUser() != null) {
            return new User(
                    this.getFirebaseUser().getDisplayName(),
                    this.getFirebaseUser().getEmail(),
                    this.getFirebaseUser().getPhotoUrl(),
                    // The user's ID, unique to the Firebase project. Do NOT use this value to
                    // authenticate with your backend server, if you have one. Use
                    // FirebaseUser.getToken() instead.
                    this.getFirebaseUser().getUid(),
                    this.getFirebaseUser().isEmailVerified(),
                    this.getFirebaseUser().getProviderId(),
                    this.getFirebaseUser().getProviderData()
            );
        }

        return null;
    }

    private void initStateListener(final int success, final int failure) {
        this.listenerSetted = false;
        this.authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (getFirebaseUser() != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + getFirebaseUser().getUid());

                    sendMessageToUI(success);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");

                    sendMessageToUI(failure);
                }
            }
        };
    }

    public synchronized void setStateListener(int hashCurrentActivity) {
        if (this.auth != null && this.authStateListener != null &&
                !this.listenerSetted && this.listenerOwner == hashCurrentActivity) {
            Log.d(TAG, "setStateListener: setted");
            this.auth.addAuthStateListener(this.authStateListener);
            this.listenerSetted = true;
        } else {
            Log.w(TAG, "setStateListener:error: auth/listener/setted: " + this.auth + " / " + this.authStateListener + " / " + this.listenerSetted);
        }
    }

    public synchronized void removeStateListener(int hashCurrentActivity) {
        if (this.auth != null && this.authStateListener != null &&
                this.listenerSetted && this.listenerOwner == hashCurrentActivity) {
            Log.d(TAG, "removeStateListener: removed");
            this.auth.removeAuthStateListener(this.authStateListener);
            this.listenerSetted = false;
        } else {
            Log.w(TAG, "removeStateListener:error: auth/listener/setted: " + this.auth + " / " + this.authStateListener + " / " + this.listenerSetted);
        }
    }

    public synchronized void setListenerOwner(int hashOwner) {
        this.listenerOwner = hashOwner;
    }

    public void setContext(Context context) {
        if (context != null) {
            this.context = context;
        }
    }

    public void setHandler(Handler handler) {
        if (handler != null) {
            this.handler = handler;
        }
    }

    public Context getContext() {
        return this.context;
    }

    public Handler getHandler() {
        return this.handler;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public void sendMessageToUI(int actionType) {
        if (this.handler != null) {
            Message message = this.handler.obtainMessage(actionType);
            this.handler.sendMessage(message);
        } else {
            Log.w(TAG, "Handler is null!");
        }
    }

    public void forceSignOut() {
        if (this.getFirebaseUser() != null) {
            Log.d(TAG, "force Firebase sign out");
            this.auth.signOut();
        }
    }
}
