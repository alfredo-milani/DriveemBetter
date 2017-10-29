package com.proevolutionsoftware.driveembetter.authentication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.proevolutionsoftware.driveembetter.entity.SingletonUser;

/**
 * Created by alfredo on 17/08/17.
 */

public class SingletonFirebaseProvider
        implements TypeMessages {

    private static String TAG = SingletonFirebaseProvider.class.getSimpleName();

    // Link to UI
    private Context context;
    private Handler handler;

    // Variables for authentication with Firebase platoform
    private static FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private volatile boolean listenerSetted;
    private volatile int listenerOwner; // listener's owner (which activity can set/remove it)



    @SuppressLint("StaticFieldLeak")
    private static SingletonFirebaseProvider singletonInstance;

    private SingletonFirebaseProvider(Context context, Handler handler) {
        this.handler = handler;
        this.context = context;
        SingletonFirebaseProvider.auth = FirebaseAuth.getInstance();
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

    @Nullable
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
        return SingletonFirebaseProvider.auth.getCurrentUser();
    }

    public SingletonUser getUserInformations() {
        if (this.getFirebaseUser() != null) {
            return SingletonUser.getInstance(
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
                    // SingletonUser is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + getFirebaseUser().getUid());

                    sendMessageToUI(success);
                } else {
                    // SingletonUser is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");

                    sendMessageToUI(failure);
                }
            }
        };
    }

    public synchronized void setStateListener(int hashCurrentActivity) {
        if (SingletonFirebaseProvider.auth != null && this.authStateListener != null &&
                !this.listenerSetted && this.listenerOwner == hashCurrentActivity) {
            Log.d(TAG, "setStateListener: setted");
            SingletonFirebaseProvider.auth.addAuthStateListener(this.authStateListener);
            this.listenerSetted = true;
        } else {
            Log.w(TAG, "setStateListener:error: auth/listener/setted: " + SingletonFirebaseProvider.auth + " / " + this.authStateListener + " / " + this.listenerSetted);
        }
    }

    public synchronized void removeStateListener(int hashCurrentActivity) {
        if (SingletonFirebaseProvider.auth != null && this.authStateListener != null &&
                this.listenerSetted && this.listenerOwner == hashCurrentActivity) {
            Log.d(TAG, "removeStateListener: removed");
            SingletonFirebaseProvider.auth.removeAuthStateListener(this.authStateListener);
            this.listenerSetted = false;
        } else {
            Log.w(TAG, "removeStateListener:error: auth/listener/setted: " + SingletonFirebaseProvider.auth + " / " + this.authStateListener + " / " + this.listenerSetted);
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

    public static FirebaseAuth getAuth() {
        return SingletonFirebaseProvider.auth;
    }

    public void sendMessageToUI(int actionType) {
        if (this.handler != null) {
            Message message = this.handler.obtainMessage(actionType);
            this.handler.sendMessage(message);
        } else {
            Log.w(TAG, "Handler is null!");
        }
    }

    public void signOut() {
        if (this.getFirebaseUser() != null) {
            Log.d(TAG, "force Firebase sign out");
            SingletonFirebaseProvider.auth.signOut();
        }
    }

    public void reauthenticateUser() {
        if (this.getFirebaseUser() != null) {
            this.getFirebaseUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // Sign in success
                        Log.d(TAG, "reauthenticate:success");
                    } else {
                        // Sign in fails
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            Log.d(TAG, "reauthenticate:failed", task.getException());
                            sendMessageToUI(BAD_EMAIL_OR_PSW);
                        } catch (FirebaseAuthInvalidUserException e3) {
                            Log.d(TAG, "reauthenticate:failed", task.getException());
                            sendMessageToUI(INVALID_USER);
                        } catch (FirebaseNetworkException e4) {
                            Log.d(TAG, "reauthenticate:failed", task.getException());
                            sendMessageToUI(NETWORK_ERROR);
                        } catch (Exception e1) {
                            Log.w(TAG, "reauthenticate:failed", task.getException());
                            sendMessageToUI(UNKNOWN_EVENT);
                        }
                        signOut();
                    }
                }
            });
        } else {
            Log.d(TAG, "reauthenticate:failed");
            this.sendMessageToUI(INVALID_USER);
        }
    }
}
