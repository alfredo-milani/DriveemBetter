package com.driveembetter.proevolutionsoftware.driveembetter.authentication;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

/**
 * Created by alfredo on 11/08/17.
 */

class Provider extends Authentication {

    private static final String TAG = "Provider";

    private boolean signIn = false;

    Provider(Context context) {
        super(context);
        Log.d(TAG, "Instantiated Provider class");
    }

    @Override
    public void signIn(String email, String password) {
        this.mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) this.mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            signIn = false;
                            return;
                        }

                        getCurrentFirebaseUser();
                        getUserInformation();
                        if (firebaseUser != null) {
                            String str = "UTENTE: " + firebaseUser.getDisplayName() +
                                    "\nEM: " + firebaseUser.getEmail() + "\nID: " + firebaseUser.getUid() + "\nPROVID: " + firebaseUser.getProviderId() + "\nPROVDATA: " + firebaseUser.getProviderData();
                            Toast.makeText(mContext, str, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mContext, "DIOMMERDA", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void signUp(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        String str = "UTENTE: " + firebaseUser.getDisplayName() +
                                "\nEM: " + firebaseUser.getEmail() + "\nID: " + firebaseUser.getUid();
                        Toast.makeText(mContext, str,
                                Toast.LENGTH_LONG).show();
                        if (!task.isSuccessful()) {
                            Toast.makeText(mContext, "DIOMMERDA",
                                    Toast.LENGTH_LONG).show();
                        }

                        // ...
                    }
                });
    }

    @Override
    public void signOut() {
        this.mAuth.signOut();

        // Log.d(TAG, "CANE" + user.getEmail());
        if (firebaseUser != null) {
            String str = "UTENTE: " + firebaseUser.getDisplayName() +
                    "\nEM: " + firebaseUser.getEmail() + "\nID: " + firebaseUser.getUid();
            Toast.makeText(mContext, str,
                    Toast.LENGTH_LONG).show();
        } else {
            String str = "";
            Toast.makeText(mContext, str,
                    Toast.LENGTH_LONG).show();
        }
    }
}