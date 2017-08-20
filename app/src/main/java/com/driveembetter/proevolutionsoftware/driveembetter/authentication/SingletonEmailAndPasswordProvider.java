package com.driveembetter.proevolutionsoftware.driveembetter.authentication;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

/**
 * Created by alfredo on 17/08/17.
 */

public class SingletonEmailAndPasswordProvider
        implements BaseProvider, TypeMessages {

    private static final String TAG = "SEmailAndPswProvider";

    private boolean resendVerificationEmail;
    private boolean signIn;
    private SingletonFirebaseProvider singletonFirebaseProvider;



    // Singleton
    private SingletonEmailAndPasswordProvider() {
        this.singletonFirebaseProvider = SingletonFirebaseProvider.getInstance();

        Log.d(TAG, "Instantiated SingleEmailAndPasswordProvider.");

        this.signIn = this.resendVerificationEmail = false;
    }

    private static class EmailAndPasswordProviderContainer {
        private final static SingletonEmailAndPasswordProvider INSTANCE = new SingletonEmailAndPasswordProvider();
    }

    public static SingletonEmailAndPasswordProvider getInstance() {
        return EmailAndPasswordProviderContainer.INSTANCE;
    }



    public void signIn(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            this.singletonFirebaseProvider.sendMessageToUI(EMAIL_REQUIRED);
            return;
        } else if (TextUtils.isEmpty(password)) {
            this.singletonFirebaseProvider.sendMessageToUI(PASSWORD_REQUIRED);
            return;
        }

        this.singletonFirebaseProvider
                .getAuth()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) this.singletonFirebaseProvider.getContext(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {

                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                Log.d(TAG, "signInWithEmail:failed", task.getException());
                                singletonFirebaseProvider.sendMessageToUI(INVALID_CREDENTIALS);
                            } catch (FirebaseAuthInvalidUserException e3) {
                                Log.d(TAG, "signInWithEmail:failed", task.getException());
                                singletonFirebaseProvider.sendMessageToUI(INVALID_USER);
                            } catch (FirebaseNetworkException e4) {
                                Log.d(TAG, "signInWithEmail:failed", task.getException());
                                singletonFirebaseProvider.sendMessageToUI(NETWORK_ERROR);
                            } catch (Exception e1) {
                                Log.w(TAG, "signInWithEmail:failed", task.getException());
                                singletonFirebaseProvider.sendMessageToUI(UNKNOWN_EVENT);
                            }

                        } else {
                            checkIfEmailVerified();
                            signIn = true;
                        }
                    }
                });
    }

    private void checkIfEmailVerified() {
        if (this.singletonFirebaseProvider
                .getFirebaseUser()
                .isEmailVerified()) {
            // User verified
            Log.d(TAG, "checkIfEmailVerified:success");
            this.singletonFirebaseProvider.sendMessageToUI(USER_LOGIN_EMAIL_PSW);
        } else {
            // Email is not verified
            if (this.resendVerificationEmail) {
                Log.d(TAG, "checkIfEmailVerified:resend_verification_email");
                this.sendVerificationEmail();
                this.resendVerificationEmail = false;
                this.singletonFirebaseProvider.sendMessageToUI(RESEND_VERIFICATION_EMAIL);
                // Log out user
                this.signOut();
            } else {
                Log.d(TAG, "checkIfEmailVerified:failure");
                this.singletonFirebaseProvider.sendMessageToUI(EMAIL_NOT_VERIFIED);
                // Log out user
                this.signOut();
            }
        }
    }

    public void signUp(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            this.singletonFirebaseProvider.sendMessageToUI(EMAIL_REQUIRED);
            return;
        } else if (TextUtils.isEmpty(password)) {
            this.singletonFirebaseProvider.sendMessageToUI(PASSWORD_REQUIRED);
            return;
        }

        this.singletonFirebaseProvider
                .getAuth()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) this.singletonFirebaseProvider.getContext(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {

                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e) {
                                Log.d(TAG, "createUserWithEmailAndPassword:failed", task.getException());
                                singletonFirebaseProvider.sendMessageToUI(USER_ALREADY_EXIST);
                            } catch (FirebaseNetworkException e4) {
                                Log.d(TAG, "signInWithEmail:failed", task.getException());
                                singletonFirebaseProvider.sendMessageToUI(NETWORK_ERROR);
                            } catch (FirebaseAuthWeakPasswordException e3) {
                                Log.d(TAG, "createUserWithEmailAndPassword:failed", task.getException());
                                singletonFirebaseProvider.sendMessageToUI(PASSWORD_INVALID);
                            } catch (FirebaseAuthInvalidCredentialsException e2) {
                                Log.d(TAG, "createUserWithEmailAndPassword:failed", task.getException());
                                singletonFirebaseProvider.sendMessageToUI(BAD_FORMATTED_EMAIL);
                            } catch (Exception e1) {
                                Log.w(TAG, "createUserWithEmailAndPassword:failed", task.getException());
                                singletonFirebaseProvider.sendMessageToUI(UNKNOWN_EVENT);
                            }

                        } else {
                            sendVerificationEmail();
                        }
                    }
                });
    }

    public void resendVerificationEmail(String email, String password) {
        this.resendVerificationEmail = true;
        this.signIn(email, password);
    }

    private void sendVerificationEmail() {
        // Send email and wait for confirmation
        this.singletonFirebaseProvider
                .getFirebaseUser()
                .sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Email sent
                            Log.d(TAG, "sendVerificationEmail:success");
                            singletonFirebaseProvider.sendMessageToUI(VERIFICATION_EMAIL_SENT);
                        } else {
                            // Email not sent
                            Log.d(TAG, "sendVerificationEmail:failure");
                            singletonFirebaseProvider.sendMessageToUI(VERIFICATION_EMAIL_NOT_SENT);
                        }
                        signOut();
                    }
                });
    }

    public void signOut() {
        this.signIn = false;
        this.singletonFirebaseProvider
                .getAuth()
                .signOut();
    }

    @Override
    public boolean isSignIn() {
        return this.signIn;
    }
}