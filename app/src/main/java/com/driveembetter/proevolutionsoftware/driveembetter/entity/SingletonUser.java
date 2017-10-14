package com.driveembetter.proevolutionsoftware.driveembetter.entity;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants;
import com.driveembetter.proevolutionsoftware.driveembetter.exceptions.CallbackNotInitialized;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.FirebaseDatabaseManager;
import com.driveembetter.proevolutionsoftware.driveembetter.utils.NonReentrantLock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by alfredo on 10/08/17.
 */

public class SingletonUser extends User
        implements Constants,
        FirebaseDatabaseManager.RetrieveVehiclesFromDB {

    private final static String TAG = SingletonUser.class.getSimpleName();

    // Data from Firebase Authentication
    private boolean emailVerified;
    private String providerId;
    private List providerData;

    // SingletonUser data to store in Firebase DB
    private double latitude;
    private double longitude;
    private String country, region, subRegion, city, address;
    private ArrayList<Vehicle> vehicleArrayList;
    private List<Double> historicalFeedback;

    // Statistics
    private MeanDay meanDay;
    private MeanWeek meanWeek;

    // Miscellaneous user data
    private Vehicle currentVehicle;

    // Sync
    private final Lock mtxSyncData;
    private final NonReentrantLock mtxUpdatePosition;

    private static SingletonUser singletonInstance;

    private SingletonUser(String username, String email, Uri photoUrl, String uid, boolean emailVerified, String providerId, List providerData) {
        super(uid, username, email, photoUrl, 0, null, 0.0, "");
        this.setEmailVerified(emailVerified);
        this.setProviderId(providerId);
        this.setProviderData(providerData);
        this.setAvailability(UNAVAILABLE);
        this.setCountry(COUNTRY);
        this.setRegion(REGION);
        this.setSubRegion(SUB_REGION);
        this.setCity(CITY);
        this.setAddress(ADDRESS);
        this.meanDay = new MeanDay();
        this.meanWeek = new MeanWeek();
        // Lock rientrante perché i thread sono diversi
        this.mtxSyncData = new ReentrantLock(true);
        // Lock non rientrante perché lo stesso thread opera sulle risorse
        this.mtxUpdatePosition = new NonReentrantLock();
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
        void onVehiclesReceive();
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

    public Double getFeedback() {
        double sum = 0.0;
        if (historicalFeedback != null) {
            for (int i = 0; i < historicalFeedback.size(); i++) {
                sum += historicalFeedback.get(i);
            }
            return sum / historicalFeedback.size();
        }
        return sum;
    }

    public void updateHistoricalFeedback(Double currentFeedback) {
        historicalFeedback.add(currentFeedback);
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
            throw new CallbackNotInitialized(TAG);
        }

        FirebaseDatabaseManager.getCurrentVehicle(this, userDataCallback);
        FirebaseDatabaseManager.getVehiclesDB(this, userDataCallback);
        //FirebaseDatabaseManager.getCurrentVehicle(this, userDataCallback);
    }

    @Override
    public void onUserVehiclesReceived(ArrayList<Vehicle> vehicles) {
        this.vehicleArrayList = vehicles;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSubRegion() {
        return subRegion;
    }

    public void setSubRegion(String subRegion) {
        this.subRegion = subRegion;
    }

    public static void resetSession() {
        SingletonUser.singletonInstance = null;
    }

    public Lock getMtxSyncData() {
        return this.mtxSyncData;
    }

    public NonReentrantLock getMtxUpdatePosition() {
        return this.mtxUpdatePosition;
    }

    public MeanDay getMeanDay() {
        return this.meanDay;
    }

    public void setMeanDay(MeanDay meanDay) {
        this.meanDay = meanDay;
    }

    public MeanWeek getMeanWeek() {
        return this.meanWeek;
    }

    public void setMeanWeek(MeanWeek meanWeek) {
        this.meanWeek = meanWeek;
    }
}
