package com.proevolutionsoftware.driveembetter.authentication.factoryProvider;

import android.net.Uri;
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
import com.proevolutionsoftware.driveembetter.authentication.BaseProvider;
import com.proevolutionsoftware.driveembetter.authentication.SingletonFirebaseProvider;
import com.proevolutionsoftware.driveembetter.authentication.TypeMessages;
import com.proevolutionsoftware.driveembetter.constants.Constants;
import com.proevolutionsoftware.driveembetter.entity.SingletonUser;
import com.proevolutionsoftware.driveembetter.exceptions.CallbackNotInitialized;
import com.proevolutionsoftware.driveembetter.utils.FirebaseDatabaseManager;

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

    public static SingletonEmailAndPasswordProvider getInstance() {
        return EmailAndPasswordProviderContainer.INSTANCE;
    }


    @Override
    public void signIn(String email, String password) {
        SingletonFirebaseProvider
                .getAuth()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
        SingletonFirebaseProvider
                .getAuth()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
                            if (!TextUtils.isEmpty(username)) {
                                Log.d(TAG, "username received");
                                setUsername(username);
                            } else {
                                sendVerificationEmail();
                            }
                        }
                    }
                });
    }

    public interface EditProfileCallback {
        int UP_USERNAME_SUCCESS = 1;
        int UP_USERNAME_FAILURE = -1;
        int UP_PICTURE_SUCCESS_STORAGE = 2;
        int UP_PICTURE_FAILURE_STORAGE = -2;
        int UP_PICTURE_SUCCESS_AUTH = 5;
        int UP_PICTURE_FAILURE_AUTH = -5;
        int UP_PASSWORD_SUCCESS = 3;
        int UP_PASSWORD_FAILURE = -3;
        int UP_EMAIL_SUCCESS = 4;
        int UP_EMAIL_FAILURE = -4;


        void onProfileModified(int response);
    }

    public void editUsername(final EditProfileCallback callback, final String username) {
        if (callback == null) {
            throw new CallbackNotInitialized(TAG);
        }
        
        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
        if (username != null) {
            builder.setDisplayName(username);
            this.singletonFirebaseProvider
                    .getFirebaseUser()
                    .updateProfile(builder.build())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "SingletonUser profile updated.");
                                callback.onProfileModified(EditProfileCallback.UP_USERNAME_SUCCESS);
                                FirebaseDatabaseManager.updateUserData(Constants.CHILD_USERNAME, username);
                                FirebaseDatabaseManager.updatePositionData(Constants.CHILD_USERNAME, username);
                            } else {
                                Log.d(TAG, "SingletonUser profile NOT updated.");
                                callback.onProfileModified(EditProfileCallback.UP_USERNAME_FAILURE);
                            }
                        }
                    });
        }
    }

    public void setUsername(String username) {
        Log.d(TAG, "setUsername");
        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();

        if (username != null) {
            builder.setDisplayName(username);
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

                            sendVerificationEmail();
                        }
                    });
        }
    }

    public void setEmail(final EditProfileCallback callback, final String email) {
        if (callback == null) {
            throw new CallbackNotInitialized(TAG);
        }

        if (email != null) {
            this.singletonFirebaseProvider
                    .getFirebaseUser()
                    .updateEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "SingletonUser email address updated");
                                callback.onProfileModified(EditProfileCallback.UP_EMAIL_SUCCESS);
                            } else {
                                Log.d(TAG, "SingletonUser email address NOT updated");
                                callback.onProfileModified(EditProfileCallback.UP_EMAIL_FAILURE);
                            }
                        }
                    });
        }
    }

    /**
     * If there is only Google account, with this method will be created a Firebase account with given password (Google account will not be modified)
     * else the password of Firebase account will be modified as well
     * @param callback which method notify on  result
     * @param password data to modify
     */
    public void setPassword(final EditProfileCallback callback, String password) {
        if (callback == null) {
            throw new CallbackNotInitialized(TAG);
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
                                callback.onProfileModified(EditProfileCallback.UP_PASSWORD_SUCCESS);
                            } else {
                                Log.d(TAG, "SingletonUser password NOT updated.");
                                callback.onProfileModified(EditProfileCallback.UP_PASSWORD_FAILURE);
                            }
                        }
                    });
        }
    }

    public void setImageProfile(final EditProfileCallback callback, final Uri imageProfile) {
        if (callback == null) {
            throw new CallbackNotInitialized(TAG);
        }

        if (imageProfile != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(imageProfile)
                    .build();

            this.singletonFirebaseProvider.getFirebaseUser()
                    .updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User profile picture updated: " + singletonFirebaseProvider.getFirebaseUser().getPhotoUrl().toString());
                                callback.onProfileModified(EditProfileCallback.UP_PICTURE_SUCCESS_AUTH);
                            } else {
                                Log.d(TAG, "User profile picture not updated");
                                callback.onProfileModified(EditProfileCallback.UP_PICTURE_FAILURE_AUTH);
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
        Log.d(TAG, "sendVerificationEmail");
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
        this.singletonFirebaseProvider.signOut();
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

    @Override
    public SingletonUser getUserInformations() {
        return this.singletonFirebaseProvider.getUserInformations();
    }
}
