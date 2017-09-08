package com.driveembetter.proevolutionsoftware.driveembetter.entity;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants;
import com.driveembetter.proevolutionsoftware.driveembetter.exceptions.CallbackNotInitialized;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alfredo on 10/08/17.
 */

public class SingletonUser
        extends User
        implements Constants,
        DatabaseManager.RetrieveVehiclesFromDB {

    private final static String TAG = SingletonUser.class.getSimpleName();

    // Data from Firebase Authentication
    private boolean emailVerified;
    private String providerId;
    private List providerData;

    // SingletonUser data to store in Firebase DB
    private float currentUserLatitude;
    private float currentUserLongitude;
    private ArrayList<Vehicle> vehicleArrayList;

    // Miscellaneous user data
    private Vehicle currentVehicle;

    private static SingletonUser singletonInstance;

    private SingletonUser(String username, String email, Uri photoUrl, String uid, boolean emailVerified, String providerId, List providerData) {
        super(uid, username, email, photoUrl, 0, null);
        this.setEmailVerified(emailVerified);
        this.setProviderId(providerId);
        this.setProviderData(providerData);
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



    public interface UserDataCallback {
        public void onVehiclesReceive();
    }

    public boolean isEmailVerified() {
        return this.emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getProviderId() {
        return this.providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public List getProviderData() {
        return this.providerData;
    }

    public void setProviderData(List providerData) {
        this.providerData = providerData;
    }

    public float getCurrentUserLatitude() {
        return this.currentUserLatitude;
    }

    public void setCurrentUserLatitude(float currentUserLatitude) {
        this.currentUserLatitude = currentUserLatitude;
    }

    public float getCurrentUserLongitude() {
        return this.currentUserLongitude;
    }

    public void setCurrentUserLongitude(float currentUserLongitude) {
        this.currentUserLongitude = currentUserLongitude;
    }

    public ArrayList<Vehicle> getVehicleArrayList() {
        synchronized (this) {
            return this.vehicleArrayList;
        }
    }

    public void getVehicles(final UserDataCallback userDataCallback)
            throws CallbackNotInitialized {
        if (userDataCallback == null) {
            Log.w(TAG, "Callback not initialized");
            throw new CallbackNotInitialized("Callback not initialized");
        }

        DatabaseManager.getVehiclesDB(this, userDataCallback);
    }

    @Override
    public void onUserVehiclesReceived(ArrayList<Vehicle> vehicles) {
        this.vehicleArrayList = vehicles;
    }

    public void setVehicleArrayList(ArrayList<Vehicle> vehicleArrayList) {
        this.vehicleArrayList = vehicleArrayList;
    }

    public Vehicle getCurrentVehicle() {
        return this.currentVehicle;
    }

    public void setCurrentVehicle(Vehicle currentVehicle) {
        this.currentVehicle = currentVehicle;
    }

    public static void resetSession() {
        SingletonUser.singletonInstance = null;
    }
}
