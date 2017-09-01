package com.driveembetter.proevolutionsoftware.driveembetter.entity;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.driveembetter.proevolutionsoftware.driveembetter.authentication.SingletonFirebaseProvider;
import com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants;
import com.driveembetter.proevolutionsoftware.driveembetter.exception.CallbackNotInitialized;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alfredo on 10/08/17.
 */

public class SingletonUser
        extends User
        implements Constants {

    private final static String TAG = SingletonUser.class.getSimpleName();

    // TODO utilizza l'interfaccia Parcelable se si deve passare SingletonUser da un activity ad un'altra
    // Data from Firebase Authentication
    private boolean emailVerified;
    private String providerId;
    private List providerData;

    // SingletonUser data to store in Firebase DB
    private float currentUserLatitude;
    private float currentUserLongitude;
    private ArrayList<Vehicle> vehicleArrayList;
    private UserDataCallback userDataCallback;

    // Miscellaneous user data
    private Vehicle currentVehicle;

    private static SingletonUser singletonInstance;

    // Short constructor
    private SingletonUser(String username, String email) {
        super(username, email);
    }

    // Extended constructor
    private SingletonUser(String username, String email, Uri photoUrl, String uid, boolean emailVerified, String providerId, List providerData) {
        super(username, email, photoUrl, uid);
        this.emailVerified = emailVerified;
        this.providerId = providerId;
        this.providerData = providerData;
    }



    // Singleton
    public static SingletonUser getInstance(String username, String email, Uri photoUrl, String uid, boolean emailVerified, String providerId, List providerData) {
        if(SingletonUser.singletonInstance == null) {
            synchronized (SingletonUser.class) {
                if(SingletonUser.singletonInstance == null) {
                    SingletonUser.singletonInstance =
                            new SingletonUser(username, email, photoUrl, uid, emailVerified, providerId, providerData);
                }
            }
        } else {
            Log.w(TAG, "getInstance:FirebaseProvider already initialized");
        }

        return SingletonUser.getInstance();
    }

    @Nullable
    public static SingletonUser getInstance() {
        return SingletonUser.singletonInstance;
    }



    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public List getProviderData() {
        return providerData;
    }

    public void setProviderData(List providerData) {
        this.providerData = providerData;
    }

    public float getCurrentUserLatitude() {
        return currentUserLatitude;
    }

    public void setCurrentUserLatitude(float currentUserLatitude) {
        this.currentUserLatitude = currentUserLatitude;
    }

    public float getCurrentUserLongitude() {
        return currentUserLongitude;
    }

    public void setCurrentUserLongitude(float currentUserLongitude) {
        this.currentUserLongitude = currentUserLongitude;
    }

    public ArrayList<Vehicle> getVehicleArrayList() {
        return this.vehicleArrayList;
    }

    public void getVehicles(final UserDataCallback userDataCallback)
            throws CallbackNotInitialized {
        if (userDataCallback == null) {
            Log.w(TAG, "Callback not initialized");
            throw new CallbackNotInitialized("Callback not initialized");
        }

        // Get a reference to our posts
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child(NODE_USERS)
                .child(SingletonFirebaseProvider
                        .getInstance()
                        .getFirebaseUser()
                        .getUid())
                .child(NODE_VEHICLES);

        // Attach a listener to read the data at our posts reference
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> vehiclesList = dataSnapshot.getChildren();
                if (!dataSnapshot.exists() || vehiclesList == null) {
                    vehicleArrayList = null;
                } else {
                    for (DataSnapshot vehicle : vehiclesList) {
                        if (vehicle.getValue() != null) {
                            String[] parts = vehicle.getValue().toString().split(";");
                            vehicleArrayList = new ArrayList<Vehicle>();
                            vehicleArrayList.add(
                                    new Vehicle(
                                            parts[0],
                                            parts[1],
                                            parts[2],
                                            parts[3]
                                    )
                            );
                        } else {
                            Log.d(TAG, "Error while retrieve data from database");
                            vehicleArrayList = null;
                        }
                    }
                }
                userDataCallback.onVehiclesReceive();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public void setVehicleArrayList(ArrayList<Vehicle> vehicleArrayList) {
        this.vehicleArrayList = vehicleArrayList;
    }

    public Vehicle getCurrentVehicle() {
        return currentVehicle;
    }

    public void setCurrentVehicle(Vehicle currentVehicle) {
        this.currentVehicle = currentVehicle;
    }

    public static void resetSession() {
        SingletonUser.singletonInstance = null;
    }
}
