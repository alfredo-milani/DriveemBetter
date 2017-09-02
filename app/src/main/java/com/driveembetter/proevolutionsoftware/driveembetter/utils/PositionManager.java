package com.driveembetter.proevolutionsoftware.driveembetter.utils;


import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.driveembetter.proevolutionsoftware.driveembetter.constants.Constants;
import com.driveembetter.proevolutionsoftware.driveembetter.entity.SingletonUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by matti on 28/08/2017.
 */

public class PositionManager
        extends Application
        implements Constants {
    @Override
    public void onTerminate() {
        super.onTerminate();
        if (region != null && subRegion != null && country != null) {
            myRef = database.getReference("position" + "/" + country + "/" + region + "/" + subRegion + "/" + userId);
            myRef.removeValue();
        }
    }

    private double latitude = 0;
    private double longitude = 0;
    private double oldLatitude, oldLongitude;
    private String oldCountry, oldRegion, oldSubRegion;
    private String country, region, subRegion;
    private static PositionManager positionManager;
    private Activity activity;
    private String userId, email;
    private Geocoder geocoder;
    private FirebaseDatabase database;
    private Map<String, Object> emailMap;
    Location oldLocation;
    private DatabaseReference myRef, checkRef, userRef;


    //Singleton
    private PositionManager(Activity activity) {
        this.activity = activity;
        updatePosition();
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public static PositionManager getInstance(Activity activity) {
        if (positionManager == null) {
            positionManager = new PositionManager(activity);
        }
        return positionManager;
    }

    private void updatePosition() {
        LocationManager locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            Log.e("DB", "PERMISSION GRANTED");
        }

        userId = SingletonUser.getInstance().getUid();
        email = SingletonUser.getInstance().getEmail();
        geocoder = new Geocoder(activity.getApplicationContext(), Locale.ENGLISH);
        database = FirebaseDatabase.getInstance();
        oldLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (oldLocation != null) {
            oldLatitude = oldLocation.getLatitude();
            oldLongitude = oldLocation.getLongitude();

            List<Address> oldAddresses;
            try {
                oldAddresses = geocoder.getFromLocation(oldLatitude, oldLongitude, 1);
                if (oldAddresses.size()!=0) {
                    oldCountry = oldAddresses.get(0).getCountryName();
                    oldRegion = oldAddresses.get(0).getAdminArea();
                    oldSubRegion = oldAddresses.get(0).getSubAdminArea();
                }
                if (oldCountry==null)
                    oldCountry = "oldCountry";
                if (oldSubRegion==null)
                    oldSubRegion = "oldSubRegion";
                if (oldRegion==null)
                    oldRegion = "oldRegion";
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            oldCountry = "oldCountry";
            oldRegion = "oldRegion";
            oldSubRegion = "oldSubRegion";
        }


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                List<Address> addresses;
                Map<String, Object> coordinates = new ArrayMap<>();
                emailMap = new ArrayMap<>();
                coordinates.put(CHILD_CURRENT_POSITION, latitude+";"+longitude);
                emailMap.put("email", email);

                String[] strings = getLocationFromCoordinates(latitude, longitude, 1);
                country = strings[0];
                region = strings[1];
                subRegion = strings[2];

                //coordinates.put("lat", latitude);
                //coordinates.put("lon", longitude);
                Boolean radicalChange = false;
                if (oldCountry != null && oldRegion != null && oldSubRegion != null) {
                    if (!oldCountry.equals(country) || !oldRegion.equals(region) || !oldSubRegion.equals(subRegion)) {
                        //delete the current user instance and create a newer
                        myRef = database.getReference(NODE_POSITION + "/" + oldCountry + "/" + oldRegion + "/" + oldSubRegion + "/" + userId);
                        myRef.removeValue();
                        radicalChange = true;
                    }
                }
                if (radicalChange) {
                    //I have to add old user information
                    radicalChange = false;

                }
                oldCountry = country;
                oldRegion = region;
                oldSubRegion = subRegion;
                myRef = database.getReference(NODE_POSITION + "/" + country + "/" + region + "/" + subRegion + "/" + userId);
                checkRef = database.getReference(NODE_POSITION + "/" + country + "/" + region + "/" + subRegion + "/" + userId);

                myRef.updateChildren(coordinates);

                // we need to update even "users" node
                userRef = database.getReference(NODE_USERS).child(userId);
                // TODO controlla comportamento se non esistono i figli
                userRef.updateChildren(coordinates);
                ////

                checkRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.hasChild("email")) {
                            myRef.updateChildren(emailMap);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                if (region != null && subRegion != null && country != null) {
                    myRef = database.getReference(NODE_POSITION + "/" + country + "/" + region + "/" + subRegion + "/" + userId);
                    myRef.removeValue();
                }
            }
        });
    }

    public void deletePosition() {
        if (region != null && subRegion != null && country != null) {
            myRef = database.getReference("position" + "/" + country + "/" + region + "/" + subRegion + "/" + userId);
            myRef.removeValue();
        }
    }

    /**
     * Get Location from coordinates
     * @param latitude latitude
     * @param longitude longitude
     * @param maxResult result's number
     * @return string[0] --> Nation; string[1] --> Region; string[2] --> District
     */
    public String[] getLocationFromCoordinates(double latitude, double longitude, int maxResult) {
        String[] strings = new String[3];
        try {
            List<Address> addresses = this.geocoder.getFromLocation(latitude, longitude, maxResult);

            strings[0] = addresses.get(0).getCountryName();
            strings[1] = addresses.get(0).getAdminArea();
            strings[2] = addresses.get(0).getSubAdminArea();
        } catch (IOException e) {
            e.printStackTrace();

            strings[0] = strings[1] = strings[2] = null;
            // TODO TO HANDLE
        }
        return strings;
    }
}