package com.driveembetter.proevolutionsoftware.driveembetter.utils;

import android.net.Uri;
import android.util.Log;

import com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.SingletonUser;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.User;
import com.driveembetter.proevolutionsoftware.driveembetter.exception.CallbackNotInitialized;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by alfredo on 01/09/17.
 */

public class DatabaseManager
        implements Constants {

    private final static String TAG = DatabaseManager.class.getSimpleName();



    public interface SendData {
        public void dataReceived(ArrayList<User> users);
    }

    public static void manageDataUserDB() {
        final DatabaseReference reference = FirebaseDatabase
                .getInstance()
                .getReference();
        final Query query = reference
                .child(NODE_USERS)
                .child(SingletonUser
                        .getInstance()
                        .getUid());

        // Attach a listener to read the data at our posts reference
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> users = dataSnapshot.getChildren();
                if (users == null || !dataSnapshot.exists()) {
                    Log.d(TAG, "Create user data");
                    reference
                            .child(CHILD_POINTS)
                            .setValue(SingletonUser.getInstance().getPoints());

                    reference
                            .child(CHILD_IMAGE)
                            .setValue(SingletonUser.getInstance().getPhotoUrl().toString());

                    if (SingletonUser.getInstance().getUsername() != null &&
                            !SingletonUser.getInstance().getUsername().isEmpty()) {
                        reference
                                .child(CHILD_USERNAME)
                                .setValue(SingletonUser.getInstance().getUsername());
                    } else if (SingletonUser.getInstance().getEmail() != null &&
                            !SingletonUser.getInstance().getEmail().isEmpty()) {
                        reference
                                .child(CHILD_USERNAME)
                                .setValue(SingletonUser.getInstance().getEmail());
                    }
                } else {
                    retrieveUserData(query);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "The read failed: " + databaseError.getCode());
            }
        });
    }

    private static void retrieveUserData(Query query) {
        query
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                        if (dataSnapshot.exists() && data != null) {
                            Log.d(TAG, "Update user data");
                            for (DataSnapshot value : data) {
                                if (value.getKey().equals(CHILD_POINTS)) {
                                    Log.d(TAG, "Current point: " + value.getValue());
                                    SingletonUser
                                            .getInstance()
                                            .setPoints((long) value.getValue());
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public static void getUserRank(final SendData sendData) {
        if (sendData == null) {
            Log.w(TAG, "Callback not initialized");
            throw new CallbackNotInitialized("Callback not initialized");
        }

        final DatabaseReference reference = FirebaseDatabase
                .getInstance()
                .getReference();
        final Query query = reference
                .child(NODE_USERS);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<User> userList;
                Iterable<DataSnapshot> users = dataSnapshot.getChildren();
                if (users != null || dataSnapshot.exists()) {
                    userList = new ArrayList<User>();
                    for (DataSnapshot user : users) {
                        String username; Uri image; long points;

                        DataSnapshot tmp = user.child(CHILD_USERNAME);
                        if (tmp != null && tmp.getValue() != null) {
                            username = tmp.getValue().toString();
                        } else {
                            username = null;
                        }

                        tmp = user.child(CHILD_IMAGE);
                        if (tmp != null && tmp.getValue() != null) {
                            image = Uri.parse(tmp.getValue().toString());
                        } else {
                            image = null;
                        }

                        tmp = user.child(CHILD_POINTS);
                        if (tmp != null && tmp.getValue() != null) {
                            points = Long.parseLong(tmp.getValue().toString());
                        } else {
                            points = 0;
                        }

                        userList.add(new User(
                                username,
                                image,
                                points
                        ));
                    }
                } else {
                    userList = null;
                }
                sendData.dataReceived(userList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "The read failed: " + databaseError.getCode());
            }
        });
    }
}
