package com.driveembetter.proevolutionsoftware.driveembetter.utils;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.driveembetter.proevolutionsoftware.driveembetter.authentication.factoryProvider.SingletonEmailAndPasswordProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.SingletonUser;
import com.driveembetter.proevolutionsoftware.driveembetter.exceptions.CallbackNotInitialized;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Created by alfredo on 12/10/17.
 */

public class FirebaseStorageManager
        implements Constants {

    private final static String TAG = FirebaseStorageManager.class.getSimpleName();

    private final static StorageReference storageReference = FirebaseStorage.getInstance().getReference();



    /**
     *  MISCELLANEOUS METHODS
     */
    public static StorageReference getStorageReference() {
        return FirebaseStorageManager.storageReference;
    }

    public static void uploadProfileImage(final SingletonEmailAndPasswordProvider.EditProfileCallback callback, Uri data) {
        if (callback == null) {
            throw new CallbackNotInitialized(TAG);
        }

        SingletonUser user = SingletonUser.getInstance();
        if (user != null) {
            StorageReference fileReference = FirebaseStorageManager.storageReference
                    .child(CHILD_STORAGE_IMAGES)
                    .child(user.getUid())
                    .child(data.getLastPathSegment());
            UploadTask uploadTask = fileReference.putFile(data);

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Log.d(TAG, "Unsuccess upload");
                    callback.onProfileModified(SingletonEmailAndPasswordProvider.EditProfileCallback.UP_PICTURE_FAILURE_STORAGE);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    if (SingletonUser.getInstance() != null) {
                        SingletonUser.getInstance().setPhotoUrl(downloadUrl);
                    }
                    callback.onProfileModified(SingletonEmailAndPasswordProvider.EditProfileCallback.UP_PICTURE_SUCCESS_STORAGE);
                    FirebaseDatabaseManager.updateUserData(Constants.CHILD_IMAGE, downloadUrl.toString());
                    FirebaseDatabaseManager.updatePositionData(Constants.CHILD_IMAGE, downloadUrl.toString());
                }
            });
        }
    }
}
