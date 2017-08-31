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
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Created by alfredo on 17/08/17.
 */

public class SingletonEmailAndPasswordProvider
        implements BaseProvider, TypeMessages {

    private static final String TAG = SingletonEmailAndPasswordProvider.class.getSimpleName();

    private boolean resendVerificationEmail;
    private SingletonFirebaseProvider singletonFirebaseProvider;



    // Singleton
    private SingletonEmailAndPasswordProvider() {
        this.singletonFirebaseProvider = SingletonFirebaseProvider.getInstance();

        Log.d(TAG, "Instantiated SingleEmailAndPasswordProvider.");

        this.resendVerificationEmail = false;
    }

    private static class EmailAndPasswordProviderContainer {
        private final static SingletonEmailAndPasswordProvider INSTANCE = new SingletonEmailAndPasswordProvider();
    }

    static SingletonEmailAndPasswordProvider getInstance() {
        return EmailAndPasswordProviderContainer.INSTANCE;
    }


    @Override
    public void signIn(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            this.singletonFirebaseProvider.sendMessageToUI(EMAIL_REQUIRED);
            return;
        } else if (TextUtils.isEmpty(password)) {
            this.singletonFirebaseProvider.sendMessageToUI(PASSWORD_REQUIRED);
            return;
        }

        SingletonFirebaseProvider
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
                                singletonFirebaseProvider.sendMessageToUI(BAD_EMAIL_OR_PSW);
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
                        }
                    }
                });
    }

    private void checkIfEmailVerified() {
        if (this.singletonFirebaseProvider
                .getFirebaseUser()
                .isEmailVerified()) {
            // SingletonUser verified
            Log.d(TAG, "checkIfEmailVerified:success");
            this.singletonFirebaseProvider.sendMessageToUI(USER_LOGIN_EMAIL_PSW);
        } else {
            // Email is not verified
            if (this.resendVerificationEmail) {
                Log.d(TAG, "checkIfEmailVerified:resend_verification_email");
                this.sendVerificationEmail();
                this.resendVerificationEmail = false;
                this.singletonFirebaseProvider.sendMessageToUI(RESEND_VERIFICATION_EMAIL);
            } else {
                Log.d(TAG, "checkIfEmailVerified:failure");
                this.singletonFirebaseProvider.sendMessageToUI(EMAIL_NOT_VERIFIED);
            }
            // Log out user
            this.signOut();
        }
    }

    public void signUp(String email, String password, final String username) {
        if (TextUtils.isEmpty(email)) {
            this.singletonFirebaseProvider.sendMessageToUI(EMAIL_REQUIRED);
            return;
        } else if (TextUtils.isEmpty(password)) {
            this.singletonFirebaseProvider.sendMessageToUI(PASSWORD_REQUIRED);
            return;
        }

        SingletonFirebaseProvider
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

    public void setUsername(String username, String email, String password) {
        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();

        if (username != null) {
            builder.setDisplayName(username);
        }
        if (email != null) {
            this.singletonFirebaseProvider
                    .getFirebaseUser()
                    .updateEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "SingletonUser email address updated.");
                            } else {
                                Log.d(TAG, "SingletonUser email address NOT updated.");
                            }
                        }
                    });
        }
        if (password != null) {
            this.singletonFirebaseProvider
                    .getFirebaseUser()
                    .updatePassword(password)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "SingletonUser password updated.");
                            } else {
                                Log.d(TAG, "SingletonUser password NOT updated.");
                            }
                        }
                    });
        }

        if (username != null) {
            this.singletonFirebaseProvider
                    .getFirebaseUser()
                    .updateProfile(builder.build())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "SingletonUser profile updated.");
                            } else {
                                Log.d(TAG, "SingletonUser profile NOT updated.");
                            }
                        }
                    });
        }
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

    @Override
    public void signOut() {
        SingletonFirebaseProvider
                .getAuth()
                .signOut();
    }

    /**
     * Check if a user is signed in and his email is verified with Firease provider
     * @return true: if user is signed in and his email is verified; false: the user is not signed in or his email is not verified
     */
    @Override
    public boolean isSignIn() {
        return this.singletonFirebaseProvider.isFirebaseSignIn() &&
                this.singletonFirebaseProvider.getFirebaseUser().isEmailVerified();
    }
}
