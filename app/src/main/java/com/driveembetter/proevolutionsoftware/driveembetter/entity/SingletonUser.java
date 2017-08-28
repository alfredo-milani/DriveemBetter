package com.driveembetter.proevolutionsoftware.driveembetter.entity;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.driveembetter.proevolutionsoftware.driveembetter.authentication.SingletonFirebaseProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by alfredo on 10/08/17.
 */

public class SingletonUser {

    private final static String TAG = "SingletonUser";

    // TODO utilizza l'interfaccia Parcelable se si deve passare SingletonUser da un activity ad un'altra
    // Data from Firebase Authentication
    private String username;
    private String email;
    private Uri photoUrl;
    private String uid;
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
    public SingletonUser(String username, String email) {
        this.username = username;
        this.email = email;
    }

    // Extended constructor
    private SingletonUser(String username, String email, Uri photoUrl, String uid, boolean emailVerified, String providerId, List providerData) {
        this(username, email);
        this.photoUrl = photoUrl;
        this.uid = uid;
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



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Uri getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(Uri photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public void setUserDataCallback(UserDataCallback userDataCallback) {
        this.userDataCallback = userDataCallback;
    }

    public ArrayList<Vehicle> getVehicleArrayList() {
        // Get a reference to our posts
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("users/" + SingletonFirebaseProvider
                .getInstance()
                .getFirebaseUser()
                .getUid() + "/vehicles");

        this.vehicleArrayList = new ArrayList<>();

        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                if (data == null) {
                    Log.d(TAG, "vehicle NULL");

                    vehicleArrayList = null;
                    return;
                }

                for (String vehicle : data.keySet()) {
                    Log.d(TAG, "data: " + data.get(vehicle));

                    String[] parts = data.get(vehicle).toString().split(";");

                    vehicleArrayList.add(
                            new Vehicle(
                                    parts[0],
                                    parts[1],
                                    parts[2],
                                    parts[3]
                            )
                    );
                }

                if (userDataCallback != null) {
                    userDataCallback.setVehicles();
                    return;
                }

                Log.w(TAG, "Callback not setted");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        return vehicleArrayList;
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

    public void resetSession() {
        SingletonUser.singletonInstance = null;
    }
}